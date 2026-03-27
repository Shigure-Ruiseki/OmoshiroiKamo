package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
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

        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.AQUA + LangHelpers.localize("command.ok.status_header")));

        String initialized = manager.isInitialized()
            ? EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.status_yes")
            : EnumChatFormatting.RED + LangHelpers.localize("command.ok.status_no");
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + LangHelpers.localize("command.ok.status_initialized") + initialized));

        if (manager.hasErrors()) {
            String summary = manager.getErrorCollector()
                .getSummary();
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LangHelpers.localize("command.ok.status_errors") + summary));
        } else {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + LangHelpers.localize("command.ok.status_errors")
                        + LangHelpers.localize("command.ok.status_none")));
        }
    }
}
