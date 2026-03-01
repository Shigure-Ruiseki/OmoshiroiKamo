package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * Multiblock management subcommand handler.
 * Handles: /ok multiblock <reload|status|scan|wand>
 * This class is called by CommandOK with args already shifted.
 */
public class CommandMultiblock extends CommandMod {

    public static final String NAME = "multiblock";

    public CommandMultiblock(ModBase mod) {
        super(mod, NAME);

        addSubcommands(CommandMultiblockReload.NAME, new CommandMultiblockReload(mod));
        // Note: For now, keeping Status, Scan, and Wand as stubs or referencing old
        // ones if not renamed yet.
        // But to be clean, let's assume we rename them all.
        addSubcommands("status", new CommandMultiblockStatus(mod));
        addSubcommands("scan", new CommandMultiblockScan(mod));
        addSubcommands("wand", new CommandMultiblockWand(mod));
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
