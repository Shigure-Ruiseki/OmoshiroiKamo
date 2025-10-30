package ruiseki.omoshiroikamo;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

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
import ruiseki.omoshiroikamo.client.render.block.anvil.AnvilISBRH;
import ruiseki.omoshiroikamo.client.render.block.anvil.AnvilTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectableISBRH;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectorEVTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectorHVTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectorIVTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectorLVTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectorMVTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.ConnectorULVTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.InsulatorTESR;
import ruiseki.omoshiroikamo.client.render.block.connectable.TransformerTESR;
import ruiseki.omoshiroikamo.client.render.block.nanoBotBeacon.NanoBotBeaconTESR;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.LaserCoreTESR;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.LaserLensTESR;
import ruiseki.omoshiroikamo.client.render.block.quantumExtractor.QuantumExtractorTESR;
import ruiseki.omoshiroikamo.client.render.block.solarArray.SolarArrayTESR;
import ruiseki.omoshiroikamo.client.render.block.solarArray.SolarCellTESR;
import ruiseki.omoshiroikamo.client.render.item.backpack.BackpackRenderer;
import ruiseki.omoshiroikamo.client.render.item.hammer.HammerRenderer;
import ruiseki.omoshiroikamo.client.render.item.pufferfish.PufferFishRenderer;
import ruiseki.omoshiroikamo.common.block.anvil.TEAnvil;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEConnectorEV;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEConnectorHV;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEConnectorIV;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEConnectorLV;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEConnectorMV;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEConnectorULV;
import ruiseki.omoshiroikamo.common.block.energyConnector.TEInsulator;
import ruiseki.omoshiroikamo.common.block.energyConnector.TETransformer;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT1;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT2;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT3;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.TEQuantumBeaconT4;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.core.TELaserCore;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.lens.TELaserLens;
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
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.cell.TESolarCell;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.config.item.ItemConfig;

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

        ConnectableISBRH connectableISBRH = new ConnectableISBRH();
        RenderingRegistry.registerBlockHandler(connectableISBRH);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.CONNECTABLE.get()), connectableISBRH);
        ClientRegistry.bindTileEntitySpecialRenderer(TEInsulator.class, new InsulatorTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEConnectorULV.class, new ConnectorULVTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEConnectorLV.class, new ConnectorLVTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEConnectorMV.class, new ConnectorMVTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEConnectorHV.class, new ConnectorHVTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEConnectorEV.class, new ConnectorEVTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TEConnectorIV.class, new ConnectorIVTESR());
        ClientRegistry.bindTileEntitySpecialRenderer(TETransformer.class, new TransformerTESR());

        AnvilISBRH anvilISBRH = new AnvilISBRH();
        RenderingRegistry.registerBlockHandler(anvilISBRH);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.ANVIL.get()), anvilISBRH);
        ClientRegistry.bindTileEntitySpecialRenderer(TEAnvil.class, new AnvilTESR());

        SolarArrayTESR solarArrayTESR = new SolarArrayTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT1.class, solarArrayTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT2.class, solarArrayTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT3.class, solarArrayTESR);
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarArrayT4.class, solarArrayTESR);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.SOLAR_ARRAY.get()), solarArrayTESR);

        SolarCellTESR solarCellTESR = new SolarCellTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TESolarCell.class, solarCellTESR);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.SOLAR_CELL.get()), solarCellTESR);

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

        LaserCoreTESR laserCoreTESR = new LaserCoreTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TELaserCore.class, laserCoreTESR);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.LASER_CORE.get()), laserCoreTESR);
        LaserLensTESR laserLensTESR = new LaserLensTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TELaserLens.class, laserLensTESR);
        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(ModBlocks.LASER_LENS.get()), laserLensTESR);

        ModItems.registerItemRenderer();

        if (ItemConfig.renderPufferFish) {
            MinecraftForgeClient.registerItemRenderer(Items.fish, new PufferFishRenderer());
        }

        MinecraftForgeClient.registerItemRenderer(ModItems.HAMMER.get(), new HammerRenderer());
        MinecraftForgeClient.registerItemRenderer(ModItems.BACKPACK.get(), new BackpackRenderer());
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
