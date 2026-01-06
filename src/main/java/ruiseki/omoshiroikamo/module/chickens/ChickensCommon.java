package ruiseki.omoshiroikamo.module.chickens;

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
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensRecipes;
import ruiseki.omoshiroikamo.module.chickens.registries.ModChickens;

public class ChickensCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Chickens";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useChicken;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ChickensBlocks.preInit();
        ChickensItems.preInit();
        ModChickens.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModChickens.init();
        ChickensRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModChickens.postInit();
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
