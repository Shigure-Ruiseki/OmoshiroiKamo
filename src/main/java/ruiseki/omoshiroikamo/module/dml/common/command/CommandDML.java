package ruiseki.omoshiroikamo.module.dml.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * DML subcommand handler.
 * Handles: /ok dml <reload>
 */
public class CommandDML extends CommandMod {

    public static final String NAME = "dml";

    public CommandDML(ModBase mod) {
        super(mod);
        addSubcommands(CommandDMLReload.NAME, new CommandDMLReload(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[DML] Usage:"));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  /ok dml reload - Reload DML models and configurations"));
    }
}
