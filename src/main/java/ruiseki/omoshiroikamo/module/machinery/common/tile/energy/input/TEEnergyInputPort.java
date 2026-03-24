package ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.Optional;
import mekanism.api.lasers.ILaserReceptor;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.energy.EnergyTransfer;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;

/**
 * Unified Energy Input Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * Accepts RF energy for machine processing.
 * Also supports Mekanism laser energy when Mekanism is present.
 *
 * This replaces the legacy per-tier TE classes (TEEnergyInputPortT1-T6).
 * Old TE classes are automatically remapped to this class on world load.
 */
@Optional.Interface(iface = "mekanism.api.lasers.ILaserReceptor", modid = "Mekanism")
public class TEEnergyInputPort extends AbstractEnergyIOPortTE implements ILaserReceptor {

    @NBTPersist
    private int tier = 0;

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEEnergyInputPort() {
        super(2048, 128); // Default capacity and transfer rate, will be updated when tier is set
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEEnergyInputPort(int tier) {
        super(MachineryConfig.getEnergyPortCapacity(tier + 1), MachineryConfig.getEnergyPortTransfer(tier + 1));
        this.tier = tier;
    }

    /**
     * Sets the tier and updates energy storage accordingly.
     * Called after TE creation when placed from ItemStack.
     */
    public void setTier(int tier) {
        if (this.tier != tier) {
            this.tier = tier;
            int requiredCapacity = MachineryConfig.getEnergyPortCapacity(tier + 1);
            int requiredTransfer = MachineryConfig.getEnergyPortTransfer(tier + 1);
            if (energyStorage.getMaxEnergyStored() != requiredCapacity) {
                energyStorage.setCapacity(requiredCapacity);
                energyStorage.setMaxReceive(requiredTransfer);
            }
            markDirty();
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getLocalizedName() {
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
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
            return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_energyinput_" + (getTier() + 1));
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        if (root.hasKey("tier")) {
            this.tier = root.getInteger("tier");
            int requiredCapacity = MachineryConfig.getEnergyPortCapacity(tier + 1);
            int requiredTransfer = MachineryConfig.getEnergyPortTransfer(tier + 1);
            if (energyStorage.getMaxEnergyStored() != requiredCapacity) {
                energyStorage.setCapacity(requiredCapacity);
                energyStorage.setMaxReceive(requiredTransfer);
            }
        }
        super.readCommon(root);
    }
}
