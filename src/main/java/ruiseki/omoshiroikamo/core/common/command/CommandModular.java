package ruiseki.omoshiroikamo.core.common.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.MachineryCommon;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

/**
 * Modular machinery subcommand handler.
 * Handles: /ok modular <reload>
 */
public class CommandModular {

    public void processCommand(ICommandSender sender, String[] args) {
        // args[0] = "modular"
        // args[1] = action (reload)
        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "reload":
                reload(sender);
                break;
            default:
                sendUsage(sender);
                break;
        }
    }

    private void reload(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Reloading..."));

        boolean hasErrors = false;

        // Reload structures
        try {
            StructureManager.getInstance()
                .reload();
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  Structures reloaded"));
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "  Structures failed: " + e.getMessage()));
            hasErrors = true;
        }

        // Reload recipes
        try {
            RecipeLoader.getInstance()
                .reload(MachineryCommon.getConfigDir());
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "  Recipes reloaded"));
        } catch (Exception e) {
            sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "  Recipes failed: " + e.getMessage()));
            hasErrors = true;
        }

        if (hasErrors) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Reload completed with errors"));
        } else {
            sender
                .addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] Reload completed!"));
        }
    }

    private void sendUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Modular] Usage:"));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  /ok modular reload - Reload structures and recipes"));
    }
}
