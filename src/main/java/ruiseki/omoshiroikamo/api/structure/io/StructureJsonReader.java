package ruiseki.omoshiroikamo.api.structure.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;

/**
 * Reader that parses JSON into IStructureEntry.
 */
public class StructureJsonReader {

    public static class FileData {

        public final Map<String, IStructureEntry> structures = new LinkedHashMap<>();
        public final Map<Character, ISymbolMapping> defaultMappings = new HashMap<>();
    }

    public static FileData readFile(JsonElement root) {
        FileData data = new FileData();

        if (root.isJsonObject()) {
            JsonObject obj = root.getAsJsonObject();
            if (obj.has("layers")) {
                IStructureEntry entry = readEntry(obj);
                data.structures.put(entry.getName(), entry);
            } else if (obj.has("mappings")) {
                parseDefaultMappings(obj, data.defaultMappings);
            }
        } else if (root.isJsonArray()) {
            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = obj.has("name") ? obj.get("name")
                        .getAsString() : "";
                    if ("default".equals(name) || "defaults".equals(name)) {
                        parseDefaultMappings(obj, data.defaultMappings);
                    } else if (obj.has("layers")) {
                        IStructureEntry entry = readEntry(obj);
                        data.structures.put(entry.getName(), entry);
                    }
                }
            }
        }

        return data;
    }

    private static void parseDefaultMappings(JsonObject obj, Map<Character, ISymbolMapping> target) {
        if (obj.has("mappings")) {
            JsonObject mappingsObj = obj.getAsJsonObject("mappings");
            for (Map.Entry<String, JsonElement> entry : mappingsObj.entrySet()) {
                char symbol = entry.getKey()
                    .charAt(0);
                ISymbolMapping mapping = parseMapping(symbol, entry.getValue());
                if (mapping != null) target.put(symbol, mapping);
            }
        }
    }

    public static IStructureEntry readEntry(JsonObject json) {
        StructureEntryBuilder builder = new StructureEntryBuilder();

        // 1. Basic Info
        String name = json.get("name")
            .getAsString();
        builder.setName(name);
        if (json.has("displayName")) {
            builder.setDisplayName(
                json.get("displayName")
                    .getAsString());
        }

        // 2. recipeGroup
        if (json.has("recipeGroup")) {
            JsonElement groupElement = json.get("recipeGroup");
            if (groupElement.isJsonArray()) {
                for (JsonElement ge : groupElement.getAsJsonArray()) {
                    builder.addRecipeGroup(ge.getAsString());
                }
            } else {
                builder.addRecipeGroup(groupElement.getAsString());
            }
        }

        // 3. Mappings
        if (json.has("mappings")) {
            JsonObject mappingsObj = json.getAsJsonObject("mappings");
            for (Map.Entry<String, JsonElement> entry : mappingsObj.entrySet()) {
                char symbol = entry.getKey()
                    .charAt(0);
                ISymbolMapping mapping = parseMapping(symbol, entry.getValue());
                if (mapping != null) {
                    builder.addMapping(symbol, mapping);
                }
            }
        }

        // 3. Layers
        if (json.has("layers")) {
            JsonArray layersArray = json.getAsJsonArray("layers");
            for (int i = 0; i < layersArray.size(); i++) {
                JsonElement layerEl = layersArray.get(i);
                if (layerEl.isJsonObject()) {
                    IStructureLayer layer = parseLayer(layerEl.getAsJsonObject());
                    if (layer != null) builder.addLayer(layer);
                } else if (layerEl.isJsonArray()) {
                    // Traditional format: Array of strings
                    JsonArray rowsArray = layerEl.getAsJsonArray();
                    List<String> rows = new ArrayList<>();
                    for (JsonElement rowEl : rowsArray) {
                        rows.add(rowEl.getAsString());
                    }
                    builder.addLayer(new StructureLayer("y" + (layersArray.size() - 1 - i), rows));
                }
            }
        }

        // 4. controllerOffset
        if (json.has("controllerOffset")) {
            JsonArray offsetArray = json.getAsJsonArray("controllerOffset");
            if (offsetArray.size() >= 3) {
                int[] offset = new int[3];
                offset[0] = offsetArray.get(0)
                    .getAsInt();
                offset[1] = offsetArray.get(1)
                    .getAsInt();
                offset[2] = offsetArray.get(2)
                    .getAsInt();
                builder.setControllerOffset(offset);
            }
        }

        // 5. tintColor
        if (json.has("tintColor")) {
            builder.setTintColor(
                json.get("tintColor")
                    .getAsString());
        }

        // 6. tier
        if (json.has("tier")) {
            builder.setTier(
                json.get("tier")
                    .getAsInt());
        }

        // 7. defaultFacing
        if (json.has("defaultFacing")) {
            builder.setDefaultFacing(
                json.get("defaultFacing")
                    .getAsString());
        }

        // 8. Requirements
        if (json.has("requirements")) {
            JsonArray reqsArray = json.getAsJsonArray("requirements");
            for (int i = 0; i < reqsArray.size(); i++) {
                JsonObject reqObj = reqsArray.get(i)
                    .getAsJsonObject();
                String type = reqObj.get("type")
                    .getAsString();
                IStructureRequirement req = RequirementRegistry.parse(type, reqObj);
                if (req != null) {
                    builder.addRequirement(req);
                }
            }
        }

        return builder.build();
    }

    private static ISymbolMapping parseMapping(char symbol, JsonElement element) {
        if (element.isJsonPrimitive()) {
            return new BlockMapping(symbol, element.getAsString());
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("block")) {
                return new BlockMapping(
                    symbol,
                    obj.get("block")
                        .getAsString());
            } else if (obj.has("blocks")) {
                JsonArray blocksArray = obj.getAsJsonArray("blocks");
                List<String> blocks = new ArrayList<>();
                for (JsonElement blockEl : blocksArray) {
                    blocks.add(blockEl.getAsString());
                }
                return new BlockMapping(symbol, blocks);
            }
        }
        return null;
    }

    private static IStructureLayer parseLayer(JsonObject obj) {
        String name = obj.has("name") ? obj.get("name")
            .getAsString() : null;
        JsonArray rowsArray = obj.getAsJsonArray("rows");
        List<String> rows = new ArrayList<>();
        for (JsonElement rowEl : rowsArray) {
            rows.add(rowEl.getAsString());
        }
        return new StructureLayer(name, rows);
    }
}
