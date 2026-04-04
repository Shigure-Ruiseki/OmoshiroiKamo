package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import net.minecraft.network.PacketBuffer;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.storage.IStoragePanel;
import ruiseki.omoshiroikamo.api.storage.syncHandler.UpgradeSlotSH;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class BUpgradeSLotSH extends UpgradeSlotSH {

    public final BackpackWrapper wrapper;

    public BUpgradeSLotSH(ModularSlot slot, BackpackWrapper wrapper, IStoragePanel panel) {
        super(slot, wrapper, panel);
        this.wrapper = wrapper;
    }

    @Override
    public void readOnClient(int id, PacketBuffer buf) {
        super.readOnClient(id, buf);
        if (id == UPDATE_UPGRADE_TAB_STATE || id == UPDATE_UPGRADE_TOGGLE
            || id == UPDATE_BASIC_FILTERABLE
            || id == UPDATE_ADVANCED_FILTERABLE
            || id == UPDATE_ADVANCED_FEEDING
            || id == UPDATE_FILTER
            || id == UPDATE_MAGNET
            || id == UPDATE_CRAFTING
            || id == UPDATE_VOID
            || id == UPDATE_CRAFTING_R
            || id == UPDATE_CRAFTING_G
            || id == UPDATE_CRAFTING_C

        ) {
            wrapper.syncToServer();
        }
    }
}
