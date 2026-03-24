package ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output;

import net.minecraft.nbt.NBTTagCompound;
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
 * Unified Fluid Output Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEFluidOutputPortT1-T6).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEFluidOutputPort extends AbstractFluidPortTE {

    @NBTPersist
    private int tier = 0;

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEFluidOutputPort() {
        super(1000); // Default 1000mB capacity, will be updated when tier is set
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEFluidOutputPort(int tier) {
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
        return EnumIO.OUTPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        if (isRedstoneActive()) {
            FluidTransfer transfer = new FluidTransfer();
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
        return super.processTasks(redstoneCheckPassed);
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
        }
        if (renderPass == 1) {
            if (getSideIO(side) != EnumIO.NONE) {
                return IconRegistry.getIcon("overlay_fluidoutput_" + (getTier() + 1));
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        if (root.hasKey("tier")) {
            this.tier = root.getInteger("tier");
            int requiredCapacity = MachineryConfig.getFluidPortCapacity(tier + 1);
            if (tank.getCapacity() != requiredCapacity) {
                tank.setCapacity(requiredCapacity);
            }
        }
        super.readCommon(root);
    }
}
