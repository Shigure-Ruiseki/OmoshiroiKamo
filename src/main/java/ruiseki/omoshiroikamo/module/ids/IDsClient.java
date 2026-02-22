package ruiseki.omoshiroikamo.module.ids;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.RenderingRegistry;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.ids.ICablePartItem;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.proxy.ClientProxyComponent;
import ruiseki.omoshiroikamo.module.ids.client.render.CableISBRH;
import ruiseki.omoshiroikamo.module.ids.client.render.ItemPartRenderer;
import ruiseki.omoshiroikamo.module.ids.client.render.PartTESR;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.BlockCable;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.TECable;
import ruiseki.omoshiroikamo.module.ids.common.item.CablePartRegistry;

public class IDsClient extends ClientProxyComponent {

    public IDsClient() {
        super(new IDsCommon());
    }

    @Override
    public ModBase getMod() {
        return OmoshiroiKamo.instance;
    }

    @Override
    public void registerRenderers() {
        BlockCable.rendererId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(CableISBRH.INSTANCE);
        registerRenderer(TECable.class, new PartTESR());
        for (Object obj : Item.itemRegistry) {
            Item item = (Item) obj;
            if (item instanceof ICablePartItem) {
                MinecraftForgeClient.registerItemRenderer(item, new ItemPartRenderer());
            }
        }
        CablePartRegistry.initModels();
        super.registerRenderers();
    }
}
