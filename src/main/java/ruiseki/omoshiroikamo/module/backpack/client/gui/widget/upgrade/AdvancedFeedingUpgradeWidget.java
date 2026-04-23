package ruiseki.omoshiroikamo.module.backpack.client.gui.widget.upgrade;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.network.NetworkUtils;

import lombok.Getter;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.api.storage.wrapper.IFeedingUpgrade;
import ruiseki.omoshiroikamo.core.client.gui.OKGuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.widget.CyclicVariantButtonWidget;
import ruiseki.omoshiroikamo.module.backpack.common.block.BackpackPanel;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedFeedingUpgradeWrapper;

public class AdvancedFeedingUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedFeedingUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> HUNGER_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.complete_hunger"),
            OKGuiTextures.COMPLETE_HUNGER_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.half_hunger"), OKGuiTextures.HALF_HUNGER_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.immediate_hunger"),
            OKGuiTextures.IMMEDIATE_HUNGER_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> HEART_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.consider_health"), OKGuiTextures.HALF_HEART_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.ignore_health"),
            OKGuiTextures.IGNORE_HALF_HEART_ICON));

    @Getter
    private final CyclicVariantButtonWidget hungerButton;
    @Getter
    private final CyclicVariantButtonWidget heartButton;

    public AdvancedFeedingUpgradeWidget(int slotIndex, AdvancedFeedingUpgradeWrapper wrapper, ItemStack stack,
        BackpackPanel panel, String titleKey) {
        super(slotIndex, wrapper, stack, titleKey, "adv_feeding_filter", 6, 100);

        this.hungerButton = new CyclicVariantButtonWidget(
            HUNGER_VARIANTS,
            wrapper.getHungerFeedingStrategy()
                .ordinal(),
            index -> {
                this.wrapper.setHungerFeedingStrategy(IFeedingUpgrade.FeedingStrategy.Hunger.values()[index]);
                updateWrapper();
            });

        this.heartButton = new CyclicVariantButtonWidget(
            HEART_VARIANTS,
            wrapper.getHealthFeedingStrategy()
                .ordinal(),
            index -> {
                this.wrapper.setHealthFeedingStrategy(IFeedingUpgrade.FeedingStrategy.HEALTH.values()[index]);
                updateWrapper();
            });

        this.startingRow.leftRel(0.5f)
            .height(20)
            .childPadding(2)
            .child(this.hungerButton)
            .child(this.heartButton);
    }

    public void updateWrapper() {
        if (this.filterWidget.getSlotSyncHandler() != null) {
            this.filterWidget.getSyncHandler()
                .syncToServer(UpgradeSlotSH.UPDATE_ADVANCED_FEEDING, writer -> {
                    NetworkUtils.writeEnumValue(writer, wrapper.getHungerFeedingStrategy());
                    NetworkUtils.writeEnumValue(writer, wrapper.getHealthFeedingStrategy());
                });
        }
    }
}
