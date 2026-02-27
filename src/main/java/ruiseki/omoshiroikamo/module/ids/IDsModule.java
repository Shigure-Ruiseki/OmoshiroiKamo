package ruiseki.omoshiroikamo.module.ids;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.persist.world.GlobalCounters;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBiomes;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsItems;
import ruiseki.omoshiroikamo.module.ids.common.item.CablePartRegistry;
import ruiseki.omoshiroikamo.module.ids.common.item.IDsNetworkTickHandler;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.key.LogicKeys;
import ruiseki.omoshiroikamo.module.ids.common.item.logic.type.LogicTypes;

public class IDsModule extends ModModuleBase {

    public static GlobalCounters globalCounters = null;

    public static IDsNetworkTickHandler IDsNetworkTickHandler = null;

    public IDsModule() {
        super(OmoshiroiKamo.instance);
        registerWorldStorage(globalCounters = new GlobalCounters(this.getMod()));
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new IDsClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new IDsCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableIDs;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LogicTypes.preInit();
        LogicKeys.preInit();

        CablePartRegistry.init();

        IDsBlocks.preInit();
        IDsItems.preInit();
        IDsBiomes.preInit();
        IDsCreative.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        super.onServerStarting(event);
        IDsNetworkTickHandler = new IDsNetworkTickHandler();
        FMLCommonHandler.instance()
            .bus()
            .register(IDsNetworkTickHandler);
    }

    @Override
    public void onServerStopping(FMLServerStoppingEvent event) {
        super.onServerStopping(event);
        FMLCommonHandler.instance()
            .bus()
            .unregister(IDsNetworkTickHandler);
        IDsNetworkTickHandler = null;
    }
}
