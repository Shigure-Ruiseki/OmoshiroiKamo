package ruiseki.omoshiroikamo.module.chickens;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.chickens.client.util.TextureGenerator;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensRecipes;
import ruiseki.omoshiroikamo.module.chickens.registries.ModChickens;

public class ChickensModule extends ModModuleBase {

    public ChickensModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new ChickensClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new ChickensCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableChickens;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ChickensBlocks.preInit();
        ChickensItems.preInit();
        ChickensCreative.preInit();
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
        TextureGenerator.generateCustomChickenTextures();
    }
}
