package ruiseki.omoshiroikamo.core.command.utils;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;

public class CommandUtils extends CommandMod {

    public static final String NAME = "utils";

    public CommandUtils(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandDump.NAME, new CommandDump(mod));
    }
}
