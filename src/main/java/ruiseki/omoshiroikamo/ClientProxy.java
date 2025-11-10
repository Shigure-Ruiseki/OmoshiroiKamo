package ruiseki.omoshiroikamo;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

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
import ruiseki.omoshiroikamo.client.ResourePackGen;
import ruiseki.omoshiroikamo.client.handler.DameEvents;
import ruiseki.omoshiroikamo.client.handler.KeyHandler;
import ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH;
import ruiseki.omoshiroikamo.client.render.block.anvil.AnvilTESR;
import ruiseki.omoshiroikamo.client.render.block.chicken.RoostTESR;
import ruiseki.omoshiroikamo.client.render.block.nanoBotBeacon.NanoBotBeaconTESR;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.client.render.block.solarArray.SolarArrayTESR;
import ruiseki.omoshiroikamo.client.render.entity.RenderChickensChicken;
import ruiseki.omoshiroikamo.client.render.entity.RenderCowsCow;
import ruiseki.omoshiroikamo.client.render.item.backpack.BackpackRenderer;
import ruiseki.omoshiroikamo.client.render.item.hammer.HammerRenderer;
import ruiseki.omoshiroikamo.client.render.item.pufferfish.PufferFishRenderer;
import ruiseki.omoshiroikamo.common.block.anvil.TEAnvil;
import ruiseki.omoshiroikamo.common.block.chicken.TERoost;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT1;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT2;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT3;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT4;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT1;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT2;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT3;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.TEQuantumOreExtractorT4;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.TEQuantumResExtractorT1;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.TEQuantumResExtractorT2;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.TEQuantumResExtractorT3;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.TEQuantumResExtractorT4;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.TESolarArrayT1;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.TESolarArrayT2;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.TESolarArrayT3;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.TESolarArrayT4;
import ruiseki.omoshiroikamo.common.entity.chicken.EntityChickensChicken;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.item.ItemConfig;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {

    public ClientProxy() {
    }

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

        AnvilTESR anvilTESR = new AnvilTESR();
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.ANVIL.get()), anvilTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEAnvil.class, anvilTESR);

        SolarArrayTESR solarArrayTESR = new SolarArrayTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT1.class, solarArrayTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT2.class, solarArrayTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT3.class, solarArrayTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT4.class, solarArrayTESR);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.SOLAR_ARRAY.get()), solarArrayTESR);

        QuantumExtractorTESR quantumExtractorTESR = new QuantumExtractorTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumOreExtractorT1.class, quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumOreExtractorT2.class, quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumOreExtractorT3.class, quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumOreExtractorT4.class, quantumExtractorTESR);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(ModBlocks.QUANTUM_ORE_EXTRACTOR.get()), quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumResExtractorT1.class, quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumResExtractorT2.class, quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumResExtractorT3.class, quantumExtractorTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumResExtractorT4.class, quantumExtractorTESR);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(ModBlocks.QUANTUM_RES_EXTRACTOR.get()), quantumExtractorTESR);

        NanoBotBeaconTESR nanoBotBeaconTESR = new NanoBotBeaconTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumBeaconT1.class, nanoBotBeaconTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumBeaconT2.class, nanoBotBeaconTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumBeaconT3.class, nanoBotBeaconTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuantumBeaconT4.class, nanoBotBeaconTESR);
        MinecraftForgeClient
            .registerItemRenderer(Item.getItemFromBlock(ModBlocks.QUANTUM_BEACON.get()), nanoBotBeaconTESR);

        RoostTESR roostTESR = new RoostTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TERoost.class, roostTESR);

        if (ItemConfig.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }

        MinecraftForgeClient.registerItemRenderer(ModItems.HAMMER.get(), new HammerRenderer());
        MinecraftForgeClient.registerItemRenderer(ModItems.BACKPACK.get(), new BackpackRenderer());

        RenderingRegistry.registerEntityRenderingHandler(EntityChickensChicken.class, new RenderChickensChicken());
        RenderingRegistry.registerEntityRenderingHandler(EntityCowsCow.class, new RenderCowsCow());

        RenderingRegistry.registerBlockHandler(new JsonModelISBRH());
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        MinecraftForge.EVENT_BUS.register(new DameEvents());
    }

    @Override
    public void callAssembleResourcePack(FMLPreInitializationEvent event) {
        ResourePackGen.applyAllTexture(event);
        super.callAssembleResourcePack(event);
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
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
