package ruiseki.omoshiroikamo.core.client.util;

import java.io.File;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import ruiseki.omoshiroikamo.core.common.util.Logger;

public class TextureLoader {

    private TextureLoader() {}

    /**
     * Load textures from:
     * config/<modid>/textures/
     * following vanilla Minecraft structure.
     */
    public static void loadFromConfig(String modid, String packName, Class<?> jarClass) {

        if (FMLCommonHandler.instance()
            .getEffectiveSide() != Side.CLIENT) return;

        try {
            File configDir = new File("config/" + modid);

            if (!configDir.exists()) {
                configDir.mkdirs();
                Logger.info("[TextureLoader] Created config texture folder: {}", configDir);
                return;
            }

            File workDir = new File(configDir, "resourcepack");
            OKFileUtils.safeDeleteDirectory(workDir);

            ResourcePackAssembler assembler = new ResourcePackAssembler(workDir, packName, modid)
                .setHasPackPng(jarClass);

            File assetsRoot = new File(configDir, "assets/" + modid + "/textures");
            File langFolder = new File(configDir, "assets/" + modid + "/lang");
            File modelFolder = new File(configDir, "assets/" + modid + "/models");

            if (!assetsRoot.exists()) {
                new File(assetsRoot, "items").mkdirs();
                new File(assetsRoot, "blocks").mkdirs();
                new File(assetsRoot, "entity").mkdirs();
                if (!modelFolder.exists()) modelFolder.mkdirs();
                if (!langFolder.exists()) langFolder.mkdirs();
                Logger.info("[TextureLoader] Created vanilla texture skeleton for {}", modid);
                return;
            }

            ResourcePackUtils
                .scanVanillaLike(assembler, new File(assetsRoot, "items"), ResourcePackAssembler.IconTarget.ITEM);

            ResourcePackUtils
                .scanVanillaLike(assembler, new File(assetsRoot, "blocks"), ResourcePackAssembler.IconTarget.BLOCK);

            ResourcePackUtils
                .scanVanillaLike(assembler, new File(assetsRoot, "entity"), ResourcePackAssembler.IconTarget.ENTITY);

            if (modelFolder.exists()) {
                ResourcePackUtils.addModelsFromConfig(assembler, new File(configDir, "assets/" + modid));
            }

            if (langFolder.exists() && langFolder.isDirectory()) {
                File[] langFiles = langFolder.listFiles((dir, name) -> name.endsWith(".lang"));
                if (langFiles != null) {
                    for (File file : langFiles) {
                        if (file.isFile()) {
                            assembler.addCustomFile("assets/" + modid + "/lang", file);
                        }
                    }
                }
            }

            if (modelFolder.exists()) {
                ResourcePackUtils.addModelsFromConfig(assembler, new File(configDir, "assets/" + modid));
            }

            assembler.assemble()
                .inject();

            Logger.info("[TextureLoader] Loaded config textures");
        } catch (Exception e) {
            Logger.error("[TextureLoader] Failed to load textures", e);
        }
    }
}
