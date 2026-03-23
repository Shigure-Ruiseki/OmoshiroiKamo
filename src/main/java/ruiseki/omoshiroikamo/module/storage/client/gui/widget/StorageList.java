package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.widget.scroll.ScrollArea;
import com.cleanroommc.modularui.widget.scroll.ScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;

public class StorageList extends ListWidget<Column, StorageList> {

    private final StoragePanel panel;
    private final int thickness = 4;

    public StorageList(StoragePanel panel) {
        this.panel = panel;
        width(panel.rowSize * ItemSlot.SIZE + thickness * 2);
    }

    @Override
    public void onInit() {
        if (this.getScrollData() == null) {
            scrollDirection(new StorageScrollData(panel, thickness));
        }
    }

    public static class StorageScrollData extends VerticalScrollData {

        private final StoragePanel panel;

        public StorageScrollData(StoragePanel panel, int thickness) {
            super(false, thickness);
            this.panel = panel;
        }

        @Override
        public boolean isInsideScrollbarArea(ScrollArea area, int mouseX, int mouseY) {
            if (!isScrollBarActive(area)) {
                return false;
            }

            int x = panel.rowSize * ItemSlot.SIZE + getThickness();
            int y = 0;

            int w = getThickness();
            int h = area.height;

            return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
        }

        @Override
        public void drawScrollbar(ScrollArea area, ModularGuiContext context, WidgetTheme widgetTheme,
            IDrawable texture) {
            boolean isOtherActive = isOtherScrollBarActive(area, true);
            int l = this.getScrollBarLength(area);
            int x = panel.rowSize * ItemSlot.SIZE + getThickness();
            int y = 0;
            int w = getThickness();
            int h = area.height;
            GuiDraw.drawRect(x, y, w, h, area.getScrollBarBackgroundColor());

            y = getScrollBarStart(area, l, isOtherActive);
            ScrollData data2 = getOtherScrollData(area);
            if (data2 != null && isOtherActive && data2.isOnAxisStart()) {
                y += data2.getThickness();
            }
            h = l;
            drawScrollBar(context, x, y, w, h, widgetTheme, texture);
        }
    }
}
