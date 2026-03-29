package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IBasicFilterable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapperBase;

public class BasicExpandedTabWidget<T extends UpgradeWrapperBase & IBasicFilterable>
    extends ExpandedUpgradeTabWidget<T> {

    protected final T wrapper;
    protected final Row startingRow;
    protected final BasicFilterWidget filterWidget;

    public BasicExpandedTabWidget(int slotIndex, ItemStack stack, T wrapper, String titleKey, String filterSyncKey,
        int coveredTabSize, int width) {
        super(slotIndex, coveredTabSize, stack, titleKey, width);

        this.wrapper = wrapper;

        this.startingRow = (Row) new Row().height(0)
            .name("starting_row");

        this.filterWidget = new BasicFilterWidget(wrapper, slotIndex, filterSyncKey).width(64)
            .coverChildrenHeight()
            .name("filter_widget");

        Column column = (Column) new Column().pos(8, 28)
            .coverChildren()
            .childPadding(2)
            .child(startingRow)
            .child(filterWidget);

        child(column);
    }

    public BasicExpandedTabWidget(int slotIndex, ItemStack stack, T wrapper, String titleKey) {
        this(slotIndex, stack, wrapper, titleKey, "common_filter", 4, 75);
    }

    public BasicExpandedTabWidget(int slotIndex, ItemStack stack, T wrapper, String titleKey, String filterSyncKey) {
        this(slotIndex, stack, wrapper, titleKey, filterSyncKey, 4, 75);
    }

    @Override
    public T getWrapper() {
        return wrapper;
    }
}
