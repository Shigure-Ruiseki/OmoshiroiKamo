package ruiseki.omoshiroikamo.core.command.structure.wand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandStructureWandClear extends CommandMod {

    public static final String NAME = "clear";

    public CommandStructureWandClear(ModBase mod) {
        super(mod, NAME);

    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;
        if (WandSelectionManager.getInstance()
            .hasPendingScan(player.getUniqueID())) {
            WandSelectionManager.getInstance()
                .clearPendingScan(player.getUniqueID());
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.wand_cleared")));
        } else {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + LibMisc.LANG.localize("command.ok.wand_no_selection")));
        }
    }
}
