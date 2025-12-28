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
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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

    public static void init() {
        File file = new File(CONFIG_PATH);
        if (file.exists()) {
            dimensionList = loadFromJson(file);
        } else {
            dimensionList = getDefaults();
            saveToJson(file, dimensionList);
        }
    }

    public static List<DimensionEntry> getDimensions() {
        if (dimensionList == null) {
            init();
        }
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
        DimensionEntry entry = getByDimensionId(dimId);
        if (entry == null) {
            return null;
        }
        Item item = GameData.getItemRegistry()
            .getObject(entry.catalystItem);
        if (item == null) {
            return null;
        }
        return new ItemStack(item, 1, entry.catalystMeta);
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
}
