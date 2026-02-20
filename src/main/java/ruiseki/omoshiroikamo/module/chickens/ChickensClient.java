package ruiseki.omoshiroikamo.module.chickens;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.module.chickens.client.render.BreederTESR;
import ruiseki.omoshiroikamo.module.chickens.client.render.RenderChickensChicken;
import ruiseki.omoshiroikamo.module.chickens.client.render.RoostTESR;
import ruiseki.omoshiroikamo.module.chickens.common.block.TEBreeder;
import ruiseki.omoshiroikamo.module.chickens.common.block.TERoost;
import ruiseki.omoshiroikamo.module.chickens.common.entity.EntityChickensChicken;

@SideOnly(Side.CLIENT)
public class ChickensClient extends ClientProxyComponent {

    public ChickensClient() {
        super(new ChickensCommon());
    }

    @Override
    public void registerRenderers() {

        registerRenderer(TEBreeder.class, new BreederTESR());
        registerRenderer(TERoost.class, new RoostTESR());

        registerRenderer(EntityChickensChicken.class, new RenderChickensChicken());

        super.registerRenderers();
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }
}
