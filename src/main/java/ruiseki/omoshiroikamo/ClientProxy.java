package ruiseki.omoshiroikamo;

import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;

@SuppressWarnings("unused")
public class ClientProxy extends ClientProxyComponent {

    public ClientProxy() {
        super(new CommonProxy());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
    }
}
