package ruiseki.omoshiroikamo.common.command;

import java.io.File;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.common.FMLCommonHandler;
import ruiseki.omoshiroikamo.common.structure.StructureManager;
import ruiseki.omoshiroikamo.common.structure.StructureScanner;
import ruiseki.omoshiroikamo.common.structure.WandSelectionManager;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

/**
 * 構造体管理コマンド
 * Usage:
 * /ok reload - 設定を再読み込み
 * /ok status - 状態を表示
 * /ok scan <name> <x1> <y1> <z1> <x2> <y2> <z2> - 範囲をスキャン
 */
public class CommandStructure extends CommandBase {

    @Override
    public String getCommandName() {
        return "ok";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ok <reload|status|scan>";
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

        String subcommand = args[0].toLowerCase();

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
            new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Reloading structure configurations..."));

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
                        EnumChatFormatting.RED + "[OmoshiroiKamo] Reload completed with " + errorCount + " error(s)!"));
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GRAY + "Check: config/omoshiroikamo/structures/errors.txt"));
            } else {
                sender.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.GREEN + "[OmoshiroiKamo] Structure configurations reloaded successfully!"));
            }
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[OmoshiroiKamo] Reload failed: " + e.getMessage()));
        }
    }

    private void showStatus(ICommandSender sender) {
        StructureManager manager = StructureManager.getInstance();

        sender
            .addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "=== OmoshiroiKamo Structure Status ==="));

        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "Initialized: "
                    + (manager.isInitialized() ? EnumChatFormatting.GREEN + "Yes" : EnumChatFormatting.RED + "No")));

        if (manager.hasErrors()) {
            String summary = manager.getErrorCollector()
                .getSummary();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Errors: " + summary));
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Errors: None"));
        }
    }

    private void scanStructure(ICommandSender sender, String[] args) {
        // /ok scan <name> <x1> <y1> <z1> <x2> <y2> <z2>
        if (args.length < 8) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "Usage: /ok scan <name> <x1> <y1> <z1> <x2> <y2> <z2>"));
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + "Example: /ok scan myStructure 0 64 0 10 70 10"));
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
                new ChatComponentText(EnumChatFormatting.RED + "Invalid coordinates: " + e.getMessage()));
            return;
        }

        // ワールドを取得
        World world = null;
        if (sender instanceof EntityPlayer) {
            world = ((EntityPlayer) sender).worldObj;
        } else {
            world = FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getEntityWorld();
        }

        if (world == null) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not get world instance"));
            return;
        }

        // サイズチェック
        int sizeX = Math.abs(x2 - x1) + 1;
        int sizeY = Math.abs(y2 - y1) + 1;
        int sizeZ = Math.abs(z2 - z1) + 1;
        int totalBlocks = sizeX * sizeY * sizeZ;

        if (totalBlocks > 10000) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "Area too large! Max 10000 blocks, got " + totalBlocks));
            return;
        }

        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.YELLOW + "Scanning " + sizeX + "x" + sizeY + "x" + sizeZ + " area..."));

        // configディレクトリを取得
        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + LibMisc.MOD_ID);

        // スキャン実行
        StructureScanner.ScanResult result = StructureScanner.scan(world, name, x1, y1, z1, x2, y2, z2, configDir);

        if (result.success) {
            sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] " + result.message));
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GRAY + "File: config/omoshiroikamo/structures/scanned_" + name + ".json"));
        } else {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[OmoshiroiKamo] Scan failed: " + result.message));
        }
    }

    private void sendUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "Usage:"));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  /ok reload"
                    + EnumChatFormatting.GRAY
                    + " - Reload structure configurations"));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  /ok status" + EnumChatFormatting.GRAY + " - Show current status"));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  /ok scan <name> <x1> <y1> <z1> <x2> <y2> <z2>"
                    + EnumChatFormatting.GRAY
                    + " - Scan area to JSON"));
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + "  /ok wand save <name>"
                    + EnumChatFormatting.GRAY
                    + " - Save wand selection to JSON"));
    }

    private void handleWandCommand(ICommandSender sender, String[] args) {
        if (!(sender instanceof EntityPlayer)) {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[OmoshiroiKamo] This command can only be used by players!"));
            return;
        }

        if (args.length < 2) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /ok wand save <name>"));
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
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /ok wand save <name>"));
                break;
        }
    }

    private void saveWandSelection(EntityPlayer player, String[] args) {
        if (args.length < 3) {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /ok wand save <name>"));
            return;
        }

        String name = args[2];

        WandSelectionManager.PendingScan pending = WandSelectionManager.getInstance()
            .getPendingScan(player.getUniqueID());

        if (pending == null) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[OmoshiroiKamo] No pending scan! Use the Structure Wand first."));
            return;
        }

        // ディメンションチェック
        if (pending.dimensionId != player.worldObj.provider.dimensionId) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[OmoshiroiKamo] Selection is in a different dimension!"));
            return;
        }

        // サイズチェック
        int blockCount = pending.getBlockCount();
        if (blockCount > 32768) {
            player.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + "[OmoshiroiKamo] Area too large! Max 32768 blocks, got " + blockCount));
            return;
        }

        player.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Scanning " + blockCount + " blocks..."));

        // configディレクトリを取得
        File configDir = new File(
            FMLCommonHandler.instance()
                .getMinecraftServerInstance()
                .getFile("."),
            "config/" + LibMisc.MOD_ID);

        // スキャン実行
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
                    EnumChatFormatting.GRAY + "File: config/omoshiroikamo/structures/scanned_" + name + ".json"));

            // 仮保存をクリア
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
                new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] Wand selection cleared."));
        } else {
            player.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GRAY + "[OmoshiroiKamo] No pending selection to clear."));
        }
    }
}
