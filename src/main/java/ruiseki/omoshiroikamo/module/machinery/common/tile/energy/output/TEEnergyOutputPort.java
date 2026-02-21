package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Energy Output Port TileEntity.
 * RF energy output for machine processing.
 */
public abstract class TEEnergyOutputPort extends AbstractEnergyIOPortTE implements IOKEnergySource {

    public TEEnergyOutputPort(int energyCapacity, int energyMaxReceive) {
        super(energyCapacity, energyMaxReceive);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.OUTPUT;
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

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_energyoutput_" + getTier());
            }
            return null;
        }
        return AbstractPortBlock.baseIcon;
    }

    @Override
    public boolean canOutput(ForgeDirection side) {
        return getSideIO(side).canOutput();
    }
}
