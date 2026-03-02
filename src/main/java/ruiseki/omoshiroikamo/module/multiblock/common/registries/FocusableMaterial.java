package ruiseki.omoshiroikamo.module.multiblock.common.registries;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;
import ruiseki.omoshiroikamo.module.multiblock.common.registries.FocusableHandler.FocusableType;

/**
 * Material class representing a Focusable entry (Ore, Item, or Block).
 */
public class FocusableMaterial extends AbstractJsonMaterial {

    public FocusableType type;
    public String id;
    public int meta;
    public EnumDye focusColor;
    public double[] weights;
    public double[] focusedWeights;
    public boolean isOreDict;
    public int[] dimensions;

    @Override
    public void read(JsonObject json) {
        // Determine type
        String typeStr = getString(json, "type", null);
        if (typeStr != null) {
            try {
                this.type = FocusableType.valueOf(typeStr.toUpperCase());
            } catch (Exception ignored) {}
        }

        this.id = getString(json, "id", null);
        this.meta = getInt(json, "meta", 0);
        this.isOreDict = getBoolean(json, "isOreDict", false);

        // Inherit legacy type detection if not explicit
        if (this.type == null) {
            if (isOreDict) {
                this.type = FocusableType.ORE;
            } else if (id != null && id.contains("block")) {
                this.type = FocusableType.BLOCK;
            } else {
                this.type = FocusableType.ITEM;
            }
        }

        // Color
        String colorStr = getString(json, "focusColor", "WHITE");
        try {
            this.focusColor = EnumDye.valueOf(colorStr.toUpperCase());
        } catch (Exception e) {
            this.focusColor = EnumDye.WHITE;
        }

        // Weights
        if (json.has("weights")) {
            JsonArray arr = json.getAsJsonArray("weights");
            this.weights = new double[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                this.weights[i] = arr.get(i)
                    .getAsDouble();
            }
        }

        if (json.has("focusedWeights")) {
            JsonArray arr = json.getAsJsonArray("focusedWeights");
            this.focusedWeights = new double[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                this.focusedWeights[i] = arr.get(i)
                    .getAsDouble();
            }
        }

        // Dimensions
        if (json.has("dimensions")) {
            JsonArray arr = json.getAsJsonArray("dimensions");
            this.dimensions = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                this.dimensions[i] = arr.get(i)
                    .getAsInt();
            }
        }

        captureUnknownProperties(
            json,
            "type",
            "id",
            "meta",
            "isOreDict",
            "focusColor",
            "weights",
            "focusedWeights",
            "dimensions");
    }

    @Override
    public void write(JsonObject json) {
        if (type != null) json.addProperty("type", type.name());
        if (id != null) json.addProperty("id", id);
        if (meta != 0) json.addProperty("meta", meta);
        if (isOreDict) json.addProperty("isOreDict", isOreDict);
        if (focusColor != null) json.addProperty("focusColor", focusColor.name());

        if (weights != null) {
            JsonArray arr = new JsonArray();
            for (double w : weights) arr.add(new com.google.gson.JsonPrimitive(w));
            json.add("weights", arr);
        }

        if (focusedWeights != null) {
            JsonArray arr = new JsonArray();
            for (double w : focusedWeights) arr.add(new com.google.gson.JsonPrimitive(w));
            json.add("focusedWeights", arr);
        }

        if (dimensions != null) {
            JsonArray arr = new JsonArray();
            for (int d : dimensions) arr.add(new com.google.gson.JsonPrimitive(d));
            json.add("dimensions", arr);
        }

        // Write unknown properties
        unknownProperties.forEach(json::add);
    }

    @Override
    public boolean validate() {
        if (id == null || id.isEmpty()) {
            logValidationError("ID is missing");
            return false;
        }
        if (type == null) {
            logValidationError("Type is missing or invalid for " + id);
            return false;
        }
        return true;
    }

    @Override
    public void set(String key, Object value) {
        // Implement if needed for writing back
    }

    @Override
    public Object get(String key) {
        switch (key) {
            case "id":
                return id;
            case "type":
                return type;
            case "meta":
                return meta;
            default:
                return null;
        }
    }
}
