package ruiseki.omoshiroikamo.core.common.command;

import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureDefinitionData.StructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.module.machinery.MachineryCommon;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

/**
 * Modular machinery subcommand handler.
 * Handles: /ok modular <reload|list>
 */
public class CommandModular {

    public void processCommand(ICommandSender sender, String[] args) {
        // args[0] = "modular"
        // args[1] = action (reload, list)
        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "reload":
                reload(sender);
                break;
            case "list":
                listStructures(sender);
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

    private void listStructures(ICommandSender sender) {
        Set<String> names = StructureManager.getInstance()
            .getCustomStructureNames();

        if (names.isEmpty()) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.YELLOW + "[Modular] No custom structures registered"));
            return;
        }

        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.GREEN + "[Modular] Custom Structures (" + names.size() + "):"));

        for (String name : names) {
            StructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(name);
            String displayName = entry != null && entry.displayName != null ? entry.displayName : name;
            String recipeGroup = entry != null && entry.recipeGroup != null ? entry.recipeGroup : "default";
            boolean hasStructureDef = CustomStructureRegistry.hasDefinition(name);

            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.WHITE + "  "
                        + name
                        + EnumChatFormatting.GRAY
                        + " ("
                        + displayName
                        + ")"
                        + EnumChatFormatting.AQUA
                        + " -> "
                        + recipeGroup
                        + (hasStructureDef ? EnumChatFormatting.GREEN + " [OK]" : EnumChatFormatting.RED + " [ERR]")));
        }
    }

    private void sendUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[Modular] Usage:"));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  /ok modular reload - Reload structures and recipes"));
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.WHITE + "  /ok modular list - List custom structures"));
    }
}
