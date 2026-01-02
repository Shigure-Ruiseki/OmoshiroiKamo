package ruiseki.omoshiroikamo.module.cable;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;

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

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
