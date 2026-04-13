package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandMultiblockStatus extends CommandMod {

    public static final String NAME = "status";

    public CommandMultiblockStatus(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        StructureManager manager = StructureManager.getInstance();

        sendLocalizedMessage(sender, "command.ok.status_header", EnumChatFormatting.AQUA);

        String initialized = manager.isInitialized()
            ? EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.status_yes")
            : EnumChatFormatting.RED + LangHelpers.localize("command.ok.status_no");

        sendLocalizedMessage(
            sender,
            "command.ok.status_initialized",
            EnumChatFormatting.WHITE.toString() + initialized);

        if (manager.hasErrors()) {
            String summary = manager.getErrorCollector()
                .getSummary();
            sendLocalizedMessage(sender, "command.ok.status_errors", EnumChatFormatting.RED.toString() + summary);
        } else {
            sendLocalizedMessage(
                sender,
                "command.ok.status_errors",
                EnumChatFormatting.GREEN.toString() + LangHelpers.localize("command.ok.status_none"));
        }
    }
}
