package ruiseki.omoshiroikamo.module.machinery.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * Modular machinery subcommand handler.
 * Handles: /ok modular <reload|list>
 */
public class CommandModular extends CommandMod {

    public static final String NAME = "modular";

    public CommandModular(ModBase mod) {
        super(mod);
        addSubcommands(CommandModularReload.NAME, new CommandModularReload(mod));
        addSubcommands(CommandModularList.NAME, new CommandModularList(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Modular] Usage:"));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  /ok modular reload - Reload structures and recipes"));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  /ok modular list - List custom structures"));
    }
}
