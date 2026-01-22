package ruiseki.omoshiroikamo.module.machinery.client.render;

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
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.ISidedTexture;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.common.util.RenderUtils;
import ruiseki.omoshiroikamo.module.machinery.common.block.AbstractPortBlock;
import ruiseki.omoshiroikamo.module.machinery.common.init.MachineryBlocks;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

/**
 * ISBRH for rendering port overlays.
 * Optimized for performance - only renders when chunk is rebuilt.
 */
@SideOnly(Side.CLIENT)
@ThreadSafeISBRH(perThread = false)
public class PortOverlayISBRH implements ISimpleBlockRenderingHandler {

    public static final PortOverlayISBRH INSTANCE = new PortOverlayISBRH();

    private static final float EPS = 0.003f;

    private PortOverlayISBRH() {}

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        Tessellator tess = TessellatorManager.get();
        IIcon baseIcon = AbstractPortBlock.baseIcon;
        if (baseIcon == null) {
            baseIcon = block.getIcon(0, metadata);
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);

        // Enable proper lighting for inventory
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_LIGHT1);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);

        // Apply config color for inventory rendering
        int invColor = MachineryConfig.getDefaultTintColorInt();
        float r = ((invColor >> 16) & 0xFF) / 255.0f;
        float g = ((invColor >> 8) & 0xFF) / 255.0f;
        float b = (invColor & 0xFF) / 255.0f;

        tess.startDrawingQuads();
        tess.setColorOpaque_F(r, g, b);
        tess.setNormal(0.0F, 1.0F, 0.0F);
        RenderUtils.renderCube(tess, 0, 0, 0, 1, 1, 1, baseIcon);
        tess.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Get tint color from cache or config
        Integer structureColor = StructureTintCache.get(world, x, y, z);
        int tintColor = structureColor != null ? structureColor : MachineryConfig.getDefaultTintColorInt();

        // Calculate tint components
        float r = ((tintColor >> 16) & 0xFF) / 255.0f;
        float g = ((tintColor >> 8) & 0xFF) / 255.0f;
        float b = (tintColor & 0xFF) / 255.0f;

        // Ensure not black (fallback if calculation failed)
        if (r == 0 && g == 0 && b == 0) {
            r = g = b = 1.0f;
        }

        // Get TileEntity for textures and IO state
        TileEntity te = world.getTileEntity(x, y, z);
        ISidedIO ioConfig = te instanceof ISidedIO ? (ISidedIO) te : null;
        ISidedTexture sidedTexture = te instanceof ISidedTexture ? (ISidedTexture) te : null;

        // Prepare base icons
        IIcon portBaseIcon = AbstractPortBlock.baseIcon != null ? AbstractPortBlock.baseIcon : block.getIcon(0, 0);
        IIcon casingIcon = MachineryBlocks.MACHINE_CASING.getBlock()
            .getIcon(0, 0);

        Tessellator t = Tessellator.instance;
        t.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));

        // Iterate faces and render using RenderBlocks for Base, Manual for Overlay
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];

            // Render Base with RenderBlocks (Fixes lighting/AO/Shading)
            // Select icon
            IIcon baseIcon = portBaseIcon;
            if (ioConfig != null && ioConfig.getSideIO(dir) == EnumIO.NONE) {
                baseIcon = casingIcon;
            }

            // Verify neighbor opacity to prevent rendering internal faces
            int ax = x + dir.offsetX;
            int ay = y + dir.offsetY;
            int az = z + dir.offsetZ;
            // Optimally skipping opaque faces
            if (world.getBlock(ax, ay, az)
                .isOpaqueCube()) {
                continue;
            }

            // Apply Mesh Tint Color
            // RenderBlocks usually resets color, so we set it immediately before renderFace
            t.setColorOpaque_F(r, g, b);

            renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
            renderer.setOverrideBlockTexture(baseIcon);
            switch (dir) {
                case DOWN:
                    renderer.renderFaceYNeg(block, x, y, z, baseIcon);
                    break;
                case UP:
                    renderer.renderFaceYPos(block, x, y, z, baseIcon);
                    break;
                case NORTH:
                    renderer.renderFaceZNeg(block, x, y, z, baseIcon);
                    break;
                case SOUTH:
                    renderer.renderFaceZPos(block, x, y, z, baseIcon);
                    break;
                case WEST:
                    renderer.renderFaceXNeg(block, x, y, z, baseIcon);
                    break;
                case EAST:
                    renderer.renderFaceXPos(block, x, y, z, baseIcon);
                    break;
                default:
                    break;
            }
            renderer.clearOverrideBlockTexture();

            // Render Overlay (Manual with Offset)
            if (sidedTexture != null) {
                IIcon overlayIcon = sidedTexture.getTexture(dir, 1);
                if (overlayIcon != null) {
                    // Reset brightness for overlay (manual render)
                    // We must calculate brightness manually or use the base block's brightness
                    // Using neighbor brightness prevents dark overlay on lit faces
                    int neighborBrightness = world.getLightBrightnessForSkyBlocks(ax, ay, az, 0);
                    t.setBrightness(neighborBrightness);

                    // Set color to white (untinted) + Shade
                    float shade;
                    switch (dir) {
                        case DOWN:
                            shade = 0.5f;
                            break;
                        case UP:
                            shade = 1.0f;
                            break;
                        case NORTH:
                        case SOUTH:
                            shade = 0.8f;
                            break;
                        default:
                            shade = 0.6f;
                            break;
                    }
                    t.setColorOpaque_F(shade, shade, shade);
                    t.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ); // Ensure normal for shaders

                    renderFace(t, dir, x, y, z, overlayIcon, EPS);
                }
            }
        }

        return true;
    }

    /**
     * Helper to render a single face with given icon and offset.
     */
    private void renderFace(Tessellator t, ForgeDirection dir, double x, double y, double z, IIcon icon, float offset) {
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        // Apply offset based on direction to expand/contract the face
        // Use offset for overlay render (EPS)
        double eps = offset;

        // Set normal to ensure correct lighting calculation
        t.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);

        switch (dir) {
            case DOWN:
                t.addVertexWithUV(x, y - eps, z, minU, minV);
                t.addVertexWithUV(x + 1, y - eps, z, maxU, minV);
                t.addVertexWithUV(x + 1, y - eps, z + 1, maxU, maxV);
                t.addVertexWithUV(x, y - eps, z + 1, minU, maxV);
                break;
            case UP:
                t.addVertexWithUV(x, y + 1 + eps, z + 1, minU, maxV);
                t.addVertexWithUV(x + 1, y + 1 + eps, z + 1, maxU, maxV);
                t.addVertexWithUV(x + 1, y + 1 + eps, z, maxU, minV);
                t.addVertexWithUV(x, y + 1 + eps, z, minU, minV);
                break;
            case NORTH:
                t.addVertexWithUV(x + 1, y, z - eps, maxU, maxV);
                t.addVertexWithUV(x, y, z - eps, minU, maxV);
                t.addVertexWithUV(x, y + 1, z - eps, minU, minV);
                t.addVertexWithUV(x + 1, y + 1, z - eps, maxU, minV);
                break;
            case SOUTH:
                t.addVertexWithUV(x, y, z + 1 + eps, minU, maxV);
                t.addVertexWithUV(x + 1, y, z + 1 + eps, maxU, maxV);
                t.addVertexWithUV(x + 1, y + 1, z + 1 + eps, maxU, minV);
                t.addVertexWithUV(x, y + 1, z + 1 + eps, minU, minV);
                break;
            case WEST:
                t.addVertexWithUV(x - eps, y, z, minU, maxV);
                t.addVertexWithUV(x - eps, y, z + 1, maxU, maxV);
                t.addVertexWithUV(x - eps, y + 1, z + 1, maxU, minV);
                t.addVertexWithUV(x - eps, y + 1, z, minU, minV);
                break;
            case EAST:
                t.addVertexWithUV(x + 1 + eps, y, z + 1, maxU, maxV);
                t.addVertexWithUV(x + 1 + eps, y, z, minU, maxV);
                t.addVertexWithUV(x + 1 + eps, y + 1, z, minU, minV);
                t.addVertexWithUV(x + 1 + eps, y + 1, z + 1, maxU, minV);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return AbstractPortBlock.portRendererId;
    }
}
