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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.item.weighted.IFocusableRegistry;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedItemStack;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedOreStack;
import ruiseki.omoshiroikamo.api.item.weighted.WeightedStackBase;

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
        protected double weight;
        protected double focusedWeight;
        protected boolean isOreDict;

        public abstract WeightedStackBase getRegistryEntry();

        public EnumDye getFocusColor() {
            return focusColor != null ? focusColor.getColor() : EnumDye.WHITE;
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

        public FocusableOre(String oreName, EnumDye color, double weight) {
            this(oreName, color, weight, weight);
        }

        public FocusableOre(String oreName, EnumDye color, double weight, double focusedWeight) {
            this.id = oreName;
            this.meta = 0;
            this.focusColor = EnumFocusColor.getFromDye(color);
            this.weight = weight;
            this.focusedWeight = focusedWeight;
            this.isOreDict = true;
        }

        @Override
        public WeightedStackBase getRegistryEntry() {
            if (!isValid()) {
                return null;
            }
            double fw = focusedWeight > 0 ? focusedWeight : weight;
            return new WeightedOreStack(id, weight, fw);
        }
    }

    // ------------------------------
    // Item / Block Shared Logic
    // ------------------------------

    public static class FocusableItem extends FocusableEntry {

        public FocusableItem() {}

        public FocusableItem(String id, int meta, EnumDye color, double weight, double focusedWeight,
            boolean isOreDict) {
            this.id = id;
            this.meta = meta;
            this.focusColor = EnumFocusColor.getFromDye(color);
            this.weight = weight;
            this.focusedWeight = focusedWeight;
            this.isOreDict = isOreDict;
        }

        public FocusableItem(String id, int meta, EnumDye color, double weight, boolean isOreDict) {
            this(id, meta, color, weight, weight, isOreDict);
        }

        public FocusableItem(String id, int meta, EnumDye color, double weight) {
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
            double fw = focusedWeight > 0 ? focusedWeight : weight;
            return new WeightedItemStack(new ItemStack(item, 1, meta), weight, fw);
        }

        WeightedStackBase getOreDictStack() {
            List<ItemStack> ores = OreDictionary.getOres(id);
            if (ores.isEmpty()) {
                return null;
            }
            double fw = focusedWeight > 0 ? focusedWeight : weight;
            return new WeightedItemStack(
                ores.get(0)
                    .copy(),
                weight,
                fw);
        }
    }

    public static class FocusableBlock extends FocusableItem {

        public FocusableBlock() {}

        public FocusableBlock(String id, int meta, EnumDye color, double weight, boolean isOreDict) {
            super(id, meta, color, weight, isOreDict);
        }

        public FocusableBlock(String id, int meta, EnumDye color, double weight) {
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
            double fw = focusedWeight > 0 ? focusedWeight : weight;
            return new WeightedItemStack(new ItemStack(block, 1, meta), weight, fw);
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
        SILVER(EnumDye.SILVER),
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
