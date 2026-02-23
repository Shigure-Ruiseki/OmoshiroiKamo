package ruiseki.omoshiroikamo.module.ids.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.core.client.render.BaseBlockRender;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.BlockCable;
import ruiseki.omoshiroikamo.module.ids.common.block.cable.TECable;

public class RenderCable extends BaseBlockRender<BlockCable, TECable> {

    private static final float PART_INSET = 3f / 16f;

    public RenderCable() {
        super(true, 30);
    }

    @Override
    public boolean renderInWorld(BlockCable block, IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable cable)) return false;

        float min = 6f / 16f + 0.001f;
        float max = 10f / 16f - 0.001f;

        // Core
        if (cable.hasCore()) {
            renderer.setRenderBounds(min, min, min, max, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        renderArm(block, renderer, world, x, y, z, cable, ForgeDirection.UP, min, max);
        renderArm(block, renderer, world, x, y, z, cable, ForgeDirection.DOWN, min, max);
        renderArm(block, renderer, world, x, y, z, cable, ForgeDirection.NORTH, min, max);
        renderArm(block, renderer, world, x, y, z, cable, ForgeDirection.SOUTH, min, max);
        renderArm(block, renderer, world, x, y, z, cable, ForgeDirection.WEST, min, max);
        renderArm(block, renderer, world, x, y, z, cable, ForgeDirection.EAST, min, max);

        return true;
    }

    private void renderArm(Block block, RenderBlocks renderer, IBlockAccess world, int x, int y, int z, ICable cable,
        ForgeDirection dir, float min, float max) {

        if (!cable.hasVisualConnection(dir)) return;

        boolean hasPart = cable.hasPart(dir);

        switch (dir) {
            case UP -> renderer.setRenderBounds(min, max, min, max, hasPart ? 1.0f - PART_INSET : 1.0f, max);

            case DOWN -> renderer.setRenderBounds(min, hasPart ? PART_INSET : 0f, min, max, min, max);

            case NORTH -> renderer.setRenderBounds(min, min, hasPart ? PART_INSET : 0f, max, max, min);

            case SOUTH -> renderer.setRenderBounds(min, min, max, max, max, hasPart ? 1.0f - PART_INSET : 1.0f);

            case WEST -> renderer.setRenderBounds(hasPart ? PART_INSET : 0f, min, min, min, max, max);

            case EAST -> renderer.setRenderBounds(max, min, min, hasPart ? 1.0f - PART_INSET : 1.0f, max, max);
        }

        renderer.renderStandardBlock(block, x, y, z);
    }

    @Override
    public void renderInventory(final BlockCable blk, final ItemStack is, final RenderBlocks renderer,
        final IItemRenderer.ItemRenderType type, final Object[] obj) {
        renderer.setRenderBounds(6f / 16f, 6f / 16f, 6f / 16f, 10f / 16f, 10f / 16f, 10f / 16f);
        super.renderInventory(blk, is, renderer, type, obj);
    }

    @Override
    public void renderTile(BlockCable block, TECable tile, Tessellator tess, double x, double y, double z, float f, RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);
        for (ICablePart part : tile.getParts()) {
            part.renderPart(tess, f);
        }
        GL11.glPopMatrix();
    }
}
