package ruiseki.omoshiroikamo.core.command.multiblock.wand;

import java.io.File;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureConstants;
import ruiseki.omoshiroikamo.core.common.structure.StructureScanner;
import ruiseki.omoshiroikamo.core.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandMultiblockWandSave extends CommandMod {

    public static final String NAME = "save";

    public CommandMultiblockWandSave(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;
        if (args.length < 1) {
            sendLocalizedMessage(player, "command.ok.wand_usage", EnumChatFormatting.RED);
            return;
        }

        String name = args[0];

        WandSelectionManager.PendingScan pending = WandSelectionManager.getInstance()
            .getPendingScan(player.getUniqueID());

        if (pending == null) {
            sendLocalizedMessage(player, "command.ok.wand_no_pending", EnumChatFormatting.RED);
            return;
        }

        if (pending.dimensionId != player.worldObj.provider.dimensionId) {
            sendLocalizedMessage(player, "command.ok.wand_different_dimension", EnumChatFormatting.RED);
            return;
        }

        int blockCount = pending.getBlockCount();
        if (blockCount > StructureConstants.MAX_WAND_SCAN_BLOCKS) {
            sendLocalizedMessage(
                player,
                "chat.wand.area_too_large",
                EnumChatFormatting.RED.toString(),
                String.format("%,d", StructureConstants.MAX_WAND_SCAN_BLOCKS),
                String.format("%,d", blockCount));
            return;
        }

        sendLocalizedMessage(player, "command.ok.wand_scanning", EnumChatFormatting.YELLOW, blockCount);

        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + LibMisc.MOD_ID);

        StructureScanner.ScanResult result = StructureScanner.scan(
            player.worldObj,
            name,
            pending.pos1.posX,
            pending.pos1.posY,
            pending.pos1.posZ,
            pending.pos2.posX,
            pending.pos2.posY,
            pending.pos2.posZ,
            configDir);

        if (result.success) {
            sendLocalizedMessage(player, "command.ok.scan_success", EnumChatFormatting.GREEN + result.message);
            sendLocalizedMessage(player, "command.ok.scan_file", EnumChatFormatting.GRAY.toString() + name);

            WandSelectionManager.getInstance()
                .clearPendingScan(player.getUniqueID());
        } else {
            sendLocalizedMessage(player, "command.ok.scan_failed", EnumChatFormatting.RED + result.message);
        }
    }
}
