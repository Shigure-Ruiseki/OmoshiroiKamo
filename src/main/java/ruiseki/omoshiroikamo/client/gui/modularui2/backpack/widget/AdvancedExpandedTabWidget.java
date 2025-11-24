package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;

import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IAdvancedFilterable;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.UpgradeWrapper;

public class AdvancedExpandedTabWidget<T extends UpgradeWrapper & IAdvancedFilterable>
    extends ExpandedUpgradeTabWidget<T> {

    protected final T wrapper;
    protected final Row startingRow;
    protected final AdvancedFilterWidget filterWidget;

    public AdvancedExpandedTabWidget(int slotIndex, T wrapper, ItemStack delegatedIconStack, String titleKey,
        String filterSyncKey, int coveredTabSize, int width) {
        super(slotIndex, coveredTabSize, delegatedIconStack, titleKey, width);

        this.wrapper = wrapper;

        this.startingRow = (Row) new Row().height(0)
            .name("starting_row");

        this.filterWidget = new AdvancedFilterWidget(slotIndex, wrapper, filterSyncKey).width(88)
            .coverChildrenHeight()
            .name("adv_filter_widget");

        Column column = (Column) new Column().pos(8, 28)
            .width(88)
            .childPadding(2)
            .child(this.startingRow)
            .child(this.filterWidget);

        child(column);
    }

    public AdvancedExpandedTabWidget(int slotIndex, T wrapper, ItemStack delegatedIconStack, String titleKey) {
        this(slotIndex, wrapper, delegatedIconStack, titleKey, "adv_common_filter", 6, 100);
    }

    @Override
    protected T getWrapper() {
        return wrapper;
    }
}
