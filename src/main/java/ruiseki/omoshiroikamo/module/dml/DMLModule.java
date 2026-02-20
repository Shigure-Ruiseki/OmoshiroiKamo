package ruiseki.omoshiroikamo.module.dml;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLBlocks;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLRecipes;
import ruiseki.omoshiroikamo.module.dml.common.registries.ModModels;

public class DMLModule extends ModModuleBase {

    public DMLModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new DMLClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new DMLCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableDML;
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
}
