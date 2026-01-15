package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;

import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedVoidUpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IVoidUpgrade;

public class AdvancedVoidUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedVoidUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> VOID_INPUT_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.void_all"), OKGuiTextures.VOID_ALL),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.void_automation"),
            OKGuiTextures.VOID_AUTOMATION));

    private static final List<CyclicVariantButtonWidget.Variant> VOID_TYPE_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.void_overflow"), OKGuiTextures.VOID_OVERFLOW),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.void_any"), OKGuiTextures.VOID_ANY));

    public AdvancedVoidUpgradeWidget(int slotIndex, AdvancedVoidUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            new ItemStack(BackpackItems.ADVANCED_VOID_UPGRADE.getItem()),
            "gui.backpack.advanced_void_settings",
            "adv_common_filter",
            6,
            100);

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
