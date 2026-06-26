package ruiseki.omoshiroikamo;

import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ClientProxyComponent;

@SuppressWarnings("unused")
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
