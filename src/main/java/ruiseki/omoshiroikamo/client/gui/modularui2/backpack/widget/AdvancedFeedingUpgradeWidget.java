package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;

import lombok.Getter;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiTextures;
import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.item.backpack.wrapper.AdvancedFeedingUpgradeWrapper;

public class AdvancedFeedingUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedFeedingUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> HUNGER_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.complete_hunger"), MGuiTextures.COMPLETE_HUNGER_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.half_hunger"), MGuiTextures.HALF_HUNGER_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.immediate_hunger"), MGuiTextures.IMMEDIATE_HUNGER_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> HEART_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.consider_health"), MGuiTextures.HALF_HEART_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.ignore_health"), MGuiTextures.IGNORE_HALF_HEART_ICON));

    @Getter
    private final CyclicVariantButtonWidget hungerButton;
    @Getter
    private final CyclicVariantButtonWidget heartButton;

    public AdvancedFeedingUpgradeWidget(int slotIndex, AdvancedFeedingUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            new ItemStack(ModItems.ADVANCED_FEEDING_UPGRADE.getItem()),
            "gui.advanced_feeding_settings",
            "adv_feeding_filter",
            6,
            100);

        this.hungerButton = new CyclicVariantButtonWidget(
            HUNGER_VARIANTS,
            wrapper.getHungerFeedingStrategy()
                .ordinal(),
            index -> {
                this.wrapper
                    .setHungerFeedingStrategy(AdvancedFeedingUpgradeWrapper.FeedingStrategy.Hunger.values()[index]);
                updateWrapper();
            });

        this.heartButton = new CyclicVariantButtonWidget(
            HEART_VARIANTS,
            wrapper.getHealthFeedingStrategy()
                .ordinal(),
            index -> {
                this.wrapper
                    .setHealthFeedingStrategy(AdvancedFeedingUpgradeWrapper.FeedingStrategy.HEALTH.values()[index]);
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
                    writer.writeInt(
                        wrapper.getHungerFeedingStrategy()
                            .ordinal());
                    writer.writeInt(
                        wrapper.getHealthFeedingStrategy()
                            .ordinal());
                });
        }
    }
}
