package ruiseki.omoshiroikamo.api.storage.wrapper;

import ruiseki.omoshiroikamo.core.client.gui.handler.ItemStackHandlerBase;

public interface IStorageUpgrade {

    String STORAGE_TAG = "Storage";

    ItemStackHandlerBase getStorage();
}
