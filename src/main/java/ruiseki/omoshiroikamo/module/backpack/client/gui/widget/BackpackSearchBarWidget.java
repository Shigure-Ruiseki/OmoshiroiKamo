package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.widgets.layout.Column;

import ruiseki.omoshiroikamo.core.client.gui.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.module.backpack.client.gui.slot.BackpackSlot;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;

public class BackpackSearchBarWidget extends SearchBarWidget {

    private final BackpackPanel panel;
    private List<BackpackSlot> originalOrder;

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
}
