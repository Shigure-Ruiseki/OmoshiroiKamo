package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.theme.WidgetThemeEntry;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.BackpackSlot;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class SearchBarWidget extends TextFieldWidget {

    private final BackpackPanel panel;
    private List<BackpackSlot> originalOrder;

    static protected String prevText = "";

    public static final UITexture VANILLA_SEARCH_BACKGROUND = UITexture.builder()
        .location(LibMisc.MOD_ID, "gui/vanilla_search")
        .imageSize(18, 18)
        .adaptable(1)
        .name("vanilla_search")
        .build();

    public SearchBarWidget(BackpackPanel panel) {
        super();
        this.panel = panel;
        background(VANILLA_SEARCH_BACKGROUND);
        hintText(
            IKey.lang("gui.backpack.search_hint")
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

    private void cacheOriginalOrder() {
        Column backpackSlots = panel.getBackpackInvCol();
        if (backpackSlots == null) return;

        originalOrder = new ArrayList<>();
        for (IWidget child : panel.getBackpackInvCol()
            .getChildren()) {
            if (child instanceof BackpackSlot slot) {
                originalOrder.add(slot);
            }
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
        cacheOriginalOrder();
        if (panel.getHandler()
            .isSearchBackpack()) {
            prevText = "";
            value(new StringValue(prevText));
        } else {
            doSearch(prevText);
        }
    }

    public void research() {
        doSearch(prevText);
    }

    public void doSearch(String search) {
        Column backpackSlots = panel.getBackpackInvCol();
        if (backpackSlots == null) return;

        IWidget parent = backpackSlots.getParent();
        if (!(parent instanceof BackpackList backpackList)) return;

        int columns = panel.getRowSize();
        int slotSize = BackpackSlot.SIZE;

        if (search.isEmpty()) {
            for (int i = 0; i < originalOrder.size(); i++) {
                BackpackSlot slot = originalOrder.get(i);
                slot.setFocus(true);

                int x = (i % columns) * slotSize;
                int y = (i / columns) * slotSize;
                slot.left(x)
                    .top(y);
            }
            return;
        }

        List<BackpackSlot> filledSlots = new ArrayList<>();
        List<BackpackSlot> emptySlots = new ArrayList<>();

        for (BackpackSlot slot : originalOrder) {
            if (slot.getSlot()
                .getHasStack()) {
                filledSlots.add(slot);
                slot.setFocus(slot.matches(search));
            } else {
                emptySlots.add(slot);
                slot.setFocus(false);
            }
        }

        filledSlots.sort((a, b) -> {
            boolean matchA = a.matches(search);
            boolean matchB = b.matches(search);
            if (matchA && !matchB) return -1;
            if (!matchA && matchB) return 1;
            return 0;
        });

        List<BackpackSlot> sortedSlots = new ArrayList<>();
        sortedSlots.addAll(filledSlots);
        sortedSlots.addAll(emptySlots);

        for (int i = 0; i < sortedSlots.size(); i++) {
            BackpackSlot slot = sortedSlots.get(i);
            int x = (i % columns) * slotSize;
            int y = (i / columns) * slotSize;
            slot.left(x)
                .top(y);
            slot.scheduleResize();
        }

        ((BackpackList) backpackSlots.getParent()).getScrollData()
            .scrollTo(((BackpackList) backpackSlots.getParent()).getScrollArea(), 0);
    }

    public void onInventoryRebuilt() {
        originalOrder = null;
        cacheOriginalOrder();
        doSearch(prevText);
    }
}
