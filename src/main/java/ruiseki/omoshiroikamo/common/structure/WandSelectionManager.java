package ruiseki.omoshiroikamo.common.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.ChunkCoordinates;

/**
 * プレイヤーごとのWand選択範囲を管理するマネージャー
 */
public class WandSelectionManager {

    private static final WandSelectionManager INSTANCE = new WandSelectionManager();

    /**
     * プレイヤーごとの仮保存スキャンデータ
     */
    private final Map<UUID, PendingScan> pendingScans = new HashMap<>();

    private WandSelectionManager() {}

    public static WandSelectionManager getInstance() {
        return INSTANCE;
    }

    /**
     * 仮保存スキャンデータを設定
     */
    public void setPendingScan(UUID playerId, ChunkCoordinates pos1, ChunkCoordinates pos2, int dimensionId) {
        pendingScans.put(playerId, new PendingScan(pos1, pos2, dimensionId));
    }

    /**
     * 仮保存スキャンデータを取得
     */
    public PendingScan getPendingScan(UUID playerId) {
        return pendingScans.get(playerId);
    }

    /**
     * 仮保存スキャンデータをクリア
     */
    public void clearPendingScan(UUID playerId) {
        pendingScans.remove(playerId);
    }

    /**
     * 仮保存スキャンデータがあるかどうか
     */
    public boolean hasPendingScan(UUID playerId) {
        return pendingScans.containsKey(playerId);
    }

    /**
     * 仮保存スキャンデータ
     */
    public static class PendingScan {

        public final ChunkCoordinates pos1;
        public final ChunkCoordinates pos2;
        public final int dimensionId;

        public PendingScan(ChunkCoordinates pos1, ChunkCoordinates pos2, int dimensionId) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.dimensionId = dimensionId;
        }

        public int getBlockCount() {
            int sizeX = Math.abs(pos2.posX - pos1.posX) + 1;
            int sizeY = Math.abs(pos2.posY - pos1.posY) + 1;
            int sizeZ = Math.abs(pos2.posZ - pos1.posZ) + 1;
            return sizeX * sizeY * sizeZ;
        }
    }
}
