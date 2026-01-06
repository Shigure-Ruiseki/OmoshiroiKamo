package ruiseki.omoshiroikamo.module.cable.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.cable.common.cable.BlockCable;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class CableISBRH implements ISimpleBlockRenderingHandler {

    public static final CableISBRH INSTANCE = new CableISBRH();

    public CableISBRH() {}

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tess = Tessellator.instance;
        IIcon icon = block.getIcon(0, 0);

        float min = 6f / 16f;
        float max = 10f / 16f;
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        RenderUtils.renderCube(tess, min, min, min, max, max, max, icon);
        RenderUtils.renderCube(tess, min, min, 0.0f, max, max, min, icon);
        RenderUtils.renderCube(tess, min, min, max, max, max, 1.0f, icon);
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable cable)) return false;

        float min = 6f / 16f;
        float max = 10f / 16f;

        // Core
        renderer.setRenderBounds(min, min, min, max, max, max);
        renderer.renderStandardBlock(block, x, y, z);

        // UP (+Y)
        if (cable.hasVisualConnection(ForgeDirection.UP)) {
            renderer.setRenderBounds(min, max, min, max, 1.0, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // DOWN (-Y)
        if (cable.hasVisualConnection(ForgeDirection.DOWN)) {
            renderer.setRenderBounds(min, 0.0, min, max, min, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // NORTH (-Z)
        if (cable.hasVisualConnection(ForgeDirection.NORTH)) {
            renderer.setRenderBounds(min, min, 0.0, max, max, min);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // SOUTH (+Z)
        if (cable.hasVisualConnection(ForgeDirection.SOUTH)) {
            renderer.setRenderBounds(min, min, max, max, max, 1.0);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // WEST (-X)
        if (cable.hasVisualConnection(ForgeDirection.WEST)) {
            renderer.setRenderBounds(0.0, min, min, min, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // EAST (+X)
        if (cable.hasVisualConnection(ForgeDirection.EAST)) {
            renderer.setRenderBounds(max, min, min, 1.0, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // PARTS
        for (ICablePart part : cable.getParts()) {
            AxisAlignedBB bb = part.getCollisionBox();
            if (bb == null) continue;

            IIcon icon = part.getIcon();

            renderer.setOverrideBlockTexture(icon);
            renderer.setRenderBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.clearOverrideBlockTexture();
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return BlockCable.rendererId;
    }
}
