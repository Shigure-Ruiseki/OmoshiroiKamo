package ruiseki.omoshiroikamo.module.backpack.client.gui.widget.updateGroup;

import ruiseki.omoshiroikamo.api.storage.widget.UpgradeSlotGroupRegistry;
import ruiseki.omoshiroikamo.core.init.IInitListener;

public class UpgradeSlotGroupRegisters implements IInitListener {

    @Override
    public void onInit(Step step) {
        if (step == Step.POSTINIT) {
            UpgradeSlotGroupRegistry.register(new CommonFilterSlotGroupFactory());
            UpgradeSlotGroupRegistry.register(new AdvancedCommonFilterSlotGroupFactory());
            UpgradeSlotGroupRegistry.register(new FeedingFilterSlotGroupFactory());
            UpgradeSlotGroupRegistry.register(new AdvancedFeedingFilterSlotGroupFactory());
            UpgradeSlotGroupRegistry.register(new CraftingSlotGroup());
        }
    }
}
