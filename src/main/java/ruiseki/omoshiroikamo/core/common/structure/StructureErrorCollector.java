package ruiseki.omoshiroikamo.core.common.structure;

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

import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Collector that gathers and reports structure-system errors.
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
     * Configure the directory where error files are written.
     */
    public void setConfigDir(File configDir) {
        this.configDir = configDir;
    }

    /**
     * Collect an error instance.
     */
    public void collect(StructureException e) {
        errors.add(e);
        Logger.error("[Structure] " + e.getFormattedMessage());

        // Log the underlying cause when present
        if (e.getCause() != null) {
            Logger.error(
                "Caused by: " + e.getCause()
                    .getMessage());
        }
    }

    /**
     * Collect an error from basic fields (lightweight helper).
     */
    public void collect(StructureException.ErrorType type, String fileName, String message) {
        collect(new StructureException(type, fileName, message));
    }

    /**
     * Whether any errors have been recorded.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Return the total error count.
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Get a copy of the error list.
     */
    public List<StructureException> getErrors() {
        return new ArrayList<>(errors);
    }

    /**
     * Clear all recorded errors.
     */
    public void clear() {
        errors.clear();
        notifiedPlayers.clear();

        // Remove the persisted error file
        if (configDir != null) {
            File errorsFile = new File(configDir, "structures/errors.txt");
            if (errorsFile.exists()) {
                errorsFile.delete();
            }
        }
    }

    /**
     * Write errors to disk.
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

                // File information
                if (e.getFileName() != null) {
                    writer.println("   File: " + e.getFileName());
                }
                if (e.getEntryName() != null) {
                    writer.println("   Entry: " + e.getEntryName());
                }

                // Cause
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
     * Notify a player about errors on login.
     */
    public void notifyPlayer(EntityPlayer player) {
        if (!hasErrors()) return;

        String playerName = player.getCommandSenderName();
        if (notifiedPlayers.contains(playerName)) return;

        notifiedPlayers.add(playerName);

        // Header
        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.RED + "[OmoshiroiKamo] "
                    + EnumChatFormatting.YELLOW
                    + "Structure config has "
                    + errors.size()
                    + " error(s)!"));

        // Summary by error type
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

        // File path hint
        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GRAY + "Check: config/" + LibMisc.MOD_ID + "/structures/errors.txt"));
    }

    /**
     * Build a human-readable error summary.
     */
    public String getSummary() {
        if (errors.isEmpty()) return "No errors";

        StringBuilder sb = new StringBuilder();
        sb.append(errors.size())
            .append(" error(s): ");

        // Count by error type
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
