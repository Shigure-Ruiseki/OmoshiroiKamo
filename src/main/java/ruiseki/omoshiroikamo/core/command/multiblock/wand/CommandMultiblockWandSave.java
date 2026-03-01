package ruiseki.omoshiroikamo.core.command.multiblock.wand;

import java.io.File;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
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
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_usage")));
            return;
        }

        String name = args[0];

        WandSelectionManager.PendingScan pending = WandSelectionManager.getInstance()
            .getPendingScan(player.getUniqueID());

        if (pending == null) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_no_pending")));
            return;
        }

        if (pending.dimensionId != player.worldObj.provider.dimensionId) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_different_dimension")));
            return;
        }

        int blockCount = pending.getBlockCount();
        if (blockCount > StructureConstants.MAX_WAND_SCAN_BLOCKS) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize(
                        "chat.wand.area_too_large",
                        String.format("%,d", StructureConstants.MAX_WAND_SCAN_BLOCKS),
                        String.format("%,d", blockCount))));
            return;
        }

        player.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.wand_scanning", blockCount)));

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
            player
                .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] " + result.message));
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GRAY + "File: config/"
                        + LibMisc.MOD_ID
                        + "/structures/custom/"
                        + name
                        + ".json"));

            WandSelectionManager.getInstance()
                .clearPendingScan(player.getUniqueID());
        } else {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[OmoshiroiKamo] Scan failed: " + result.message));
        }
    }
}
