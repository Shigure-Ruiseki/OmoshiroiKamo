package ruiseki.omoshiroikamo.module.chickens;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.chickens.client.render.BreederTESR;
import ruiseki.omoshiroikamo.module.chickens.client.render.RenderChickensChicken;
import ruiseki.omoshiroikamo.module.chickens.client.render.RoostTESR;
import ruiseki.omoshiroikamo.module.chickens.client.util.TextureGenerator;
import ruiseki.omoshiroikamo.module.chickens.common.block.TEBreeder;
import ruiseki.omoshiroikamo.module.chickens.common.block.TERoost;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;

@SideOnly(Side.CLIENT)
public class ChickensClient implements IModuleClient {

    @Override
    public String getId() {
        return "Chickens";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableChicken;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TERoost.class, new RoostTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEBreeder.class, new BreederTESR());
        RenderingRegistry.registerEntityRenderingHandler(EntityChickensChicken.class, new RenderChickensChicken());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        TextureGenerator.generateCustomChickenTextures();
    }
}
