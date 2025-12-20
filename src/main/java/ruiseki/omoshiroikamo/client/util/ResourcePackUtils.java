package ruiseki.omoshiroikamo.client.util;

import static ruiseki.omoshiroikamo.client.util.ResourcePackAssembler.IconTarget;

import java.io.File;

public final class ResourcePackUtils {

    private ResourcePackUtils() {}

    public static void addIconsFromConfig(ResourcePackAssembler assembler, File baseDir) {
        File iconRoot = new File(baseDir, "icons");

        scan(new File(iconRoot, "items"), assembler, IconTarget.ITEM);
        scan(new File(iconRoot, "blocks"), assembler, IconTarget.BLOCK);
        scan(new File(iconRoot, "entity"), assembler, IconTarget.ENTITY);
        scan(new File(iconRoot, "both"), assembler, IconTarget.BOTH);
    }

    private static void scan(File root, ResourcePackAssembler assembler, IconTarget target) {
        if (!root.exists()) return;
        scanRecursive(root, root, assembler, target);
    }

    private static void scanRecursive(File root, File current, ResourcePackAssembler assembler, IconTarget target) {
        File[] files = current.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                scanRecursive(root, f, assembler, target);
            } else if (isIconFile(f)) {
                String subPath = root.toURI()
                    .relativize(
                        f.getParentFile()
                            .toURI())
                    .getPath();
                if (subPath.endsWith("/")) subPath = subPath.substring(0, subPath.length() - 1);

                assembler.addIcon(subPath, f, target);
            }
        }
    }

    private static boolean isIconFile(File f) {
        String n = f.getName()
            .toLowerCase();
        return n.endsWith(".png") || n.endsWith(".mcmeta");
    }

    public static void scanVanillaLike(ResourcePackAssembler assembler, File root,
        ResourcePackAssembler.IconTarget target) {
        ensureDir(root);
        scanVanillaRecursive(root, root, assembler, target);
    }

    private static void scanVanillaRecursive(File root, File current, ResourcePackAssembler assembler,
        ResourcePackAssembler.IconTarget target) {
        File[] files = current.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                scanVanillaRecursive(root, f, assembler, target);
            } else if (f.getName()
                .endsWith(".png")) {
                    String subPath = getRelativePath(root, f.getParentFile());
                    assembler.addIcon(subPath, f, target);
                }
        }
    }

    private static void ensureDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static String getRelativePath(File root, File folder) {
        String path = root.toURI()
            .relativize(folder.toURI())
            .getPath();

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
