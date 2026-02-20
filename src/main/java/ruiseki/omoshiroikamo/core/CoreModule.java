package ruiseki.omoshiroikamo.core;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.common.init.CoreCapabilities;
import ruiseki.omoshiroikamo.core.common.init.CoreItems;
import ruiseki.omoshiroikamo.core.common.init.CoreOreDict;
import ruiseki.omoshiroikamo.core.common.init.CoreRecipes;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.init.ModModuleBase;
import ruiseki.omoshiroikamo.core.proxy.ICommonProxy;

public class CoreModule extends ModModuleBase {

    public CoreModule() {
        super(OmoshiroiKamo.instance);
    }

    @Override
    protected ICommonProxy createProxy() {
        try {
            if (MinecraftHelpers.isClientSide()) {
                return new CoreClient();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new CoreCommon();
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CoreItems.preInit();

        // Initialize the custom structure system
        StructureManager.getInstance()
            .initialize(
                event.getModConfigurationDirectory()
                    .getParentFile());

        CoreCapabilities.preInit();

    }

    @Override
    public void init(FMLInitializationEvent event) {
        CoreOreDict.init();
        CoreRecipes.init();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

}
