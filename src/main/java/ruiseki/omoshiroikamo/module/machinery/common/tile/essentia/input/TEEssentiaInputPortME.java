package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.input;

import java.util.EnumSet;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import appeng.api.config.Actionable;
import appeng.api.networking.GridFlags;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.grid.IEssentiaGrid;
import thaumicenergistics.api.storage.IAspectStack;

/**
 * ME Essentia Input Port - pulls Essentia directly from ME Network.
 * Requires Thaumic Energistics.
 */
public class TEEssentiaInputPortME extends TEEssentiaInputPort implements IGridProxyable, IActionHost {

    private AENetworkProxy gridProxy;
    private boolean proxyInitialized = false;

    public TEEssentiaInputPortME() {
        super();
    }

    @Override
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            gridProxy = new AENetworkProxy(this, "proxy", getVisualItemStack(), true);
            gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
            gridProxy.setValidSides(EnumSet.allOf(ForgeDirection.class));
        }
        return gridProxy;
    }

    private ItemStack getVisualItemStack() {
        // Return a visual representation - use bedrock as placeholder
        return new ItemStack(Blocks.bedrock);
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public void gridChanged() {
        // Handle grid changes if needed
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public void securityBreak() {
        worldObj.func_147480_a(xCoord, yCoord, zCoord, true);
    }

    @Override
    public IGridNode getGridNode(ForgeDirection dir) {
        return getProxy().getNode();
    }

    @Override
    public IGridNode getActionableNode() {
        return getProxy().getNode();
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (!worldObj.isRemote && shouldDoWorkThisTick(20)) {
            extractEssentiaFromME();
        }
        return super.processTasks(redstoneCheckPassed);
    }

    private void extractEssentiaFromME() {
        try {
            if (gridProxy == null || !gridProxy.isReady()) {
                return;
            }

            IEssentiaGrid essentiaGrid = gridProxy.getGrid()
                .getCache(IEssentiaGrid.class);
            if (essentiaGrid == null) {
                return;
            }

            // Try to extract essentia from ME network
            for (IAspectStack stack : essentiaGrid.getEssentiaList()) {
                if (stack != null && stack.getAspect() != null) {
                    Aspect aspect = stack.getAspect();
                    long available = stack.getStackSize();

                    if (available > 0) {
                        int space = maxCapacityPerAspect - aspects.getAmount(aspect);
                        int toExtract = (int) Math.min(available, Math.min(space, 8));

                        if (toExtract > 0) {
                            long extracted = essentiaGrid
                                .extractEssentia(aspect, toExtract, Actionable.MODULATE, new MachineSource(this), true);
                            if (extracted > 0) {
                                addToContainer(aspect, (int) extracted);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Grid not ready or ThE not installed
        }
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        if (gridProxy != null) {
            gridProxy.writeToNBT(root);
        }
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        getProxy().readFromNBT(root);
    }

    @Override
    public void validate() {
        super.validate();
        if (!proxyInitialized) {
            getProxy().onReady();
            proxyInitialized = true;
        }
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
