package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.fluid.FluidTransfer;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;

/**
 * Unified Fluid Input Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEFluidInputPortT1-T6).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEFluidInputPort extends AbstractFluidPortTE {

    @NBTPersist
    private int tier = 0;

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEFluidInputPort() {
        super(1000); // Default 1000mB capacity, will be updated when tier is set
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEFluidInputPort(int tier) {
        super(MachineryConfig.getFluidPortCapacity(tier + 1));
        this.tier = tier;
    }

    /**
     * Sets the tier and updates tank capacity accordingly.
     * Called after TE creation when placed from ItemStack.
     */
    public void setTier(int tier) {
        if (this.tier != tier) {
            this.tier = tier;
            int requiredCapacity = MachineryConfig.getFluidPortCapacity(tier + 1);
            if (tank.getCapacity() != requiredCapacity) {
                tank.setCapacity(requiredCapacity);
            }
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public String getLocalizedName() {
        // Use format string from lang file: tile.modularFluidInput.name=Fluid Input Port Tier %d
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (isRedstoneActive() && !tank.isFull()) {
            FluidTransfer transfer = new FluidTransfer();
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
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_fluidinput_" + (getTier() + 1));
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
    }
}
