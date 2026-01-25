package ruiseki.omoshiroikamo.module.cable;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.cable.common.init.CableBlocks;
import ruiseki.omoshiroikamo.module.cable.common.init.CableItems;
import ruiseki.omoshiroikamo.module.cable.common.network.CableNetworkTickHandler;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.key.LogicKeys;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicTypes;

public class CableCommon implements IModuleCommon {

    public static CableNetworkTickHandler cableNetworkTickHandler = null;

    @Override
    public String getId() {
        return "Cable";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableCable;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LogicTypes.preInit();
        LogicKeys.preInit();
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

    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        cableNetworkTickHandler = new CableNetworkTickHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(cableNetworkTickHandler);
    }

    public void onServerStopped(FMLServerStoppedEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .unregister(cableNetworkTickHandler);
        cableNetworkTickHandler = null;
    }
}
