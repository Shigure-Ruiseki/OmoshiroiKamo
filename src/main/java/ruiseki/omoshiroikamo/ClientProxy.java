package ruiseki.omoshiroikamo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.client.handler.KeyHandler;
import ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH;
import ruiseki.omoshiroikamo.client.render.block.chicken.RoostTESR;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.client.render.entity.RenderChickensChicken;
import ruiseki.omoshiroikamo.client.render.entity.RenderCowsCow;
import ruiseki.omoshiroikamo.client.render.item.backpack.BackpackRenderer;
import ruiseki.omoshiroikamo.client.render.item.pufferfish.PufferFishRenderer;
import ruiseki.omoshiroikamo.common.block.chicken.TERoost;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.config.item.ItemConfigs;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {}

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        FMLCommonHandler.instance()
            .bus()
            .register(KeyHandler.instance);

        QuantumExtractorTESR quantumExtractorTESR = new QuantumExtractorTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumExtractor.class, quantumExtractorTESR);

        RoostTESR roostTESR = new RoostTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TERoost.class, roostTESR);

        if (ItemConfigs.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }

        MinecraftForgeClient.registerItemRenderer(ModItems.BACKPACK.get(), new BackpackRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityChickensChicken.class, new RenderChickensChicken());
        RenderingRegistry.registerEntityRenderingHandler(EntityCowsCow.class, new RenderCowsCow());

        RenderingRegistry.registerBlockHandler(new JsonModelISBRH());
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
