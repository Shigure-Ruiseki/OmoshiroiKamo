package ruiseki.omoshiroikamo.module.chickens;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.api.IModuleMod;
import ruiseki.omoshiroikamo.client.render.block.chicken.BreederTESR;
import ruiseki.omoshiroikamo.client.render.block.chicken.RoostTESR;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.chickens.client.render.RenderChickensChicken;
import ruiseki.omoshiroikamo.module.chickens.client.util.TextureGenerator;
import ruiseki.omoshiroikamo.module.chickens.common.block.TEBreeder;
import ruiseki.omoshiroikamo.module.chickens.common.block.TERoost;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensBlocks;
import ruiseki.omoshiroikamo.module.chickens.common.init.ChickensItems;
import ruiseki.omoshiroikamo.module.chickens.registries.ModChickens;

public class ChickenModule implements IModuleMod {

    @Override
    public String getId() {
        return "Chickens";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useChicken;
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
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModChickens.postInit();
    }

    @Override
    public void preInitClient(FMLPreInitializationEvent event) {}

    @Override
    public void initClient(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TERoost.class, new RoostTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEBreeder.class, new BreederTESR());
        RenderingRegistry.registerEntityRenderingHandler(EntityChickensChicken.class, new RenderChickensChicken());
    }

    @Override
    public void postInitClient(FMLPostInitializationEvent event) {
        TextureGenerator.generateCustomChickenTextures();
    }
}
