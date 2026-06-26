package ruiseki.omoshiroikamo.module.chickens;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.okcore.helper.MinecraftHelpers;
import ruiseki.okcore.init.ModModuleBase;
import ruiseki.okcore.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.condition.Conditions;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
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
        Conditions.registerDefaults();
        ChickensBlocks.preInit();
        ChickensItems.preInit();
        ChickensCreative.preInit();
        ModChickens.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModChickens.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModChickens.postInit();
        ChickensRecipes.init();
        if (MinecraftHelpers.isClientSide()) {
            TextureGenerator.generateCustomChickenTextures();
        }
    }
}
