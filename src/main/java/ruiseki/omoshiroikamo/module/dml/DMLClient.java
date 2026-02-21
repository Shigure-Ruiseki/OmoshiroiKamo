package ruiseki.omoshiroikamo.module.dml;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;

@SideOnly(Side.CLIENT)
public class DMLClient extends ClientProxyComponent {

    public DMLClient() {
        super(new DMLCommon());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
