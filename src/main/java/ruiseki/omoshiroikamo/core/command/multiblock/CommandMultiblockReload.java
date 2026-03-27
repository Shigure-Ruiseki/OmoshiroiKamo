package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.module.multiblock.common.init.QuantumExtractorRecipes;

public class CommandMultiblockReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandMultiblockReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + LangHelpers.localize("command.ok.reloading")));

        try {
            // Core structure data
            StructureManager.getInstance()
                .reload();

            // Multiblock module - Extractor recipes
            QuantumExtractorRecipes.reload();

            if (StructureManager.getInstance()
                .hasErrors()
                || JsonErrorCollector.getInstance()
                    .hasErrors()) {
                JsonErrorCollector.getInstance()
                    .writeToFile();
                int errorCount = StructureManager.getInstance()
                    .getErrorCollector()
                    .getErrorCount()
                    + JsonErrorCollector.getInstance()
                        .getErrorCount();
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.RED + LangHelpers.localize("command.ok.reload_errors", errorCount)));
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GRAY + LangHelpers.localize("command.ok.reload_check_file")));
            } else {
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.reload_success")));
            }
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LangHelpers.localize("command.ok.reload_failed", e.getMessage())));
        }
    }
}
