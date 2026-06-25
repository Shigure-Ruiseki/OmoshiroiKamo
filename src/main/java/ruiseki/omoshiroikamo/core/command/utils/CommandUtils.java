package ruiseki.omoshiroikamo.core.command.utils;

import ruiseki.okcore.command.CommandMod;
import ruiseki.okcore.init.ModBase;

public class CommandUtils extends CommandMod {

    public static final String NAME = "utils";

    public CommandUtils(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandDump.NAME, new CommandDump(mod));
    }
}
