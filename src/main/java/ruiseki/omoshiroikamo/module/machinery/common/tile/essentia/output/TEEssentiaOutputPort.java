package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.output;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.api.aspects.IEssentiaTransport;

/**
 * Essentia Output Port - provides Essentia to Infusion Altar and Essentia
 * Tubes.
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
    public IO getIOLimit() {
        return IO.OUTPUT;
    }

    // ========== Processing ==========

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        // Output port is passive - essentia is pulled by other devices
        return false;
    }

    // ========== IEssentiaTransport Implementation ==========

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

    // ========== IModularPort ==========

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.OUTPUT;
    }
}
