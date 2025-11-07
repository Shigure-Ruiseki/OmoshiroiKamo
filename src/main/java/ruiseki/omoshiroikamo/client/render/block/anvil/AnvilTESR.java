package ruiseki.omoshiroikamo.client.render.block.anvil;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.enderio.core.client.render.RenderUtil;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.block.anvil.TEAnvil;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

@SideOnly(Side.CLIENT)
public class AnvilTESR extends TileEntitySpecialRenderer implements IItemRenderer {

    private final IModelCustom model;
    private static final String MODEL = LibResources.PREFIX_MODEL + "anvil.obj";

    private static final ResourceLocation texture = new ResourceLocation(LibResources.PREFIX_MODEL + "anvil.png");

    public AnvilTESR() {
        model = AdvancedModelLoader.loadModel(new ResourceLocation(MODEL));
    }

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {

        TEAnvil te = (TEAnvil) tile;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.375, z + 0.5);

        RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

        int inputIndex = 0;
        for (int i = te.getSlotDefinition()
            .getMinItemInput(); i <= te.getSlotDefinition()
                .getMaxItemInput(); i++) {
            ItemStack stack = te.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.3f, inputIndex * 0.1f, 0f); // phải + chồng lên
            if (!(stack.getItem() instanceof ItemBlock)) {
                GL11.glRotatef(90f, 1f, 0f, 0f);
                GL11.glTranslatef(0f, -0.25f, -0.1f);
            }
            EntityItem ghostItem = new EntityItem(tile.getWorldObj(), 0, 0, 0, stack);
            ghostItem.hoverStart = 0f;
            renderItem.doRender(ghostItem, 0, 0, 0, 0, 0);
            GL11.glPopMatrix();
            inputIndex++;
        }

        int outputIndex = 0;
        for (int i = te.getSlotDefinition()
            .getMinItemOutput(); i <= te.getSlotDefinition()
                .getMaxItemOutput(); i++) {
            ItemStack stack = te.getStackInSlot(i);
            if (stack == null) {
                continue;
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(-0.3f, outputIndex * 0.1f, 0f); // trái + chồng lên
            if (!(stack.getItem() instanceof ItemBlock)) {
                GL11.glRotatef(90f, 1f, 0f, 0f);
                GL11.glTranslatef(0f, -0.25f, -0.1f);
            }
            EntityItem ghostItem = new EntityItem(tile.getWorldObj(), 0, 0, 0, stack);
            ghostItem.hoverStart = 0f;
            renderItem.doRender(ghostItem, 0, 0, 0, 0, 0);
            GL11.glPopMatrix();
            outputIndex++;
        }

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        RenderUtil.bindTexture(texture);
        model.renderAll();
        GL11.glPopMatrix();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        } else if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0f, -0.2f, 0f);
        } else if (type == ItemRenderType.EQUIPPED) {
            GL11.glTranslatef(0.5f, 0.5f, 0.5f);
        }
        RenderUtil.bindTexture(texture);
        model.renderAll();
        GL11.glPopMatrix();
    }
}
