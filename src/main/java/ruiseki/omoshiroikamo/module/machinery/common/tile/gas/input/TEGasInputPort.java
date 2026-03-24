package ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;

/**
 * Unified Gas Input Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEGasInputPortT1-T6).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEGasInputPort extends AbstractGasPortTE {

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEGasInputPort() {
        super();
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEGasInputPort(int tier) {
        super();
        setTier(tier);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.INPUT;
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
                return IconRegistry.getIcon("overlay_gasinput_" + (getTier() + 1));
            }
            return null;
        }
        return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
    }
}
