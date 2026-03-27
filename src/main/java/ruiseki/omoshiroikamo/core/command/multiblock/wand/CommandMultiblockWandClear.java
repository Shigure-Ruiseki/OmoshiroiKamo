package ruiseki.omoshiroikamo.core.command.multiblock.wand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandMultiblockWandClear extends CommandMod {

    public static final String NAME = "clear";

    public CommandMultiblockWandClear(ModBase mod) {
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
                new ChatComponentText(EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.wand_cleared")));
        } else {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + LangHelpers.localize("command.ok.wand_no_selection")));
        }
    }
}
