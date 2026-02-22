package ruiseki.omoshiroikamo.core.client.util;

import static ruiseki.omoshiroikamo.core.client.util.ResourcePackAssembler.IconTarget;

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

    public static void addModelsFromConfig(ResourcePackAssembler assembler, File baseDir) {
        File modelRoot = new File(baseDir, "models");
        scanModels(modelRoot, assembler);
    }

    private static void scanModels(File root, ResourcePackAssembler assembler) {
        if (!root.exists()) return;
        scanModelsRecursive(root, root, assembler);
    }

    private static void scanModelsRecursive(File root, File current, ResourcePackAssembler assembler) {
        File[] files = current.listFiles();
        if (files == null) return;

        for (File f : files) {

            if (f.isDirectory()) {
                scanModelsRecursive(root, f, assembler);
            }

            else if (isObjFile(f)) {

                String subPath = root.toURI()
                    .relativize(
                        f.getParentFile()
                            .toURI())
                    .getPath();

                if (subPath.endsWith("/")) {
                    subPath = subPath.substring(0, subPath.length() - 1);
                }

                File mtl = findMtlForObj(f);

                assembler.addModel(subPath, f, mtl);
            }
        }
    }

    private static boolean isObjFile(File f) {
        return f.getName()
            .toLowerCase()
            .endsWith(".obj");
    }

    private static File findMtlForObj(File objFile) {

        String base = objFile.getName();
        int i = base.lastIndexOf('.');
        if (i > 0) {
            base = base.substring(0, i);
        }

        File mtl = new File(objFile.getParentFile(), base + ".mtl");

        return mtl.exists() ? mtl : null;
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
