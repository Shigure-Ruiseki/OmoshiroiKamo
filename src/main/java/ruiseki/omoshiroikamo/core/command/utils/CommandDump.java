package ruiseki.omoshiroikamo.core.command.utils;

import ruiseki.omoshiroikamo.core.command.CommandMod;
import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.module.dml.common.command.CommandMobDump;

public class CommandDump extends CommandMod {

    public static final String NAME = "dump";

    public CommandDump(ModBase mod) {
        super(mod, NAME);
        addSubcommands(CommandMobDump.NAME, new CommandMobDump(mod));
    }
}
