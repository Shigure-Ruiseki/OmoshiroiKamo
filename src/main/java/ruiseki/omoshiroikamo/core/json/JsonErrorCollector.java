package ruiseki.omoshiroikamo.core.json;

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

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.api.recipe.expression.RecipeScriptException;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Universal collector for JSON-related parsing and validation errors.
 * Replaces and generalizes module-specific collectors.
 */
public class JsonErrorCollector {

    private static JsonErrorCollector INSTANCE;

    private final List<JsonErrorInfo> errors = new ArrayList<>();
    private final Set<String> notifiedPlayers = new HashSet<>();
    private File configDir;

    private JsonErrorCollector() {}

    public static JsonErrorCollector getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JsonErrorCollector();
        }
        return INSTANCE;
    }

    public void setConfigDir(File configDir) {
        this.configDir = configDir;
    }

    /**
     * Collect a new error.
     */
    public void collect(String materialType, String message) {
        File source = ParsingContext.getCurrentFile();
        String fileName = source != null ? source.getName() : "Unknown";
        JsonErrorInfo info = new JsonErrorInfo(materialType, fileName, message);
        errors.add(info);

        Logger.error("[JSON Error] [{}] in {}: {}", materialType, fileName, message);
    }

    public void collectScriptError(String materialType, RecipeScriptException e) {
        File source = ParsingContext.getCurrentFile();
        String fileName = source != null ? source.getName() : "Unknown";
        // Use the visual message from exception
        JsonErrorInfo info = new JsonErrorInfo(materialType, fileName, e.getMessage());
        errors.add(info);

        Logger.error("[Script Error] [{}] in {}:\n{}", materialType, fileName, e.getMessage());
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public void clear() {
        errors.clear();
        notifiedPlayers.clear();
        if (configDir != null) {
            File errorsFile = new File(configDir, "json_errors.txt");
            if (errorsFile.exists()) {
                errorsFile.delete();
            }
        }
    }

    /**
     * Writes all collected errors to a file for the user to inspect.
     */
    public void writeToFile() {
        if (errors.isEmpty() || configDir == null) return;

        File errorsFile = new File(configDir, "json_errors.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(errorsFile))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            writer.println("=== OmoshiroiKamo JSON System Errors ===");
            writer.println("Generated: " + sdf.format(new Date()));
            writer.println("Total Error Count: " + errors.size());
            writer.println();
            writer.println("These errors occurred during JSON loading/validation.");
            writer.println("Please check the files listed below.");
            writer.println();

            for (int i = 0; i < errors.size(); i++) {
                JsonErrorInfo info = errors.get(i);
                writer.println(String.format("%d. [%s] File: %s", (i + 1), info.materialType, info.fileName));
                writer.println("   Error: " + info.message);
                writer.println();
            }

            writer.println("--- End of Error Log ---");
        } catch (IOException e) {
            Logger.error("Failed to write JSON errors file", e);
        }
    }

    /**
     * Notifies a player about errors when they join.
     */
    public void notifyPlayer(EntityPlayer player) {
        if (!hasErrors()) return;

        String playerName = player.getCommandSenderName();
        if (notifiedPlayers.contains(playerName)) return;
        notifiedPlayers.add(playerName);

        reportToChat(player);
    }

    /**
     * Prints errors to Chat.
     */
    public void reportToChat(ICommandSender sender) {
        if (!hasErrors()) return;

        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.RED + "[OmoshiroiKamo] "
                    + EnumChatFormatting.YELLOW
                    + "Recipe system has "
                    + errors.size()
                    + " error(s)!"));

        // Show first 3 errors in detail
        int limit = Math.min(errors.size(), 3);
        for (int i = 0; i < limit; i++) {
            JsonErrorInfo info = errors.get(i);
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + " Error in " + EnumChatFormatting.WHITE + info.fileName + ":"));

            // Split message by lines to preserve snippet formatting
            String[] lines = info.message.split("\n");
            for (String line : lines) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + line));
            }
        }

        if (errors.size() > 3) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + "... and " + (errors.size() - 3) + " more."));
        }

        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.GRAY + "Check: config/" + LibMisc.MOD_ID + "/json_errors.txt for details."));
    }

    /**
     * Simple data class for error info.
     */
    public static class JsonErrorInfo {

        public final String materialType;
        public final String fileName;
        public final String message;

        public JsonErrorInfo(String materialType, String fileName, String message) {
            this.materialType = materialType;
            this.fileName = fileName;
            this.message = message;
        }
    }
}
