package ruiseki.omoshiroikamo.module.dml.common.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.module.dml.DMLModule;

public class CommandDMLReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandDMLReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Reloading DML..."));

        DMLModule dmlModule = getMod().getModuleManager()
            .getModuleByType(DMLModule.class);
        if (dmlModule == null || !dmlModule.isEnable()) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "[DML] Module is disabled."));
            return;
        }

        try {
            dmlModule.reload(sender);
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[DML] Reload failed: " + e.getMessage()));
            return;
        }

        if (!JsonErrorCollector.getInstance()
            .hasErrors()) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] DML reload completed!"));
        }
    }
}
