package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.helper.InventoryHelpers;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;

/**
 * ME Output Port - outputs items directly to AE2 ME Network.
 * Extends TEItemOutputPort and implements IGridProxyable for AE2 integration.
 * Flow:
 * 1. Receives items via ISidedInventory (from adjacent machines like
 * QuantumExtractor)
 * 2. Periodically flushes internal slots to ME cache
 * 3. Then flushes ME cache to ME Network
 */
public class TEItemOutputPortME extends TEItemOutputPort implements IGridProxyable, IActionHost {

    private static final int BUFFER_SLOTS = 63;

    private AENetworkProxy gridProxy;
    private BaseActionSource requestSource;
    private final IItemList<IAEItemStack> itemCache = AEApi.instance()
        .storage()
        .createItemList();

    private boolean proxyReady = false;

    // Cached item count for fast lookup (avoid iterating itemCache every tick)
    private long cachedItemCount = 0;

    // Client-synced status for Waila
    private boolean clientIsActive = false;
    private boolean clientIsPowered = false;

    public TEItemOutputPortME() {
        super(BUFFER_SLOTS); // Has physical buffer slots for receiving items
    }

    @Override
    public int getTier() {
        return 0; // No tier for ME version
    }

    // ========== IGridProxyable Implementation ==========

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null && worldObj != null) {
            gridProxy = new AENetworkProxy(this, "proxy", getVisualItemStack(), true);
            gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
            // Use complementOf to exclude UNKNOWN
            gridProxy.setValidSides(EnumSet.complementOf(EnumSet.of(ForgeDirection.UNKNOWN)));
        }
        return gridProxy;
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void gridChanged() {
        // Called when the grid changes - no special handling needed
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {
        // Security violation - drop the block
        worldObj.func_147480_a(xCoord, yCoord, zCoord, true); // destroyBlock
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        AENetworkProxy proxy = getProxy();
        return proxy != null ? proxy.getNode() : null;
    }

    // ========== IActionHost Implementation ==========

    @Override
    public IGridNode getActionableNode() {
        AENetworkProxy proxy = getProxy();
        return proxy != null ? proxy.getNode() : null;
    }

    // ========== Item Handling ==========

    /**
     * Returns an ItemStack for visual representation in AE2 interfaces.
     */
    protected ItemStack getVisualItemStack() {
        if (getBlockType() != null) {
            return new ItemStack(getBlockType(), 1, getBlockMetadata());
        }
        return new ItemStack(Blocks.stone);
    }

    protected BaseActionSource getRequest() {
        if (requestSource == null) {
            requestSource = new MachineSource(this);
        }
        return requestSource;
    }

    /**
     * Move items from physical slots to ME cache.
     */
    protected void moveToCache() {
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                // Add to cache and update count
                IAEItemStack aeStack = AEApi.instance()
                    .storage()
                    .createItemStack(stack.copy());
                itemCache.add(aeStack);
                cachedItemCount += stack.stackSize;
                setInventorySlotContents(i, null);
            }
        }
    }

    protected long getCachedAmount() {
        return cachedItemCount;
    }

    // ========== ME Network Transfer ==========

    protected void flushCachedStack() {
        if (!isActive() || itemCache.isEmpty()) {
            return;
        }

        AENetworkProxy proxy = getProxy();
        try {
            IMEMonitor<IAEItemStack> storage = proxy.getStorage()
                .getItemInventory();

            for (IAEItemStack s : itemCache) {
                if (s.getStackSize() == 0) continue;

                long before = s.getStackSize();
                IAEItemStack rest = Platform.poweredInsert(proxy.getEnergy(), storage, s, getRequest());

                if (rest != null && rest.getStackSize() > 0) {
                    cachedItemCount -= (before - rest.getStackSize());
                    s.setStackSize(rest.getStackSize());
                    continue;
                }
                cachedItemCount -= before;
                s.setStackSize(0);
            }
        } catch (final GridAccessException e) {
            Logger.debug("ME Output Port: Grid access exception during flush");
        }
    }

    public boolean isActive() {
        if (worldObj != null && worldObj.isRemote) {
            return clientIsActive; // Use synced value on client
        }
        AENetworkProxy proxy = getProxy();
        return proxy != null && proxy.isActive();
    }

    public boolean isPowered() {
        if (worldObj != null && worldObj.isRemote) {
            return clientIsPowered; // Use synced value on client
        }
        AENetworkProxy proxy = getProxy();
        return proxy != null && proxy.isPowered();
    }

    // ========== Tick Processing ==========

    @Override
    public void doUpdate() {
        super.doUpdate();

        if (worldObj.isRemote) return;

        // Initialize proxy on first tick
        if (!proxyReady && getProxy() != null) {
            getProxy().onReady();
            proxyReady = true;
            Logger.info("ME Output Port: Proxy initialized");
        }
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        // Only process every 10 ticks to reduce overhead
        if (!shouldDoWorkThisTick(10)) {
            return false;
        }

        // Move items from physical slots to cache
        moveToCache();

        // Flush cache to ME network if there are cached items
        if (cachedItemCount > 0) {
            flushCachedStack();
        }

        return false;
    }

    // ========== NBT Handling ==========

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        // Sync ME status to client (for Waila) - use cached values to avoid AE2 calls
        // These are updated in processTasks, not here
        root.setBoolean("meActive", proxyReady);
        root.setBoolean("mePowered", proxyReady);

        // Only save cached items that have content
        if (cachedItemCount > 0) {
            NBTTagList items = new NBTTagList();
            for (IAEItemStack s : itemCache) {
                if (s.getStackSize() == 0) continue;
                NBTTagCompound tag = new NBTTagCompound();
                s.getItemStack()
                    .writeToNBT(tag);
                tag.setLong("count", s.getStackSize());
                items.appendTag(tag);
            }
            root.setTag("cachedItems", items);
        }

        // Save proxy state
        if (gridProxy != null) {
            gridProxy.writeToNBT(root);
        }
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        // Read synced ME status (for Waila on client)
        clientIsActive = root.getBoolean("meActive");
        clientIsPowered = root.getBoolean("mePowered");

        // Load cached items
        itemCache.resetStatus();
        if (root.hasKey("cachedItems")) {
            NBTTagList items = root.getTagList("cachedItems", 10);
            for (int i = 0; i < items.tagCount(); i++) {
                NBTTagCompound tag = items.getCompoundTagAt(i);
                ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
                if (stack != null) {
                    IAEItemStack aeStack = AEItemStack.create(stack);
                    aeStack.setStackSize(tag.getLong("count"));
                    itemCache.add(aeStack);
                }
            }
        }

        // Load proxy state
        if (getProxy() != null) {
            getProxy().readFromNBT(root);
        }
    }

    // ========== Lifecycle ==========

    @Override
    public void validate() {
        super.validate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (gridProxy != null) {
            gridProxy.invalidate();
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (gridProxy != null) {
            gridProxy.onChunkUnload();
        }
    }

    // super to enable openGui
    @Override
    public boolean onBlockActivated(World world, EntityPlayer player, ForgeDirection side, float hitX, float hitY,
        float hitZ) {
        return false;
    }

    // dropStack when break
    public void dropCachedItems() {
        flushCachedStack();
        if (worldObj == null || worldObj.isRemote) return;

        for (IAEItemStack aeStack : itemCache) {
            if (aeStack == null || aeStack.getStackSize() <= 0) continue;

            ItemStack stack = aeStack.getItemStack();
            if (stack == null) continue;
            stack.stackSize = (int) aeStack.getStackSize();

            InventoryHelpers.dropItems(worldObj, stack, getPos());
        }

        itemCache.resetStatus();
    }

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        return EnumIO.OUTPUT;
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        // Disable IO configuration for ME ports
    }

    @Override
    public void toggleSide(ForgeDirection side) {
        // Disable IO configuration for ME ports
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1) {
            return IconRegistry.getIcon("overlay_itemoutput_me");
        }
        return AbstractPortBlock.baseIcon;
    }
}
