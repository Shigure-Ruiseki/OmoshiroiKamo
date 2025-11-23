package ruiseki.omoshiroikamo.client.gui.modularui2.backpack.widget;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.value.sync.SyncHandler;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.common.item.backpack.capabilities.UpgradeWrapper;

public abstract class ExpandedUpgradeTabWidget<U extends UpgradeWrapper> extends ExpandedTabWidget {

    protected abstract U getWrapper();

    private UpgradeSlotSH slotSyncHandler = null;

    public ExpandedUpgradeTabWidget(int slotIndex, int coveredTabSize, ItemStack delegatedIconStack, String titleKey,
        int width) {
        super(coveredTabSize, new ItemDrawable(delegatedIconStack), titleKey, width);
        this.syncHandler("upgrades", slotIndex);
    }

    public ExpandedUpgradeTabWidget(int slotIndex, int coveredTabSize, ItemStack delegatedIconStack, String titleKey) {
        this(slotIndex, coveredTabSize, delegatedIconStack, titleKey, 75);
    }

    public void updateTabState() {
        U wrapper = getWrapper();
        if (wrapper != null) {
            boolean newState = !wrapper.getTabOpened();
            wrapper.setTabOpened(newState);

            if (slotSyncHandler != null) {
                slotSyncHandler
                    .syncToServer(UpgradeSlotSH.UPDATE_UPGRADE_TAB_STATE, buf -> { buf.writeBoolean(newState); });
            }
        }
    }

    @Override
    public boolean isValidSyncHandler(SyncHandler syncHandler) {
        if (syncHandler instanceof UpgradeSlotSH) {
            slotSyncHandler = (UpgradeSlotSH) syncHandler;
        }
        return slotSyncHandler != null;
    }
}
