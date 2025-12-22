package ruiseki.omoshiroikamo.common.structure;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import ruiseki.omoshiroikamo.common.block.multiblock.quantumBeacon.QuantumBeaconShapes;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore.QuantumOreExtractorShapes;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res.QuantumResExtractorShapes;
import ruiseki.omoshiroikamo.common.block.multiblock.solarArray.SolarArrayShapes;
import ruiseki.omoshiroikamo.common.util.Logger;

/**
 * デフォルト構造体JSONファイルを生成・更新
 */
public class DefaultStructureGenerator {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();

    /**
     * 全ての構造体JSONを生成または更新（欠けているエントリを追加）
     */
    public static void generateAllIfMissing(File configDir) {
        File structuresDir = new File(configDir, "structures");
        if (!structuresDir.exists()) {
            structuresDir.mkdirs();
        }

        // Ore Miner
        updateOreMinerJson(new File(structuresDir, "ore_miner.json"));

        // Resource Miner
        updateResMinerJson(new File(structuresDir, "res_miner.json"));

        // Solar Array
        updateSolarArrayJson(new File(structuresDir, "solar_array.json"));

        // Quantum Beacon
        updateBeaconJson(new File(structuresDir, "quantum_beacon.json"));
    }

    /**
     * Ore Miner JSONを更新（欠けているエントリを追加）
     */
    private static void updateOreMinerJson(File file) {
        Map<String, Map<String, Object>> required = new LinkedHashMap<>();
        required.put("default", createDefaultMappings("oreMiner"));
        required.put(
            "oreExtractorTier1",
            createStructureEntry("oreExtractorTier1", QuantumOreExtractorShapes.SHAPE_TIER_1));
        required.put(
            "oreExtractorTier2",
            createStructureEntry("oreExtractorTier2", QuantumOreExtractorShapes.SHAPE_TIER_2));
        required.put(
            "oreExtractorTier3",
            createStructureEntry("oreExtractorTier3", QuantumOreExtractorShapes.SHAPE_TIER_3));
        required.put(
            "oreExtractorTier4",
            createStructureEntry("oreExtractorTier4", QuantumOreExtractorShapes.SHAPE_TIER_4));
        required.put(
            "oreExtractorTier5",
            createStructureEntry("oreExtractorTier5", QuantumOreExtractorShapes.SHAPE_TIER_5));
        required.put(
            "oreExtractorTier6",
            createStructureEntry("oreExtractorTier6", QuantumOreExtractorShapes.SHAPE_TIER_6));

        updateConfigWithMissing(file, required, "Ore Miner");
    }

    /**
     * Resource Miner JSONを更新
     */
    private static void updateResMinerJson(File file) {
        Map<String, Map<String, Object>> required = new LinkedHashMap<>();
        required.put("default", createDefaultMappings("resMiner"));
        required.put(
            "resExtractorTier1",
            createStructureEntry("resExtractorTier1", QuantumResExtractorShapes.SHAPE_TIER_1));
        required.put(
            "resExtractorTier2",
            createStructureEntry("resExtractorTier2", QuantumResExtractorShapes.SHAPE_TIER_2));
        required.put(
            "resExtractorTier3",
            createStructureEntry("resExtractorTier3", QuantumResExtractorShapes.SHAPE_TIER_3));
        required.put(
            "resExtractorTier4",
            createStructureEntry("resExtractorTier4", QuantumResExtractorShapes.SHAPE_TIER_4));
        required.put(
            "resExtractorTier5",
            createStructureEntry("resExtractorTier5", QuantumResExtractorShapes.SHAPE_TIER_5));
        required.put(
            "resExtractorTier6",
            createStructureEntry("resExtractorTier6", QuantumResExtractorShapes.SHAPE_TIER_6));

        updateConfigWithMissing(file, required, "Resource Miner");
    }

    /**
     * Solar Array JSONを更新
     */
    private static void updateSolarArrayJson(File file) {
        Map<String, Map<String, Object>> required = new LinkedHashMap<>();
        required.put("default", createDefaultMappings("solarArray"));
        required.put("solarArrayTier1", createStructureEntry("solarArrayTier1", SolarArrayShapes.SHAPE_TIER_1));
        required.put("solarArrayTier2", createStructureEntry("solarArrayTier2", SolarArrayShapes.SHAPE_TIER_2));
        required.put("solarArrayTier3", createStructureEntry("solarArrayTier3", SolarArrayShapes.SHAPE_TIER_3));
        required.put("solarArrayTier4", createStructureEntry("solarArrayTier4", SolarArrayShapes.SHAPE_TIER_4));
        required.put("solarArrayTier5", createStructureEntry("solarArrayTier5", SolarArrayShapes.SHAPE_TIER_5));
        required.put("solarArrayTier6", createStructureEntry("solarArrayTier6", SolarArrayShapes.SHAPE_TIER_6));

        updateConfigWithMissing(file, required, "Solar Array");
    }

