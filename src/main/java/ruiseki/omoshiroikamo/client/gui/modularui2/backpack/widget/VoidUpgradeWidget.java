package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;

import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IVoidUpgrade;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.VoidUpgradeWrapper;

public class VoidUpgradeWidget extends BasicExpandedTabWidget<VoidUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> VOID_INPUT_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.void_all"), MGuiTextures.VOID_ALL),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.void_automation"), MGuiTextures.VOID_AUTOMATION));

    private static final List<CyclicVariantButtonWidget.Variant> VOID_TYPE_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.void_overflow"), MGuiTextures.VOID_OVERFLOW),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.void_any"), MGuiTextures.VOID_ANY));

    public VoidUpgradeWidget(int slotIndex, VoidUpgradeWrapper wrapper) {
        super(slotIndex, wrapper, ModItems.VOID_UPGRADE.newItemStack(), "gui.void_settings", "common_filter", 5, 80);

        CyclicVariantButtonWidget inputButton = new CyclicVariantButtonWidget(
            VOID_INPUT_VARIANTS,
            wrapper.getVoidInput()
                .ordinal(),
            index -> {
                wrapper.setVoidInput(IVoidUpgrade.VoidInput.values()[index]);
                updateWrapper();
            });

        CyclicVariantButtonWidget voidButton = new CyclicVariantButtonWidget(
            VOID_TYPE_VARIANTS,
            wrapper.getVoidType()
                .ordinal(),
            index -> {
                wrapper.setVoidType(IVoidUpgrade.VoidType.values()[index]);
                updateWrapper();
            });
        this.startingRow.leftRel(0.5f)
            .height(20)
            .childPadding(2)
            .child(inputButton)
            .child(voidButton);
    }

    private void updateWrapper() {
        if (this.filterWidget.getSlotSyncHandler() != null) {
            this.filterWidget.getSyncHandler()
                .syncToServer(UpgradeSlotSH.UPDATE_VOID, buf -> {
                    buf.writeInt(
                        wrapper.getVoidType()
                            .ordinal());
                    buf.writeInt(
                        wrapper.getVoidInput()
                            .ordinal());
                });
        }
    }
}
