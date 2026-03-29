package ruiseki.omoshiroikamo.module.storage.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.network.NetworkUtils;

import lombok.Getter;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.storage.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.AdvancedFilterUpgradeWrapper;
import ruiseki.omoshiroikamo.module.storage.common.item.wrapper.IFilterUpgrade;

public class AdvancedFilterUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedFilterUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> FILTER_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.input_output"), OKGuiTextures.IN_OUT_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.input"), OKGuiTextures.IN_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.output"), OKGuiTextures.OUT_ICON));

    @Getter
    private final CyclicVariantButtonWidget filterButton;

    public AdvancedFilterUpgradeWidget(int slotIndex, ItemStack stack, AdvancedFilterUpgradeWrapper wrapper) {
        super(slotIndex, stack, wrapper, "gui.backpack.advanced_filter_settings", "adv_common_filter", 6, 100);

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
                            buf -> { NetworkUtils.writeEnumValue(buf, wrapper.getfilterWay()); });
                }
            });

        this.startingRow.leftRel(0.5f)
            .height(20)
            .childPadding(2)
            .child(this.filterButton);
    }

}
