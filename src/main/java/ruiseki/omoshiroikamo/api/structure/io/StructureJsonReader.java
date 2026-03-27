package ruiseki.omoshiroikamo.api.structure.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.structure.core.BlockMapping;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.api.structure.core.IStructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.ISymbolMapping;
import ruiseki.omoshiroikamo.api.structure.core.StructureEntryBuilder;
import ruiseki.omoshiroikamo.api.structure.core.StructureLayer;
import ruiseki.omoshiroikamo.api.structure.core.TierStructureRef;
import ruiseki.omoshiroikamo.api.structure.core.TieredBlockMapping;
import ruiseki.omoshiroikamo.core.json.AbstractJsonReader;

/**
 * Reader that parses JSON into IStructureEntry.
 * Implements AbstractJsonReader for consistency with other JSON loaders.
 */
public class StructureJsonReader extends AbstractJsonReader<StructureJsonReader.FileData> {

    public static class FileData {

        public final Map<String, IStructureEntry> structures = new LinkedHashMap<>();
        public final Map<Character, ISymbolMapping> defaultMappings = new HashMap<>();

        public void merge(FileData other) {
            if (other == null) return;
            this.structures.putAll(other.structures);
            this.defaultMappings.putAll(other.defaultMappings);
        }
    }

    public StructureJsonReader(File path) {
        super(path);
    }

    /**
     * Static helper to read FileData from a JsonElement.
     * Useful for testing and cases where the JSON is already parsed.
     */
    public static FileData readFile(JsonElement root) {
        return new StructureJsonReader(null).readFile(root, null);
    }

    @Override
    public FileData read() throws IOException {
        FileData data = new FileData();
        if (path.isDirectory()) {
            List<File> files = listJsonFiles(path);
            for (File file : files) {
                data.merge(readFile(file));
            }
        } else if (path.exists()) {
            data.merge(readFile(path));
        }
        this.cache = data;
        return data;
    }

