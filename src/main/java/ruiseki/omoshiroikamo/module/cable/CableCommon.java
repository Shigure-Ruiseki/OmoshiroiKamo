package ruiseki.omoshiroikamo.module.cable;

import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.module.cable.common.init.CableBlocks;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;

public class CableCommon implements IModuleCommon {

    @Override
    public String getId() {
        return "Cable";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CableBlocks.preInit();
        CableItems.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void serverLoad(FMLServerStartingEvent event) {

    }

    @Override
    public void serverStarted(FMLServerStartedEvent event) {

    }
}
