package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Condition that checks the current biome.
 * Supports biome names, IDs, BiomeDictionary tags, and environmental properties
 * (temp/humidity).
 */
public class BiomeCondition implements ICondition {

    private final List<String> allowedBiomes;
    private final List<String> allowedTags;
    private Double minTemp;
    private Double maxTemp;
    private Double minHumid;
    private Double maxHumid;

    public BiomeCondition(List<String> allowedBiomes) {
        this(allowedBiomes, new ArrayList<>());
    }

    public BiomeCondition(List<String> allowedBiomes, List<String> allowedTags) {
        this.allowedBiomes = allowedBiomes;
        this.allowedTags = allowedTags;
    }

    public BiomeCondition withTemp(Double min, Double max) {
        this.minTemp = min;
        this.maxTemp = max;
        return this;
    }

    public BiomeCondition withHumid(Double min, Double max) {
        this.minHumid = min;
        this.maxHumid = max;
        return this;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        BiomeGenBase biome = context.getWorld()
            .getBiomeGenForCoords(context.getX(), context.getZ());

        // 1. Check Biome Name or ID
        if (!allowedBiomes.isEmpty()) {
            String name = biome.biomeName;
            int id = biome.biomeID;
            boolean matched = false;
            for (String allowed : allowedBiomes) {
                if (allowed.equalsIgnoreCase(name) || allowed.equals(String.valueOf(id))) {
                    matched = true;
                    break;
                }
            }
            if (!matched) return false;
        }

        // 2. Check BiomeDictionary Tags
        if (!allowedTags.isEmpty()) {
            boolean matched = false;
            for (String tag : allowedTags) {
                try {
                    BiomeDictionary.Type type = BiomeDictionary.Type.valueOf(tag.toUpperCase());
                    if (BiomeDictionary.isBiomeOfType(biome, type)) {
                        matched = true;
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    // Ignore invalid tags
                }
            }
            if (!matched) return false;
        }

        // 3. Check Temperature
        if (minTemp != null || maxTemp != null) {
            float temp = biome.getFloatTemperature(context.getX(), context.getY(), context.getZ());
            if (minTemp != null && temp < minTemp) return false;
            if (maxTemp != null && temp > maxTemp) return false;
        }

        // 4. Check Humidity
        if (minHumid != null || maxHumid != null) {
            float humid = biome.getFloatRainfall();
            if (minHumid != null && humid < minHumid) return false;
            if (maxHumid != null && humid > maxHumid) return false;
        }

        return true;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(StatCollector.translateToLocal("omoshiroikamo.condition.biome.base"));
        if (!allowedBiomes.isEmpty()) sb.append(" Biomes:")
            .append(allowedBiomes);
        if (!allowedTags.isEmpty()) sb.append(" Tags:")
            .append(allowedTags);
        if (minTemp != null || maxTemp != null) sb.append(String.format(" Temp[%s, %s]", minTemp, maxTemp));
        if (minHumid != null || maxHumid != null) sb.append(String.format(" Humid[%s, %s]", minHumid, maxHumid));
        return sb.toString();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "biome");
        if (!allowedBiomes.isEmpty()) {
            JsonArray array = new JsonArray();
            for (String b : allowedBiomes) array.add(new JsonPrimitive(b));
            json.add("biomes", array);
        }
        if (!allowedTags.isEmpty()) {
            JsonArray array = new JsonArray();
            for (String t : allowedTags) array.add(new JsonPrimitive(t));
            json.add("tags", array);
        }
        if (minTemp != null) json.addProperty("minTemp", minTemp);
        if (maxTemp != null) json.addProperty("maxTemp", maxTemp);
        if (minHumid != null) json.addProperty("minHumid", minHumid);
        if (maxHumid != null) json.addProperty("maxHumid", maxHumid);
    }

    public static ICondition fromJson(JsonObject json) {
        List<String> biomes = new ArrayList<>();
        if (json.has("biomes")) {
            JsonArray array = json.getAsJsonArray("biomes");
            for (JsonElement e : array) biomes.add(e.getAsString());
        }
        List<String> tags = new ArrayList<>();
        if (json.has("tags")) {
            JsonArray array = json.getAsJsonArray("tags");
            for (JsonElement e : array) tags.add(e.getAsString());
        }
        BiomeCondition cond = new BiomeCondition(biomes, tags);
        if (json.has("minTemp")) cond.minTemp = json.get("minTemp")
            .getAsDouble();
        if (json.has("maxTemp")) cond.maxTemp = json.get("maxTemp")
            .getAsDouble();
        if (json.has("minHumid")) cond.minHumid = json.get("minHumid")
            .getAsDouble();
        if (json.has("maxHumid")) cond.maxHumid = json.get("maxHumid")
            .getAsDouble();
        return cond;
    }
}
