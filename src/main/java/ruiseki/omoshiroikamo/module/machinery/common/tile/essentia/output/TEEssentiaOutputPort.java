package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.output;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

/**
 * Provides Essentia to Infusion Altar and Essentia Tubes.
 * Implements IAspectSource for Infusion compatibility and IEssentiaTransport
 * for Tubes.
 *
 * TODO: Create TEEssentiaOutputPortME subclass (export Essentia to ME network)
 */

public class TEEssentiaOutputPort extends AbstractEssentiaPortTE implements IEssentiaTransport, IAspectSource {

    private static final int DEFAULT_CAPACITY = 64;
    private static final int MINIMUM_SUCTION = 32;

    public TEEssentiaOutputPort() {
        super(DEFAULT_CAPACITY);
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.OUTPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (worldObj.isRemote) return false;
        if (!shouldDoWorkThisTick(10)) return false;
        if (getTotalEssentiaAmount() <= 0) return false;

        // Try to push essentia to adjacent blocks
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (getTotalEssentiaAmount() <= 0) break;

            int nx = xCoord + dir.offsetX;
            int ny = yCoord + dir.offsetY;
            int nz = zCoord + dir.offsetZ;
            TileEntity te = worldObj.getTileEntity(nx, ny, nz);
            if (te == null) continue;

            // Try IEssentiaTransport first (tubes, jars)
            if (te instanceof IEssentiaTransport) {
                IEssentiaTransport target = (IEssentiaTransport) te;
                ForgeDirection opposite = dir.getOpposite();

                if (!target.canInputFrom(opposite)) continue;

                // Active push - push any aspect we have
                for (Aspect aspect : aspects.getAspects()) {
                    if (aspect == null) continue;
                    int available = aspects.getAmount(aspect);
                    if (available <= 0) continue;

                    int pushed = target.addEssentia(aspect, 1, opposite);
                    if (pushed > 0) {
                        takeFromContainer(aspect, pushed);
                        return true; // Did work
                    }
                }
            }
            // Try IAspectContainer (jars, etc.)
            else if (te instanceof IAspectContainer) {
                IAspectContainer container = (IAspectContainer) te;

                for (Aspect aspect : aspects.getAspects()) {
                    if (aspect == null) continue;
                    int available = aspects.getAmount(aspect);
                    if (available <= 0) continue;

                    if (container.doesContainerAccept(aspect)) {
                        int leftover = container.addToContainer(aspect, 1);
                        if (leftover == 0) {
                            takeFromContainer(aspect, 1);
                            return true; // Did work
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return false;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return true;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        // Not needed for output
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        // Low suction - we're a provider, not consumer
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        // Provide essentia to tubes/devices
        int available = aspects.getAmount(aspect);
        int toTake = Math.min(amount, available);
        if (toTake > 0 && takeFromContainer(aspect, toTake)) {
            return toTake;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        // Output port doesn't accept essentia via this method
        return 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        Aspect[] aspectArray = aspects.getAspects();
        return (aspectArray != null && aspectArray.length > 0) ? aspectArray[0] : null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        return getTotalEssentiaAmount();
    }

    @Override
    public int getMinimumSuction() {
        // Require suction to draw from us
        return MINIMUM_SUCTION;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getOverlayIcon(ForgeDirection side) {
        if (getSideIO(side) == EnumIO.NONE) {
            return IconRegistry.getIcon("overlay_port_disabled");
        }
        return IconRegistry.getIcon("overlay_essentiaoutput_" + getTier());
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1) {
            return getOverlayIcon(side);
        }
        return AbstractPortBlock.baseIcon;
    }
}
