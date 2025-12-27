package ruiseki.omoshiroikamo.module.machinery.client;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Generates combined textures for Machinery blocks using TextureStitchEvent.
 * Combines a tinted base texture with an overlay texture.
 * 
 * Register this class with MinecraftForge.EVENT_BUS on client init.
 */
@SideOnly(Side.CLIENT)
public class MachineryTextureGenerator {

    // Texture paths - centralized for easy modification
    private static final String TEXTURE_PREFIX = "textures/blocks/";
    private static final String BASE_TEXTURE = "modular_machine_casing";
    private static final String OVERLAY_FOLDER = "modularmachineryOverlay";

    // Storage for generated icons
    private static final Map<String, IIcon> generatedIcons = new HashMap<>();

    // Texture generation requests
    private static final Map<String, TextureRequest> pendingRequests = new HashMap<>();

    /**
     * Request a combined texture with default base and overlay folder.
     * 
     * @param name        Unique name for this texture (used as key)
     * @param overlayName Overlay texture file name (without folder prefix or
     *                    extension)
     */
    public static void requestTexture(String name, String overlayName) {
        requestTexture(name, overlayName, 0xFFFFFF);
    }

    /**
     * Request a combined texture with tint color.
     * 
     * @param name        Unique name for this texture (used as key)
     * @param overlayName Overlay texture file name (without folder prefix or
     *                    extension)
     * @param tintColor   Tint color to apply to base (0xFFFFFF = no tint)
     */
    public static void requestTexture(String name, String overlayName, int tintColor) {
        String overlayPath = OVERLAY_FOLDER + "/" + overlayName;
        pendingRequests.put(name, new TextureRequest(BASE_TEXTURE, overlayPath, tintColor));
        Logger.info("Registered texture request: {} (overlay: {})", name, overlayName);
    }

    /**
     * Request a combined texture with custom base texture.
     * 
     * @param name        Unique name for this texture
     * @param baseTexture Base texture path (without folder prefix or extension)
     * @param overlayName Overlay texture file name
     * @param tintColor   Tint color to apply to base
     */
    public static void requestTextureCustomBase(String name, String baseTexture, String overlayName, int tintColor) {
        String overlayPath = OVERLAY_FOLDER + "/" + overlayName;
        pendingRequests.put(name, new TextureRequest(baseTexture, overlayPath, tintColor));
        Logger.info("Registered texture request: {} (base: {}, overlay: {})", name, baseTexture, overlayName);
    }

    /**
     * Get a generated icon by name.
     * Call this after TextureStitchEvent.Post has fired.
     */
    public static IIcon getGeneratedIcon(String name) {
        return generatedIcons.get(name);
    }

    /**
     * Get the default base texture name.
     */
    public static String getBaseTexture() {
        return BASE_TEXTURE;
    }

    /**
     * Get the overlay folder path.
     */
    public static String getOverlayFolder() {
        return OVERLAY_FOLDER;
    }

    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        TextureMap textureMap = event.map;

        // Only process block textures (type 0)
        if (textureMap.getTextureType() != 0) {
            return;
        }

        Logger.info("MachineryTextureGenerator: Processing {} texture requests", pendingRequests.size());

