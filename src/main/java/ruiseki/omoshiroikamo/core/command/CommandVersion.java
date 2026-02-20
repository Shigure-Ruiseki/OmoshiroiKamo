package ruiseki.omoshiroikamo.core.command;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import ruiseki.omoshiroikamo.core.init.ModBase;

/**
 * Command for checking the version.
 *
 * @author rubensworks
 *
 */
public class CommandVersion extends CommandMod {

    public static final String NAME = "version";

    public CommandVersion(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return super.addTabCompletionOptions(sender, args);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText(getMod().getReferenceValue(ModBase.REFKEY_MOD_VERSION)));
    }
}
