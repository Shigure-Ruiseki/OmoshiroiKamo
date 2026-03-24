package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

/**
 * Unified Mana Input Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEManaInputPortT1).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEManaInputPort extends AbstractManaPortTE {

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEManaInputPort() {
        super();
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEManaInputPort(int tier) {
        super();
        setTier(tier);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return true;
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
            return IconRegistry.getIcon("overlay_manainput_" + (getTier() + 1));
        }
        return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
    }
}
