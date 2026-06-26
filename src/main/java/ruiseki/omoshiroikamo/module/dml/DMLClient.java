package ruiseki.omoshiroikamo.module.dml;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;

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
