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
        ruiseki.omoshiroikamo.core.common.util.RenderUtils.renderCube(tess, 0, 0, 0, 1, 1, 1, baseIcon);
        tess.draw();

        GL11.glPopMatrix();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId,
        RenderBlocks renderer) {

        // Get tint color from cache or config
        Integer structureColor = StructureTintCache.get(world, x, y, z);
        int tintColor = structureColor != null ? structureColor : MachineryConfig.getDefaultTintColorInt();

        // Apply color to base block by setting renderer color override
        float r = ((tintColor >> 16) & 0xFF) / 255.0f;
        float g = ((tintColor >> 8) & 0xFF) / 255.0f;
        float b = (tintColor & 0xFF) / 255.0f;

        // Store original color and override
        renderer.setRenderAllFaces(false);

        // Render base block with color manually by setting tessellator color
        Tessellator t = Tessellator.instance;
        int baseBrightness = block.getMixedBrightnessForBlock(world, x, y, z);
        t.setBrightness(baseBrightness);

        // Render each face of base block with tint
        renderTintedBlock(world, x, y, z, block, renderer, r, g, b);

        // Get tile entity for overlay textures
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISidedTexture sided)) {
            return true;
        }

        // Render overlays for each direction
        for (int i = 0; i < 6; i++) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
            IIcon icon = sided.getTexture(dir, 1);
            if (icon == null) continue;

            // Check if adjacent block is opaque
            int ax = x + dir.offsetX;
            int ay = y + dir.offsetY;
            int az = z + dir.offsetZ;
            if (world.getBlock(ax, ay, az)
                .isOpaqueCube()) continue;

            // Set brightness and color
            t.setBrightness(baseBrightness);

            // Inline side shading for performance
            float shade;
            switch (i) {
                case 0:
                    shade = 0.5f;
                    break; // DOWN
                case 1:
                    shade = 1.0f;
                    break; // UP
                case 2:
                case 3:
                    shade = 0.8f;
                    break; // NORTH/SOUTH
                default:
                    shade = 0.6f;
                    break; // WEST/EAST
            }
            t.setColorOpaque_F(shade, shade, shade);

            // Inline face rendering for performance
            float u0 = icon.getMinU();
            float u1 = icon.getMaxU();
            float v0 = icon.getMinV();
            float v1 = icon.getMaxV();

            switch (dir) {
                case DOWN:
                    t.addVertexWithUV(x, y - EPS, z, u0, v0);
                    t.addVertexWithUV(x + 1, y - EPS, z, u1, v0);
                    t.addVertexWithUV(x + 1, y - EPS, z + 1, u1, v1);
                    t.addVertexWithUV(x, y - EPS, z + 1, u0, v1);
                    break;
                case UP:
                    t.addVertexWithUV(x, y + 1 + EPS, z + 1, u0, v1);
                    t.addVertexWithUV(x + 1, y + 1 + EPS, z + 1, u1, v1);
                    t.addVertexWithUV(x + 1, y + 1 + EPS, z, u1, v0);
                    t.addVertexWithUV(x, y + 1 + EPS, z, u0, v0);
                    break;
                case NORTH:
                    t.addVertexWithUV(x + 1, y, z - EPS, u1, v1);
                    t.addVertexWithUV(x, y, z - EPS, u0, v1);
                    t.addVertexWithUV(x, y + 1, z - EPS, u0, v0);
                    t.addVertexWithUV(x + 1, y + 1, z - EPS, u1, v0);
                    break;
                case SOUTH:
                    t.addVertexWithUV(x, y, z + 1 + EPS, u0, v1);
                    t.addVertexWithUV(x + 1, y, z + 1 + EPS, u1, v1);
                    t.addVertexWithUV(x + 1, y + 1, z + 1 + EPS, u1, v0);
                    t.addVertexWithUV(x, y + 1, z + 1 + EPS, u0, v0);
                    break;
                case WEST:
                    t.addVertexWithUV(x - EPS, y, z, u0, v1);
                    t.addVertexWithUV(x - EPS, y, z + 1, u1, v1);
                    t.addVertexWithUV(x - EPS, y + 1, z + 1, u1, v0);
                    t.addVertexWithUV(x - EPS, y + 1, z, u0, v0);
                    break;
                case EAST:
                    t.addVertexWithUV(x + 1 + EPS, y, z + 1, u1, v1);
                    t.addVertexWithUV(x + 1 + EPS, y, z, u0, v1);
                    t.addVertexWithUV(x + 1 + EPS, y + 1, z, u0, v0);
                    t.addVertexWithUV(x + 1 + EPS, y + 1, z + 1, u1, v0);
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    /**
     * Render base block with tint color applied.
     * Uses the same vertex order as overlay rendering for consistency.
     */
    private void renderTintedBlock(IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, float r,
        float g, float b) {
        Tessellator t = Tessellator.instance;
        int brightness = block.getMixedBrightnessForBlock(world, x, y, z);

        // Get TileEntity to check IO state
        TileEntity te = world.getTileEntity(x, y, z);
        ISidedIO ioConfig = te instanceof ISidedIO ? (ISidedIO) te : null;

        IIcon portBaseIcon = AbstractPortBlock.baseIcon != null ? AbstractPortBlock.baseIcon : block.getIcon(0, 0);
        IIcon casingIcon = MachineryBlocks.MACHINE_CASING.getBlock()
            .getIcon(0, 0);

        // Helper to get icon for a specific face
        // If IO is NONE (Disabled), use Casing texture. Otherwise use Port Base
        // texture.
        // If TE is not ISidedIO (shouldn't happen for ports), fallback to Port Base.
        IIcon iconDown = (ioConfig != null && ioConfig.getSideIO(ForgeDirection.DOWN) == EnumIO.NONE) ? casingIcon
            : portBaseIcon;
        IIcon iconUp = (ioConfig != null && ioConfig.getSideIO(ForgeDirection.UP) == EnumIO.NONE) ? casingIcon
            : portBaseIcon;
        IIcon iconNorth = (ioConfig != null && ioConfig.getSideIO(ForgeDirection.NORTH) == EnumIO.NONE) ? casingIcon
            : portBaseIcon;
        IIcon iconSouth = (ioConfig != null && ioConfig.getSideIO(ForgeDirection.SOUTH) == EnumIO.NONE) ? casingIcon
            : portBaseIcon;
        IIcon iconWest = (ioConfig != null && ioConfig.getSideIO(ForgeDirection.WEST) == EnumIO.NONE) ? casingIcon
            : portBaseIcon;
        IIcon iconEast = (ioConfig != null && ioConfig.getSideIO(ForgeDirection.EAST) == EnumIO.NONE) ? casingIcon
            : portBaseIcon;

        // Render each face with appropriate shading (same order as overlays) and
        // specific icon
        // DOWN face
        if (!world.getBlock(x, y - 1, z)
            .isOpaqueCube()) {
            t.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
            t.setColorOpaque_F(r * 0.5f, g * 0.5f, b * 0.5f);
            t.addVertexWithUV(x, y, z + 1, iconDown.getMinU(), iconDown.getMaxV());
            t.addVertexWithUV(x, y, z, iconDown.getMinU(), iconDown.getMinV());
            t.addVertexWithUV(x + 1, y, z, iconDown.getMaxU(), iconDown.getMinV());
            t.addVertexWithUV(x + 1, y, z + 1, iconDown.getMaxU(), iconDown.getMaxV());
        }

        // UP face
        if (!world.getBlock(x, y + 1, z)
            .isOpaqueCube()) {
            t.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z));
            t.setColorOpaque_F(r * 1.0f, g * 1.0f, b * 1.0f);
            t.addVertexWithUV(x, y + 1, z, iconUp.getMinU(), iconUp.getMinV());
            t.addVertexWithUV(x, y + 1, z + 1, iconUp.getMinU(), iconUp.getMaxV());
            t.addVertexWithUV(x + 1, y + 1, z + 1, iconUp.getMaxU(), iconUp.getMaxV());
            t.addVertexWithUV(x + 1, y + 1, z, iconUp.getMaxU(), iconUp.getMinV());
        }

        // NORTH face
        if (!world.getBlock(x, y, z - 1)
            .isOpaqueCube()) {
            t.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z - 1));
            t.setColorOpaque_F(r * 0.8f, g * 0.8f, b * 0.8f);
            t.addVertexWithUV(x, y, z, iconNorth.getMaxU(), iconNorth.getMaxV());
            t.addVertexWithUV(x, y + 1, z, iconNorth.getMaxU(), iconNorth.getMinV());
            t.addVertexWithUV(x + 1, y + 1, z, iconNorth.getMinU(), iconNorth.getMinV());
            t.addVertexWithUV(x + 1, y, z, iconNorth.getMinU(), iconNorth.getMaxV());
        }

        // SOUTH face
        if (!world.getBlock(x, y, z + 1)
            .isOpaqueCube()) {
            t.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z + 1));
            t.setColorOpaque_F(r * 0.8f, g * 0.8f, b * 0.8f);
            t.addVertexWithUV(x + 1, y, z + 1, iconSouth.getMaxU(), iconSouth.getMaxV());
            t.addVertexWithUV(x + 1, y + 1, z + 1, iconSouth.getMaxU(), iconSouth.getMinV());
            t.addVertexWithUV(x, y + 1, z + 1, iconSouth.getMinU(), iconSouth.getMinV());
            t.addVertexWithUV(x, y, z + 1, iconSouth.getMinU(), iconSouth.getMaxV());
        }

        // WEST face
        if (!world.getBlock(x - 1, y, z)
            .isOpaqueCube()) {
            t.setBrightness(block.getMixedBrightnessForBlock(world, x - 1, y, z));
            t.setColorOpaque_F(r * 0.6f, g * 0.6f, b * 0.6f);
            t.addVertexWithUV(x, y, z + 1, iconWest.getMaxU(), iconWest.getMaxV());
            t.addVertexWithUV(x, y + 1, z + 1, iconWest.getMaxU(), iconWest.getMinV());
            t.addVertexWithUV(x, y + 1, z, iconWest.getMinU(), iconWest.getMinV());
            t.addVertexWithUV(x, y, z, iconWest.getMinU(), iconWest.getMaxV());
        }

        // EAST face
        if (!world.getBlock(x + 1, y, z)
            .isOpaqueCube()) {
            t.setBrightness(block.getMixedBrightnessForBlock(world, x + 1, y, z));
            t.setColorOpaque_F(r * 0.6f, g * 0.6f, b * 0.6f);
            t.addVertexWithUV(x + 1, y, z, iconEast.getMinU(), iconEast.getMaxV());
            t.addVertexWithUV(x + 1, y + 1, z, iconEast.getMinU(), iconEast.getMinV());
            t.addVertexWithUV(x + 1, y + 1, z + 1, iconEast.getMaxU(), iconEast.getMinV());
            t.addVertexWithUV(x + 1, y, z + 1, iconEast.getMaxU(), iconEast.getMaxV());
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
