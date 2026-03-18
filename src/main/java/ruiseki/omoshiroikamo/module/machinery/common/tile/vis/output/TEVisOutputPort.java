package ruiseki.omoshiroikamo.module.machinery.common.tile.vis.output;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType.Direction;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

/**
 * Unified Vis Output Port TileEntity for all tiers (0-15).
 * Provides Vis to Vis Relay network as a source.
 * Uses tier field instead of separate classes for each tier.
 */
public class TEVisOutputPort extends AbstractVisPortTE {

    /**
     * No-arg constructor required for TE instantiation.
     * Tier will be set after construction via setTier().
     */
    public TEVisOutputPort() {
        super();
    }

    /**
     * Constructor with tier parameter.
     *
     * @param tier Tier level (0-15)
     */
    public TEVisOutputPort(int tier) {
        super();
        setTier(tier);
    }

    @Override
    public EnumIO getIOLimit() {
        return EnumIO.OUTPUT;
    }

    @Override
    public boolean processTasks(boolean redstoneCheckPassed) {
        // Output port is passive - vis is consumed via consumeVis method
        return false;
    }

    @Override
    public void validate() {
        super.validate();
        registerAsVisSource();
    }

    @Override
    public void invalidate() {
        unregisterAsVisSource();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        unregisterAsVisSource();
        super.onChunkUnload();
    }

    /**
     * Called by VisNetHandler when vis is requested.
     * This method signature matches TileVisNode.consumeVis.
     */
    public int consumeVis(Aspect aspect, int amount) {
        return drainVis(aspect, amount);
    }

    /**
     * Check if this is a vis source (required for VisNetHandler).
     */
    public boolean isSource() {
        return getTotalVis() > 0;
    }

    /**
     * Range for vis network (0 = connected directly).
     */
    public int getRange() {
        return 0;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
        }
        if (renderPass == 1) {
            if (getSideIO(side) == EnumIO.NONE) {
                return null;
            }
            return IconRegistry.getIcon("overlay_visoutput_" + (getTier() + 1));
        }
        return ((AbstractPortBlock<?>) getBlockType()).baseIcon;
    }
}
