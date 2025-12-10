package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.slot.BackpackSlot;
import ruiseki.omoshiroikamo.common.block.backpack.BackpackPanel;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class SearchBarWidget extends TextFieldWidget {

    private final Column backpackSlots;
    private final BackpackPanel panel;

    static protected String prevText = "";

    public static final UITexture VANILLA_SEARCH_BACKGROUND = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/vanilla_search")
        .imageSize(18, 18)
        .adaptable(1)
        .name("vanilla_search")
        .build();

    public SearchBarWidget(BackpackPanel panel, Column backpackSlots) {
        super();
        this.backpackSlots = backpackSlots;
        this.panel = panel;
        background(VANILLA_SEARCH_BACKGROUND);
        hintText(
            IKey.lang("gui.search_hint")
                .get());
        value(new StringValue(prevText));
    }

    @Override
    public void drawBackground(ModularGuiContext context, WidgetThemeEntry<?> widgetTheme) {
        IDrawable bg = getCurrentBackground(context.getTheme(), widgetTheme);
        if (bg != null) {
            bg.draw(context, 2, -1, getArea().width - 4, getArea().height + 1, widgetTheme.getTheme());
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        String txt = getText();

        if (!txt.equals(prevText)) {
            doSearch(txt);
            prevText = txt;
        }
    }

    @Override
    public void onInit() {
        super.onInit();
        if (panel.getHandler()
            .isSearchBackpack()) {
            prevText = "";
            value(new StringValue(prevText));
        } else {
            doSearch(prevText);
        }
    }

    public void doSearch(String search) {
        if (prevText.isEmpty()) {
            return;
        }

        for (IWidget backpackRow : backpackSlots.getChildren()) {
            int foundCount = 0;
            for (int i = 0; i < backpackRow.getChildren()
                .size(); i++) {
                BackpackSlot slot = (BackpackSlot) backpackRow.getChildren()
                    .get(i);

                slot.setEnabled(slot.matches(search));
                if (slot.isEnabled()) {
                    ((BackpackRow) backpackRow).moveChild(i, foundCount);
                    foundCount++;
                }
                slot.scheduleResize();
            }
            backpackRow.scheduleResize();
        }
        ((BackpackList) backpackSlots.getParent()).getScrollData()
            .scrollTo(((BackpackList) backpackSlots.getParent()).getScrollArea(), 0);
    }

}
