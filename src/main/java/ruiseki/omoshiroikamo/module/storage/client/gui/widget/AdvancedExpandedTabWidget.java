package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IAdvancedFilterable;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.UpgradeWrapper;

public class AdvancedExpandedTabWidget<T extends UpgradeWrapper & IAdvancedFilterable>
    extends ExpandedUpgradeTabWidget<T> {

    protected final T wrapper;
    protected final Row startingRow;
    protected final AdvancedFilterWidget filterWidget;

    public AdvancedExpandedTabWidget(int slotIndex, ItemStack stack, T wrapper, String titleKey,
        String filterSyncKey, int coveredTabSize, int width) {
        super(slotIndex, coveredTabSize, stack, titleKey, width);

        this.wrapper = wrapper;

        this.startingRow = (Row) new Row().height(0)
            .name("starting_row");

        this.filterWidget = new AdvancedFilterWidget(slotIndex, wrapper, filterSyncKey).width(88)
            .coverChildrenHeight()
            .name("adv_filter_widget");

        Column column = (Column) new Column().pos(8, 28)
            .coverChildren()
            .childPadding(2)
            .child(this.startingRow)
            .child(this.filterWidget);

        child(column);
    }

    public AdvancedExpandedTabWidget(int slotIndex, ItemStack stack, T wrapper, String titleKey) {
        this(slotIndex, stack, wrapper, titleKey, "adv_common_filter", 6, 100);
    }

    @Override
    protected T getWrapper() {
        return wrapper;
    }
}