    /**
     * Quantum Beacon JSONを更新
     */
    private static void updateBeaconJson(File file) {
        Map<String, Map<String, Object>> required = new LinkedHashMap<>();
        required.put("default", createDefaultMappings("beacon"));
        required.put("beaconTier1", createStructureEntry("beaconTier1", QuantumBeaconShapes.SHAPE_TIER_1));
        required.put("beaconTier2", createStructureEntry("beaconTier2", QuantumBeaconShapes.SHAPE_TIER_2));
        required.put("beaconTier3", createStructureEntry("beaconTier3", QuantumBeaconShapes.SHAPE_TIER_3));
        required.put("beaconTier4", createStructureEntry("beaconTier4", QuantumBeaconShapes.SHAPE_TIER_4));
        required.put("beaconTier5", createStructureEntry("beaconTier5", QuantumBeaconShapes.SHAPE_TIER_5));
        required.put("beaconTier6", createStructureEntry("beaconTier6", QuantumBeaconShapes.SHAPE_TIER_6));

        updateConfigWithMissing(file, required, "Quantum Beacon");
    }

    /**
     * 既存JSONファイルを読み込み、欠けているエントリを追加
     */
    private static void updateConfigWithMissing(File file, Map<String, Map<String, Object>> required, String typeName) {
        List<Map<String, Object>> existing = new ArrayList<>();
        boolean fileExisted = file.exists();
        boolean loadFailed = false;

        // 既存ファイルを読み込む
        if (fileExisted) {
            try (FileReader reader = new FileReader(file)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(true);
                Type listType = new TypeToken<ArrayList<Map<String, Object>>>() {}.getType();
                List<Map<String, Object>> loaded = GSON.fromJson(jsonReader, listType);
                if (loaded != null) {
                    existing.addAll(loaded);
                }
            } catch (Exception e) {
                Logger.error("Failed to read existing structure config: " + file.getName(), e);
                loadFailed = true;
                // 読み込み失敗時は新規作成として扱う
            }
        } else {
            // フォルダ作成
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        }

        // 読み込み失敗かつファイルが存在する場合は、バックアップを取って新規作成
        if (loadFailed && fileExisted) {
            File backup = new File(file.getAbsolutePath() + ".backup");
            if (file.renameTo(backup)) {
                Logger.warn("Corrupted config backed up to: " + backup.getName());
            }
            fileExisted = false;
        }

        // 既存エントリの名前を取得
        List<String> existingNames = new ArrayList<>();
        for (Map<String, Object> entry : existing) {
            Object name = entry.get("name");
            if (name != null) existingNames.add(name.toString());
        }

        // 欠けているエントリを追加
        boolean updated = false;
        List<String> addedEntries = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> req : required.entrySet()) {
            if (!existingNames.contains(req.getKey())) {
                existing.add(req.getValue());
                addedEntries.add(req.getKey());
                updated = true;
            }
        }

