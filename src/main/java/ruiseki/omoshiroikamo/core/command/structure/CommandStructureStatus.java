package ruiseki.omoshiroikamo.core.command.structure;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class CommandStructureStatus extends CommandMod {

    public static final String NAME = "status";

    public CommandStructureStatus(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        StructureManager manager = StructureManager.getInstance();

        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.AQUA + LibMisc.LANG.localize("command.ok.status_header")));

        String initialized = manager.isInitialized()
            ? EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.status_yes")
            : EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.status_no");
        sender.addChatMessage(
            new ChatComponentText(
                EnumChatFormatting.WHITE + LibMisc.LANG.localize("command.ok.status_initialized") + initialized));

        if (manager.hasErrors()) {
            String summary = manager.getErrorCollector()
                .getSummary();
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.RED + LibMisc.LANG.localize("command.ok.status_errors") + summary));
        } else {
            sender.addChatMessage(
                new ChatComponentText(
                    EnumChatFormatting.GREEN + LibMisc.LANG.localize("command.ok.status_errors")
                        + LibMisc.LANG.localize("command.ok.status_none")));
        }
    }
}
