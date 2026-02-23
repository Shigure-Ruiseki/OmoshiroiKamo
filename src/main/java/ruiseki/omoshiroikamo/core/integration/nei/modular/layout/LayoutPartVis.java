package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;

public class LayoutPartVis extends LayoutPartRenderer {

    public LayoutPartVis(INEIPositionedRenderer renderer) {
        super(renderer);
    }

    @Override
    public int getMaxHorizontalCount() {
        return 6;
    }

    @Override
    public int getSortOrder() {
        return 50;
    }
}
