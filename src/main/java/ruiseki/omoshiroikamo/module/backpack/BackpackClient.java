package ruiseki.omoshiroikamo.module.backpack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.client.key.IKeyRegistry;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMods;
import ruiseki.omoshiroikamo.core.proxy.CommonProxyComponent;
import ruiseki.omoshiroikamo.module.backpack.integration.nei.BackpackGuiOpener;

@SideOnly(Side.CLIENT)
public class BackpackClient extends CommonProxyComponent {

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry) {
        super.registerKeyBindings(keyRegistry);
        if (LibMods.NotEnoughItems.isLoaded()) {
            BackpackGuiOpener backpackGuiOpener = new BackpackGuiOpener();
        }
    }
}
