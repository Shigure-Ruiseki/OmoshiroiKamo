package ruiseki.omoshiroikamo.core.integration.nei.modular.layout;

import ruiseki.omoshiroikamo.core.integration.nei.modular.renderer.INEIPositionedRenderer;

public class LayoutPartMana extends LayoutPartRenderer {

    public LayoutPartMana(INEIPositionedRenderer renderer) {
        super(renderer);
    }

    @Override
    public int getMaxHorizontalCount() {
        return 1;
    }

    @Override
    public int getSortOrder() {
        return 900;
    }
}
