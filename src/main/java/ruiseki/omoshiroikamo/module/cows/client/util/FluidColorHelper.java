package ruiseki.omoshiroikamo.module.cows.client.util;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.util.Logger;

/**
 * Helper to determine colors for fluids, with client-side texture analysis.
 */
public class FluidColorHelper {

    private static final Map<String, Integer> colorCache = new HashMap<>();

    /**
     * Gets a representative color for the given fluid.
     * If fluid.getColor() is white, it analyzes the fluid's still texture.
     */
    public static int getFluidColor(Fluid fluid) {
        if (fluid == null) return 0xFFFFFF;

        String name = fluid.getName();
        if (colorCache.containsKey(name)) {
            return colorCache.get(name);
        }

        int fluidColor = fluid.getColor();
        // If fluid color is not default white, use it
        if (fluidColor != 0xFFFFFF && fluidColor != 0xFFFFFFFF && fluidColor != 0 && fluidColor != -1) {
            colorCache.put(name, fluidColor);
            return fluidColor;
        }

        // Try to analyze texture IF we are on client side
        if (FMLCommonHandler.instance()
            .getSide() == Side.CLIENT) {
            try {
                int texColor = analyzeFluidTextureClient(fluid);
                if (texColor != -1) {
                    colorCache.put(name, texColor);
                    return texColor;
                }
            } catch (Throwable e) {
                Logger.error("Failed to analyze texture for fluid: " + name);
            }
        }

        return 0xFFFFFF; // Fallback gray
    }

    @SideOnly(Side.CLIENT)
    private static int analyzeFluidTextureClient(Fluid fluid) {
        IIcon icon = fluid.getStillIcon();
        if (icon == null && fluid.getBlock() != null) {
            icon = fluid.getBlock()
                .getIcon(0, 0);
        }

        if (icon == null) return -1;

        String iconName = icon.getIconName();
        if (iconName == null || iconName.isEmpty()) return -1;

        // TextureAtlasSprite (IIcon implementation) usually prefix with texture type
        String path = iconName;
        String domain = "minecraft";
        if (path.contains(":")) {
            String[] split = path.split(":");
            domain = split[0];
            path = split[1];
        }

        ResourceLocation loc = new ResourceLocation(domain, "textures/" + path + ".png");
        return getAverageColor(loc);
    }

    @SideOnly(Side.CLIENT)
    private static int getAverageColor(ResourceLocation loc) {
        try {
            IResource res = Minecraft.getMinecraft()
                .getResourceManager()
                .getResource(loc);
            if (res == null) return -1;

            try (java.io.InputStream is = res.getInputStream()) {
                BufferedImage image = ImageIO.read(is);
                if (image == null) return -1;

                long r = 0, g = 0, b = 0;
                int count = 0;

                int w = image.getWidth();
                int h = image.getHeight();

                // Sample every few pixels to be fast
                int step = Math.max(1, w / 16);

                for (int x = 0; x < w; x += step) {
                    for (int y = 0; y < h; y += step) {
                        int argb = image.getRGB(x, y);
                        // Skip transparent
                        if (((argb >> 24) & 0xFF) < 128) continue;

                        r += (argb >> 16) & 0xFF;
                        g += (argb >> 8) & 0xFF;
                        b += (argb) & 0xFF;
                        count++;
                    }
                }

                if (count == 0) return -1;

                return (int) ((r / count) << 16 | (g / count) << 8 | (b / count));
            }
        } catch (Exception e) {
            return -1;
        }
    }
}
