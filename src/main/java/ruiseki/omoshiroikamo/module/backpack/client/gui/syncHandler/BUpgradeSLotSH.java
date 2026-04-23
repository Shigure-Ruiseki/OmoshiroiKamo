package ruiseki.omoshiroikamo.module.backpack.client.gui.syncHandler;

import com.cleanroommc.modularui.widgets.slot.ModularSlot;

import ruiseki.omoshiroikamo.api.storage.IStoragePanel;
import ruiseki.omoshiroikamo.module.backpack.common.handler.BackpackWrapper;

public class BUpgradeSLotSH extends UpgradeSlotSH {

    public final BackpackWrapper wrapper;

    public BUpgradeSLotSH(ModularSlot slot, BackpackWrapper wrapper, IStoragePanel panel) {
        super(slot, wrapper, panel);
        this.wrapper = wrapper;
    }
}
