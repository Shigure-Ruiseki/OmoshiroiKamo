package ruiseki.omoshiroikamo.module.dml;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;

@SideOnly(Side.CLIENT)
public class DMLClient implements IModuleClient {

    @Override
    public String getId() {
        return "Deep Mod Learning";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableDML;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}
