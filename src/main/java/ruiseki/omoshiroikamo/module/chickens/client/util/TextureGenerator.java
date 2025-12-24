package ruiseki.omoshiroikamo.module.chickens.client.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class TextureGenerator {

    public static void generateCustomChickenTextures() {
        for (DataChicken data : DataChicken.getAllChickens()) {
            ChickensRegistryItem item = data.getItem();
            if (item.getTextureOverlay() != null) {
                ResourceLocation baseLoc = item.getTexture();
                ResourceLocation overlayLoc = item.getTextureOverlay();
                int tintColor = item.getTintColor();

                try {
                    ResourceLocation generated = combineTextures(baseLoc, overlayLoc, tintColor, item.getEntityName());
                    if (generated != null) {
                        // Update the registry item to use the generated texture
                        item.setTexture(generated);
                        // Nullify overlay so renderer uses single pass
                        item.setTextureOverlay(null);
                        Logger.info("Generated dynamic texture for {}", item.getEntityName());
                    }
                } catch (Exception e) {
                    Logger.error("Failed to generate texture for {}: {}", item.getEntityName(), e);
                    e.printStackTrace();
                }
            }
        }
    }

    private static ResourceLocation combineTextures(ResourceLocation base, ResourceLocation overlay, int tint,
        String name) throws IOException {
        BufferedImage baseImage = readTexture(base);
        BufferedImage overlayImage = readTexture(overlay);

        if (baseImage == null || overlayImage == null) return null;

        if (baseImage.getWidth() != overlayImage.getWidth() || baseImage.getHeight() != overlayImage.getHeight()) {
            Logger.warn("Texture dimensions mismatch for {}", name);
        }

        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Tint the Base Image (basePixelsRaw)
        int[] basePixelsRaw = baseImage.getRGB(0, 0, width, height, null, 0, width);
        int rTint = (tint >> 16) & 0xFF;
        int gTint = (tint >> 8) & 0xFF;
        int bTint = (tint) & 0xFF;

        for (int i = 0; i < basePixelsRaw.length; i++) {
            int p = basePixelsRaw[i];
            int a = (p >> 24) & 0xFF;
            if (a == 0) continue;

            int r = (p >> 16) & 0xFF;
            int g = (p >> 8) & 0xFF;
            int b = (p) & 0xFF;

            r = (r * rTint) / 255;
            g = (g * gTint) / 255;
            b = (b * bTint) / 255;

            basePixelsRaw[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        BufferedImage tintedBase = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        tintedBase.setRGB(0, 0, width, height, basePixelsRaw, 0, width);

        // Draw Tinted Base + Untinted Overlay
        java.awt.Graphics2D g2 = combined.createGraphics();
        g2.drawImage(tintedBase, 0, 0, null);
        g2.drawImage(overlayImage, 0, 0, null);
        g2.dispose();

        // Convert to DynamicTexture
        DynamicTexture dynTex = new DynamicTexture(combined);
        ResourceLocation dynLoc = Minecraft.getMinecraft()
            .getTextureManager()
            .getDynamicTextureLocation(LibMisc.MOD_ID + "_generated_" + name, dynTex);
        return dynLoc;
    }

    private static BufferedImage readTexture(ResourceLocation loc) throws IOException {
        IResource res = Minecraft.getMinecraft()
            .getResourceManager()
            .getResource(loc);
        if (res == null) return null;
        try (InputStream stream = res.getInputStream()) {
            return ImageIO.read(stream);
        }
    }
}
