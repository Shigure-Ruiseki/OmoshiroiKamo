package ruiseki.omoshiroikamo.core.command.structure;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandStructureReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandStructureReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.reloading")));

        try {
            StructureManager.getInstance()
                .reload();

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
                        EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.reload_errors", errorCount)));
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GRAY + LibMisc.LANG.localize("command.ok.reload_check_file")));
            } else {
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.reload_success")));
            }
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.reload_failed", e.getMessage())));
        }
    }
}
