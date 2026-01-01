package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 * Also supports Mekanism laser energy when Mekanism is present.
 */
@Optional.Interface(iface = "mekanism.api.lasers.ILaserReceptor", modid = "Mekanism")
public abstract class TEEnergyInputPort extends AbstractEnergyIOPortTE implements IOKEnergySink {

    public TEEnergyInputPort(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
    }

    @Override
    public boolean canInput(ForgeDirection side) {
        IO io = getSideIO(side);
        return io != IO.OUTPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneChecksPassed) {
        if (isRedstoneActive()) {
            EnergyTransfer transfer = new EnergyTransfer();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                if (!getSideIO(direction).canInput()) {
                    continue;
                }
                TileEntity source = getPos().offset(direction)
                    .getTileEntity(worldObj);
                transfer.pull(this, direction, source);
                transfer.transfer();
            }
        }

        return super.processTasks(redstoneChecksPassed);
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.INPUT;
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (!isRedstoneActive() || !canInput(side)) {
            return 0;
        }
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Optional.Method(modid = "Mekanism")
    public void receiveLaserEnergy(double amount, ForgeDirection from) {
        this.receiveEnergy(from, (int) amount, false);
    }

    @Optional.Method(modid = "Mekanism")
    public boolean canLasersDig() {
        return false;
    }
}
