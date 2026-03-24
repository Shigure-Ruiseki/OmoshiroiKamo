package ruiseki.omoshiroikamo.core.client.render.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.helper.RenderHelpers;

public interface IPlayerItemRender {

    @SideOnly(Side.CLIENT)
    default void collectContext(ItemStack stack, EntityPlayer player, PlayerRenderContext context) {}

    @SideOnly(Side.CLIENT)
    void render(ItemStack stack, EntityPlayer player, RenderPlayerEvent event, RenderHelpers.RenderType type);
}
