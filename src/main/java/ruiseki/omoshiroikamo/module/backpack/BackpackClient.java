package ruiseki.omoshiroikamo.module.backpack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.backpack.client.gui.MGuiFactories;

@SideOnly(Side.CLIENT)
public class BackpackClient implements IModuleClient {

    @Override
    public String getId() {
        return "Backpack";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.useBackpack;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MGuiFactories.init();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
