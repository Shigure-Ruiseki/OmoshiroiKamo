package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedFilterUpgradeWrapper;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.IFilterUpgrade;

public class AdvancedFilterUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedFilterUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> FILTER_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.input_output"), MGuiTextures.IN_OUT_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.input"), MGuiTextures.IN_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.output"), MGuiTextures.OUT_ICON));

    @Getter
    private final CyclicVariantButtonWidget filterButton;

    public AdvancedFilterUpgradeWidget(int slotIndex, AdvancedFilterUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            new ItemStack(BackpackItems.ADVANCED_FILTER_UPGRADE.getItem()),
            "gui.backpack.advanced_filter_settings",
            "adv_common_filter",
            6,
            100);

        this.filterButton = new CyclicVariantButtonWidget(
            FILTER_VARIANTS,
            wrapper.getfilterWay()
                .ordinal(),
            index -> {
                wrapper.setFilterWay(IFilterUpgrade.FilterWayType.values()[index]);
                if (this.filterWidget.getSlotSyncHandler() != null) {
                    this.filterWidget.getSyncHandler()
                        .syncToServer(
                            UpgradeSlotSH.UPDATE_FILTER,
                            buf -> {
                                buf.writeInt(
                                    wrapper.getfilterWay()
                                        .ordinal());
                            });
                }
            });

        this.startingRow.leftRel(0.5f)
            .height(20)
            .childPadding(2)
            .child(this.filterButton);
    }

}
