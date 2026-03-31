package ruiseki.omoshiroikamo.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;

/**
 * /ok reload — reloads all enabled modules at once.
 */
public class CommandReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        JsonErrorCollector.getInstance()
            .clear();
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Reloading all modules..."));

        getMod().getModuleManager()
            .reloadAll(sender);

        if (JsonErrorCollector.getInstance()
            .hasErrors()) {
            JsonErrorCollector.getInstance()
                .writeToFile();
            JsonErrorCollector.getInstance()
                .reportToChat(sender);
        } else {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] All modules reloaded!"));
        }
    }
}
