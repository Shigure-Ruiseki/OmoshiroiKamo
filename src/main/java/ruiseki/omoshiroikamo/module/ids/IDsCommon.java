package ruiseki.omoshiroikamo.module.ids;

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
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.network.CablePartRegistry;
import ruiseki.omoshiroikamo.module.ids.common.network.IDsNetworkTickHandler;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.key.LogicKeys;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicTypes;

public class IDsCommon implements IModuleCommon {

    public static IDsNetworkTickHandler IDsNetworkTickHandler = null;

    @Override
    public String getId() {
        return "IntegratedDynamics";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableIDs;
    }

    @Override
    public void onConstruction(FMLConstructionEvent event) {

    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LogicTypes.preInit();
        LogicKeys.preInit();

        CablePartRegistry.init();

        IDsBlocks.preInit();
        IDsItems.preInit();
        IDsCreative.preInit();
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
        IDsNetworkTickHandler = new IDsNetworkTickHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(IDsNetworkTickHandler);
    }

    public void onServerStopped(FMLServerStoppedEvent event) {
        FMLCommonHandler.instance()
            .bus()
            .unregister(IDsNetworkTickHandler);
        IDsNetworkTickHandler = null;
    }
}
