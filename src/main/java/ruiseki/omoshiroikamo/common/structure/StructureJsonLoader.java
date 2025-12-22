package ruiseki.omoshiroikamo.common.structure;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.BlockEntry;
import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.BlockMapping;
import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.Layer;
import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.common.util.Logger;

/**
 * 構造体JSONファイルのロードとパース
 */
public class StructureJsonLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();

    /** デフォルトマッピング（全構造体で共有） */
    private Map<Character, BlockMapping> defaultMappings = new HashMap<>();

    /** 構造体定義のキャッシュ（name -> StructureEntry） */
    private Map<String, StructureEntry> structureCache = new HashMap<>();

    /** パース済みの形状（name -> String[][]） */
    private Map<String, String[][]> parsedShapes = new HashMap<>();

    /** 収集されたエラーリスト */
    private List<String> errors = new ArrayList<>();

    /**
     * エラーリストを取得
     */
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * エラーを追加
     */
    private void addError(String error) {
        errors.add(error);
    }

    /**
     * JSONファイルをロード
     */
    public boolean loadFromFile(File file) {
        if (!file.exists()) {
            Logger.warn("Structure file not found: " + file.getAbsolutePath());
            return false;
        }

        try (FileReader reader = new FileReader(file)) {
            JsonElement rootElement = new JsonParser().parse(reader);

            // 空ファイルや不正な形式のチェック
            if (rootElement == null || rootElement.isJsonNull()) {
                Logger.warn("Structure file is empty or null: " + file.getName());
                return false;
            }

            if (!rootElement.isJsonArray()) {
                Logger.warn("Structure file is not a JSON array: " + file.getName());
                return false;
            }

            JsonArray array = rootElement.getAsJsonArray();
            if (array.size() == 0) {
                Logger.warn("Structure file contains empty array: " + file.getName());
                return false;
            }

            for (JsonElement element : array) {
                if (element == null || !element.isJsonObject()) {
                    Logger.warn("Invalid entry in structure file (not an object): " + file.getName());
                    continue;
                }

                JsonObject obj = element.getAsJsonObject();

                if (!obj.has("name") || obj.get("name")
                    .isJsonNull()) {
                    Logger.warn("Structure entry missing 'name' field: " + file.getName());
                    continue;
                }

                String name = obj.get("name")
                    .getAsString();

                if ("default".equals(name)) {
                    // デフォルトマッピングをパース
                    parseDefaultMappings(obj);
                } else {
                    // 構造体エントリをパース
                    StructureEntry entry = parseStructureEntry(obj);
                    if (entry != null) {
                        structureCache.put(name, entry);
                    }
                }
            }

            Logger.info("Loaded structure file: " + file.getName() + " (" + structureCache.size() + " structures)");
            return true;

        } catch (Exception e) {
            Logger.error("Failed to load structure file: " + file.getAbsolutePath(), e);
            return false;
        }
    }

    /**
     * デフォルトマッピングをパース
     */
    private void parseDefaultMappings(JsonObject obj) {
        if (!obj.has("mappings")) return;

        JsonObject mappingsObj = obj.getAsJsonObject("mappings");
        for (Map.Entry<String, JsonElement> entry : mappingsObj.entrySet()) {
            char symbol = entry.getKey()
                .charAt(0);
            BlockMapping mapping = parseBlockMapping(entry.getValue());
            if (mapping != null) {
                defaultMappings.put(symbol, mapping);
            }
        }
    }

    /**
     * 構造体エントリをパース
     */
    private StructureEntry parseStructureEntry(JsonObject obj) {
        StructureEntry entry = new StructureEntry();
        entry.name = obj.get("name")
            .getAsString();

        // レイヤーをパース
        if (obj.has("layers")) {
            entry.layers = new ArrayList<>();
            JsonArray layersArray = obj.getAsJsonArray("layers");
            for (JsonElement layerElement : layersArray) {
                Layer layer = parseLayer(layerElement.getAsJsonObject());
                if (layer != null) {
                    entry.layers.add(layer);
                }
            }
        }

        // マッピングをパース（オプション）
        if (obj.has("mappings")) {
            entry.mappings = new HashMap<>();
            JsonObject mappingsObj = obj.getAsJsonObject("mappings");
            for (Map.Entry<String, JsonElement> mapEntry : mappingsObj.entrySet()) {
                entry.mappings.put(mapEntry.getKey(), parseBlockMapping(mapEntry.getValue()));
            }
        }

        return entry;
    }

    /**
     * レイヤーをパース
     */
    private Layer parseLayer(JsonObject obj) {
        Layer layer = new Layer();
        layer.name = obj.has("name") ? obj.get("name")
            .getAsString() : "";
        layer.rows = new ArrayList<>();

        if (obj.has("rows")) {
            JsonArray rowsArray = obj.getAsJsonArray("rows");
            for (JsonElement rowElement : rowsArray) {
                layer.rows.add(rowElement.getAsString());
            }
        }

        return layer;
    }

    /**
     * ブロックマッピングをパース（文字列 or オブジェクト形式）
     */
    private BlockMapping parseBlockMapping(JsonElement element) {
        BlockMapping mapping = new BlockMapping();

        if (element.isJsonPrimitive()) {
            // 単純な文字列形式: "mod:block:meta"
            mapping.block = element.getAsString();
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();

            // 単一ブロック形式: { "block": "...", "max": 1 }
            if (obj.has("block")) {
                mapping.block = obj.get("block")
                    .getAsString();
            }

            // 複数候補形式: { "blocks": [...] }
            if (obj.has("blocks")) {
                mapping.blocks = new ArrayList<>();
                JsonArray blocksArray = obj.getAsJsonArray("blocks");
                for (JsonElement blockElement : blocksArray) {
                    BlockEntry blockEntry = parseBlockEntry(blockElement);
                    if (blockEntry != null) {
                        mapping.blocks.add(blockEntry);
                    }
                }
            }

            // min/max
            if (obj.has("min")) mapping.min = obj.get("min")
                .getAsInt();
            if (obj.has("max")) mapping.max = obj.get("max")
                .getAsInt();
        }

        return mapping;
    }

    /**
     * 個別ブロックエントリをパース
     */
    private BlockEntry parseBlockEntry(JsonElement element) {
        BlockEntry entry = new BlockEntry();

        if (element.isJsonPrimitive()) {
            // 単純な文字列: "mod:block:meta"
            entry.id = element.getAsString();
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            entry.id = obj.get("id")
                .getAsString();
            if (obj.has("min")) entry.min = obj.get("min")
                .getAsInt();
            if (obj.has("max")) entry.max = obj.get("max")
                .getAsInt();
        }

        return entry;
    }

    /**
     * 構造体名からString[][]形状を取得
     */
    public String[][] getShape(String structureName) {
        // キャッシュ確認
        if (parsedShapes.containsKey(structureName)) {
            return parsedShapes.get(structureName);
        }

        StructureEntry entry = structureCache.get(structureName);
        if (entry == null || entry.layers == null || entry.layers.isEmpty()) {
            return null;
        }

        try {
            // レイヤーからString[][]を構築
            String[][] shape = new String[entry.layers.size()][];
            for (int i = 0; i < entry.layers.size(); i++) {
                Layer layer = entry.layers.get(i);
                if (layer == null || layer.rows == null) {
                    // 不正なレイヤーがある場合は空配列で対応
                    shape[i] = new String[0];
                } else {
                    shape[i] = layer.rows.toArray(new String[0]);
                }
            }

            parsedShapes.put(structureName, shape);
            return shape;
        } catch (Exception e) {
            Logger.error("Failed to parse structure shape: " + structureName, e);
            return null;
        }
    }

    /**
     * シンボルからマッピングを取得（構造体固有 > デフォルト）
     */
    public BlockMapping getMapping(String structureName, char symbol) {
        // 構造体固有マッピングを確認
        StructureEntry entry = structureCache.get(structureName);
        if (entry != null && entry.mappings != null) {
            Object mapping = entry.mappings.get(String.valueOf(symbol));
            if (mapping instanceof BlockMapping) {
                return (BlockMapping) mapping;
            }
        }

        // デフォルトマッピングを返す
        return defaultMappings.get(symbol);
    }

    /**
     * JSONファイルに書き出し
     */
    public static void writeToFile(File file, List<StructureEntry> entries) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(entries, writer);
        }
    }
}
