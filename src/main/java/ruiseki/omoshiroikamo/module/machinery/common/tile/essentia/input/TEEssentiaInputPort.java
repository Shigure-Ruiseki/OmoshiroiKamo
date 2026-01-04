package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.input;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

/**
 * Essentia Input Port - absorbs Essentia from nearby containers using suction.
 * Implements IEssentiaTransport to connect with Essentia Tubes.
 */
public class TEEssentiaInputPort extends AbstractEssentiaPortTE implements IEssentiaTransport {

    private static final int DEFAULT_CAPACITY = 64;
    private static final int SUCTION_AMOUNT = 128;

    public TEEssentiaInputPort() {
        super(DEFAULT_CAPACITY);
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
    }

    @Override
    public boolean isConnectable(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canInputFrom(ForgeDirection face) {
        return true;
    }

    @Override
    public boolean canOutputTo(ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
        // Handled internally
    }

    @Override
    public Aspect getSuctionType(ForgeDirection face) {
        // null = accept any aspect
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection face) {
        // High suction to pull from nearby containers
        return SUCTION_AMOUNT;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
        // Input port doesn't output via this method
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
        // Accept essentia into our storage
        int leftover = addToContainer(aspect, amount);
        return amount - leftover;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection face) {
        // Return the first aspect we have
        Aspect[] aspects = this.aspects.getAspects();
        return (aspects != null && aspects.length > 0) ? aspects[0] : null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection face) {
        return getTotalEssentiaAmount();
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (!worldObj.isRemote && shouldDoWorkThisTick(10)) {
            drawEssentiaFromNeighbors();
        }
        return false;
    }

    // Actively draw essentia from adjacent IEssentiaTransport blocks.
    private void drawEssentiaFromNeighbors() {
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            TileEntity te = ThaumcraftApiHelper.getConnectableTile(worldObj, xCoord, yCoord, zCoord, dir);
            if (!(te instanceof IEssentiaTransport)) {
                continue;
            }
            IEssentiaTransport source = (IEssentiaTransport) te;
            ForgeDirection opposite = dir.getOpposite();
            if (!(source.canOutputTo(opposite))) {
                continue;
            }
            Aspect aspect = source.getEssentiaType(opposite);
            if (aspect != null && source.getEssentiaAmount(opposite) <= 0) {
                continue;
            }
            int suction = source.getSuctionAmount(opposite);
            if (suction >= SUCTION_AMOUNT) {
                continue;
            }
            int taken = source.takeEssentia(aspect, 1, opposite);
            if (taken > 0) {
                addToContainer(aspect, taken);
            }
        }
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1 && getSideIO(side) != IO.NONE) {
            return IconRegistry.getIcon("overlay_essentiainput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
