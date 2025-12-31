package ruiseki.omoshiroikamo.module.machinery.common.tile.vis.output;

import cpw.mods.fml.common.Optional;
import ruiseki.omoshiroikamo.api.modular.IPortType;
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
    public boolean processTasks(boolean redstoneCheckPassed) {
        // Output port is passive - vis is consumed via consumeVis method
        return false;
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public void validate() {
        super.validate();
        registerAsVisSource();
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public void invalidate() {
        unregisterAsVisSource();
        super.invalidate();
    }

    @Override
    @Optional.Method(modid = "Thaumcraft")
    public void onChunkUnload() {
        unregisterAsVisSource();
        super.onChunkUnload();
    }

    /**
     * Called by VisNetHandler when vis is requested.
     * This method signature matches TileVisNode.consumeVis.
     */
    @Optional.Method(modid = "Thaumcraft")
    public int consumeVis(Aspect aspect, int amount) {
        return drainVis(aspect, amount);
    }

    /**
     * Check if this is a vis source (required for VisNetHandler).
     */
    @Optional.Method(modid = "Thaumcraft")
    public boolean isSource() {
        return getTotalVis() > 0;
    }

    /**
     * Range for vis network (0 = connected directly).
     */
    @Optional.Method(modid = "Thaumcraft")
    public int getRange() {
        return 0;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.OUTPUT;
    }
}
