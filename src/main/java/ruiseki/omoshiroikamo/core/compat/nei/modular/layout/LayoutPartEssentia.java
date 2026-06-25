package ruiseki.omoshiroikamo.core.compat.nei.modular.layout;

import ruiseki.omoshiroikamo.core.compat.nei.modular.renderer.INEIPositionedRenderer;

public class LayoutPartEssentia extends LayoutPartRenderer {

    public LayoutPartEssentia(INEIPositionedRenderer renderer) {
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
