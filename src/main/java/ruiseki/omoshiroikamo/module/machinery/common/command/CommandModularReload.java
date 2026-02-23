package ruiseki.omoshiroikamo.module.machinery.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.module.machinery.MachineryModule;
import ruiseki.omoshiroikamo.module.machinery.common.recipe.RecipeLoader;

public class CommandModularReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandModularReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
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
                .reload(MachineryModule.getConfigDir());
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
}
