package ruiseki.omoshiroikamo.module.machinery.common.tile.item.output;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * ME Output Port - outputs items directly to AE2 ME Network.
 * Extends TEItemOutputPort and implements IGridProxyable for AE2 integration.
 *
 * Flow:
 * 1. Receives items via ISidedInventory (from adjacent machines like
 * QuantumExtractor)
 * 2. Periodically flushes internal slots to ME cache
 * 3. Then flushes ME cache to ME Network
 */
public class TEItemOutputPortME extends TEItemOutputPort implements IGridProxyable, IActionHost {

    private static final int BUFFER_SLOTS = 128;
    private static final long CACHE_CAPACITY = 1600;

    private AENetworkProxy gridProxy;
    private BaseActionSource requestSource;
    private final IItemList<IAEItemStack> itemCache = AEApi.instance()
        .storage()
        .createItemList();

    private long lastOutputTick = 0;
    private long tickCounter = 0;
    private boolean proxyReady = false;

    public TEItemOutputPortME() {
        super(BUFFER_SLOTS); // Has physical buffer slots for receiving items
    }

    @Override
    public int getTier() {
        return 0; // No tier for ME version
    }

    // ========== Allow item insertion ==========

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true; // Accept items from adjacent machines
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
        return new ItemStack(net.minecraft.init.Blocks.stone);
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
                // Add to cache
                itemCache.add(
                    AEApi.instance()
                        .storage()
                        .createItemStack(stack.copy()));
                setInventorySlotContents(i, null);
            }
        }
    }

    protected long getCachedAmount() {
        long amount = 0;
        for (IAEItemStack item : itemCache) {
            amount += item.getStackSize();
        }
        return amount;
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

                IAEItemStack rest = Platform.poweredInsert(proxy.getEnergy(), storage, s, getRequest());

                if (rest != null && rest.getStackSize() > 0) {
                    s.setStackSize(rest.getStackSize());
                    continue;
                }
                s.setStackSize(0);
            }
        } catch (final GridAccessException e) {
            Logger.debug("ME Output Port: Grid access exception during flush");
        }
        lastOutputTick = tickCounter;
    }

    public boolean isActive() {
        AENetworkProxy proxy = getProxy();
        return proxy != null && proxy.isActive();
    }

    public boolean isPowered() {
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
        tickCounter = worldObj.getTotalWorldTime();

        // Step 1: Move items from physical slots to cache
        moveToCache();

        // Step 2: Flush cache to ME network periodically
        if (tickCounter > lastOutputTick + 20 || getCachedAmount() >= CACHE_CAPACITY) {
            flushCachedStack();
        }

        return false; // Don't call super - we don't push to adjacent blocks
    }

    // ========== NBT Handling ==========

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        // Save cached items
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

        // Save proxy state
        if (gridProxy != null) {
            gridProxy.writeToNBT(root);
        }
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);

        // Load cached items
        itemCache.resetStatus();
        if (root.hasKey("cachedItems")) {
            NBTTagList items = root.getTagList("cachedItems", 10);
            for (int i = 0; i < items.tagCount(); i++) {
                NBTTagCompound tag = items.getCompoundTagAt(i);
                ItemStack stack = ItemStack.loadItemStackFromNBT(tag);
                if (stack != null) {
                    IAEItemStack aeStack = AEItemStack.create(stack);
                    if (aeStack != null) {
                        aeStack.setStackSize(tag.getLong("count"));
                        itemCache.add(aeStack);
                    }
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
}
