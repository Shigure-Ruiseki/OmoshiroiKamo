package ruiseki.omoshiroikamo.module.multiblock;

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
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockItems;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockOreDicts;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiblockRecipes;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiblockWorldGenerator;

public class MultiBlockCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "MultiBlock";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useMultiBlock;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MultiBlockBlocks.preInit();
        MultiBlockItems.preInit();
        MultiBlockOreDicts.preInit();
        MultiBlockAchievements.preInit();
        MultiblockWorldGenerator.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MultiblockRecipes.init();
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
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {

    }

    @Override
    public void onServerStopped(FMLServerStoppedEvent event) {

    }
}