        // 変更があれば書き込み
        if (updated || !fileExisted) {
            writeJson(file, existing.isEmpty() ? new ArrayList<>(required.values()) : existing);
            if (!fileExisted) {
                Logger.info("Generated " + typeName + " structure file: " + file.getName());
            } else {
                Logger.info("Updated " + typeName + " config with missing entries: " + String.join(", ", addedEntries));
            }
        } else {
            Logger.debug("No new entries to add to " + typeName + " config");
        }
    }

    /**
     * デフォルトマッピングオブジェクトを作成
     */
    private static Map<String, Object> createDefaultMappings(String machineType) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("name", "default");

        Map<String, Object> mappings = new LinkedHashMap<>();

        // 共通マッピング
        mappings.put("_", "air");
        mappings.put("F", "omoshiroikamo:basalt_structure:0");
        mappings.put("P", "omoshiroikamo:machine_base:0");
        mappings.put("C", "omoshiroikamo:laser_core:0");

        // マシンタイプ別のマッピング
        if ("oreMiner".equals(machineType) || "resMiner".equals(machineType)) {
            // コントローラー
            Map<String, Object> controller = new LinkedHashMap<>();
            String controllerBlock = "oreMiner".equals(machineType) ? "omoshiroikamo:quantum_ore_extractor:0"
                : "omoshiroikamo:quantum_res_extractor:0";
            controller.put("block", controllerBlock);
            controller.put("max", 1);
            mappings.put("Q", controller);

            // レンズ
            Map<String, Object> lens = new LinkedHashMap<>();
            List<Map<String, Object>> lensBlocks = new ArrayList<>();
            Map<String, Object> normalLens = new LinkedHashMap<>();
            normalLens.put("id", "omoshiroikamo:lens:0");
            normalLens.put("max", 1);
            lensBlocks.add(normalLens);
            Map<String, Object> coloredLens = new LinkedHashMap<>();
            coloredLens.put("id", "omoshiroikamo:colored_lens:*");
            coloredLens.put("max", 1);
            lensBlocks.add(coloredLens);
            lens.put("blocks", lensBlocks);
            mappings.put("L", lens);

            // モディファイア
            Map<String, Object> modifier = new LinkedHashMap<>();
            List<Map<String, Object>> modBlocks = new ArrayList<>();
            Map<String, Object> nullMod = new LinkedHashMap<>();
            nullMod.put("id", "omoshiroikamo:modifier_null:0");
            modBlocks.add(nullMod);
            Map<String, Object> accMod = new LinkedHashMap<>();
            accMod.put("id", "omoshiroikamo:modifier_accuracy:0");
            modBlocks.add(accMod);
            Map<String, Object> speedMod = new LinkedHashMap<>();
            speedMod.put("id", "omoshiroikamo:modifier_speed:0");
            modBlocks.add(speedMod);
            modifier.put("blocks", modBlocks);
            mappings.put("A", modifier);

        } else if ("solarArray".equals(machineType)) {
            Map<String, Object> controller = new LinkedHashMap<>();
            controller.put("block", "omoshiroikamo:solar_array:0");
            controller.put("max", 1);
            mappings.put("Q", controller);

            // ソーラーセル
            Map<String, Object> cell = new LinkedHashMap<>();
            List<Map<String, Object>> cellBlocks = new ArrayList<>();
            for (int i = 0; i <= 5; i++) {
                Map<String, Object> c = new LinkedHashMap<>();
                c.put("id", "omoshiroikamo:solar_cell:" + i);
                cellBlocks.add(c);
            }
            cell.put("blocks", cellBlocks);
            mappings.put("G", cell);

            // モディファイア
            Map<String, Object> modifier = new LinkedHashMap<>();
            List<Map<String, Object>> modBlocks = new ArrayList<>();
            Map<String, Object> nullMod = new LinkedHashMap<>();
            nullMod.put("id", "omoshiroikamo:modifier_null:0");
            modBlocks.add(nullMod);
            Map<String, Object> piezoMod = new LinkedHashMap<>();
            piezoMod.put("id", "omoshiroikamo:modifier_piezo:0");
            modBlocks.add(piezoMod);
            modifier.put("blocks", modBlocks);
            mappings.put("A", modifier);

        } else if ("beacon".equals(machineType)) {
            Map<String, Object> controller = new LinkedHashMap<>();
            controller.put("block", "omoshiroikamo:quantum_beacon:0");
            controller.put("max", 1);
            mappings.put("Q", controller);
        }

        entry.put("mappings", mappings);
        return entry;
    }

    /**
     * 構造体エントリを作成
     */
    private static Map<String, Object> createStructureEntry(String name, String[][] shape) {
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("name", name);

        List<Map<String, Object>> layers = new ArrayList<>();
        for (int i = 0; i < shape.length; i++) {
            Map<String, Object> layer = new LinkedHashMap<>();
            layer.put("name", "layer" + i);

            List<String> rows = new ArrayList<>();
            for (String row : shape[i]) {
                rows.add(row);
            }
            layer.put("rows", rows);

            layers.add(layer);
        }
        entry.put("layers", layers);
        entry.put("mappings", new LinkedHashMap<>());

        return entry;
    }

    /**
     * JSONファイルに書き出し
     */
    private static void writeJson(File file, Object data) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(data, writer);
            Logger.info("Generated structure file: " + file.getName());
        } catch (IOException e) {
            Logger.error("Failed to generate structure file: " + file.getAbsolutePath(), e);
        }
    }
}
