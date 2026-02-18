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
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.omoshiroikamo.api.mod.IModuleCommon;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.CablePartRegistry;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.IDsNetworkTickHandler;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.key.LogicKeys;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicTypes;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsCapabilities;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.persist.world.NetworkWorldStorage;

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

        IDsCapabilities.preInit();

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
        NetworkWorldStorage.getInstance(LibMisc.MOD_ID)
            .onStartedEvent(event);
    }

    @Override
    public void serverStopping(FMLServerStoppingEvent event) {
        NetworkWorldStorage.getInstance(LibMisc.MOD_ID)
            .onStoppingEvent(event);
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
