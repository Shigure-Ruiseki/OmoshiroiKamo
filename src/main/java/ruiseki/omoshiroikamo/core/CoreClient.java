package ruiseki.omoshiroikamo.core;

import net.minecraft.init.Items;
import net.minecraftforge.client.MinecraftForgeClient;

import com.gtnewhorizon.gtnhlib.itemrendering.TexturedItemRenderer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.client.key.IKeyRegistry;
import ruiseki.okcore.init.ModBase;
import ruiseki.okcore.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;
import ruiseki.omoshiroikamo.core.client.handler.KeyHandler;
import ruiseki.omoshiroikamo.core.client.render.item.PufferFishRenderer;
import ruiseki.omoshiroikamo.core.init.CoreItems;
import ruiseki.omoshiroikamo.core.item.ItemFluidCanister;

@SideOnly(Side.CLIENT)
public class CoreClient extends ClientProxyComponent {

    public CoreClient() {
        super(new CoreCommon());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
        TexturedItemRenderer.register((ItemFluidCanister) CoreItems.FLUID_CANISTER.getItem());
        if (ItemConfigs.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }
    }

    @Override
    public void registerKeyBindings(IKeyRegistry keyRegistry) {
        super.registerKeyBindings(keyRegistry);

        FMLCommonHandler.instance()
            .bus()
            .register(KeyHandler.instance);
    }
}
