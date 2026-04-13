package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.json.JsonErrorCollector;
import ruiseki.omoshiroikamo.module.multiblock.MultiBlockModule;

public class CommandMultiblockReload extends CommandMod {

    public static final String NAME = "reload";

    public CommandMultiblockReload(ModBase mod) {
        super(mod, NAME);
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        sendLocalizedMessage(sender, "command.ok.multiblock_reloading", EnumChatFormatting.YELLOW);

        MultiBlockModule multiblockModule = getMod().getModuleManager()
            .getModuleByType(MultiBlockModule.class);
        if (multiblockModule == null || !multiblockModule.isEnable()) {
            sendLocalizedMessage(sender, "command.ok.multiblock_disabled", EnumChatFormatting.RED);
            return;
        }

        try {
            getMod().getModuleManager()
                .getModuleByType(ruiseki.omoshiroikamo.core.CoreModule.class)
                .reload(sender);
            multiblockModule.reload(sender);
        } catch (Exception e) {
            sendLocalizedMessage(
                sender,
                "command.ok.multiblock_reload_failed",
                EnumChatFormatting.RED.toString() + e.getMessage());
            return;
        }

        if (!JsonErrorCollector.getInstance()
            .hasErrors()) {
            sendLocalizedMessage(sender, "command.ok.multiblock_reload_success", EnumChatFormatting.GREEN);
        }
    }
}
