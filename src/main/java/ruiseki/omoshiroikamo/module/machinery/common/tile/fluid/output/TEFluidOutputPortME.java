package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import java.util.EnumSet;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.Platform;
import appeng.util.item.AEFluidStack;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;

/**
 * ME Fluid Output Port - outputs fluids directly to AE2 ME Network.
 * Extends TEFluidOutputPort and implements IGridProxyable for AE2 integration.
 * Flow:
 * 1. Receives fluids via IFluidHandler (from adjacent machines)
 * 2. Periodically flushes internal tank to ME cache
 * 3. Then flushes ME cache to ME Network
 */
public class TEFluidOutputPortME extends TEFluidOutputPort implements IGridProxyable, IActionHost {

    private static final int TANK_CAPACITY = 16000; // 16 buckets

    private AENetworkProxy gridProxy;
    private BaseActionSource requestSource;
    private final IItemList<IAEFluidStack> fluidCache = AEApi.instance()
        .storage()
        .createFluidList();

    private boolean proxyReady = false;

    // Cached fluid amount for fast lookup (avoid iterating fluidCache every tick)
    private long cachedFluidAmount = 0;

    // Client-synced status for Waila
    private boolean clientIsActive = false;
    private boolean clientIsPowered = false;

    public TEFluidOutputPortME() {
        super(TANK_CAPACITY);
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

    // ========== Fluid Handling ==========

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
     * Move fluids from internal tank to ME cache.
     */
    protected void moveToCache() {
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null && fluidStack.amount > 0) {
            // Add to cache and update count
            IAEFluidStack aeStack = AEFluidStack.create(fluidStack.copy());
            fluidCache.add(aeStack);
            cachedFluidAmount += fluidStack.amount;
            tank.setFluid(null);
        }
    }

    protected long getCachedAmount() {
        return cachedFluidAmount;
    }

    // ========== ME Network Transfer ==========

    public void flushCachedStack() {
        if (!isActive() || fluidCache.isEmpty()) {
            return;
        }

        AENetworkProxy proxy = getProxy();
        try {
            IMEMonitor<IAEFluidStack> storage = proxy.getStorage()
                .getFluidInventory();

            for (IAEFluidStack s : fluidCache) {
                if (s.getStackSize() == 0) continue;

                long before = s.getStackSize();
                IAEFluidStack rest = Platform.poweredInsert(proxy.getEnergy(), storage, s, getRequest());

                if (rest != null && rest.getStackSize() > 0) {
                    cachedFluidAmount -= (before - rest.getStackSize());
                    s.setStackSize(rest.getStackSize());
                    continue;
                }
                cachedFluidAmount -= before;
                s.setStackSize(0);
            }
        } catch (final GridAccessException e) {
            Logger.debug("ME Fluid Output Port: Grid access exception during flush");
        }
    }

    @Override
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
            Logger.info("ME Fluid Output Port: Proxy initialized");
        }
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        // Only process every 10 ticks to reduce overhead
        if (!shouldDoWorkThisTick(10)) {
            return false;
        }

        // Move fluids from tank to cache
        moveToCache();

        // Flush cache to ME network if there are cached fluids
        if (cachedFluidAmount > 0) {
            flushCachedStack();
        }

        return false;
    }
    // ========== NBT Handling ==========

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);

        // Sync ME status to client (for Waila) - use cached values to avoid AE2 calls
        root.setBoolean("meActive", proxyReady);
        root.setBoolean("mePowered", proxyReady);

        // Only save cached fluids that have content
        if (cachedFluidAmount > 0) {
            NBTTagList fluids = new NBTTagList();
            for (IAEFluidStack s : fluidCache) {
                if (s.getStackSize() == 0) continue;
                NBTTagCompound tag = new NBTTagCompound();
                s.getFluidStack()
                    .writeToNBT(tag);
                tag.setLong("count", s.getStackSize());
                fluids.appendTag(tag);
            }
            root.setTag("cachedFluids", fluids);
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

        // Load cached fluids
        fluidCache.resetStatus();
        if (root.hasKey("cachedFluids")) {
            NBTTagList fluids = root.getTagList("cachedFluids", 10);
            for (int i = 0; i < fluids.tagCount(); i++) {
                NBTTagCompound tag = fluids.getCompoundTagAt(i);
                FluidStack stack = FluidStack.loadFluidStackFromNBT(tag);
                if (stack != null) {
                    IAEFluidStack aeStack = AEFluidStack.create(stack);
                    if (aeStack != null) {
                        aeStack.setStackSize(tag.getLong("count"));
                        fluidCache.add(aeStack);
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
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            return IconRegistry.getIcon("overlay_fluidoutput_me");
        }
        return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
    }
}
