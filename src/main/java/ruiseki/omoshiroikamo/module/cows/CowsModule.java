package ruiseki.omoshiroikamo.module.cows;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsBlocks;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsItems;
import ruiseki.omoshiroikamo.module.cows.common.init.CowsRecipes;
import ruiseki.omoshiroikamo.module.cows.common.registries.ModCows;

public class CowsModule extends ModModuleBase {

    public CowsModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new CowsClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new CowsCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableCows;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CowsBlocks.preInit();
        CowsItems.preInit();
        CowsCreative.preInit();
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
}
