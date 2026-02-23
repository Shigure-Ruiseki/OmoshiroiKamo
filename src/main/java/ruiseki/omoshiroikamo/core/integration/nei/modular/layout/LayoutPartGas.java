package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;

public class LayoutPartGas extends LayoutPartRenderer {

    public LayoutPartGas(INEIPositionedRenderer renderer) {
        super(renderer);
    }

    @Override
    public int getMaxHorizontalCount() {
        return 2;
    }

    @Override
    public int getSortOrder() {
        return 100;
    }
}
