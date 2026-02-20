package ruiseki.omoshiroikamo.core.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.persist.world.WorldStorage;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;

public abstract class ModModuleBase {

    @Getter
    protected final ModBase mod;
    protected final ICommonProxy moduleProxy;

    public ModModuleBase(ModBase mod) {
        this.mod = mod;
        this.moduleProxy = createProxy();
    }

    protected abstract ICommonProxy createProxy();

    public abstract boolean isEnable();

    protected void addInitListener(IInitListener listener) {
        mod.addInitListeners(listener);
    }

    protected void registerWorldStorage(WorldStorage storage) {
        mod.registerWorldStorage(storage);
    }

    public ICommonProxy getModuleProxy() {
        return moduleProxy;
    }

    public abstract void preInit(FMLPreInitializationEvent event);

    public abstract void init(FMLInitializationEvent event);

    public abstract void postInit(FMLPostInitializationEvent event);

    public void onServerStarting(FMLServerStartingEvent event) {};

    public void onServerStarted(FMLServerStartedEvent event) {};

    public void onServerStopping(FMLServerStoppingEvent event) {};
}
