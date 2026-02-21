package ruiseki.omoshiroikamo.module.backpack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackBlocks;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackItems;
import ruiseki.omoshiroikamo.module.backpack.common.init.BackpackRecipes;
import ruiseki.omoshiroikamo.module.backpack.integration.bauble.BackpackBaubleCompat;

public class BackpackModule extends ModModuleBase {

    public BackpackModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new BackpackClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new BackpackCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableBackpack;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BackpackBlocks.preInit();
        BackpackItems.preInit();
        BackpackCreative.preInit();
        BackpackBaubleCompat.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        BackpackRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        BackpackBaubleCompat.postInit();
    }
}
