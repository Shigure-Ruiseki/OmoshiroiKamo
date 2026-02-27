package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.WeightedRandom;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Decorator that picks outputs randomly from a weighted pool.
 */
public class WeightedRandomDecorator extends RecipeDecorator {

    private final List<WeightedOutputEntry> pool;
    private final int rolls;
    private final Random rand = new Random();

    public WeightedRandomDecorator(IModularRecipe internal, List<WeightedOutputEntry> pool, int rolls) {
        super(internal);
        this.pool = pool;
        this.rolls = rolls;
    }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        // Process original outputs first
        if (!internal.processOutputs(outputPorts, simulate)) {
            return false;
        }

        if (!simulate) {
            for (int i = 0; i < rolls; i++) {
                WeightedOutputEntry picked = (WeightedOutputEntry) WeightedRandom.getRandomItem(rand, pool);
                if (picked != null) {
                    List<IModularPort> filtered = filterByType(outputPorts, picked.output.getPortType());
                    if (picked.output.checkCapacity(filtered)) {
                        picked.output.apply(filtered);
                    }
                }
            }
        }

        return true;
    }

    private List<IModularPort> filterByType(List<IModularPort> ports, IPortType.Type type) {
        List<IModularPort> filtered = new ArrayList<>();
        for (IModularPort port : ports) {
            if (port.getPortType() == type) {
                filtered.add(port);
            }
        }
        return filtered;
    }

    /**
     * Entry for the weighted pool.
     */
    public static class WeightedOutputEntry extends WeightedRandom.Item {

        public final IRecipeOutput output;

        public WeightedOutputEntry(IRecipeOutput output, int weight) {
            super(weight);
            this.output = output;
        }
    }

    public List<WeightedOutputEntry> getPool() {
        return pool;
    }

    public int getRolls() {
        return rolls;
    }

    public static IModularRecipe fromJson(IModularRecipe recipe, JsonObject json) {
        int rolls = json.get("rolls")
            .getAsInt();
        List<WeightedOutputEntry> pool = new ArrayList<>();
        JsonArray arr = json.getAsJsonArray("pool");
        for (JsonElement e : arr) {
            JsonObject obj = e.getAsJsonObject();
            int weight = obj.get("weight")
                .getAsInt();
            IRecipeOutput out = OutputParserRegistry.parse(obj.getAsJsonObject("output"));
            pool.add(new WeightedOutputEntry(out, weight));
        }
        return new WeightedRandomDecorator(recipe, pool, rolls);
    }
}
