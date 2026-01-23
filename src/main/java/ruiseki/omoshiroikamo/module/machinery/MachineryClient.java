package ruiseki.omoshiroikamo.module.machinery;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.client.render.ItemPortRenderer;
import ruiseki.omoshiroikamo.module.machinery.client.render.PortOverlayISBRH;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;

/**
 * Client-side module for Machinery.
 * Handles renderers and other client-only features.
 * Port overlays are rendered via ISBRH for optimal performance.
 */
@SideOnly(Side.CLIENT)
public class MachineryClient implements IModuleClient {

    @Override
    public String getId() {
        return "Machinery";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableMachinery;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Logger.info("MachineryClient: Pre-init complete");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        // Register ISBRH for port overlays (much faster than TESR)
        AbstractPortBlock.portRendererId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(PortOverlayISBRH.INSTANCE);
        Logger.info("MachineryClient: Registered PortOverlayISBRH with ID " + AbstractPortBlock.portRendererId);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        // Register item renderers for port blocks
        for (Object obj : Block.blockRegistry) {
            Block block = (Block) obj;
            if (block instanceof AbstractPortBlock<?>) {
                Item item = Item.getItemFromBlock(block);
                if (item != null) {
                    MinecraftForgeClient.registerItemRenderer(item, new ItemPortRenderer());
                }
            }
        }
    }
}
