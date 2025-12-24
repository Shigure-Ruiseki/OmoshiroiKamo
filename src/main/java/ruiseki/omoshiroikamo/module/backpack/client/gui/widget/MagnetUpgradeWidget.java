package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import com.cleanroommc.modularui.api.drawable.IKey;

import lombok.Getter;
import ruiseki.omoshiroikamo.core.client.gui.GuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.MagnetUpgradeWrapper;

public class MagnetUpgradeWidget extends BasicExpandedTabWidget<MagnetUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> EXP_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.exp_magnet"), GuiTextures.EXP_MAGNET_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.ignore_exp_magnet"),
            GuiTextures.IGNORE_EXP_MAGNET_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> ITEM_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.item_magnet"), GuiTextures.ITEM_MAGNET_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.ignore_item_magnet"),
            GuiTextures.IGNORE_ITEM_MAGNET_ICON));

    @Getter
    private final CyclicVariantButtonWidget itemButton;
    @Getter
    private final CyclicVariantButtonWidget expButton;

    public MagnetUpgradeWidget(int slotIndex, MagnetUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            BackpackItems.MAGNET_UPGRADE.newItemStack(),
            "gui.backpack.magnet_settings",
            "common_filter",
            5,
            75);

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
