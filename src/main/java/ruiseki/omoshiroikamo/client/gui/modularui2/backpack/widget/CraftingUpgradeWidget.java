package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.widgets.SlotGroupWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.slot.ItemSlot;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.CraftingUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.ICraftingUpgrade;

public class CraftingUpgradeWidget extends ExpandedUpgradeTabWidget<CraftingUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> INTO_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.into_backpack"), MGuiTextures.INTO_BACKPACK),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.into_inventory"), MGuiTextures.INTO_INVENTORY));

    private static final List<CyclicVariantButtonWidget.Variant> USED_BACKPACK_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.used_backpack"), MGuiTextures.USED_BACKPACK),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.unused_backpack"), MGuiTextures.UNUSED_BACKPACK));

    private final CraftingUpgradeWrapper wrapper;

    public CraftingUpgradeWidget(int slotIndex, CraftingUpgradeWrapper wrapper) {
        super(slotIndex, 5, ModItems.CRAFTING_UPGRADE.newItemStack(), "gui.crafting_settings");
        this.wrapper = wrapper;

        this.syncHandler("upgrades", slotIndex);

        CyclicVariantButtonWidget craftingDesButton = new CyclicVariantButtonWidget(
            INTO_VARIANTS,
            wrapper.getCraftingDes()
                .ordinal(),
            index -> {
                wrapper.setCraftingDes(ICraftingUpgrade.CraftingDestination.values()[index]);
                updateWrapper();
            }).size(20, 20);

        CyclicVariantButtonWidget usedBackpackButton = new CyclicVariantButtonWidget(
            USED_BACKPACK_VARIANTS,
            wrapper.isUseBackpack() ? 0 : 1,
            index -> {
                wrapper.setUseBackpack(index == 0);
                updateWrapper();
            }).size(20, 20);

        SlotGroupWidget craftingGroupsWidget = new SlotGroupWidget().name("crafting_matrix")
            .coverChildren();

        ItemSlot[] craftingMatrix = new ItemSlot[9];
        for (int i = 0; i < 9; i++) {
            ItemSlot itemSlot = new ItemSlot().syncHandler("crafting_slot_" + slotIndex, i)
                .pos(i % 3 * 18, i / 3 * 18)
                .name("crafting_slot_" + i);

            craftingGroupsWidget.child(itemSlot);
            craftingMatrix[i] = itemSlot;
        }

        ItemSlot craftingResult = new ItemSlot().syncHandler("crafting_result_" + slotIndex, 9)
            .pos(18, 18 * 3 + 9)
            .name("crafting_result_" + slotIndex);
        craftingGroupsWidget.child(craftingResult);
        Row buttonRow = (Row) new Row().height(20)
            .child(craftingDesButton)
            .child(usedBackpackButton);

        Column column = (Column) new Column().pos(8, 28)
            .width(64)
            .childPadding(2)
            .child(buttonRow)
            .child(craftingGroupsWidget);

        child(column);
    }

    @Override
    protected CraftingUpgradeWrapper getWrapper() {
        return wrapper;
    }

    public void updateWrapper() {
        this.getSyncHandler()
            .syncToServer(UpgradeSlotSH.UPDATE_CRAFTING_DES, buf -> {
                buf.writeInt(
                    wrapper.getCraftingDes()
                        .ordinal());
                buf.writeBoolean(wrapper.isUseBackpack());
            });
    }

}
