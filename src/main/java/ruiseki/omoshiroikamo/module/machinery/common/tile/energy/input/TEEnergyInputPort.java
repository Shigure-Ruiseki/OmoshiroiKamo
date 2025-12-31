package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;
import mekanism.api.lasers.ILaserReceptor;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.IEnergySink;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 */
@Optional.InterfaceList({ @Optional.Interface(iface = "mekanism.api.lasers.ILaserReceptor", modid = "Mekanism"), })
public abstract class TEEnergyInputPort extends AbstractEnergyIOPortTE implements IEnergySink, ILaserReceptor {

    public TEEnergyInputPort(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
    }

    @Override
    public double getDemandedEnergy() {
        if (!isUseIC2Compat()) {
            return 0;
        }
        return IEnergySink.super.getDemandedEnergy();
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

    // Always allow input regardless of side IO setting
    @Override
    public boolean canInput(ForgeDirection side) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (!isRedstoneActive() || !canInput(side)) {
            return 0;
        }
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    public void receiveLaserEnergy(double amount, ForgeDirection from) {
        this.receiveEnergy(from, (int) amount, false);
    }

    @Override
    public boolean canLasersDig() {
        return false;
    }
}
