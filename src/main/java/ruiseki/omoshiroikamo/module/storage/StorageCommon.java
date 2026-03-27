package ruiseki.omoshiroikamo.module.storage;

import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.CommonProxyComponent;

public class StorageCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
