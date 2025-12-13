package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import static ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel.TILE_TAB_TEXTURE;

import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.TextWidget;

import ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel;

public class TileWidget extends TextWidget<TileWidget> {

    private final BackpackPanel panel;

    public TileWidget(BackpackPanel panel) {
        super(
            panel.getHandler()
                .getDisplayName());
        this.panel = panel;
        this.padding(5, 5, 3, 1);
        pos(4, -12);
        maxWidth(panel.getWidth());
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        super.drawBackground(context, widgetTheme);
        TILE_TAB_TEXTURE.draw(0, 0, this.getArea().width, this.getArea().height);
    }
}
