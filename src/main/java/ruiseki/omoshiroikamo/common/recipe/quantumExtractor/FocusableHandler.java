package ruiseki.omoshiroikamo.common.recipe.quantumExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.enderio.core.common.util.DyeColor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.api.item.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.WeightedItemStack;
import ruiseki.omoshiroikamo.api.item.WeightedOreStack;
import ruiseki.omoshiroikamo.api.item.WeightedStackBase;

public class FocusableHandler {

    private static final Gson GSON = buildGson();

    private static Gson buildGson() {
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting()
            .disableHtmlEscaping();

        builder
            .registerTypeAdapter(FocusableEntry.class, (JsonDeserializer<FocusableEntry>) (json, typeOfT, context) -> {
                JsonObject obj = json.getAsJsonObject();
                FocusableType type = null;
                if (obj.has("type")) {
                    try {
                        type = FocusableType.valueOf(
                            obj.get("type")
                                .getAsString()
                                .toUpperCase());
                    } catch (Exception ignored) {}
                }

                if (type == null) {
                    boolean isOre = obj.has("isOreDict") && obj.get("isOreDict")
                        .getAsBoolean();
                    if (isOre) {
                        type = FocusableType.ORE;
                    } else if (obj.has("id") && obj.get("id")
                        .getAsString()
                        .contains("block")) {
                            type = FocusableType.BLOCK;
                        } else {
                            type = FocusableType.ITEM;
                        }
                }

                switch (type) {
                    case ORE:
                        return context.deserialize(json, FocusableOre.class);
                    case BLOCK:
                        return context.deserialize(json, FocusableBlock.class);
                    default:
                        return context.deserialize(json, FocusableItem.class);
                }
            });

        return builder.create();
    }

    // ------------------------------
    // JSON I/O
    // ------------------------------

    public static void saveRegistryDefaultsToJson(File file, FocusableList defaults) {
        if (defaults == null || file == null) {
            return;
        }
        try {
            file.getParentFile()
                .mkdirs();
            try (Writer writer = new BufferedWriter(new FileWriter(file))) {
                GSON.toJson(defaults, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadRegistryFromJson(File file, IFocusableRegistry registry) {
        if (file == null || !file.exists()) {
            return;
        }
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            FocusableList loaded = GSON.fromJson(reader, FocusableList.class);
            if (loaded != null && registry != null) {
                loadIntoRegistry(loaded, registry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadIntoRegistry(FocusableList list, IFocusableRegistry registry) {
        if (list == null || registry == null) {
            return;
        }

        list.getEntries()
            .stream()
            .filter(Objects::nonNull)
            .forEach(entry -> registry.addResource(entry.getRegistryEntry(), entry.getFocusColor()));
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
        protected int weight;
        protected boolean isOreDict;

        public abstract WeightedStackBase getRegistryEntry();

        public DyeColor getFocusColor() {
            return focusColor != null ? focusColor.getColor() : DyeColor.WHITE;
        }

        public boolean isValid() {
            return id != null && !id.isEmpty() && weight > 0 && meta >= 0;
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
    }

    // ------------------------------
    // Ore Entry
    // ------------------------------

    public static class FocusableOre extends FocusableEntry {

        public FocusableOre() {}

        public FocusableOre(String oreName, DyeColor color, int weight) {
            this.id = oreName;
            this.meta = 0;
            this.focusColor = EnumFocusColor.getFromDye(color);
            this.weight = weight;
            this.isOreDict = true;
        }

        @Override
        public WeightedStackBase getRegistryEntry() {
            if (!isValid()) {
                return null;
            }
            return new WeightedOreStack(id, weight);
        }
    }

    // ------------------------------
    // Item / Block Shared Logic
    // ------------------------------

    public static class FocusableItem extends FocusableEntry {

        public FocusableItem() {}

        public FocusableItem(String id, int meta, DyeColor color, int weight, boolean isOreDict) {
            this.id = id;
            this.meta = meta;
            this.focusColor = EnumFocusColor.getFromDye(color);
            this.weight = weight;
            this.isOreDict = isOreDict;
        }

        public FocusableItem(String id, int meta, DyeColor color, int weight) {
            this(id, meta, color, weight, false);
        }

        @Override
        public WeightedStackBase getRegistryEntry() {
            if (!isValid()) {
                return null;
            }

            if (isOreDict) {
                return getOreDictStack();
            }

            Item item = GameData.getItemRegistry()
                .getObject(id);
            if (item == null) {
                return null;
            }
            return new WeightedItemStack(new ItemStack(item, 1, meta), weight);
        }

        WeightedStackBase getOreDictStack() {
            List<ItemStack> ores = OreDictionary.getOres(id);
            if (ores.isEmpty()) {
                return null;
            }
            return new WeightedItemStack(
                ores.get(0)
                    .copy(),
                weight);
        }
    }

    public static class FocusableBlock extends FocusableItem {

        public FocusableBlock() {}

        public FocusableBlock(String id, int meta, DyeColor color, int weight, boolean isOreDict) {
            super(id, meta, color, weight, isOreDict);
        }

        public FocusableBlock(String id, int meta, DyeColor color, int weight) {
            this(id, meta, color, weight, false);
        }

        @Override
        public WeightedStackBase getRegistryEntry() {
            if (!isValid()) {
                return null;
            }

            if (isOreDict) {
                return getOreDictStack();
            }

            Block block = GameData.getBlockRegistry()
                .getObject(id);
            if (block == null) {
                return null;
            }
            return new WeightedItemStack(new ItemStack(block, 1, meta), weight);
        }
    }

    // ------------------------------
    // Enum Mappings
    // ------------------------------

    public enum EnumFocusColor {

        WHITE(DyeColor.WHITE),
        ORANGE(DyeColor.ORANGE),
        MAGENTA(DyeColor.MAGENTA),
        LIGHT_BLUE(DyeColor.LIGHT_BLUE),
        YELLOW(DyeColor.YELLOW),
        LIME(DyeColor.LIME),
        PINK(DyeColor.PINK),
        GRAY(DyeColor.GRAY),
        SILVER(DyeColor.SILVER),
        CYAN(DyeColor.CYAN),
        PURPLE(DyeColor.PURPLE),
        BLUE(DyeColor.BLUE),
        BROWN(DyeColor.BROWN),
        GREEN(DyeColor.GREEN),
        RED(DyeColor.RED),
        BLACK(DyeColor.BLACK);

        private final DyeColor dyeColor;

        EnumFocusColor(DyeColor color) {
            this.dyeColor = color;
        }

        public DyeColor getColor() {
            return dyeColor;
        }

        public static EnumFocusColor getFromDye(DyeColor dye) {
            return Arrays.stream(values())
                .filter(c -> c.dyeColor == dye)
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
