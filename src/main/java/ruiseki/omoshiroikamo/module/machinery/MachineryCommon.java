package ruiseki.omoshiroikamo.module.machinery;

import java.io.File;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryItems;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryPackets;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

/**
 * Modular Machinery Backport module entry point.
 * Provides a flexible multiblock machine system with JSON-based structure
 * definitions.
 */
public class MachineryCommon implements IModuleCommon {

    private static File configDir;

    @Override
    public String getId() {
        return "Machinery";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableMachinery;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        configDir = event.getModConfigurationDirectory();
        MachineryBlocks.preInit();
        MachineryItems.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        // TODO: Register recipes
        MachineryPackets.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        RecipeLoader.getInstance()
            .loadAll(configDir);
    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {}

    @Override
    public void serverStarted(FMLServerStartedEvent event) {}

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {

    }

    @Override
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }

    public static File getConfigDir() {
        return configDir;
    }
}
