package ruiseki.omoshiroikamo;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.core.ModuleManager;
import ruiseki.omoshiroikamo.core.client.util.TextureLoader;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
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

    public void preInit(FMLPreInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {
        ModuleManager.initClient(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModuleManager.postInitClient(event);
        TextureLoader.loadFromConfig(LibMisc.MOD_ID, LibMisc.MOD_NAME + " Runtime Textures", OmoshiroiKamo.class);
    }
}
