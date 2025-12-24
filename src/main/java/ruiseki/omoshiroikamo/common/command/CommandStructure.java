package ruiseki.omoshiroikamo.common.command;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.common.structure.StructureConstants;
import ruiseki.omoshiroikamo.common.structure.StructureManager;
import ruiseki.omoshiroikamo.common.structure.StructureScanner;
import ruiseki.omoshiroikamo.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

/**
 * Structure management command.
 * Usage:
 * /ok reload - reload configuration
 * /ok status - show current status
 * /ok scan <name> <x1> <y1> <z1> <x2> <y2> <z2> - scan a region
 */
public class CommandStructure extends CommandBase {

    @Override
    public String getCommandName() {
        return "ok structure";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ok structure <reload|status|scan>";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2; // OP required
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        String subcommand = args[1].toLowerCase();

        switch (subcommand) {
            case "reload":
                reloadStructures(sender);
                break;
            case "status":
                showStatus(sender);
                break;
            case "scan":
                scanStructure(sender, args);
                break;
            case "wand":
                handleWandCommand(sender, args);
                break;
            default:
                sendUsage(sender);
                break;
        }
    }

    private void reloadStructures(ICommandSender sender) {
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + LibMisc.LANG.localize("command.ok.reloading")));

        try {
            StructureManager.getInstance()
                .reload();

            if (StructureManager.getInstance()
                .hasErrors()) {
                int errorCount = StructureManager.getInstance()
                    .getErrorCollector()
                    .getErrorCount();
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.reload_errors", errorCount)));
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GRAY + LibMisc.LANG.localize("command.ok.reload_check_file")));
            } else {
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.reload_success")));
            }
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.reload_failed", e.getMessage())));
        }
    }

    private void showStatus(ICommandSender sender) {
        StructureManager manager = StructureManager.getInstance();

        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.AQUA + LibMisc.LANG.localize("command.ok.status_header")));

        String initialized = manager.isInitialized()
            ? EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.status_yes")
            : EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.status_no");
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + LibMisc.LANG.localize("command.ok.status_initialized") + initialized));

        if (manager.hasErrors()) {
            String summary = manager.getErrorCollector()
                .getSummary();
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.status_errors") + summary));
        } else {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.status_errors")
                        + LibMisc.LANG.localize("command.ok.status_none")));
        }
    }

    private void scanStructure(ICommandSender sender, String[] args) {
        // /ok scan <name> <x1> <y1> <z1> <x2> <y2> <z2>
        if (args.length < 8) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.scan_usage")));
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + LibMisc.LANG.localize("command.ok.scan_example")));
            return;
        }

        String name = args[1];
        int x1, y1, z1, x2, y2, z2;

        try {
            x1 = parseInt(sender, args[2]);
            y1 = parseInt(sender, args[3]);
            z1 = parseInt(sender, args[4]);
            x2 = parseInt(sender, args[5]);
            y2 = parseInt(sender, args[6]);
            z2 = parseInt(sender, args[7]);
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

    private void sendUsage(ICommandSender sender) {
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

    private void handleWandCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_players_only")));
            return;
        }

        if (args.length < 2) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_usage")));
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        String subAction = args[1].toLowerCase();

        switch (subAction) {
            case "save":
                saveWandSelection(player, args);
                break;
            case "clear":
                clearWandSelection(player);
                break;
            default:
                sender.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_usage")));
                break;
        }
    }

    private void saveWandSelection(EntityPlayer player, String[] args) {
        if (args.length < 3) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_usage")));
            return;
        }

        String name = args[2];

        WandSelectionManager.PendingScan pending = WandSelectionManager.getInstance()
            .getPendingScan(player.getUniqueID());

        if (pending == null) {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_no_pending")));
            return;
        }

        // Dimension guard
        if (pending.dimensionId != player.worldObj.provider.dimensionId) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.wand_different_dimension")));
            return;
        }

        // Size guard
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

        // Locate the config directory
        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + LibMisc.MOD_ID);

        // Execute the scan
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

            // Clear the pending selection
            WandSelectionManager.getInstance()
                .clearPendingScan(player.getUniqueID());
        } else {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[OmoshiroiKamo] Scan failed: " + result.message));
        }
    }

    private void clearWandSelection(EntityPlayer player) {
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
