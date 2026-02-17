package ruiseki.omoshiroikamo.core;

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
import ruiseki.omoshiroikamo.core.common.init.CoreItems;
import ruiseki.omoshiroikamo.core.common.init.CoreOreDict;
import ruiseki.omoshiroikamo.core.common.init.CorePacket;
import ruiseki.omoshiroikamo.core.common.init.CoreRecipes;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;

public class CoreCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Core";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CoreItems.preInit();

        // Initialize the custom structure system
        StructureManager.getInstance()
            .initialize(
                event.getModConfigurationDirectory()
                    .getParentFile());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        CoreOreDict.init();
        CoreRecipes.init();
        CorePacket.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {

    }

    @Override
    public void serverStarted(FMLServerStartedEvent event) {

    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {

    }

    @Override
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }
}
