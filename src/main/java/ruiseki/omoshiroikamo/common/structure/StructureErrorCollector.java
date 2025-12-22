package ruiseki.omoshiroikamo.common.structure;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.common.util.Logger;

/**
 * 構造体システムのエラーを収集・通知するコレクター
 */
public class StructureErrorCollector {

    private static StructureErrorCollector INSTANCE;

    private final List<StructureException> errors = new ArrayList<>();
    private final Set<String> notifiedPlayers = new HashSet<>();
    private File configDir;

    private StructureErrorCollector() {}

    public static StructureErrorCollector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StructureErrorCollector();
        }
        return INSTANCE;
    }

    /**
     * 設定ディレクトリを設定
     */
    public void setConfigDir(File configDir) {
        this.configDir = configDir;
    }

    /**
     * エラーを収集
     */
    public void collect(StructureException e) {
        errors.add(e);
        Logger.error("[Structure] " + e.getFormattedMessage());

        // 原因がある場合はスタックトレースも出力
        if (e.getCause() != null) {
            Logger.error(
                "Caused by: " + e.getCause()
                    .getMessage());
        }
    }

    /**
     * 文字列からエラーを収集（簡易版）
     */
    public void collect(StructureException.ErrorType type, String fileName, String message) {
        collect(new StructureException(type, fileName, message));
    }

    /**
     * エラーがあるかどうか
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * エラー数を取得
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * エラーリストを取得（コピー）
     */
    public List<StructureException> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * エラーをクリア
     */
    public void clear() {
        errors.clear();
        notifiedPlayers.clear();

        // エラーファイルも削除
        if (configDir != null) {
            File errorsFile = new File(configDir, "structures/errors.txt");
            if (errorsFile.exists()) {
                errorsFile.delete();
            }
        }
    }

    /**
     * エラーをファイルに書き出し
     */
    public void writeToFile() {
        if (errors.isEmpty() || configDir == null) return;

        File errorsFile = new File(configDir, "structures/errors.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(errorsFile))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            writer.println("=== OmoshiroiKamo Structure Errors ===");
            writer.println("Generated: " + sdf.format(new Date()));
            writer.println("Error count: " + errors.size());
            writer.println();
            writer.println("--- Error Details ---");
            writer.println();

            for (int i = 0; i < errors.size(); i++) {
                StructureException e = errors.get(i);
                writer.println((i + 1) + ". " + e.getFormattedMessage());

                // ファイル情報
                if (e.getFileName() != null) {
                    writer.println("   File: " + e.getFileName());
                }
                if (e.getEntryName() != null) {
                    writer.println("   Entry: " + e.getEntryName());
                }

                // 原因
                if (e.getCause() != null) {
                    writer.println(
                        "   Cause: " + e.getCause()
                            .getClass()
                            .getSimpleName()
                            + " - "
                            + e.getCause()
                                .getMessage());
                }

                writer.println();
            }

            writer.println("--- How to Fix ---");
            writer.println("1. Check the JSON syntax in the affected files");
            writer.println("2. Ensure all required fields are present");
            writer.println("3. Verify block IDs are correct (format: mod:block:meta)");
            writer.println();
            writer.println("Config location: " + configDir.getAbsolutePath() + "/structures/");

            Logger.warn("Wrote " + errors.size() + " error(s) to: " + errorsFile.getName());
        } catch (IOException e) {
            Logger.error("Failed to write errors file", e);
        }
    }

    /**
     * プレイヤーにエラーを通知（ログイン時）
     */
    public void notifyPlayer(EntityPlayer player) {
        if (!hasErrors()) return;

        String playerName = player.getCommandSenderName();
        if (notifiedPlayers.contains(playerName)) return;

        notifiedPlayers.add(playerName);

        // ヘッダー
        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.RED + "[OmoshiroiKamo] "
                    + EnumChatFormatting.YELLOW
                    + "Structure config has "
                    + errors.size()
                    + " error(s)!"));

        // エラータイプの概要
        long parseErrors = errors.stream()
            .filter(e -> e.getType() == StructureException.ErrorType.PARSE_ERROR)
            .count();
        long missingFields = errors.stream()
            .filter(e -> e.getType() == StructureException.ErrorType.MISSING_FIELD)
            .count();

        if (parseErrors > 0 || missingFields > 0) {
            StringBuilder summary = new StringBuilder();
            summary.append(EnumChatFormatting.GRAY)
                .append("  ");
            if (parseErrors > 0) summary.append("Parse: ")
                .append(parseErrors)
                .append(" ");
            if (missingFields > 0) summary.append("Missing: ")
                .append(missingFields);
            player.addChatMessage(new ChatComponentText(summary.toString()));
        }

        // ファイルパス
        player.addChatMessage(
            new ChatComponentText(EnumChatFormatting.GRAY + "Check: config/omoshiroikamo/structures/errors.txt"));
    }

    /**
     * エラーサマリーを取得
     */
    public String getSummary() {
        if (errors.isEmpty()) return "No errors";

        StringBuilder sb = new StringBuilder();
        sb.append(errors.size())
            .append(" error(s): ");

        // タイプ別にカウント
        int[] counts = new int[StructureException.ErrorType.values().length];
        for (StructureException e : errors) {
            counts[e.getType()
                .ordinal()]++;
        }

        boolean first = true;
        for (StructureException.ErrorType type : StructureException.ErrorType.values()) {
            int count = counts[type.ordinal()];
            if (count > 0) {
                if (!first) sb.append(", ");
                sb.append(type.name())
                    .append("=")
                    .append(count);
                first = false;
            }
        }

        return sb.toString();
    }
}
