package ruiseki.omoshiroikamo.module.cable.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.cable.common.cable.BlockCable;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class CableISBRH implements ISimpleBlockRenderingHandler {

    public static final CableISBRH INSTANCE = new CableISBRH();

    private static final float PART_INSET = 3f / 16f;

    public CableISBRH() {}

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tess = TessellatorManager.get();
        IIcon icon = block.getIcon(0, 0);

        float min = 6f / 16f;
        float max = 10f / 16f;
        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        tess.startDrawingQuads();
        RenderUtils.renderCube(tess, min, min, min, max, max, max, icon);
        RenderUtils.renderCube(tess, min, min, 0f, max, max, min, icon);
        RenderUtils.renderCube(tess, min, min, max, max, max, 1.0f, icon);

        tess.draw();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable cable)) return false;

        float min = 6f / 16f + 0.001f;
        float max = 10f / 16f - 0.001f;

        // Core
        renderer.setRenderBounds(min, min, min, max, max, max);
        renderer.renderStandardBlock(block, x, y, z);

        // UP (+Y)
        if (cable.hasVisualConnection(ForgeDirection.UP)) {
            boolean hasPart = cable.hasPart(ForgeDirection.UP);
            renderer.setRenderBounds(min, max, min, max, hasPart ? 1.0f - PART_INSET : 1.0f, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // DOWN (-Y)
        if (cable.hasVisualConnection(ForgeDirection.DOWN)) {
            boolean hasPart = cable.hasPart(ForgeDirection.DOWN);
            renderer.setRenderBounds(min, hasPart ? PART_INSET : 0f, min, max, min, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // NORTH (-Z)
        if (cable.hasVisualConnection(ForgeDirection.NORTH)) {
            boolean hasPart = cable.hasPart(ForgeDirection.NORTH);
            renderer.setRenderBounds(min, min, hasPart ? PART_INSET : 0f, max, max, min);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // SOUTH (+Z)
        if (cable.hasVisualConnection(ForgeDirection.SOUTH)) {
            boolean hasPart = cable.hasPart(ForgeDirection.SOUTH);
            renderer.setRenderBounds(min, min, max, max, max, hasPart ? 1.0f - PART_INSET : 1.0f);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // WEST (-X)
        if (cable.hasVisualConnection(ForgeDirection.WEST)) {
            boolean hasPart = cable.hasPart(ForgeDirection.WEST);
            renderer.setRenderBounds(hasPart ? PART_INSET : 0f, min, min, min, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // EAST (+X)
        if (cable.hasVisualConnection(ForgeDirection.EAST)) {
            boolean hasPart = cable.hasPart(ForgeDirection.EAST);
            renderer.setRenderBounds(max, min, min, hasPart ? 1.0f - PART_INSET : 1.0f, max, max);
            renderer.renderStandardBlock(block, x, y, z);
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
