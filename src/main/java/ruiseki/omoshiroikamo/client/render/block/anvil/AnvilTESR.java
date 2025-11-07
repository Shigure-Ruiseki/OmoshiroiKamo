package ruiseki.omoshiroikamo.client.render.block.anvil;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.client.models.ModelIEObj;
import ruiseki.omoshiroikamo.client.render.AbstractMTESR;
import ruiseki.omoshiroikamo.common.block.anvil.TEAnvil;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.chickenbones.Matrix4;

@SideOnly(Side.CLIENT)
public class AnvilTESR extends AbstractMTESR {

    ModelIEObj modelAnvil = new ModelIEObj(LibResources.PREFIX_MODEL + "anvil.obj") {

        @Override
        public IIcon getBlockIcon(String groupName) {
            return ModBlocks.ANVIL.get()
                .getIcon(0, 0);
        }
    };

    @Override
    public void renderDynamic(TileEntity tile, double x, double y, double z, float partialTicks) {
        TEAnvil te = (TEAnvil) tile;

        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.375, z + 0.5); // căn giữa block

        RenderItem renderItem = (RenderItem) RenderManager.instance.getEntityClassRenderObject(EntityItem.class);

        // Render input stack bên phải
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

        // Render output stack bên trái
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
    }

    @Override
    public void renderStatic(TileEntity tile, Tessellator tes, Matrix4 translationMatrix, Matrix4 rotationMatrix) {
        if (!(tile instanceof TEAnvil te)) {
            return;
        }
        translationMatrix.translate(.5, 0, .5);
        modelAnvil.render(tile, tes, translationMatrix, rotationMatrix, 1, false);
    }

    @Override
    public void renderItem(TileEntity tile, Tessellator tes, Matrix4 translationMatrix, Matrix4 rotationMatrix) {
        GL11.glPushMatrix();
        modelAnvil.renderItem();
        GL11.glPopMatrix();
    }

}
