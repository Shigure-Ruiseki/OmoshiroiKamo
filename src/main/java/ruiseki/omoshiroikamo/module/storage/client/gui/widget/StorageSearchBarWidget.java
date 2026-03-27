package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import java.util.ArrayList;
import java.util.List;

import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.core.client.gui.widget.SearchBarWidget;
import ruiseki.omoshiroikamo.core.item.ItemStackKey;
import ruiseki.omoshiroikamo.core.item.ItemStackKeyPool;
import ruiseki.omoshiroikamo.core.util.search.SearchNode;
import ruiseki.omoshiroikamo.core.util.search.SearchParser;
import ruiseki.omoshiroikamo.module.storage.client.gui.slot.StorageSlot;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;

public class StorageSearchBarWidget extends SearchBarWidget {

    private final StoragePanel panel;
    private List<StorageSlot> originalOrder;
    private SearchNode compiledSearch;

    public StorageSearchBarWidget(StoragePanel panel) {
        this.panel = panel;
    }

    private void cacheOriginalOrder() {
        Column backpackSlots = panel.storageInvCol;
        if (backpackSlots == null) return;

        originalOrder = new ArrayList<>();
        for (IWidget child : panel.storageInvCol.getChildren()) {
            if (child instanceof StorageSlot slot) {
                originalOrder.add(slot);
            }
        }
    }

    @Override
    public void doInit() {
        cacheOriginalOrder();
        doSearch(prevText);
    }

    @Override
    public void doSearch(String search) {
        Column backpackSlots = panel.storageInvCol;
        if (backpackSlots == null) return;

        IWidget parent = backpackSlots.getParent();
        if (!(parent instanceof StorageList backpackList)) return;

        int columns = panel.rowSize;
        int slotSize = ItemSlot.SIZE;

        compiledSearch = search.isEmpty() ? null : SearchParser.parse(search);

        if (compiledSearch == null) {
            for (int i = 0; i < originalOrder.size(); i++) {
                StorageSlot slot = originalOrder.get(i);
                slot.setFocus(true);

                int x = (i % columns) * slotSize;
                int y = (i / columns) * slotSize;
                slot.left(x)
                    .top(y);
            }
            return;
        }

        List<StorageSlot> matched = new ArrayList<>();
        List<StorageSlot> others = new ArrayList<>();

        for (StorageSlot slot : originalOrder) {
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
            StorageSlot slot = matched.get(i);
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
