package ruiseki.omoshiroikamo.client.render.block.solarArray;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SolarCellISBRH implements ISimpleBlockRenderingHandler {

    public static int renderId;

    public SolarCellISBRH() {
        renderId = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

        Tessellator tess = Tessellator.instance;

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);

        tess.startDrawingQuads();
        renderer.setRenderBoundsFromBlock(block);

        // BOTTOM
        renderer.renderFaceYNeg(block, 0, 0, 0, block.getIcon(0, metadata));
        // TOP
        renderer.renderFaceYPos(block, 0, 0, 0, block.getIcon(1, metadata));
        // NORTH
        renderer.renderFaceZNeg(block, 0, 0, 0, block.getIcon(2, metadata));
        // SOUTH
        renderer.renderFaceZPos(block, 0, 0, 0, block.getIcon(3, metadata));
        // WEST
        renderer.renderFaceXNeg(block, 0, 0, 0, block.getIcon(4, metadata));
        // EAST
        renderer.renderFaceXPos(block, 0, 0, 0, block.getIcon(5, metadata));

        tess.draw();
        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        renderer.setRenderBoundsFromBlock(block);
        return renderer.renderStandardBlock(block, x, y, z);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return renderId;
    }
}
