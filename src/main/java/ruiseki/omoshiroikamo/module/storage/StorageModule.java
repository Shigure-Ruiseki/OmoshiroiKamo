package ruiseki.omoshiroikamo.module.storage;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;
import ruiseki.omoshiroikamo.module.storage.common.init.StorageBlocks;
import ruiseki.omoshiroikamo.module.storage.common.init.StorageItems;

public class StorageModule extends ModModuleBase {

    public StorageModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new StorageClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new StorageCommon();
    }

    @Override
    public boolean isEnable() {
        return BackportConfigs.enableStorage;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        StorageBlocks.preInit();
        StorageItems.preInit();
        StorageCreative.preInit();
    }

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}
