package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;
import mekanism.api.lasers.ILaserReceptor;
import ruiseki.omoshiroikamo.api.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.api.energy.IOKEnergySink;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Energy Input Port TileEntity.
 * Accepts RF energy for machine processing.
 * Also supports Mekanism laser energy when Mekanism is present.
 */
@Optional.Interface(iface = "mekanism.api.lasers.ILaserReceptor", modid = "Mekanism")
public abstract class TEEnergyInputPort extends AbstractEnergyIOPortTE implements IOKEnergySink, ILaserReceptor {

    public TEEnergyInputPort(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public IO getIOLimit() {
        return IO.INPUT;
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
    public int receiveEnergy(ForgeDirection side, int amount, boolean simulate) {
        if (!isRedstoneActive() || !canInput(side)) {
            return 0;
        }
        return energyStorage.receiveEnergy(amount, simulate);
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public void receiveLaserEnergy(double amount, ForgeDirection side) {
        if (!isRedstoneActive() || !canInput(side)) {
            return;
        }
        this.receiveEnergy(side, (int) amount, false);
    }

    @Override
    @Optional.Method(modid = "Mekanism")
    public boolean canLasersDig() {
        return false;
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
        if (renderPass == 1) {
            if (getSideIO(side) != IO.NONE) {
                return IconRegistry.getIcon("overlay_energyinput_" + getTier());
            }
            return IconRegistry.getIcon("overlay_energyinput_disabled");
        }
        return AbstractPortBlock.baseIcon;
    }
}
