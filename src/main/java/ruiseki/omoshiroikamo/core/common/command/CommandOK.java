package ruiseki.omoshiroikamo.core.common.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Main command handler for /ok
 * Delegates to subcommand handlers for extensibility.
 */
public class CommandOK extends CommandBase {

    private final CommandStructure structureCommand = new CommandStructure();

    @Override
    public String getCommandName() {
        return "ok";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ok <structure|...>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP required
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "structure":
                structureCommand.processCommand(sender, args);
                break;
            default:
                sendUsage(sender);
                break;
        }
    }

    private void sendUsage(ICommandSender sender) {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.main_usage_title")));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  " + LibMisc.LANG.localize("command.ok.main_usage_structure")));
    }
}
