package ruiseki.omoshiroikamo.core.energy.capability.enderio;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import crazypants.enderio.power.IInternalPowerProvider;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.IPowerStorage;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * EnderIO integration handler using direct API access.
 * This class is only loaded when EnderIO is present at runtime.
 *
 * <p>
 * <b>IMPORTANT:</b> This class must ONLY be accessed after checking
 * {@code LibMods.EnderIO.isLoaded()} to ensure safe lazy loading.
 *
 * <p>
 * EnderIO CapBank architecture:
 * <ul>
 * <li>Individual blocks implement {@code IPowerStorage} and {@code IInternalPowerReceiver}</li>
 * <li>Multi-block structure has a {@code CapBankNetwork} controller (accessed via {@code getController()})</li>
 * <li>Controller uses {@code long} type for energy (getEnergyStoredL, getMaxEnergyStoredL)</li>
 * <li>Energy is manipulated via {@code addEnergy(int)} - negative values subtract energy</li>
 * </ul>
 *
 * <p>
 * <b>Supported EnderIO Blocks:</b>
 * <ul>
 * <li><b>Capacitor Bank</b> - Multi-block energy storage (implements IPowerStorage)</li>
 * <li>Any device implementing {@code IInternalPowerReceiver} - Energy receivers</li>
 * <li>Any device implementing {@code IInternalPowerProvider} - Energy providers</li>
 * </ul>
 */
public class EnderIOIntegration {

