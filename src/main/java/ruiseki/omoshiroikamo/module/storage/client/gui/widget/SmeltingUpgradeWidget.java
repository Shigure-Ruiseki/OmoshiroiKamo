package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.drawable.GuiTextures;
import com.cleanroommc.modularui.widgets.ProgressWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.module.storage.client.gui.slot.BigItemSlot;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.SmeltingUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.StoragePanel;

public class SmeltingUpgradeWidget extends ExpandedUpgradeTabWidget<SmeltingUpgradeWrapper> {

    private final SmeltingUpgradeWrapper wrapper;

    public SmeltingUpgradeWidget(int slotIndex, ItemStack stack, SmeltingUpgradeWrapper wrapper, StoragePanel panel) {

        super(slotIndex, 4, stack, "gui.storage.smelting_settings", 90);
        this.wrapper = wrapper;

        this.syncHandler("upgrades", slotIndex);

        Row row = (Row) new Row().coverChildren()
            .childPadding(4);

        Column left = (Column) new Column().coverChildren()
            .childPadding(2);

        ItemSlot input = new ItemSlot().syncHandler("smelting_" + slotIndex, 0);

        left.child(input);

        ProgressWidget burnProgress = new ProgressWidget().texture(GuiTextures.PROGRESS_CYCLE, 18)
            .direction(ProgressWidget.Direction.UP)
            .syncHandler("burn_progress_" + slotIndex)
            .size(18);

        left.child(burnProgress);

        ItemSlot fuel = new ItemSlot().syncHandler("smelting_" + slotIndex, 1);

        left.child(fuel);

        row.child(left);

        ProgressWidget cookProgress = new ProgressWidget().texture(GuiTextures.PROGRESS_ARROW, 20)
            .direction(ProgressWidget.Direction.RIGHT)
            .syncHandler("progress_" + slotIndex)
            .paddingRight(6)
            .size(20);

        row.child(cookProgress);

        ItemSlot output = new BigItemSlot().syncHandler("smelting_" + slotIndex, 2);

        row.child(output);

        Column root = (Column) new Column().pos(8, 38)
            .coverChildren()
            .child(row);

        child(root);
    }

    @Override
    protected SmeltingUpgradeWrapper getWrapper() {
        return wrapper;
    }
}
