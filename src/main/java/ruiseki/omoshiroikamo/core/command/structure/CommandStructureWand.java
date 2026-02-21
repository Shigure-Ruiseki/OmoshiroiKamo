package ruiseki.omoshiroikamo.core.command.structure;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.command.structure.wand.CommandStructureWandClear;
import ruiseki.omoshiroikamo.core.command.structure.wand.CommandStructureWandSave;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandStructureWand extends CommandMod {

    public static final String NAME = "wand";

    public CommandStructureWand(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandStructureWandClear.NAME, new CommandStructureWandClear(mod));
        addSubcommands(CommandStructureWandSave.NAME, new CommandStructureWandSave(mod));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_players_only")));
            return;
        }
        super.processCommand(sender, args);
    }

    @Override
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_usage")));
    }
}
