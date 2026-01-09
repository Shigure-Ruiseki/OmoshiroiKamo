package ruiseki.omoshiroikamo.module.backpack;

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
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackBlocks;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackPackets;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackRecipes;
import ruiseki.omoshiroikamo.module.backpack.integration.bauble.BackpackBaubleCompat;

public class BackpackCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Backpack";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useBackpack;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BackpackBlocks.preInit();
        BackpackItems.preInit();
        BackpackBaubleCompat.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BackpackRecipes.init();
        BackpackPackets.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        BackpackBaubleCompat.postInit();
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
