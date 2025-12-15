package ruiseki.omoshiroikamo.common.item.backpack.wrapper;

import ruiseki.omoshiroikamo.client.gui.modularui2.backpack.handler.UpgradeItemStackHandler;

public interface IStorageUpgrade {

    String STORAGE_TAG = "Storage";

    UpgradeItemStackHandler getStorage();

    void setStorage(UpgradeItemStackHandler handler);
}
