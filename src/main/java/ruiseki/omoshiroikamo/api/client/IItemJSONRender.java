package ruiseki.omoshiroikamo.api.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IItemJSONRender {

    @SideOnly(Side.CLIENT)
    void onArmorRender(ItemStack stack, RenderPlayerEvent event, RenderUtils.RenderType type);

}
