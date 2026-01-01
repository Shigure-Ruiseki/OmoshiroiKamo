package ruiseki.omoshiroikamo.module.cable;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.module.cable.client.renderer.CableISBRH;
import ruiseki.omoshiroikamo.module.cable.common.block.BlockCable;

public class CableClient implements IModuleClient {

    @Override
    public String getId() {
        return "Cable";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

    }

    @Override
    public void init(FMLInitializationEvent event) {
        BlockCable.rendererId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(CableISBRH.INSTANCE);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
