package ruiseki.omoshiroikamo.module.cable.client.gui.widget;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CableSearchBar extends TextFieldWidget {

    static protected String prevText = "";

    public static final UITexture VANILLA_SEARCH_BACKGROUND = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/vanilla_search")
        .imageSize(18, 18)
        .adaptable(1)
        .name("vanilla_search")
        .build();

    public CableSearchBar() {
        super();
        background(VANILLA_SEARCH_BACKGROUND);
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
        doSearch(prevText);
    }

    public void research() {
        doSearch(prevText);
    }

    public void doSearch(String search) {

    }

}
