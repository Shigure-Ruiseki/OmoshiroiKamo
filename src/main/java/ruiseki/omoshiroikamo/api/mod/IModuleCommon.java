package ruiseki.omoshiroikamo.api.mod;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;

public interface IModuleCommon extends IModule {

    void onConstruction(FMLConstructionEvent event);

    void preInit(FMLPreInitializationEvent event);

    void init(FMLInitializationEvent event);

    void postInit(FMLPostInitializationEvent event);

    void serverLoad(FMLServerStartingEvent event);

    void serverStarted(FMLServerStartedEvent event);

    void onServerAboutToStart(FMLServerAboutToStartEvent event);

    void onServerStopped(FMLServerStoppedEvent event);

}
