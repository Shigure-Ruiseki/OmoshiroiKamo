package ruiseki.omoshiroikamo.core.command.structure;

import java.io.File;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureConstants;
import ruiseki.omoshiroikamo.core.common.structure.StructureScanner;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandStructureScan extends CommandMod {

    public static final String NAME = "scan";

    public CommandStructureScan(ModBase mod) {
        super(mod, NAME);

    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        // /ok structure scan <name> <x1> <y1> <z1> <x2> <y2> <z2>
        // args[0] = name, ...
        if (args.length < 7) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.scan_usage")));
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + LibMisc.LANG.localize("command.ok.scan_example")));
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
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.scan_invalid_coords", e.getMessage())));
            return;
        }

        // Resolve the target world
        World world = null;
        if (sender instanceof EntityPlayer) {
            world = ((EntityPlayer) sender).worldObj;
        } else {
            world = FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getEntityWorld();
        }

        if (world == null) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.scan_no_world")));
            return;
        }

        // Size guard
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int totalBlocks = sizeX * sizeY * sizeZ;

        if (totalBlocks > StructureConstants.MAX_COMMAND_SCAN_BLOCKS) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize(
                        "command.ok.scan_area_too_large",
                        StructureConstants.MAX_COMMAND_SCAN_BLOCKS,
                        totalBlocks)));
            return;
        }

        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.scan_scanning", sizeX, sizeY, sizeZ)));

        // Locate the config directory
        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + LibMisc.MOD_ID);

        // Execute the scan
        StructureScanner.ScanResult result = StructureScanner.scan(world, name, x1, y1, z1, x2, y2, z2, configDir);

        if (result.success) {
            sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] " + result.message));
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + LibMisc.LANG.localize("command.ok.scan_file", name)));
        } else {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.scan_failed", result.message)));
        }
    }

    // Helper method to parse integers (since we no longer extend CommandBase)
    private int parseInt(ICommandSender sender, String value) throws NumberFormatException {
        return Integer.parseInt(value);
    }
}
