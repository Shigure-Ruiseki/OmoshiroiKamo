package ruiseki.omoshiroikamo.module.multiblock;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;

public class MultiBlockCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
