package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedMagnetUpgradeWrapper;

public class AdvancedMagnetUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedMagnetUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> EXP_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.exp_magnet"), MGuiTextures.EXP_MAGNET_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.ignore_exp_magnet"), MGuiTextures.IGNORE_EXP_MAGNET_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> ITEM_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.item_magnet"), MGuiTextures.ITEM_MAGNET_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.ignore_item_magnet"),
            MGuiTextures.IGNORE_ITEM_MAGNET_ICON));

    @Getter
    private final CyclicVariantButtonWidget itemButton;
    @Getter
    private final CyclicVariantButtonWidget expButton;

    public AdvancedMagnetUpgradeWidget(int slotIndex, AdvancedMagnetUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            new ItemStack(ModItems.ADVANCED_MAGNET_UPGRADE.getItem()),
            "gui.advanced_magnet_settings",
            "adv_common_filter",
            6,
            100);

        this.itemButton = new CyclicVariantButtonWidget(ITEM_VARIANTS, wrapper.isCollectItem() ? 0 : 1, index -> {
            this.wrapper.setCollectItem(index == 0);
            updateWrapper();
        });

        this.expButton = new CyclicVariantButtonWidget(EXP_VARIANTS, wrapper.isCollectExp() ? 0 : 1, index -> {
            this.wrapper.setCollectExp(index == 0);
            updateWrapper();
        });

        this.startingRow.leftRel(0.5f)
            .height(20)
            .childPadding(2)
            .child(this.itemButton)
            .child(this.expButton);
    }

    public void updateWrapper() {
        if (this.filterWidget.getSlotSyncHandler() != null) {
            this.filterWidget.getSyncHandler()
                .syncToServer(UpgradeSlotSH.UPDATE_MAGNET, buf -> {
                    buf.writeBoolean(wrapper.isCollectItem());
                    buf.writeBoolean(wrapper.isCollectExp());
                });
        }
    }
}
