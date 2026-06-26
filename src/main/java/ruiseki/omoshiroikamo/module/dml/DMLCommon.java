package ruiseki.omoshiroikamo.module.dml;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;

public class DMLCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
