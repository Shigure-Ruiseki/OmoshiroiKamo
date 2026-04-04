package ruiseki.omoshiroikamo.core.client.render.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.gtnewhorizon.gtnhlib.color.RGBColor;
import com.gtnewhorizon.gtnhlib.itemrendering.IItemTexture;
import com.gtnewhorizon.gtnhlib.itemrendering.ItemRenderUtils;

/**
 * Renders a block-atlas texture (e.g., fluid still icon) clipped to the shape
 * of a mask icon from the item atlas (e.g., canister_fluid.png).
 *
 * Uses the depth buffer to mask the fluid texture, following the same approach
 * as ThermalExpansion's RenderUtils.renderMask. No stencil buffer required.
 *
 * Algorithm:
 * 1. Render maskIcon (item atlas) normally — alpha test ensures only opaque
 * pixels write to the depth buffer at z=X.
 * 2. Switch to GL_DEPTH_FUNC=GL_EQUAL so the fluid texture only passes the
 * depth test where the mask was opaque (depth == X).
 * 3. Render fluidIcon (block atlas) with tint color.
 * 4. Restore depth state.
 */
@SideOnly(Side.CLIENT)
public class MaskedBlockItemTexture implements IItemTexture {

    private final IIcon maskIcon;
    private final IIcon fluidIcon;
    private final RGBColor tint;

    public MaskedBlockItemTexture(IIcon maskIcon, IIcon fluidIcon, RGBColor tint) {
        this.maskIcon = maskIcon;
        this.fluidIcon = fluidIcon;
        this.tint = tint;
    }

    @Override
    public void render(IItemRenderer.ItemRenderType type, ItemStack stack) {
        if (maskIcon == null || fluidIcon == null) return;

        Minecraft mc = Minecraft.getMinecraft();

        // Pass 1: render mask into depth buffer.
        // Alpha test (already enabled by TexturedItemRenderer) ensures transparent
        // pixels do not write to depth, so only the mask shape is recorded.
        GL11.glDisable(GL11.GL_CULL_FACE);
        mc.getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);
        RGBColor.WHITE.makeActive();
        ItemRenderUtils.renderItem(type, maskIcon);

        // Pass 2: render fluid texture only where depth == mask depth.
        GL11.glDepthFunc(GL11.GL_EQUAL);
        GL11.glDepthMask(false);

        mc.getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        tint.makeActive();
        ItemRenderUtils.renderItem(type, fluidIcon);

        // Restore state.
        GL11.glDepthMask(true);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager()
            .bindTexture(TextureMap.locationItemsTexture);
    }
}
