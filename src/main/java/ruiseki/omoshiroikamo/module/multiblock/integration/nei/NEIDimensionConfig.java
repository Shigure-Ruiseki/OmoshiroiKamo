package ruiseki.omoshiroikamo.module.multiblock.integration.nei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Manages dimension configuration for NEI display.
 * Loads from config/omoshiroikamo/multiblock/NEIdimensions.json
 */
public class NEIDimensionConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .disableHtmlEscaping()
        .create();
    private static final String CONFIG_PATH = "config/" + LibMisc.MOD_ID + "/multiblock/NEIdimensions.json";

    /** Special value for "Common" filter (dimensions == null only) */
    public static final int DIMENSION_COMMON = Integer.MIN_VALUE;

    private static DimensionList dimensionList;

    /**
     * Quick lookup from catalyst stack to dimension id (supports wildcard meta).
     */
    private static final Map<StackKey, Integer> catalystLookup = new HashMap<>();

    public static void init() {
        File file = new File(CONFIG_PATH);
        if (file.exists()) {
            dimensionList = loadFromJson(file);
        } else {
            dimensionList = getDefaults();
            saveToJson(file, dimensionList);
        }

        rebuildCatalystLookup();
    }

    public static List<DimensionEntry> getDimensions() {
        ensureLoaded();
        return dimensionList.getDimensions();
    }

    /**
     * Get all dimension IDs that are configured (for iteration)
     */
    public static List<Integer> getDimensionIds() {
        List<Integer> ids = new ArrayList<>();
        ids.add(DIMENSION_COMMON); // Always include "Common" as first option
        for (DimensionEntry entry : getDimensions()) {
            ids.add(entry.id);
        }
        return ids;
    }

    public static DimensionEntry getByDimensionId(int dimId) {
        if (dimId == DIMENSION_COMMON) {
            return null; // "Common" has no entry
        }
        for (DimensionEntry entry : getDimensions()) {
            if (entry.id == dimId) {
                return entry;
            }
        }
        return null;
    }

    public static String getDisplayName(int dimId) {
        if (dimId == DIMENSION_COMMON) {
            return "Common";
        }
        DimensionEntry entry = getByDimensionId(dimId);
        return entry != null ? entry.name : "Dim " + dimId;
    }

    public static ItemStack getCatalystStack(int dimId) {
        ensureLoaded();
        DimensionEntry entry = getByDimensionId(dimId);
        if (entry == null) {
            return null;
        }
        ItemStack stack = createCatalystStack(entry);
        return stack != null ? stack.copy() : null;
    }

    /**
     * Get the dimension ID associated with a catalyst item from config.
     * 
     * @param stack The catalyst item stack to check
     * @return The dimension ID, or DIMENSION_COMMON if no match found
     */
    public static int getDimensionForCatalyst(ItemStack stack) {
        if (stack == null) {
            return DIMENSION_COMMON;
        }
        ensureLoaded();

        for (Map.Entry<StackKey, Integer> entry : catalystLookup.entrySet()) {
            if (entry.getKey()
                .matches(stack)) {
                return entry.getValue();
            }
        }
        return DIMENSION_COMMON;
    }

    private static void ensureLoaded() {
        if (dimensionList == null) {
            init();
        }
        if (catalystLookup.isEmpty()) {
            rebuildCatalystLookup();
        }
    }

    /** Build the catalyst lookup map from the current dimension list. */
    private static void rebuildCatalystLookup() {
        catalystLookup.clear();
        if (dimensionList == null) {
            return;
        }

        for (DimensionEntry entry : dimensionList.getDimensions()) {
            ItemStack stack = createCatalystStack(entry);
            if (stack != null) {
                catalystLookup.put(new StackKey(stack), entry.id);
            }
        }
    }

    /**
     * Create an ItemStack from a dimension entry, accepting the legacy
     * "modid:item:meta" format and supporting wildcard metadata (-1).
     */
    private static ItemStack createCatalystStack(DimensionEntry entry) {
        if (entry == null || entry.catalystItem == null) {
            return null;
        }

        String itemName = entry.catalystItem;
        int meta = entry.catalystMeta;

        // Legacy support: allow meta suffix in the item name (e.g. "minecraft:log:1")
        int lastColon = itemName.lastIndexOf(':');
        if (lastColon > 0) {
            String tail = itemName.substring(lastColon + 1);
            try {
                int parsedMeta = Integer.parseInt(tail);
                itemName = itemName.substring(0, lastColon);
                meta = parsedMeta;
            } catch (NumberFormatException ignored) {
                // Not a numeric suffix, keep original itemName/meta
            }
        }

        Item item = GameData.getItemRegistry()
            .getObject(itemName);
        if (item == null) {
            return null;
        }

        if (meta < 0) {
            meta = OreDictionary.WILDCARD_VALUE;
        }

        return new ItemStack(item, 1, meta);
    }

    private static DimensionList loadFromJson(File file) {
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            return GSON.fromJson(reader, DimensionList.class);
        } catch (IOException e) {
            e.printStackTrace();
            return getDefaults();
        }
    }

    private static void saveToJson(File file, DimensionList list) {
        try {
            file.getParentFile()
                .mkdirs();
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                GSON.toJson(list, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static DimensionList getDefaults() {
        DimensionList list = new DimensionList();
        list.dimensions.add(new DimensionEntry(0, "Overworld", "minecraft:grass", 0));
        list.dimensions.add(new DimensionEntry(-1, "Nether", "minecraft:netherrack", 0));
        list.dimensions.add(new DimensionEntry(1, "The End", "minecraft:end_stone", 0));
        return list;
    }

    // --- Data Classes ---

    public static class DimensionList {

        private final List<DimensionEntry> dimensions = new ArrayList<>();

        public List<DimensionEntry> getDimensions() {
            return Collections.unmodifiableList(dimensions);
        }
    }

    public static class DimensionEntry {

        public int id;
        public String name;
        public String catalystItem;
        public int catalystMeta;

        public DimensionEntry() {}

        public DimensionEntry(int id, String name, String catalystItem, int catalystMeta) {
            this.id = id;
            this.name = name;
            this.catalystItem = catalystItem;
            this.catalystMeta = catalystMeta;
        }
    }

    /**
     * Lightweight key for comparing catalyst stacks, honoring wildcard metadata.
     */
    private static class StackKey {

        private final Item item;
        private final int meta;

        StackKey(ItemStack stack) {
            this.item = stack.getItem();
            this.meta = stack.getItemDamage();
        }

        boolean matches(ItemStack other) {
            if (other == null) return false;
            if (other.getItem() != item) return false;
            int otherMeta = other.getItemDamage();
            return meta == OreDictionary.WILDCARD_VALUE || meta == otherMeta;
        }

        @Override
        public int hashCode() {
            return item.hashCode() * 31 + meta;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof StackKey other)) return false;
            return item == other.item && meta == other.meta;
        }
    }
}
