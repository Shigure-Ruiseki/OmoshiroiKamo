package ruiseki.omoshiroikamo.module.chickens;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;

public class ChickensCommon extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
