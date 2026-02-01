package ruiseki.omoshiroikamo.module.ids.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;

import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.api.ids.ICablePartItem;

public class ItemPartRenderer implements IItemRenderer {

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        Item item = stack.getItem();
        if (!(item instanceof ICablePartItem partItem)) return;
        ICablePart part = partItem.createPart();
        part.setSide(ForgeDirection.SOUTH);

        GL11.glPushMatrix();

        Tessellator tess = TessellatorManager.get();

        part.renderItemPart(type, stack, tess);

        GL11.glPopMatrix();
    }

}
