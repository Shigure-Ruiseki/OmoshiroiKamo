package ruiseki.omoshiroikamo.core.command.multiblock;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
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
        sender.addChatMessage(
            new ChatComponentText(EnumChatFormatting.YELLOW + "[OmoshiroiKamo] Reloading multiblock..."));

        MultiBlockModule multiblockModule = getMod().getModuleManager().getModuleByType(MultiBlockModule.class);
        if (multiblockModule == null || !multiblockModule.isEnable()) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[Multiblock] Module is disabled."));
            return;
        }

        try {
            getMod().getModuleManager().getModuleByType(
                ruiseki.omoshiroikamo.core.CoreModule.class).reload(sender);
            multiblockModule.reload(sender);
        } catch (Exception e) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.RED + "[Multiblock] Reload failed: " + e.getMessage()));
            return;
        }

        if (!JsonErrorCollector.getInstance().hasErrors()) {
            sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.GREEN + "[OmoshiroiKamo] Multiblock reload completed!"));
        }
    }
}
