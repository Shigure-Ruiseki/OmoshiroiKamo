package ruiseki.omoshiroikamo.module.backpack.client.gui.widget;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;

import lombok.Getter;
import ruiseki.omoshiroikamo.core.client.gui.GuiTextures;
import ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.item.wrapper.AdvancedFeedingUpgradeWrapper;

public class AdvancedFeedingUpgradeWidget extends AdvancedExpandedTabWidget<AdvancedFeedingUpgradeWrapper> {

    private static final List<CyclicVariantButtonWidget.Variant> HUNGER_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.complete_hunger"),
            GuiTextures.COMPLETE_HUNGER_ICON),
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.half_hunger"), GuiTextures.HALF_HUNGER_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.immediate_hunger"),
            GuiTextures.IMMEDIATE_HUNGER_ICON));

    private static final List<CyclicVariantButtonWidget.Variant> HEART_VARIANTS = Arrays.asList(
        new CyclicVariantButtonWidget.Variant(IKey.lang("gui.backpack.consider_health"), GuiTextures.HALF_HEART_ICON),
        new CyclicVariantButtonWidget.Variant(
            IKey.lang("gui.backpack.ignore_health"),
            GuiTextures.IGNORE_HALF_HEART_ICON));

    @Getter
    private final CyclicVariantButtonWidget hungerButton;
    @Getter
    private final CyclicVariantButtonWidget heartButton;

    public AdvancedFeedingUpgradeWidget(int slotIndex, AdvancedFeedingUpgradeWrapper wrapper) {
        super(
            slotIndex,
            wrapper,
            new ItemStack(BackpackItems.ADVANCED_FEEDING_UPGRADE.getItem()),
            "gui.backpack.advanced_feeding_settings",
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
