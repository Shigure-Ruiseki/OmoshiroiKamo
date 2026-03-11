package ruiseki.omoshiroikamo.module.cows;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.module.cows.client.render.RenderCowsCow;
import ruiseki.omoshiroikamo.module.cows.client.render.StallTESR;
import ruiseki.omoshiroikamo.module.cows.client.util.ClientColorUpdater;
import ruiseki.omoshiroikamo.module.cows.common.block.TEStall;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

@SideOnly(Side.CLIENT)
public class CowsClient extends ClientProxyComponent {

    public CowsClient() {
        super(new CowsCommon());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerRenderers() {
        registerRenderer(TEStall.class, new StallTESR());
        registerRenderer(EntityCowsCow.class, new RenderCowsCow());
        MinecraftForge.EVENT_BUS.register(new ClientColorUpdater());
        super.registerRenderers();
    }
}
