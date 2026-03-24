package ruiseki.omoshiroikamo.module.machinery.common.tile.mana.output;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

/**
 * Unified Mana Output Port TileEntity for all tiers (0-15).
 * Uses tier field instead of separate classes for each tier.
 *
 * This replaces the legacy per-tier TE classes (TEManaOutputPortT1).
 * Old TE classes are automatically remapped to this class on world load.
 */
public class TEManaOutputPort extends AbstractManaPortTE {

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEManaOutputPort() {
        super();
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEManaOutputPort(int tier) {
        super();
        setTier(tier);
    }

    @Override
    public boolean canRecieveManaFromBursts() {
        return false;
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
            return IconRegistry.getIcon("overlay_manaoutput_" + (getTier() + 1));
        }
        return ((AbstractPortBlock<?>) getBlockType()).getBaseIcon(getTier());
    }
}
