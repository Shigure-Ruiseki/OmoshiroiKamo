package ruiseki.omoshiroikamo.module.backpack;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.module.backpack.integration.nei.BackpackGuiOpener;

@SideOnly(Side.CLIENT)
public class BackpackClient implements IModuleClient {

    private BackpackGuiOpener backpackGuiOpener;

    @Override
    public String getId() {
        return "Backpack";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableBackpack;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {
        if (LibMods.NotEnoughItems.isLoaded()) {
            backpackGuiOpener = new BackpackGuiOpener();
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
