package ruiseki.omoshiroikamo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.client.handler.KeyHandler;
import ruiseki.omoshiroikamo.client.handler.StructureWandRenderer;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.client.render.item.pufferfish.PufferFishRenderer;
import ruiseki.omoshiroikamo.client.util.TextureLoader;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;
import ruiseki.omoshiroikamo.core.ModuleManager;
import ruiseki.omoshiroikamo.module.backpack.BackpackClient;
import ruiseki.omoshiroikamo.module.chickens.ChickensClient;
import ruiseki.omoshiroikamo.module.cows.CowsClient;
import ruiseki.omoshiroikamo.module.dml.DMLClient;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelRegistry.registerModid(LibMisc.MOD_ID);

        ModuleManager.register(new ChickensClient());
        ModuleManager.register(new CowsClient());
        ModuleManager.register(new DMLClient());
        ModuleManager.register(new BackpackClient());

        ModuleManager.preInitClient(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLCommonHandler.instance()
            .bus()
            .register(KeyHandler.instance);

        // Structure Wand renderer (visualizes selection bounds)
        MinecraftForge.EVENT_BUS.register(StructureWandRenderer.INSTANCE);

        ModuleManager.initClient(event);

        if (BackportConfigs.useEnvironmentalTech) {
            ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumExtractor.class, new QuantumExtractorTESR());
        }

        if (ItemConfigs.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        ModuleManager.postInitClient(event);
        TextureLoader.loadFromConfig(LibMisc.MOD_ID, LibMisc.MOD_NAME + " Runtime Textures", OmoshiroiKamo.class);
    }

    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    public World getClientWorld() {
        return FMLClientHandler.instance()
            .getClient().theWorld;
    }

    @Override
    public long getTickCount() {
        return clientTickCount;
    }

    @Override
    protected void onClientTick() {
        if (!Minecraft.getMinecraft()
            .isGamePaused() && Minecraft.getMinecraft().theWorld != null) {
            ++clientTickCount;
        }
    }
}
