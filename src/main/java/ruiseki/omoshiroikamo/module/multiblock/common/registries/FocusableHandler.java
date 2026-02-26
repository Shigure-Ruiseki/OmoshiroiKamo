package ruiseki.omoshiroikamo.module.multiblock.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.core.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.core.item.weighted.WeightedItemStack;
import ruiseki.omoshiroikamo.core.item.weighted.WeightedOreStack;
import ruiseki.omoshiroikamo.core.item.weighted.WeightedStackBase;

public class FocusableHandler {

    // ------------------------------
    // JSON I/O
    // ------------------------------

    public static void saveRegistryDefaultsToJson(File file, FocusableList defaults) {
        if (defaults == null || file == null) {
            return;
        }
        try {
            List<FocusableMaterial> materials = new ArrayList<>();
            for (FocusableEntry entry : defaults.getEntries()) {
                FocusableMaterial mat = toMaterial(entry);
                if (mat != null) materials.add(mat);
            }
            new FocusableJsonWriter(file).write(materials);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static FocusableMaterial toMaterial(FocusableEntry entry) {
        if (entry == null) return null;
        FocusableMaterial mat = new FocusableMaterial();
        mat.id = entry.id;
        mat.meta = entry.meta;
        mat.focusColor = entry.focusColor.getColor();
        mat.weights = entry.weights;
        mat.focusedWeights = entry.focusedWeights;
        mat.isOreDict = entry.isOreDict;
        mat.dimensions = entry.dimensions;

        if (entry instanceof FocusableOre) mat.type = FocusableType.ORE;
        else if (entry instanceof FocusableBlock) mat.type = FocusableType.BLOCK;
        else mat.type = FocusableType.ITEM;

        return mat;
    }

    private static FocusableEntry fromMaterial(FocusableMaterial mat) {
        if (mat == null) return null;
        if (mat.type == FocusableType.ORE) {
            return new FocusableOre(mat.id, mat.focusColor, mat.weights, mat.focusedWeights);
        } else if (mat.type == FocusableType.BLOCK) {
            return new FocusableBlock(mat.id, mat.meta, mat.focusColor, mat.weights, mat.focusedWeights);
        } else {
            return new FocusableItem(mat.id, mat.meta, mat.focusColor, mat.weights, mat.focusedWeights, mat.isOreDict);
        }
    }

    public static void loadRegistryFromJson(File file, IFocusableRegistry registry) {
        if (file == null || !file.exists()) {
            return;
        }
        try {
            FocusableJsonReader reader = new FocusableJsonReader(file);
            List<FocusableMaterial> materials = reader.read();
            if (materials != null && registry != null) {
                FocusableList list = new FocusableList();
                for (FocusableMaterial mat : materials) {
                    FocusableEntry entry = fromMaterial(mat);
                    if (entry != null) list.addEntry(entry);
                }
                loadIntoRegistry(list, registry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FocusableList loadListFromJson(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        try {
            FocusableJsonReader reader = new FocusableJsonReader(file);
            List<FocusableMaterial> materials = reader.read();
            if (materials != null) {
                FocusableList list = new FocusableList();
                for (FocusableMaterial mat : materials) {
                    FocusableEntry entry = fromMaterial(mat);
                    if (entry != null) list.addEntry(entry);
                }
                return list;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadIntoRegistry(FocusableList list, IFocusableRegistry registry) {
        loadIntoRegistry(list, registry, -1, Integer.MIN_VALUE);
    }

    public static void loadIntoRegistry(FocusableList list, IFocusableRegistry registry, int tier) {
        loadIntoRegistry(list, registry, tier, Integer.MIN_VALUE);
    }

    public static void loadIntoRegistry(FocusableList list, IFocusableRegistry registry, int tier, int dimId) {
        if (list == null || registry == null) {
            return;
        }

        // If no dimension filter, use simple loading (for legacy arrays)
        if (dimId == Integer.MIN_VALUE) {
            list.getEntries()
                .stream()
                .filter(Objects::nonNull)
                .forEach(entry -> {
                    WeightedStackBase wsb = tier >= 0 ? entry.getRegistryEntry(tier) : entry.getRegistryEntry();
                    if (wsb != null) {
                        registry.addResource(wsb, entry.getFocusColor());
                    }
                });
            return;
        }

        // 3-pass loading with specificity priority:
        // Pass 1: dimensions.length == 1 (most specific)
        // Pass 2: dimensions.length > 1 (multi-dimension)
        // Pass 3: dimensions == null (fallback)

        // Pass 1: Single dimension entries (most specific)
        list.getEntries()
            .stream()
            .filter(Objects::nonNull)
            .filter(entry -> entry.getDimensionSpecificity() == 1 && entry.isValidForDimension(dimId))
            .forEach(entry -> {
                WeightedStackBase wsb = tier >= 0 ? entry.getRegistryEntry(tier) : entry.getRegistryEntry();
                if (wsb != null) {
                    registry.addResource(wsb, entry.getFocusColor());
                }
            });

        // Pass 2: Multi-dimension entries
        list.getEntries()
            .stream()
            .filter(Objects::nonNull)
            .filter(entry -> entry.getDimensionSpecificity() > 1 && entry.isValidForDimension(dimId))
            .forEach(entry -> {
                WeightedStackBase wsb = tier >= 0 ? entry.getRegistryEntry(tier) : entry.getRegistryEntry();
                if (wsb != null) {
                    registry.addResource(wsb, entry.getFocusColor());
                }
            });

        // Pass 3: Fallback entries (no dimension restriction)
        list.getEntries()
            .stream()
            .filter(Objects::nonNull)
            .filter(entry -> entry.getDimensionSpecificity() == 0)
            .forEach(entry -> {
                WeightedStackBase wsb = tier >= 0 ? entry.getRegistryEntry(tier) : entry.getRegistryEntry();
                if (wsb != null) {
                    registry.addResource(wsb, entry.getFocusColor());
                }
            });
    }

    /**
     * Load only common entries (dimensions == null) into the registry.
     * Used for NEI "Common" filter display.
     */
    public static void loadCommonOnlyIntoRegistry(FocusableList list, IFocusableRegistry registry, int tier) {
        if (list == null || registry == null) {
            return;
        }

        list.getEntries()
            .stream()
            .filter(Objects::nonNull)
            .filter(entry -> entry.getDimensionSpecificity() == 0) // Only entries with no dimension restriction
            .forEach(entry -> {
                WeightedStackBase wsb = tier >= 0 ? entry.getRegistryEntry(tier) : entry.getRegistryEntry();
                if (wsb != null) {
                    registry.addResource(wsb, entry.getFocusColor());
                }
            });
    }

    // ------------------------------
    // Focusable List
    // ------------------------------

    public static class FocusableList {

        private final List<FocusableEntry> entries = new ArrayList<>();

        public List<FocusableEntry> getEntries() {
            return Collections.unmodifiableList(entries);
        }

        public void addEntry(FocusableEntry entry) {
            if (entry != null && !entries.contains(entry)) {
                entries.add(entry);
            }
        }

        public boolean hasEntry(FocusableEntry entry) {
            return entry != null && entries.stream()
                .anyMatch(e -> e.equals(entry));
        }
    }

    // ------------------------------
    // Base Entry
    // ------------------------------

    public static abstract class FocusableEntry {

        protected String id;
        protected int meta;
        protected EnumFocusColor focusColor;
        protected double[] weights;
        protected double[] focusedWeights;
        protected boolean isOreDict;
        protected int[] dimensions; // null or empty = all dimensions

        public abstract WeightedStackBase getRegistryEntry();

        public abstract WeightedStackBase getRegistryEntry(int tier);

        public EnumDye getFocusColor() {
            return focusColor != null ? focusColor.getColor() : EnumDye.WHITE;
        }

        public boolean isValid() {
            return id != null && !id.isEmpty() && meta >= 0;
        }

        public boolean isValidForTier(int tier) {
            if (!isValid()) return false;
            if (tier < 0) return false;

            boolean hasWeight = weights != null && tier < weights.length && weights[tier] > 0;
            boolean hasFocusedWeight = focusedWeights != null && tier < focusedWeights.length
                && focusedWeights[tier] > 0;
            return hasWeight || hasFocusedWeight;
        }

        public boolean isValidForDimension(int dimId) {
            if (dimensions == null || dimensions.length == 0) {
                return true; // null or empty = all dimensions
            }
            for (int dim : dimensions) {
                if (dim == dimId) return true;
            }
            return false;
        }

        /**
         * Get the dimensions array for this entry.
         * Returns null or empty if this entry is valid for all dimensions.
         */
        public int[] getDimensions() {
            return dimensions;
        }

        /**
         * Returns the specificity level of dimension settings.
         * 0 = no dimension restriction (fallback)
         * 1 = single dimension (most specific)
         * >1 = multi-dimension
         */
        public int getDimensionSpecificity() {
            if (dimensions == null || dimensions.length == 0) {
                return 0;
            }
            return dimensions.length;
        }

        protected double getWeightForTier(int tier) {
            if (weights != null && tier >= 0 && tier < weights.length) {
                return weights[tier];
            }
            return 0;
        }

        protected double getFocusedWeightForTier(int tier) {
            if (focusedWeights != null && tier >= 0 && tier < focusedWeights.length && focusedWeights[tier] > 0) {
                return focusedWeights[tier];
            }
            return getWeightForTier(tier);
        }

        public String getIDWithMeta() {
            return id + ":" + meta;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FocusableEntry)) {
                return false;
            }
            FocusableEntry other = (FocusableEntry) obj;
            return Objects.equals(getIDWithMeta(), other.getIDWithMeta());
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, meta);
        }

        protected static double[] multiply(double[] arr, double multiplier) {
            if (arr == null) return null;
            double[] result = new double[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = arr[i] * multiplier;
            }
            return result;
        }
    }

    // ------------------------------
    // Ore Entry
    // ------------------------------

    public static class FocusableOre extends FocusableEntry {

        public FocusableOre() {}

        public FocusableOre(String oreName, EnumDye color, double weight) {
            this(oreName, color, new double[] { weight, weight, weight, weight, weight, weight });
        }

        public FocusableOre(String oreName, EnumDye color, double weight, int... dimensions) {
            this(oreName, color, new double[] { weight, weight, weight, weight, weight, weight }, null, dimensions);
        }

        public FocusableOre(String oreName, EnumDye color, double[] weights) {
            this(oreName, color, weights, null);
        }

        public FocusableOre(String oreName, EnumDye color, double[] weights, double[] focusedWeights) {
            this(oreName, color, weights, focusedWeights, (int[]) null);
        }

        public FocusableOre(String oreName, EnumDye color, double[] weights, double[] focusedWeights,
            int... dimensions) {
            this.id = oreName;
            this.meta = 0;
            this.focusColor = EnumFocusColor.getFromDye(color);
            this.weights = weights;
            this.focusedWeights = focusedWeights != null ? focusedWeights : multiply(weights, 5.0);
            this.isOreDict = true;
            this.dimensions = dimensions;
        }

        @Override
        public WeightedStackBase getRegistryEntry() {
            return getRegistryEntry(0);
        }

        @Override
        public WeightedStackBase getRegistryEntry(int tier) {
            if (!isValidForTier(tier)) {
                return null;
            }
            double w = getWeightForTier(tier);
            double fw = getFocusedWeightForTier(tier);
            return new WeightedOreStack(id, w, fw);
        }
    }

    // ------------------------------
    // Item / Block Shared Logic
    // ------------------------------

    public static class FocusableItem extends FocusableEntry {

        public FocusableItem() {}

        public FocusableItem(String id, int meta, EnumDye color, double weight) {
            this(id, meta, color, new double[] { weight, weight, weight, weight, weight, weight }, null, false);
        }

        public FocusableItem(String id, int meta, EnumDye color, double weight, boolean isOreDict) {
            this(id, meta, color, new double[] { weight, weight, weight, weight, weight, weight }, null, isOreDict);
        }

        public FocusableItem(String id, int meta, EnumDye color, double[] weights) {
            this(id, meta, color, weights, null, false);
        }

        public FocusableItem(String id, int meta, EnumDye color, double[] weights, double[] focusedWeights,
            boolean isOreDict) {
            this.id = id;
            this.meta = meta;
            this.focusColor = EnumFocusColor.getFromDye(color);
            this.weights = weights;
            this.focusedWeights = focusedWeights != null ? focusedWeights : multiply(weights, 5.0);
            this.isOreDict = isOreDict;
        }

        @Override
        public WeightedStackBase getRegistryEntry() {
            return getRegistryEntry(0);
        }

        @Override
        public WeightedStackBase getRegistryEntry(int tier) {
            if (!isValidForTier(tier)) {
                return null;
            }
            double w = getWeightForTier(tier);
            double fw = getFocusedWeightForTier(tier);

            if (isOreDict) {
                return getOreDictStack(w, fw);
            }

            Item item = GameData.getItemRegistry()
                .getObject(id);
            if (item == null || !GameData.getItemRegistry()
                .containsKey(id)) {
                return null;
            }
            return new WeightedItemStack(new ItemStack(item, 1, meta), w, fw);
        }

        WeightedStackBase getOreDictStack(double w, double fw) {
            List<ItemStack> ores = OreDictionary.getOres(id);
            if (ores.isEmpty()) {
                return null;
            }
            return new WeightedItemStack(
                ores.get(0)
                    .copy(),
                w,
                fw);
        }
    }

    public static class FocusableBlock extends FocusableItem {

        public FocusableBlock() {}

        public FocusableBlock(String id, int meta, EnumDye color, double weight) {
            super(id, meta, color, weight, false);
        }

        public FocusableBlock(String id, int meta, EnumDye color, double weight, int... dimensions) {
            super(id, meta, color, weight, false);
            this.dimensions = dimensions;
        }

        public FocusableBlock(String id, int meta, EnumDye color, double weight, boolean isOreDict) {
            super(id, meta, color, weight, isOreDict);
        }

        public FocusableBlock(String id, int meta, EnumDye color, double[] weights) {
            super(id, meta, color, weights, null, false);
        }

        public FocusableBlock(String id, int meta, EnumDye color, double[] weights, double[] focusedWeights) {
            super(id, meta, color, weights, focusedWeights, false);
        }

        @Override
        public WeightedStackBase getRegistryEntry(int tier) {
            if (!isValidForTier(tier)) {
                return null;
            }
            double w = getWeightForTier(tier);
            double fw = getFocusedWeightForTier(tier);

            if (isOreDict) {
                return getOreDictStack(w, fw);
            }

            Block block = GameData.getBlockRegistry()
                .getObject(id);
            if (block == null || !GameData.getBlockRegistry()
                .containsKey(id)) {
                return null;
            }
            return new WeightedItemStack(new ItemStack(block, 1, meta), w, fw);
        }
    }

    // ------------------------------
    // Enum Mappings
    // ------------------------------

    public enum EnumFocusColor {

        WHITE(EnumDye.WHITE),
        ORANGE(EnumDye.ORANGE),
        MAGENTA(EnumDye.MAGENTA),
        LIGHT_BLUE(EnumDye.LIGHT_BLUE),
        YELLOW(EnumDye.YELLOW),
        LIME(EnumDye.LIME),
        PINK(EnumDye.PINK),
        GRAY(EnumDye.GRAY),
        LIGHT_GRAY(EnumDye.LIGHT_GRAY),
        CYAN(EnumDye.CYAN),
        PURPLE(EnumDye.PURPLE),
        BLUE(EnumDye.BLUE),
        BROWN(EnumDye.BROWN),
        GREEN(EnumDye.GREEN),
        RED(EnumDye.RED),
        BLACK(EnumDye.BLACK),

        CRYSTAL(EnumDye.CRYSTAL);

        private final EnumDye dye;

        EnumFocusColor(EnumDye color) {
            this.dye = color;
        }

        public EnumDye getColor() {
            return dye;
        }

        public static EnumFocusColor getFromDye(EnumDye dye) {
            return Arrays.stream(values())
                .filter(c -> c.dye == dye)
                .findFirst()
                .orElse(WHITE);
        }
    }

    public enum FocusableType {
        ORE,
        ITEM,
        BLOCK
    }
}
