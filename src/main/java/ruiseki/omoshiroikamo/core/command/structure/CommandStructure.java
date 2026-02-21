package ruiseki.omoshiroikamo.core.command.structure;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Structure management subcommand handler.
 * Handles: /ok structure <reload|status|scan|wand>
 * This class is called by CommandOK with args already shifted.
 */
public class CommandStructure extends CommandMod {

    public static final String NAME = "structure";

    public CommandStructure(ModBase mod) {
        super(mod, NAME);

        addSubcommands(CommandStructureReload.NAME, new CommandStructureReload(mod));
        addSubcommands(CommandStructureStatus.NAME, new CommandStructureStatus(mod));
        addSubcommands(CommandStructureScan.NAME, new CommandStructureScan(mod));
        addSubcommands(CommandStructureWand.NAME, new CommandStructureWand(mod));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.usage_title")));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  " + LibMisc.LANG.localize("command.ok.usage_reload")));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  " + LibMisc.LANG.localize("command.ok.usage_status")));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  " + LibMisc.LANG.localize("command.ok.usage_scan")));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  " + LibMisc.LANG.localize("command.ok.usage_wand_save")));
    }
}
