package ruiseki.omoshiroikamo.api;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public interface IModuleMod {

    String getId();

    boolean isEnabled();

    default void onConstruction(FMLConstructionEvent event) {}

    default void preInit(FMLPreInitializationEvent event) {}

    default void init(FMLInitializationEvent event) {}

    default void postInit(FMLPostInitializationEvent event) {}

    default void serverLoad(FMLServerStartingEvent event) {}

    default void serverStarted(FMLServerStartedEvent event) {}

    default void preInitClient(FMLPreInitializationEvent event) {}

    default void initClient(FMLInitializationEvent event) {}

    default void postInitClient(FMLPostInitializationEvent event) {}
}
