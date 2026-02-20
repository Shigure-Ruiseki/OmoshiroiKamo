package ruiseki.omoshiroikamo.module.multiblock;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockAchievements;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockBlocks;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockItems;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiBlockOreDicts;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiblockRecipes;
import ruiseki.omoshiroikamo.module.multiblock.common.init.MultiblockWorldGenerator;

public class MultiBlockModule extends ModModuleBase {

    public MultiBlockModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new MultiBlockClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new MultiBlockCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableMultiBlock;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MultiBlockBlocks.preInit();
        MultiBlockItems.preInit();
        MultiBlockCreative.preInit();
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
}