    @Override
    protected FileData readFile(JsonElement root, File file) {
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

    private void parseDefaultMappings(JsonObject obj, Map<Character, ISymbolMapping> target) {
        if (obj.has("mappings")) {
            JsonObject mappingsObj = obj.getAsJsonObject("mappings");
            for (Map.Entry<String, JsonElement> entry : mappingsObj.entrySet()) {
                if (entry.getKey()
                    .isEmpty()) continue;
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
        String name = json.has("name") ? json.get("name")
            .getAsString() : null;
        builder.setName(name);
        if (json.has("displayName")) {
            builder.setDisplayName(
                json.get("displayName")
                    .getAsString());
        }

        // 1.5. externalPorts
        if (json.has("externalPorts")) {
            JsonArray portsArray = json.getAsJsonArray("externalPorts");
            for (JsonElement el : portsArray) {
                String str = el.getAsString();
                if (!str.isEmpty()) {
                    builder.addExternalPort(str.charAt(0));
                }
            }
        }

        if (json.has("inputOnly")) {
            JsonArray portsArray = json.getAsJsonArray("inputOnly");
            for (JsonElement el : portsArray) {
                String str = el.getAsString();
                if (!str.isEmpty()) {
                    builder.addFixedExternalPort(str.charAt(0), EnumIO.INPUT);
                }
            }
        }

        if (json.has("outputOnly")) {
            JsonArray portsArray = json.getAsJsonArray("outputOnly");
            for (JsonElement el : portsArray) {
                String str = el.getAsString();
                if (!str.isEmpty()) {
                    builder.addFixedExternalPort(str.charAt(0), EnumIO.OUTPUT);
                }
            }
        }

        if (json.has("bothOnly")) {
            JsonArray portsArray = json.getAsJsonArray("bothOnly");
            for (JsonElement el : portsArray) {
                String str = el.getAsString();
                if (!str.isEmpty()) {
                    builder.addFixedExternalPort(str.charAt(0), EnumIO.BOTH);
                }
            }
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
                if (entry.getKey()
                    .isEmpty()) continue;
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
        int[] finalOffset = null;
        if (json.has("controllerOffset")) {
            JsonArray offsetArray = json.getAsJsonArray("controllerOffset");
            if (offsetArray.size() >= 3) {
                finalOffset = new int[3];
                finalOffset[0] = offsetArray.get(0)
                    .getAsInt();
                finalOffset[1] = offsetArray.get(1)
                    .getAsInt();
                finalOffset[2] = offsetArray.get(2)
                    .getAsInt();
                builder.setControllerOffset(finalOffset);
            }
        }

        // 5. properties (tintColor, multipliers, batch limits)
        if (json.has("tintColor")) {
            builder.setTintColor(
                json.get("tintColor")
                    .getAsString());
        }
        if (json.has("speedMultiplier")) {
            builder.setSpeedMultiplier(
                json.get("speedMultiplier")
                    .getAsDouble());
        }
        if (json.has("energyMultiplier")) {
            builder.setEnergyMultiplier(
                json.get("energyMultiplier")
                    .getAsDouble());
        }
        if (json.has("batchMin")) {
            builder.setBatchMin(
                json.get("batchMin")
                    .getAsInt());
        }
        if (json.has("batchMax")) {
            builder.setBatchMax(
                json.get("batchMax")
                    .getAsInt());
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
            JsonElement reqsElement = json.get("requirements");
            if (reqsElement.isJsonArray()) {
                JsonArray reqsArray = reqsElement.getAsJsonArray();
                for (int i = 0; i < reqsArray.size(); i++) {
                    JsonObject reqObj = reqsArray.get(i)
                        .getAsJsonObject();
                    String type = reqObj.has("type") ? reqObj.get("type")
                        .getAsString() : null;
                    if (type != null) {
                        IStructureRequirement req = RequirementRegistry.parse(type, reqObj);
                        if (req != null) builder.addRequirement(req);
                    }
                }
            } else if (reqsElement.isJsonObject()) {
                JsonObject reqsObj = reqsElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : reqsObj.entrySet()) {
                    String type = entry.getKey();
                    if (entry.getValue()
                        .isJsonObject()) {
                        IStructureRequirement req = RequirementRegistry.parse(
                            type,
                            entry.getValue()
                                .getAsJsonObject());
                        if (req != null) builder.addRequirement(req);
                    }
                }
            }
        }

        // 9. tierStructures
        if (json.has("tierStructures")) {
            JsonArray tierArray = json.getAsJsonArray("tierStructures");
            for (JsonElement el : tierArray) {
                if (el.isJsonObject()) {
                    JsonObject tierObj = el.getAsJsonObject();
                    String tName = tierObj.has("name") ? tierObj.get("name")
                        .getAsString() : null;
                    if (tName == null) continue;

                    int tTier = tierObj.has("tier") ? tierObj.get("tier")
                        .getAsInt() : 1;
                    String tComponent = tierObj.has("component") ? tierObj.get("component")
                        .getAsString() : "structure";
                    String tMode = tierObj.has("mode") ? tierObj.get("mode")
                        .getAsString() : "tier";
                    boolean isCount = "count".equalsIgnoreCase(tMode);

                    List<TierStructureRef.OffsetPair> offsetPairs = new ArrayList<>();
                    if (tierObj.has("offsets")) {
                        JsonArray offsetsArray = tierObj.getAsJsonArray("offsets");
                        for (JsonElement offEl : offsetsArray) {
                            if (offEl.isJsonArray()) {
                                JsonArray ja = offEl.getAsJsonArray();
                                if (ja.size() >= 3) {
                                    int[] target = { ja.get(0)
                                        .getAsInt(),
                                        ja.get(1)
                                            .getAsInt(),
                                        ja.get(2)
                                            .getAsInt() };
                                    offsetPairs.add(new TierStructureRef.OffsetPair(target, null));
                                }
                            } else if (offEl.isJsonObject()) {
                                JsonObject offObj = offEl.getAsJsonObject();
                                int[] target = null;
                                int[] anchor = null;
                                if (offObj.has("target")) {
                                    JsonArray ja = offObj.getAsJsonArray("target");
                                    if (ja.size() >= 3) {
                                        target = new int[] { ja.get(0)
                                            .getAsInt(),
                                            ja.get(1)
                                                .getAsInt(),
                                            ja.get(2)
                                                .getAsInt() };
                                    }
                                }
                                if (offObj.has("anchor")) {
                                    JsonArray ja = offObj.getAsJsonArray("anchor");
                                    if (ja.size() >= 3) {
                                        anchor = new int[] { ja.get(0)
                                            .getAsInt(),
                                            ja.get(1)
                                                .getAsInt(),
                                            ja.get(2)
                                                .getAsInt() };
                                    }
                                }
                                if (target != null) {
                                    offsetPairs.add(new TierStructureRef.OffsetPair(target, anchor));
                                }
                            }
                        }
                    } else {
                        offsetPairs.add(new TierStructureRef.OffsetPair(null, null));
                    }

                    builder.addTierStructure(new TierStructureRef(tName, tTier, tComponent, offsetPairs, isCount));
                }
            }
        }

        return builder.build();
    }

    private static ISymbolMapping parseMapping(char symbol, JsonElement element) {
        if (element.isJsonPrimitive()) {
            return new BlockMapping(symbol, element.getAsString());
        } else if (element.isJsonArray()) {
            JsonArray blocksArray = element.getAsJsonArray();
            List<String> blocks = new ArrayList<>();
            for (JsonElement blockEl : blocksArray) {
                if (blockEl.isJsonObject()) {
                    JsonObject blockObj = blockEl.getAsJsonObject();
                    if (blockObj.has("id")) {
                        blocks.add(
                            blockObj.get("id")
                                .getAsString());
                    }
                } else {
                    blocks.add(blockEl.getAsString());
                }
            }
            return new BlockMapping(symbol, blocks);
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            ISymbolMapping mapping = null;
            if (obj.has("block") || obj.has("id")) {
                mapping = new BlockMapping(symbol, (obj.has("block") ? obj.get("block") : obj.get("id")).getAsString());
            } else if (obj.has("blocks")) {
                JsonArray blocksArray = obj.getAsJsonArray("blocks");
                List<String> blocks = new ArrayList<>();
                for (JsonElement blockEl : blocksArray) {
                    if (blockEl.isJsonObject()) {
                        JsonObject blockObj = blockEl.getAsJsonObject();
                        if (blockObj.has("id")) {
                            blocks.add(
                                blockObj.get("id")
                                    .getAsString());
                        }
                    } else {
                        blocks.add(blockEl.getAsString());
                    }
                }
                mapping = new BlockMapping(symbol, blocks);
            } else if (obj.has("component") || obj.has("tiers")) {
                String componentName = obj.has("component") ? obj.get("component")
                    .getAsString() : "default";
                Map<String, Integer> tiers = new HashMap<>();
                if (obj.has("tiers")) {
                    JsonObject tiersObj = obj.getAsJsonObject("tiers");
                    for (Map.Entry<String, JsonElement> tierEntry : tiersObj.entrySet()) {
                        tiers.put(
                            tierEntry.getKey(),
                            tierEntry.getValue()
                                .getAsInt());
                    }
                }
                mapping = new TieredBlockMapping(symbol, componentName, tiers);
            }

            if (mapping != null && obj.has("index")) {
                int index = obj.get("index")
                    .getAsInt();
                if (mapping instanceof BlockMapping bm) {
                    bm.setPortIndex(index);
                } else if (mapping instanceof TieredBlockMapping tbm) {
                    tbm.setPortIndex(index);
                }
            }
            return mapping;
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
