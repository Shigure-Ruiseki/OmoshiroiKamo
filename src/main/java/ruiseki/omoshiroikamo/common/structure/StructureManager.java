package ruiseki.omoshiroikamo.common.structure;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;

import ruiseki.omoshiroikamo.common.structure.StructureDefinitionData.BlockMapping;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

/**
 * カスタムストラクチャーシステムのメインマネージャー
 */
public class StructureManager {

    private static StructureManager INSTANCE;

    private final Map<String, StructureJsonLoader> loaders = new HashMap<>();
    private final StructureErrorCollector errorCollector = StructureErrorCollector.getInstance();
    private File configDir;
    private boolean initialized = false;

    /** 既に警告を出した構造体名（ログスパム防止） */
    private final Set<String> warnedStructures = new HashSet<>();

    private StructureManager() {}

    public static StructureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StructureManager();
        }
        return INSTANCE;
    }

    /**
     * 初期化済みかどうか
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * エラーがあるかどうか
     */
    public boolean hasErrors() {
        return errorCollector.hasErrors();
    }

    /**
     * 初期化（PreInitで呼び出し）
     */
    public void initialize(File minecraftDir) {
        if (initialized) return;

        try {
            this.configDir = new File(minecraftDir, "config/" + LibMisc.MOD_ID);
            if (!configDir.exists()) {
                configDir.mkdirs();
            }

            // エラーコレクターにconfigDirを設定
            errorCollector.setConfigDir(configDir);
            errorCollector.clear();

            // デフォルトJSONを生成
            DefaultStructureGenerator.generateAllIfMissing(configDir);

            // JSONファイルをロード
            loadStructureFile("ore_miner");
            loadStructureFile("res_miner");
            loadStructureFile("solar_array");
            loadStructureFile("quantum_beacon");

            // エラーがあればファイルに書き出し
            if (errorCollector.hasErrors()) {
                errorCollector.writeToFile();
            }

            initialized = true;
            Logger.info(
                "StructureManager initialized"
                    + (errorCollector.hasErrors() ? " with " + errorCollector.getErrorCount() + " error(s)" : ""));
        } catch (Exception e) {
            errorCollector.collect(StructureException.loadFailed("initialization", e));
            errorCollector.writeToFile();
        }
    }

    /**
     * プレイヤーにエラーを通知（ログイン時に呼び出す）
     */
    public void notifyPlayerIfNeeded(EntityPlayer player) {
        errorCollector.notifyPlayer(player);
    }

    /**
     * 構造体JSONファイルをロード
     */
    private void loadStructureFile(String name) {
        try {
            File file = new File(configDir, "structures/" + name + ".json");
            StructureJsonLoader loader = new StructureJsonLoader();

            if (loader.loadFromFile(file)) {
                loaders.put(name, loader);
                // ローダーのエラーを収集
                for (String error : loader.getErrors()) {
                    errorCollector.collect(StructureException.ErrorType.PARSE_ERROR, name + ".json", error);
                }
            } else {
                errorCollector.collect(StructureException.loadFailed(name + ".json", null));
            }
        } catch (Exception e) {
            errorCollector.collect(StructureException.loadFailed(name + ".json", e));
        }
    }

    /**
     * 構造体の形状を取得
     * 
     * @param fileKey       ファイルキー（"ore_miner", "res_miner" など）
     * @param structureName 構造体名（"oreExtractorTier1" など）
     * @return String[][] 形状、見つからない場合はnull
     */
    public String[][] getShape(String fileKey, String structureName) {
        // 初期化前はnullを返す（デフォルト形状を使用させる）
        if (!initialized) {
            return null;
        }

        StructureJsonLoader loader = loaders.get(fileKey);
        if (loader == null) {
            warnOnce(fileKey, "Structure loader not found: " + fileKey);
            return null;
        }

        String[][] shape = loader.getShape(structureName);
        if (shape == null) {
            warnOnce(fileKey + ":" + structureName, "Structure not found: " + structureName + " in " + fileKey);
        }

        return shape;
    }

    /**
     * 同じ警告を繰り返し出さないようにする
     */
    private void warnOnce(String key, String message) {
        if (!warnedStructures.contains(key)) {
            Logger.warn(message);
            warnedStructures.add(key);
        }
    }

    /**
     * ブロックマッピングを取得
     */
    public BlockMapping getMapping(String fileKey, String structureName, char symbol) {
        if (!initialized) return null;

        StructureJsonLoader loader = loaders.get(fileKey);
        if (loader == null) {
            return null;
        }
        return loader.getMapping(structureName, symbol);
    }

    /**
     * ファイルを再読み込み
     */
    public void reload() {
        loaders.clear();
        warnedStructures.clear();
        errorCollector.clear();

        loadStructureFile("ore_miner");
        loadStructureFile("res_miner");
        loadStructureFile("solar_array");
        loadStructureFile("quantum_beacon");

        if (errorCollector.hasErrors()) {
            errorCollector.writeToFile();
        }

        Logger.info(
            "StructureManager reloaded"
                + (errorCollector.hasErrors() ? " with " + errorCollector.getErrorCount() + " error(s)" : ""));
    }

    /**
     * エラーコレクターを取得
     */
    public StructureErrorCollector getErrorCollector() {
        return errorCollector;
    }

    // ===== 便利メソッド =====

    /**
     * Ore Miner の形状を取得
     */
    public static String[][] getOreMinerShape(int tier) {
        return getInstance().getShape("ore_miner", "oreExtractorTier" + tier);
    }

    /**
     * Resource Miner の形状を取得
     */
    public static String[][] getResMinerShape(int tier) {
        return getInstance().getShape("res_miner", "resExtractorTier" + tier);
    }

    /**
     * Solar Array の形状を取得
     */
    public static String[][] getSolarArrayShape(int tier) {
        return getInstance().getShape("solar_array", "solarArrayTier" + tier);
    }

    /**
     * Quantum Beacon の形状を取得
     */
    public static String[][] getBeaconShape(int tier) {
        return getInstance().getShape("quantum_beacon", "beaconTier" + tier);
    }
}
