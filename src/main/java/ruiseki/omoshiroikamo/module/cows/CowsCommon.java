package ruiseki.omoshiroikamo.module.cows;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsBlocks;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsRecipes;
import ruiseki.omoshiroikamo.module.cows.common.registries.ModCows;

public class CowsCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Cows";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useCow;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CowsBlocks.preInit();
        CowsItems.preInit();
        ModCows.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModCows.init();
        CowsRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModCows.postInit();
    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {

    }

    @Override
    public void serverStarted(FMLServerStartedEvent event) {

    }
}
