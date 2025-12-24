package ruiseki.omoshiroikamo.module.backpack.common.item.wrapper;

import ruiseki.omoshiroikamo.module.backpack.client.gui.handler.UpgradeItemStackHandler;

public interface IStorageUpgrade {

    String STORAGE_TAG = "Storage";

    UpgradeItemStackHandler getStorage();

    void setStorage(UpgradeItemStackHandler handler);
}
