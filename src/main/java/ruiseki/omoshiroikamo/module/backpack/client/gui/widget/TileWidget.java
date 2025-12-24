package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import com.cleanroommc.modularui.drawable.AdaptableUITexture;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.widgets.TextWidget;

import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class TileWidget extends TextWidget<TileWidget> {

    public static final AdaptableUITexture TILE_TAB_TEXTURE = (AdaptableUITexture) UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/gui_controls")
        .imageSize(256, 256)
        .xy(128, 0, 128, 10)
        .adaptable(4)
        .tiled()
        .build();

    public TileWidget(BackpackPanel panel) {
        super(
            panel.getHandler()
                .getDisplayName());
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
