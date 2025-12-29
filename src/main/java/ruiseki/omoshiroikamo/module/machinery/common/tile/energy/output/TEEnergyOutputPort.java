package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.IEnergySource;
import ruiseki.omoshiroikamo.api.modular.IOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
public abstract class TEEnergyOutputPort extends AbstractEnergyIOPortTE implements IEnergySource, IOutputPort {

    public TEEnergyOutputPort(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public IO getIOLimit() {
        return IO.OUTPUT;
    }

    @Override
    public double getOfferedEnergy() {
        if (!isUseIC2Compat()) {
            return 0;
        }
        return IEnergySource.super.getOfferedEnergy();
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive()) {
            EnergyTransfer transfer = new EnergyTransfer();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canOutput()) {
                    continue;
                }
                TileEntity sink = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.push(this, direction, sink);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public int extractEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (!isRedstoneActive() || !canOutput(side)) {
            return 0;
        }
        return energyStorage.extractEnergy(amount, simulate);
    }
}
