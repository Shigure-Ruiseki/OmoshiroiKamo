package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFilterUpgradeWrapper;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.IFilterUpgrade;

public class AdvancedFilterUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedFilterUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> FILTER_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.input_output"), MGuiTextures.IN_OUT_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.input"), MGuiTextures.IN_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.output"), MGuiTextures.OUT_ICON));

    @Getter
    private final CyclicVariantButtonWidget filterButton;

    public AdvancedFilterUpgradeWidget(int slotIndex, AdvancedFilterUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            new ItemStack(ModItems.ADVANCED_FILTER_UPGRADE.get()),
            "gui.advanced_filter_settings",
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
