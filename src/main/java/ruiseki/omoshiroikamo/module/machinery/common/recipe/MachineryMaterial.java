package ruiseki.omoshiroikamo.module.machinery.common.recipe;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

    @Override
    public void read(JsonObject json) {
        this.registryName = getString(json, "registryName", null);
        this.localizedName = getString(json, "localizedName", null);
        this.machine = getString(json, "machine", null);
        this.time = getInt(json, "time", 20);

        if (json.has("input")) {
            JsonArray arr = json.getAsJsonArray("input");
            for (JsonElement e : arr) {
                if (e.isJsonObject()) {
                    IRecipeInput input = InputParserRegistry.parse(e.getAsJsonObject());
                    if (input != null) inputs.add(input);
                }
            }
        }

        if (json.has("output")) {
            JsonArray arr = json.getAsJsonArray("output");
            for (JsonElement e : arr) {
                if (e.isJsonObject()) {
                    IRecipeOutput output = OutputParserRegistry.parse(e.getAsJsonObject());
                    if (output != null) outputs.add(output);
                }
            }
        }

        captureUnknownProperties(json, "registryName", "localizedName", "machine", "time", "input", "output");
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
