package ruiseki.omoshiroikamo.module.machinery;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;

/**
 * Modular Machinery Backport module entry point.
 * Provides a flexible multiblock machine system with JSON-based structure
 * definitions.
 */
public class MachineryCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Machinery";
    }

    @Override
    public boolean isEnabled() {
        // TODO: Add config option
        return true;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MachineryBlocks.preInit();

        // Client-side texture generation setup
        // MachineryClient implements IModuleClient and is called by the module loader
        if (event.getSide()
            .isClient()) {
            new MachineryClient().preInit(event);
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        // TODO: Register recipes
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverLoad(FMLServerStartingEvent event) {}

    @Override
    public void serverStarted(FMLServerStartedEvent event) {}
}
