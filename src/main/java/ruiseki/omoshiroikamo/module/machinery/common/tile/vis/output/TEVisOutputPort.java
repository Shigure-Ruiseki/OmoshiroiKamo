package ruiseki.omoshiroikamo.module.machinery.common.tile.vis.output;

import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

/**
 * Vis Output Port - provides Vis to Vis Relay network as a source.
 * Registers with VisNetHandler.sources.
 */
public class TEVisOutputPort extends AbstractVisPortTE {

    private static final int DEFAULT_VIS_CAPACITY = 100;

    public TEVisOutputPort() {
        super(DEFAULT_VIS_CAPACITY);
    }

    @Override
    public int getTier() {
        return 1;
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
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.OUTPUT;
    }

    @Override
    public IIcon getTexture(ForgeDirection side, int renderPass) {
        if (renderPass == 0) {
            return AbstractPortBlock.baseIcon;
        }
        if (renderPass == 1 && getSideIO(side) != EnumIO.NONE) {
            return IconRegistry.getIcon("overlay_visoutput_" + getTier());
        }
        return AbstractPortBlock.baseIcon;
    }
}
