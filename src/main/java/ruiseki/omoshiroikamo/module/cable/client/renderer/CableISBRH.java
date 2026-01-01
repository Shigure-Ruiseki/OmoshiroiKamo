package ruiseki.omoshiroikamo.module.cable.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizons.angelica.api.ThreadSafeISBRH;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.ICable;
import ruiseki.omoshiroikamo.api.block.ICableProps;
import ruiseki.omoshiroikamo.module.cable.common.block.BlockCable;
import ruiseki.omoshiroikamo.module.cable.common.block.TECable;

@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class CableISBRH implements ISimpleBlockRenderingHandler {

    public static final CableISBRH INSTANCE = new CableISBRH();

    public CableISBRH(float conduitScale) {}

    public CableISBRH() {}

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
                                    Block block, int modelId, RenderBlocks renderer) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable cable)) return false;

        float min = 6f / 16f;
        float max = 10f / 16f;

        // Core
        renderer.setRenderBounds(min, min, min, max, max, max);
        renderer.renderStandardBlock(block, x, y, z);

        // UP (+Y)
        if (cable.isConnected(ForgeDirection.UP)) {
            renderer.setRenderBounds(min, max, min, max, 1.0, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // DOWN (-Y)
        if (cable.isConnected(ForgeDirection.DOWN)) {
            renderer.setRenderBounds(min, 0.0, min, max, min, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // NORTH (-Z)
        if (cable.isConnected(ForgeDirection.NORTH)) {
            renderer.setRenderBounds(min, min, 0.0, max, max, min);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // SOUTH (+Z)
        if (cable.isConnected(ForgeDirection.SOUTH)) {
            renderer.setRenderBounds(min, min, max, max, max, 1.0);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // WEST (-X)
        if (cable.isConnected(ForgeDirection.WEST)) {
            renderer.setRenderBounds(0.0, min, min, min, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        // EAST (+X)
        if (cable.isConnected(ForgeDirection.EAST)) {
            renderer.setRenderBounds(max, min, min, 1.0, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

    @Override
    public int getRenderId() {
        return BlockCable.rendererId;
    }
}
