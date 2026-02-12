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
import com.gtnewhorizon.structurelib.alignment.enumerable.Rotation;
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
import ruiseki.omoshiroikamo.module.machinery.common.block.BlockMachineController;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

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

        // Render Controller Overlay if applicable
        if (block instanceof BlockMachineController) {
            BlockMachineController controllerBlock = (BlockMachineController) block;
            IIcon overlayIcon = controllerBlock.getOverlayIcon();
            if (overlayIcon != null) {
                tess.startDrawingQuads();
                tess.setColorOpaque_F(1.0f, 1.0f, 1.0f); // White (untinted)
                tess.setNormal(0.0F, 1.0F, 0.0F);
                // Render slightly larger to prevent z-fighting in inventory
                RenderUtils.renderCube(tess, -0.001, -0.001, -0.001, 1.002, 1.002, 1.002, overlayIcon);
                tess.draw();
            }
        }

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

        // Prepare base icon - use port base for all faces
        // (Disabled faces will simply not have an overlay rendered)
        // IIcon portBaseIcon = AbstractPortBlock.baseIcon != null ?
        // AbstractPortBlock.baseIcon : block.getIcon(0, 0);

        // Render base block with proper AO and lighting using
        // renderStandardBlockWithColorMultiplier
        // This handles all lighting, AO, and face culling automatically
        renderer.setRenderBounds(0, 0, 0, 1, 1, 1);

        // CRITICAL: Enable renderAllFaces to prevent face culling issues
        // Without this, faces may not render if block returns
        // shouldSideBeRendered=false
        boolean prevRenderAllFaces = renderer.renderAllFaces;
        renderer.renderAllFaces = true;

        // Render all faces with port base icon
        // Uses block.getIcon(world, x,y,z, side) which handles casing switch
        renderer.renderStandardBlockWithColorMultiplier(block, x, y, z, r, g, b);

        // Render Overlays (Manual with Offset) on top of base
        Tessellator t = Tessellator.instance;
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];

            // Skip overlay for disabled faces
            if (ioConfig != null && ioConfig.getSideIO(dir) == EnumIO.NONE) {
                continue;
            }

            // Allow disabled overlay to render (let TE decide texture via ISidedTexture)

            // Check neighbor opacity to prevent rendering internal overlays
            int ax = x + dir.offsetX;
            int ay = y + dir.offsetY;
            int az = z + dir.offsetZ;
            if (world.getBlock(ax, ay, az)
                .isOpaqueCube()) {
                continue;
            }

            if (sidedTexture != null) {
                IIcon overlayIcon = sidedTexture.getTexture(dir, 1);
                if (overlayIcon != null) {
                    // Use neighbor brightness for overlay
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
                    t.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);

                    // Determine rotation
                    Rotation rotation = Rotation.NORMAL;
                    if (te instanceof TEMachineController) {
                        rotation = ((TEMachineController) te).getExtendedFacing()
                            .getRotation();
                    }

                    renderFace(t, dir, x, y, z, overlayIcon, EPS, rotation);
                }
            }
        }

        // Restore flags
        renderer.renderAllFaces = prevRenderAllFaces;

        return true;
    }

    /**
     * Helper to render a single face with given icon and offset.
     */
    private void renderFace(Tessellator t, ForgeDirection dir, double x, double y, double z, IIcon icon, float offset,
        Rotation rotation) {
        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        // Apply offset based on direction to expand/contract the face
        // Use offset for overlay render (EPS)
        double eps = offset;

        // Set normal to ensure correct lighting calculation
        t.setNormal(dir.offsetX, dir.offsetY, dir.offsetZ);

        float[] u = new float[4];
        float[] v = new float[4];

        switch (rotation) {
            case CLOCKWISE:
                u[0] = minU;
                v[0] = maxV;
                u[1] = minU;
                v[1] = minV;
                u[2] = maxU;
                v[2] = minV;
                u[3] = maxU;
                v[3] = maxV;
                break;
            case UPSIDE_DOWN:
                u[0] = maxU;
                v[0] = maxV;
                u[1] = minU;
                v[1] = maxV;
                u[2] = minU;
                v[2] = minV;
                u[3] = maxU;
                v[3] = minV;
                break;
            case COUNTER_CLOCKWISE:
                u[0] = maxU;
                v[0] = minV;
                u[1] = maxU;
                v[1] = maxV;
                u[2] = minU;
                v[2] = maxV;
                u[3] = minU;
                v[3] = minV;
                break;
            default:
                u[0] = minU;
                v[0] = minV;
                u[1] = maxU;
                v[1] = minV;
                u[2] = maxU;
                v[2] = maxV;
                u[3] = minU;
                v[3] = maxV;
                break;
        }

        switch (dir) {
            case DOWN:
                t.addVertexWithUV(x, y - eps, z, u[0], v[0]);
                t.addVertexWithUV(x + 1, y - eps, z, u[1], v[1]);
                t.addVertexWithUV(x + 1, y - eps, z + 1, u[2], v[2]);
                t.addVertexWithUV(x, y - eps, z + 1, u[3], v[3]);
                break;
            case UP:
                t.addVertexWithUV(x, y + 1 + eps, z + 1, u[3], v[3]);
                t.addVertexWithUV(x + 1, y + 1 + eps, z + 1, u[2], v[2]);
                t.addVertexWithUV(x + 1, y + 1 + eps, z, u[1], v[1]);
                t.addVertexWithUV(x, y + 1 + eps, z, u[0], v[0]);
                break;
            case NORTH:
                t.addVertexWithUV(x + 1, y, z - eps, u[2], v[2]);
                t.addVertexWithUV(x, y, z - eps, u[3], v[3]);
                t.addVertexWithUV(x, y + 1, z - eps, u[0], v[0]);
                t.addVertexWithUV(x + 1, y + 1, z - eps, u[1], v[1]);
                break;
            case SOUTH:
                t.addVertexWithUV(x, y, z + 1 + eps, u[3], v[3]);
                t.addVertexWithUV(x + 1, y, z + 1 + eps, u[2], v[2]);
                t.addVertexWithUV(x + 1, y + 1, z + 1 + eps, u[1], v[1]);
                t.addVertexWithUV(x, y + 1, z + 1 + eps, u[0], v[0]);
                break;
            case WEST:
                t.addVertexWithUV(x - eps, y, z, u[3], v[3]);
                t.addVertexWithUV(x - eps, y, z + 1, u[2], v[2]);
                t.addVertexWithUV(x - eps, y + 1, z + 1, u[1], v[1]);
                t.addVertexWithUV(x - eps, y + 1, z, u[0], v[0]);
                break;
            case EAST:
                t.addVertexWithUV(x + 1 + eps, y, z + 1, u[2], v[2]);
                t.addVertexWithUV(x + 1 + eps, y, z, u[3], v[3]);
                t.addVertexWithUV(x + 1 + eps, y + 1, z, u[0], v[0]);
                t.addVertexWithUV(x + 1 + eps, y + 1, z + 1, u[1], v[1]);
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
