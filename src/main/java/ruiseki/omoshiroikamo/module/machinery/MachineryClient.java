package ruiseki.omoshiroikamo.module.machinery;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.module.machinery.client.render.ItemPortRenderer;
import ruiseki.omoshiroikamo.module.machinery.client.render.PortOverlayISBRH;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;

/**
 * Client-side module for Machinery.
 * Handles renderers and other client-only features.
 * Port overlays are rendered via ISBRH for optimal performance.
 */
@SideOnly(Side.CLIENT)
public class MachineryClient extends ClientProxyComponent {

    public MachineryClient() {
        super(new MachineryCommon());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerRenderers() {
        super.registerRenderers();
        AbstractPortBlock.portRendererId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(PortOverlayISBRH.INSTANCE);
        Logger.info("MachineryClient: Registered PortOverlayISBRH with ID " + AbstractPortBlock.portRendererId);

        for (Object obj : Block.blockRegistry) {
            Block block = (Block) obj;
            if (block instanceof AbstractPortBlock<?> || block instanceof BlockMachineController) {
                Item item = Item.getItemFromBlock(block);
                if (item != null) {
                    MinecraftForgeClient.registerItemRenderer(item, new ItemPortRenderer());
                }
            }
        }
    }
}