    /**
     * Attempt to extract energy from an EnderIO CapBank or compatible storage device.
     *
     * @param te       The TileEntity (must implement IPowerStorage)
     * @param side     The side to extract from (UNKNOWN for auto-detect)
     * @param amount   Amount of energy to extract
     * @param simulate If true, only simulate the extraction
     * @return Amount of energy extracted, or null if not a compatible device
     */
    public static Integer tryExtract(Object te, ForgeDirection side, int amount, boolean simulate) {
        if (te == null) return null;

        try {
            // Try standard IInternalPowerProvider first (non-storage providers)
            if (te instanceof IInternalPowerProvider) {
                return tryExtractFromProvider((IInternalPowerProvider) te, side, amount, simulate);
            }

            // Try IPowerStorage (CapBank and other storage devices)
            if (te instanceof IPowerStorage) {
                return tryExtractFromStorage((IPowerStorage) te, side, amount, simulate);
            }

            return null;

        } catch (Exception e) {
            Logger.error("Failed to extract energy from EnderIO device: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Attempt to receive energy into an EnderIO CapBank or compatible receiver.
     *
     * @param te       The TileEntity (must implement IInternalPowerReceiver)
     * @param side     The side to receive from (UNKNOWN for auto-detect)
     * @param amount   Amount of energy to receive
     * @param simulate If true, only simulate the reception
     * @return Amount of energy received, or null if not a receiver
     */
    public static Integer tryReceive(Object te, ForgeDirection side, int amount, boolean simulate) {
        if (te == null) return null;

        try {
            if (!(te instanceof IInternalPowerReceiver)) return null;

            IInternalPowerReceiver receiver = (IInternalPowerReceiver) te;

            // Find appropriate side
            ForgeDirection targetSide = side;
            if (side == ForgeDirection.UNKNOWN) {
                targetSide = findReceiveSide(receiver);
                if (targetSide == ForgeDirection.UNKNOWN) return null;
            }

            return receiver.receiveEnergy(targetSide, amount, simulate);

        } catch (Exception e) {
            Logger.error("Failed to receive energy into EnderIO device: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get stored energy from an EnderIO CapBank.
     *
     * @param te The TileEntity (must implement IPowerStorage)
     * @return Stored energy amount, or null if not a CapBank
     */
    public static Integer getEnergyStored(Object te) {
        if (te == null) return null;

        try {
            if (!(te instanceof IPowerStorage)) return null;

            IPowerStorage storage = (IPowerStorage) te;
            IPowerStorage controller = storage.getController();
            if (controller == null) return null;

            long storedL = controller.getEnergyStoredL();

            // Convert to int (capped at Integer.MAX_VALUE)
            return (int) Math.min(storedL, Integer.MAX_VALUE);

        } catch (Exception e) {
            Logger.error("Failed to get stored energy from EnderIO device: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get maximum energy capacity of an EnderIO CapBank.
     *
     * @param te The TileEntity (must implement IPowerStorage)
     * @return Maximum energy capacity, or null if not a CapBank
     */
    public static Integer getMaxEnergyStored(Object te) {
        if (te == null) return null;

        try {
            if (!(te instanceof IPowerStorage)) return null;

            IPowerStorage storage = (IPowerStorage) te;
            IPowerStorage controller = storage.getController();
            if (controller == null) return null;

            long maxL = controller.getMaxEnergyStoredL();

            // Convert to int (capped at Integer.MAX_VALUE)
            return (int) Math.min(maxL, Integer.MAX_VALUE);

        } catch (Exception e) {
            Logger.error("Failed to get max energy from EnderIO device: " + e.getMessage(), e);
            return null;
        }
    }

    // ========== Private Helper Methods ==========

    /**
     * Try to extract from standard IInternalPowerProvider (non-storage devices).
     */
    private static Integer tryExtractFromProvider(IInternalPowerProvider provider, ForgeDirection side, int amount,
        boolean simulate) {
        ForgeDirection targetSide = side;
        if (side == ForgeDirection.UNKNOWN) {
            targetSide = findExtractSide(provider);
            if (targetSide == ForgeDirection.UNKNOWN) return null;
        }

        return provider.extractEnergy(targetSide, amount, simulate);
    }

    /**
     * Try to extract from IPowerStorage (CapBank and storage devices).
     */
    private static Integer tryExtractFromStorage(IPowerStorage storage, ForgeDirection side, int amount,
        boolean simulate) {
        // Get controller (CapBankNetwork for multi-block)
        IPowerStorage controller = storage.getController();
        if (controller == null) return null;

        // Find valid output side
        ForgeDirection targetSide = findOutputSide(controller, side);
        if (targetSide == null) return null;

        // Get current stored energy (long type)
        long storedL = controller.getEnergyStoredL();
        if (storedL <= 0) return 0;

        // Get max output rate
        int maxOutput = controller.getMaxOutput();

        // Calculate extraction amount
        long maxExtractL = Math.min(amount, Math.min(storedL, maxOutput));
        int toExtract = (int) Math.min(maxExtractL, Integer.MAX_VALUE);

        // Perform extraction via addEnergy with negative value
        if (!simulate && toExtract > 0) {
            controller.addEnergy(-toExtract);

            // Mark dirty
            if (storage instanceof TileEntity) {
                ((TileEntity) storage).markDirty();
            }
        }

        return toExtract;
    }

    /**
     * Find a side that allows energy output from IPowerStorage.
     */
    private static ForgeDirection findOutputSide(IPowerStorage controller, ForgeDirection requestedSide) {
        // Check requested side first
        if (requestedSide != ForgeDirection.UNKNOWN) {
            if (controller.isOutputEnabled(requestedSide)) {
                return requestedSide;
            }
        }

        // Try UNKNOWN (omni-directional)
        if (controller.isOutputEnabled(ForgeDirection.UNKNOWN)) {
            return ForgeDirection.UNKNOWN;
        }

        // Probe all 6 sides
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (controller.isOutputEnabled(dir)) {
                return dir;
            }
        }

        return null;
    }

    /**
     * Find a side that can receive energy.
     */
    private static ForgeDirection findReceiveSide(IInternalPowerReceiver receiver) {
        // Try UNKNOWN first
        if (receiver.canConnectEnergy(ForgeDirection.UNKNOWN)) {
            int test = receiver.receiveEnergy(ForgeDirection.UNKNOWN, 1, true);
            if (test > 0) return ForgeDirection.UNKNOWN;
        }

        // Probe all 6 sides
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (receiver.canConnectEnergy(dir)) {
                int test = receiver.receiveEnergy(dir, 1, true);
                if (test > 0) return dir;
            }
        }

        return ForgeDirection.UNKNOWN;
    }

    /**
     * Find a side that can extract energy.
     */
    private static ForgeDirection findExtractSide(IInternalPowerProvider provider) {
        // Try UNKNOWN first
        if (provider.canConnectEnergy(ForgeDirection.UNKNOWN)) {
            int test = provider.extractEnergy(ForgeDirection.UNKNOWN, 1, true);
            if (test > 0) return ForgeDirection.UNKNOWN;
        }

        // Probe all 6 sides
        for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            if (provider.canConnectEnergy(dir)) {
                int test = provider.extractEnergy(dir, 1, true);
                if (test > 0) return dir;
            }
        }

        return ForgeDirection.UNKNOWN;
    }
}
