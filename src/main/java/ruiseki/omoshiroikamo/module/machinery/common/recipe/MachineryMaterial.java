package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionParserRegistry;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.modular.recipe.DecoratorParser;
import ruiseki.omoshiroikamo.api.modular.recipe.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.InputParserRegistry;
import ruiseki.omoshiroikamo.api.modular.recipe.OutputParserRegistry;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

/**
 * Material class representing a single Modular Machinery recipe.
 */
public class MachineryMaterial extends AbstractJsonMaterial {

    public String registryName;
    public String localizedName;
    public String machine;
    public int time = 20;
    public final List<IRecipeInput> inputs = new ArrayList<>();
    public final List<IRecipeOutput> outputs = new ArrayList<>();
    public final List<ICondition> conditions = new ArrayList<>();
    private JsonObject rawJson;

    @Override
    public void read(JsonObject json) {
        this.rawJson = json;
        this.registryName = getString(json, "registryName", null);
        this.localizedName = getString(json, "localizedName", getString(json, "name", null));
        this.machine = getString(json, "machine", getString(json, "group", null));
        this.time = getInt(json, "duration", getInt(json, "time", 20));

        // Auto-generate registryName if missing
        if ((this.registryName == null || this.registryName.isEmpty()) && this.localizedName != null) {
            this.registryName = this.localizedName.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");
        }

        if (json.has("input") || json.has("inputs")) {
            JsonArray arr = json.has("input") ? json.getAsJsonArray("input") : json.getAsJsonArray("inputs");
            for (JsonElement e : arr) {
                if (e.isJsonObject()) {
                    IRecipeInput input = InputParserRegistry.parse(e.getAsJsonObject());
                    if (input != null) inputs.add(input);
                }
            }
        }

        if (json.has("output") || json.has("outputs")) {
            JsonArray arr = json.has("output") ? json.getAsJsonArray("output") : json.getAsJsonArray("outputs");
            for (JsonElement e : arr) {
                if (e.isJsonObject()) {
                    IRecipeOutput output = OutputParserRegistry.parse(e.getAsJsonObject());
                    if (output != null) outputs.add(output);
                }
            }
        }

        if (json.has("condition") || json.has("conditions")) {
            JsonArray arr = json.has("condition") ? json.getAsJsonArray("condition")
                : json.getAsJsonArray("conditions");
            for (JsonElement e : arr) {
                if (e.isJsonObject()) {
                    ICondition condition = ConditionParserRegistry.parse(e.getAsJsonObject());
                    if (condition != null) conditions.add(condition);
                }
            }
        }

        captureUnknownProperties(
            json,
            "registryName",
            "localizedName",
            "machine",
            "time",
            "input",
            "inputs",
            "output",
            "outputs",
            "condition",
            "conditions",
            "decorators");
    }

    public IModularRecipe applyDecorators(IModularRecipe recipe, JsonObject json) {
        if (json.has("decorators")) {
            return DecoratorParser.parse(recipe, json.get("decorators"));
        }
        return recipe;
    }

    public JsonObject getRawJson() {
        return rawJson;
    }

    @Override
    public void write(JsonObject json) {
        if (registryName != null) json.addProperty("registryName", registryName);
        if (localizedName != null) json.addProperty("localizedName", localizedName);
        if (machine != null) json.addProperty("machine", machine);
        json.addProperty("time", time);

        if (!inputs.isEmpty()) {
            JsonArray arr = new JsonArray();
            for (IRecipeInput input : inputs) {
                JsonObject obj = new JsonObject();
                input.write(obj);
                arr.add(obj);
            }
            json.add("input", arr);
        }

        if (!outputs.isEmpty()) {
            JsonArray arr = new JsonArray();
            for (IRecipeOutput output : outputs) {
                JsonObject obj = new JsonObject();
                output.write(obj);
                arr.add(obj);
            }
            json.add("output", arr);
        }

        if (!conditions.isEmpty()) {
            JsonArray arr = new JsonArray();
            for (ICondition condition : conditions) {
                JsonObject obj = new JsonObject();
                condition.write(obj);
                arr.add(obj);
            }
            json.add("condition", arr);
        }

        unknownProperties.forEach(json::add);
    }

    @Override
    public boolean validate() {
        if (registryName == null || registryName.isEmpty()) {
            logValidationError("registryName is missing");
            return false;
        }
        if (machine == null || machine.isEmpty()) {
            logValidationError("machine is missing");
            return false;
        }
        return true;
    }

    @Override
    public void set(String key, Object value) {}

    @Override
    public Object get(String key) {
        return null;
    }
}
