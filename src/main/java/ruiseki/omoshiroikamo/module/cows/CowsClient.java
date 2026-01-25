package ruiseki.omoshiroikamo.module.cows;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.cows.client.render.RenderCowsCow;
import ruiseki.omoshiroikamo.module.cows.client.render.StallTESR;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

@SideOnly(Side.CLIENT)
public class CowsClient implements IModuleClient {

    @Override
    public String getId() {
        return "Cows";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableCow;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TEStall.class, new StallTESR());
        RenderingRegistry.registerEntityRenderingHandler(EntityCowsCow.class, new RenderCowsCow());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

}
