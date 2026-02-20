package ruiseki.omoshiroikamo.core.command;

import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Main command handler for /ok
 * Delegates to subcommand handlers for extensibility.
 */
public class CommandOK extends CommandMod {

    public CommandOK(ModBase mod, Map<String, ICommand> subCommands) {
        super(mod, subCommands);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP required
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.main_usage_title")));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  " + LibMisc.LANG.localize("command.ok.main_usage_structure")));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  /ok modular reload - Reload modular data"));
    }
}
