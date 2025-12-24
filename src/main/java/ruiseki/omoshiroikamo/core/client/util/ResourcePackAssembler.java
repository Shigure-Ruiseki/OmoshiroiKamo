package ruiseki.omoshiroikamo.core.client.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.IResourcePack;

import org.apache.commons.io.FileUtils;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import ruiseki.omoshiroikamo.core.common.util.Logger;

// Copy and modify from EnderCore
public class ResourcePackAssembler {

    public static enum IconTarget {
        ITEM,
        BLOCK,
        ENTITY,
        BOTH
    }

    private static class IconEntry {

        File file;
        String subPath;
        IconTarget target;

        IconEntry(File file, String subPath, IconTarget target) {
            this.file = file;
            this.subPath = subPath;
            this.target = target;
        }
    }

    private List<IconEntry> icons = new ArrayList<IconEntry>();

    private static class CustomFile {

        private String ext;
        private File file;

        private CustomFile(String ext, File file) {
            this.ext = ext;
            this.file = file;
        }
    }

    private List<File> langs = new ArrayList<File>();
    private List<CustomFile> customs = new ArrayList<CustomFile>();

    private static List<IResourcePack> defaultResourcePacks;

    private static final String MC_META_BASE = "{\"pack\":{\"pack_format\":1,\"description\":\"%s\"}}";

    private File dir;
    private File zip;
    private String mcmeta;
    private String modid;
    private boolean hasPackPng = false;
    private Class<?> jarClass;

    public ResourcePackAssembler(File directory, String packName, String modid) {
        this.dir = directory;
        this.zip = new File(packName + ".zip");
        this.modid = modid.toLowerCase(Locale.US);
        this.mcmeta = String.format(MC_META_BASE, packName);
    }

    public void addIcon(File icon) {
        icons.add(new IconEntry(icon, "", IconTarget.BOTH));
    }

    public void addIcon(String subPath, File icon, IconTarget target) {
        icons.add(new IconEntry(icon, normalize(subPath), target));
    }

    private String normalize(String path) {
        if (path == null || path.isEmpty()) return "";
        if (path.endsWith("/")) return path.substring(0, path.length() - 1);
        return path;
    }

    public void addLang(File lang) {
        langs.add(lang);
    }

    public void addCustomFile(String path, File file) {
        customs.add(new CustomFile(path, file));
    }

    public void addCustomFile(File file) {
        addCustomFile(null, file);
    }

    public ResourcePackAssembler setHasPackPng(Class<?> jarClass) {
        this.jarClass = jarClass;
        this.hasPackPng = true;
        return this;
    }

    public ResourcePackAssembler assemble() {
        OKFileUtils.safeDeleteDirectory(dir);
        dir.mkdirs();

        String root = dir.getAbsolutePath();

        try {
            writeNewFile(new File(root + "/pack.mcmeta"), mcmeta);
            if (hasPackPng) {
                OKFileUtils.copyFromJar(jarClass, modid + "/pack.png", new File(root + "/pack.png"));
            }

            String itemsDir = root + "/assets/" + modid + "/textures/items";
            String blocksDir = root + "/assets/" + modid + "/textures/blocks";
            String entityDir = root + "/assets/" + modid + "/textures/entity";
            String langDir = root + "/assets/" + modid + "/lang";

            /* ---- ICONS ---- */
            for (IconEntry e : icons) {

                if (e.target == IconTarget.ITEM || e.target == IconTarget.BOTH) {
                    File dest = new File(
                        itemsDir + "/" + (e.subPath.isEmpty() ? "" : e.subPath + "/") + e.file.getName());
                    dest.getParentFile()
                        .mkdirs();
                    FileUtils.copyFile(e.file, dest);
                }

                if (e.target == IconTarget.BLOCK || e.target == IconTarget.BOTH) {
                    File dest = new File(
                        blocksDir + "/" + (e.subPath.isEmpty() ? "" : e.subPath + "/") + e.file.getName());
                    dest.getParentFile()
                        .mkdirs();
                    FileUtils.copyFile(e.file, dest);
                }

                if (e.target == IconTarget.ENTITY || e.target == IconTarget.BOTH) {
                    File dest = new File(
                        entityDir + "/" + (e.subPath.isEmpty() ? "" : e.subPath + "/") + e.file.getName());
                    dest.getParentFile()
                        .mkdirs();
                    FileUtils.copyFile(e.file, dest);
                }
            }

            /* ---- LANG ---- */
            for (File lang : langs) {
                FileUtils.copyFile(lang, new File(langDir + "/" + lang.getName()));
            }

            /* ---- CUSTOM ---- */
            for (CustomFile c : customs) {
                File outDir = new File(root + (c.ext != null ? "/" + c.ext : ""));
                outDir.mkdirs();
                FileUtils.copyFile(c.file, new File(outDir, c.file.getName()));
            }

            OKFileUtils.zipFolderContents(dir, zip);
            OKFileUtils.safeDeleteDirectory(dir);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public void inject() {
        if (!FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient()) return;

        try {
            if (defaultResourcePacks == null) {
                defaultResourcePacks = ReflectionHelper.getPrivateValue(
                    Minecraft.class,
                    Minecraft.getMinecraft(),
                    "defaultResourcePacks",
                    "field_110449_ao",
                    "ap");
            }
            File rpDir = new File(dir.getParent(), "resourcepack");
            rpDir.mkdirs();
            File dest = new File(rpDir, zip.getName());
            OKFileUtils.safeDelete(dest);
            FileUtils.copyFile(zip, dest);
            OKFileUtils.safeDelete(zip);

            defaultResourcePacks.add(new FileResourcePack(dest));
        } catch (Exception e) {
            Logger.error("Failed to inject resource pack {}", modid, e);
        }
    }

    private void writeNewFile(File file, String text) throws IOException {
        OKFileUtils.safeDelete(file);
        file.getParentFile()
            .mkdirs();
        FileWriter fw = new FileWriter(file);
        fw.write(text);
        fw.close();
    }
}
