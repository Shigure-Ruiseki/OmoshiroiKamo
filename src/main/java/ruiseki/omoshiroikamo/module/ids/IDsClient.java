package ruiseki.omoshiroikamo.module.ids;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ruiseki.omoshiroikamo.api.ids.ICablePartItem;
import ruiseki.omoshiroikamo.api.mod.IModuleClient;
import ruiseki.omoshiroikamo.config.backport.BackportConfigs;
import ruiseki.omoshiroikamo.module.ids.client.render.CableISBRH;
import ruiseki.omoshiroikamo.module.ids.client.render.ItemPartRenderer;
import ruiseki.omoshiroikamo.module.ids.client.render.PartTESR;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.BlockCable;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.TECable;
import ruiseki.omoshiroikamo.module.ids.common.network.CablePartRegistry;

public class IDsClient implements IModuleClient {

    @Override
    public String getId() {
        return "IntegratedDynamics";
    }

    @Override
    public boolean isEnabled() {
        return BackportConfigs.enableIDs;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {

        CablePartRegistry.initModels();

    }

    @Override
    public void init(FMLInitializationEvent event) {
        BlockCable.rendererId = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(CableISBRH.INSTANCE);
        ClientRegistry.bindTileEntitySpecialRenderer(TECable.class, new PartTESR());
        for (Object obj : Item.itemRegistry) {
            Item item = (Item) obj;
            if (item instanceof ICablePartItem) {
                MinecraftForgeClient.registerItemRenderer(item, new ItemPartRenderer());
            }
        }

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
