package ruiseki.omoshiroikamo.api.condition;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Condition that checks the current biome.
 * Supports biome names or IDs.
 */
public class BiomeCondition implements ICondition {

    private final List<String> allowedBiomes;

    public BiomeCondition(List<String> allowedBiomes) {
        this.allowedBiomes = allowedBiomes;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        BiomeGenBase biome = context.getWorld()
            .getBiomeGenForCoords(context.getX(), context.getZ());
        String name = biome.biomeName;
        int id = biome.biomeID;

        for (String allowed : allowedBiomes) {
            if (allowed.equalsIgnoreCase(name) || allowed.equals(String.valueOf(id))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocalFormatted("omoshiroikamo.condition.biome", allowedBiomes.toString());
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "biome");
        JsonArray array = new JsonArray();
        for (String biome : allowedBiomes) {
            array.add(biome);
        }
        json.add("biomes", array);
    }

    public static ICondition fromJson(JsonObject json) {
        List<String> biomes = new ArrayList<>();
        if (json.has("biomes")) {
            JsonArray array = json.getAsJsonArray("biomes");
            for (JsonElement e : array) {
                biomes.add(e.getAsString());
            }
        }
        return new BiomeCondition(biomes);
    }
}
