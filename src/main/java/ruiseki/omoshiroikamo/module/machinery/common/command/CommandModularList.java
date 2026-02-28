package ruiseki.omoshiroikamo.module.machinery.common.command;

import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandModularList extends CommandMod {

    public static final String NAME = "list";

    public CommandModularList(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
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
            IStructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(name);
            String displayName = (entry != null && entry.getDisplayName() != null) ? entry.getDisplayName() : name;
            String recipeGroupDisplay = "default";
            if (entry != null && entry.getRecipeGroup() != null
                && !entry.getRecipeGroup()
                    .isEmpty()) {
                recipeGroupDisplay = String.join(", ", entry.getRecipeGroup());
            }
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
                        + recipeGroupDisplay
                        + (hasStructureDef ? EnumChatFormatting.GREEN + " [OK]" : EnumChatFormatting.RED + " [ERR]")));
        }
    }
}
