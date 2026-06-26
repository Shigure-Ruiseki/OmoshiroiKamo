package ruiseki.omoshiroikamo.core.command.multiblock.wand;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.init.ModBase;
import ruiseki.omoshiroikamo.core.structure.WandSelectionManager;

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
            sendLocalizedMessage(player, "command.ok.wand_cleared", EnumChatFormatting.GREEN);
        } else {
            sendLocalizedMessage(player, "command.ok.wand_no_selection", EnumChatFormatting.GRAY);
        }
    }
}
