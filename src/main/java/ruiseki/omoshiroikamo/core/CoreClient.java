package ruiseki.omoshiroikamo.core;

import net.minecraft.init.Items;
import net.minecraftforge.client.MinecraftForgeClient;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;
import ruiseki.omoshiroikamo.core.client.handler.KeyHandler;
import ruiseki.omoshiroikamo.core.client.render.PufferFishRenderer;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

@SideOnly(Side.CLIENT)
public class CoreClient implements IModuleClient {

    @Override
    public String getId() {
        return "Core";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModelRegistry.registerModid(LibMisc.MOD_ID);
    }

    @Override
    public void init(FMLInitializationEvent event) {

        FMLCommonHandler.instance()
            .bus()
            .register(KeyHandler.instance);

        if (ItemConfigs.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
