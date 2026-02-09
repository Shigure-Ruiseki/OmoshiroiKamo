package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.layout.Column;

import ruiseki.omoshiroikamo.api.item.ItemStackKey;
import ruiseki.omoshiroikamo.api.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.core.client.gui.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.core.common.search.SearchNode;
import ruiseki.omoshiroikamo.core.common.search.SearchParser;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.BackpackSlot;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class BackpackSearchBarWidget extends SearchBarWidget {

    private final BackpackPanel panel;
    private List<BackpackSlot> originalOrder;
    private SearchNode compiledSearch;

    public BackpackSearchBarWidget(BackpackPanel panel) {
        this.panel = panel;
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
    public void doInit() {
        cacheOriginalOrder();
        if (panel.getHandler()
            .isSearchBackpack()) {
            prevText = "";
            value(new StringValue(prevText));
        } else {
            doSearch(prevText);
        }
    }

    @Override
    public void doSearch(String search) {
        Column backpackSlots = panel.getBackpackInvCol();
        if (backpackSlots == null) return;

        IWidget parent = backpackSlots.getParent();
        if (!(parent instanceof BackpackList backpackList)) return;

        int columns = panel.getRowSize();
        int slotSize = BackpackSlot.SIZE;

        compiledSearch = search.isEmpty() ? null : SearchParser.parse(search);

        if (compiledSearch == null) {
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

        List<BackpackSlot> matched = new ArrayList<>();
        List<BackpackSlot> others = new ArrayList<>();

        for (BackpackSlot slot : originalOrder) {
            if (!slot.getSlot()
                .getHasStack()) {
                slot.setFocus(false);
                others.add(slot);
                continue;
            }

            ItemStackKey key = ItemStackKeyPool.get(
                slot.getSlot()
                    .getStack());
            boolean match = compiledSearch.matches(key);

            slot.setFocus(match);

            if (match) matched.add(slot);
            else others.add(slot);
        }

        matched.addAll(others);

        for (int i = 0; i < matched.size(); i++) {
            BackpackSlot slot = matched.get(i);
            int x = (i % columns) * slotSize;
            int y = (i / columns) * slotSize;
            slot.left(x)
                .top(y);
            slot.scheduleResize();
        }

        backpackList.getScrollData()
            .scrollTo(backpackList.getScrollArea(), 0);
    }

}
