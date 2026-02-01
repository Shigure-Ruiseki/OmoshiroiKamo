package ruiseki.omoshiroikamo.module.dml;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLRecipes;
import ruiseki.omoshiroikamo.module.dml.common.registries.ModModels;

public class DMLCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Deep Mod Learning";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableDML;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        DMLBlocks.preInit();
        DMLItems.preInit();
        DMLCreative.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModModels.init();
        DMLRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModModels.postInit();
    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {

    }

    @Override
    public void serverStarted(FMLServerStartedEvent event) {

    }

    @Override
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }
}
