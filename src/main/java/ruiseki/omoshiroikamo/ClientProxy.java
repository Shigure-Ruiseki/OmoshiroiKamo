package ruiseki.omoshiroikamo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import com.gtnewhorizon.gtnhlib.client.model.loading.ModelRegistry;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.client.gui.modularui2.MGuiFactories;
import ruiseki.omoshiroikamo.client.handler.KeyHandler;
import ruiseki.omoshiroikamo.client.render.block.chicken.BreederTESR;
import ruiseki.omoshiroikamo.client.render.block.chicken.RoostTESR;
import ruiseki.omoshiroikamo.client.render.block.cow.StallTESR;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.client.render.entity.RenderChickensChicken;
import ruiseki.omoshiroikamo.client.render.entity.RenderCowsCow;
import ruiseki.omoshiroikamo.client.render.item.pufferfish.PufferFishRenderer;
import ruiseki.omoshiroikamo.client.util.TextureGenerator;
import ruiseki.omoshiroikamo.common.block.chicken.TEBreeder;
import ruiseki.omoshiroikamo.common.block.chicken.TERoost;
import ruiseki.omoshiroikamo.common.block.cow.TEStall;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        ModelRegistry.registerModid(LibMisc.MOD_ID);
        MGuiFactories.init();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLCommonHandler.instance()
            .bus()
            .register(KeyHandler.instance);

        if (BackportConfigs.useEnvironmentalTech) {
            ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumExtractor.class, new QuantumExtractorTESR());
        }

        if (BackportConfigs.useChicken) {
            ClientRegistry.bindTileEntitySpecialRenderer(TERoost.class, new RoostTESR());
            ClientRegistry.bindTileEntitySpecialRenderer(TERoost.class, new BreederTESR());
            RenderingRegistry.registerEntityRenderingHandler(EntityChickensChicken.class, new RenderChickensChicken());
        }

        if (BackportConfigs.useCow) {
            ClientRegistry.bindTileEntitySpecialRenderer(TEStall.class, new StallTESR());
            RenderingRegistry.registerEntityRenderingHandler(EntityCowsCow.class, new RenderCowsCow());
        }

        if (ItemConfigs.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }

        TextureGenerator.generateCustomChickenTextures();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
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
