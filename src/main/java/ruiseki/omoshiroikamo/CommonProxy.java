package ruiseki.omoshiroikamo;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.CommonProxyComponent;

public class CommonProxy extends CommonProxyComponent {

    public CommonProxy() {}

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
