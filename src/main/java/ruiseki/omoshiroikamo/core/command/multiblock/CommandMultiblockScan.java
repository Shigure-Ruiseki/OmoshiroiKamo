package ruiseki.omoshiroikamo.core.command.multiblock;

import java.io.File;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureConstants;
import ruiseki.omoshiroikamo.core.common.structure.StructureScanner;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandMultiblockScan extends CommandMod {

    public static final String NAME = "scan";

    public CommandMultiblockScan(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 7) {
            sendLocalizedMessage(sender, "command.ok.scan_usage", EnumChatFormatting.RED);
            sendLocalizedMessage(sender, "command.ok.scan_example", EnumChatFormatting.GRAY);
            return;
        }

        String name = args[0];
        int x1, y1, z1, x2, y2, z2;

        try {
            x1 = parseInt(sender, args[1]);
            y1 = parseInt(sender, args[2]);
            z1 = parseInt(sender, args[3]);
            x2 = parseInt(sender, args[4]);
            y2 = parseInt(sender, args[5]);
            z2 = parseInt(sender, args[6]);
        } catch (Exception e) {
            sendLocalizedMessage(
                sender,
                "command.ok.scan_invalid_coords",
                EnumChatFormatting.RED.toString(),
                e.getMessage());
            return;
        }

        World world = null;
        if (sender instanceof EntityPlayer) {
            world = ((EntityPlayer) sender).worldObj;
        } else {
            world = FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getEntityWorld();
        }

        if (world == null) {
            sendLocalizedMessage(sender, "command.ok.scan_no_world", EnumChatFormatting.RED);
            return;
        }

        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int totalBlocks = sizeX * sizeY * sizeZ;

        if (totalBlocks > StructureConstants.MAX_COMMAND_SCAN_BLOCKS) {
            sendLocalizedMessage(
                sender,
                "command.ok.scan_area_too_large",
                EnumChatFormatting.RED.toString(),
                StructureConstants.MAX_COMMAND_SCAN_BLOCKS,
                totalBlocks);
            return;
        }

        sendLocalizedMessage(
            sender,
            "command.ok.scan_scanning",
            EnumChatFormatting.YELLOW.toString(),
            sizeX,
            sizeY,
            sizeZ);

        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + LibMisc.MOD_ID);

        StructureScanner.ScanResult result = StructureScanner.scan(world, name, x1, y1, z1, x2, y2, z2, configDir);

        if (result.success) {
            sendLocalizedMessage(sender, "command.ok.scan_success", EnumChatFormatting.GREEN + result.message);
            sendLocalizedMessage(sender, "command.ok.scan_file", EnumChatFormatting.GRAY.toString() + name);
        } else {
            sendLocalizedMessage(sender, "command.ok.scan_failed", EnumChatFormatting.RED + result.message);
        }
    }

    private int parseInt(ICommandSender sender, String value) throws NumberFormatException {
        return Integer.parseInt(value);
    }
}