        for (Map.Entry<String, TextureRequest> entry : pendingRequests.entrySet()) {
            String name = entry.getKey();
            TextureRequest request = entry.getValue();

            try {
                // Create combined texture sprite
                CombinedTextureSprite sprite = new CombinedTextureSprite(
                    name,
                    request.baseTexture,
                    request.overlayTexture,
                    request.tintColor);

                // Register the sprite with the texture map
                textureMap.setTextureEntry(LibMisc.MOD_ID + ":generated/" + name, sprite);
                Logger.info("Registered combined texture sprite: {}", name);

            } catch (Exception e) {
                Logger.error("Failed to create combined texture for {}: {}", name, e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public void onTextureStitchPost(TextureStitchEvent.Post event) {
        TextureMap textureMap = event.map;

        if (textureMap.getTextureType() != 0) {
            return;
        }

        // Store references to generated icons
        for (String name : pendingRequests.keySet()) {
            IIcon icon = textureMap.getTextureExtry(LibMisc.MOD_ID + ":generated/" + name);
            if (icon != null) {
                generatedIcons.put(name, icon);
                Logger.info("Stored generated icon: {}", name);
            }
        }
    }

    // ========== Inner Classes ==========

    private static class TextureRequest {

        final String baseTexture;
        final String overlayTexture;
        final int tintColor;

        TextureRequest(String baseTexture, String overlayTexture, int tintColor) {
            this.baseTexture = baseTexture;
            this.overlayTexture = overlayTexture;
            this.tintColor = tintColor;
        }
    }

    /**
     * Custom TextureAtlasSprite that combines base + overlay textures.
     */
    private static class CombinedTextureSprite extends TextureAtlasSprite {

        private final String baseTexturePath;
        private final String overlayTexturePath;
        private final int tintColor;

        public CombinedTextureSprite(String name, String baseTexture, String overlayTexture, int tintColor) {
            super(LibMisc.MOD_ID + ":generated/" + name);
            this.baseTexturePath = baseTexture;
            this.overlayTexturePath = overlayTexture;
            this.tintColor = tintColor;
        }

        @Override
        public boolean hasCustomLoader(net.minecraft.client.resources.IResourceManager manager,
            ResourceLocation location) {
            return true;
        }

        @Override
        public boolean load(net.minecraft.client.resources.IResourceManager manager, ResourceLocation location) {
            try {
                // Load base and overlay textures
                ResourceLocation baseLoc = new ResourceLocation(
                    LibMisc.MOD_ID,
                    TEXTURE_PREFIX + baseTexturePath + ".png");
                ResourceLocation overlayLoc = new ResourceLocation(
                    LibMisc.MOD_ID,
                    TEXTURE_PREFIX + overlayTexturePath + ".png");

                BufferedImage baseImage = readTexture(baseLoc);
                BufferedImage overlayImage = readTexture(overlayLoc);

                if (baseImage == null || overlayImage == null) {
                    Logger.error("Failed to load textures for combined sprite");
                    return true; // Return true to indicate loading failed
                }

                int width = baseImage.getWidth();
                int height = baseImage.getHeight();

                // Apply tint to base
                BufferedImage tintedBase = applyTint(baseImage, tintColor);

                // Combine textures
                BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = combined.createGraphics();
                g2.drawImage(tintedBase, 0, 0, null);
                g2.drawImage(overlayImage, 0, 0, null);
                g2.dispose();

                // Convert to int array for sprite
                int[] pixels = new int[width * height];
                combined.getRGB(0, 0, width, height, pixels, 0, width);

                // Set up frame data
                int[][] frameData = new int[1][];
                frameData[0] = pixels;
                this.framesTextureData.add(frameData[0]);

                this.width = width;
                this.height = height;

                Logger.info("Successfully loaded combined texture: {}", this.getIconName());
                return false; // Return false to indicate loading succeeded

            } catch (Exception e) {
                Logger.error("Error loading combined texture: {}", e.getMessage());
                return true;
            }
        }

        private BufferedImage applyTint(BufferedImage image, int tint) {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);

            int rTint = (tint >> 16) & 0xFF;
            int gTint = (tint >> 8) & 0xFF;
            int bTint = tint & 0xFF;

            for (int i = 0; i < pixels.length; i++) {
                int p = pixels[i];
                int a = (p >> 24) & 0xFF;
                if (a == 0) continue;

                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;

                r = (r * rTint) / 255;
                g = (g * gTint) / 255;
                b = (b * bTint) / 255;

                pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }

            BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            result.setRGB(0, 0, width, height, pixels, 0, width);
            return result;
        }

        private BufferedImage readTexture(ResourceLocation loc) {
            try {
                IResource res = Minecraft.getMinecraft()
                    .getResourceManager()
                    .getResource(loc);
                if (res == null) return null;
                try (InputStream stream = res.getInputStream()) {
                    return ImageIO.read(stream);
                }
            } catch (IOException e) {
                Logger.warn("Could not load texture: {}", loc);
                return null;
            }
        }
    }
}
