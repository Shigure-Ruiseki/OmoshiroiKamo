package ruiseki.omoshiroikamo.core.command;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.omoshiroikamo.core.init.ModBase;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

/**
 * The mod base command.
 *
 * @author rubensworks
 *
 */
public class CommandMod implements ICommand {

    private final ModBase mod;
    private final Map<String, ICommand> subCommands;
    private final List<String> aliases = Lists.newLinkedList();

    public CommandMod(ModBase mod, Map<String, ICommand> subCommands) {
        this.mod = mod;
        this.subCommands = subCommands;
        this.subCommands.put(CommandVersion.NAME, new CommandVersion(mod));
        addAlias(mod.getModId());
    }

    public CommandMod(ModBase mod) {
        this.mod = mod;
        this.subCommands = Maps.newHashMap();
    }

    public CommandMod(ModBase mod, String name) {
        this(mod);
        addAlias(name);
    }

    public void addAlias(String alias) {
        this.aliases.add(alias);
    }

    protected ModBase getMod() {
        return this.mod;
    }

    protected List<String> getAliases() {
        return aliases;
    }

    public Map<String, ICommand> getSubcommands() {
        return subCommands;
    }

    public void addSubcommands(String name, ICommand command) {
        subCommands.put(name, command);
    }

    private List<String> getSubCommands(String cmd) {
        List<String> completions = new LinkedList<>();
        for (String full : getSubcommands().keySet()) {
            if (full.startsWith(cmd)) {
                completions.add(full);
            }
        }
        return completions;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    @Override
    public String getCommandName() {
        return mod.getModId();
    }

    /**
     * @return Recursively returns the whole command string up to the current subcommand.
     */
    public String getFullCommand() {
        return getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        String command = "/" + getFullCommand();
        Iterator<String> it = getSubcommands().keySet()
            .iterator();
        return it.hasNext() ? joinStrings(command + " <", it, " | ", ">") : command;
    }

    @Override
    public List<String> getCommandAliases() {
        return this.getAliases();
    }

    protected String[] shortenArgumentList(String[] astring) {
        String[] asubstring = new String[astring.length - 1];
        System.arraycopy(astring, 1, asubstring, 0, astring.length - 1);
        return asubstring;
    }

    /**
     * This method is called when the user uses the command in an incorrect way. This command
     * should either print out a friendly message explaining how to use the given (sub)command,
     * or throw a CommandException with extra helpful information.
     *
     * @param sender Use this commandsender to print chat message.
     * @param args   The list of strings that were entered as subcommands to the current command by the user.
     * @throws CommandException Thrown when the user entered something wrong, should contain some helpful information as
     *                          to what wrong.
     */
    public void processCommandHelp(ICommandSender sender, String[] args) throws CommandException {
        throw new WrongUsageException(getCommandUsage(sender));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            processCommandHelp(sender, args);
        } else {
            ICommand subcommand = getSubcommands().get(args[0]);
            if (subcommand != null) {
                String[] asubstring = shortenArgumentList(args);
                subcommand.processCommand(sender, asubstring);
            } else {
                throw new WrongUsageException(LibMisc.LANG.localize("command.ok.invalidSubcommand"));
            }
        }
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length != 0) {
            ICommand subcommand = getSubcommands().get(args[0]);
            if (subcommand != null) {
                String[] asubstring = shortenArgumentList(args);
                return subcommand.addTabCompletionOptions(sender, asubstring);
            } else {
                return getSubCommands(args[0]);
            }
        } else {
            return getSubCommands("");
        }
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i) {
        return false;
    }

    // == Helper functions ==//

    protected String joinStrings(Iterator<String> it, String delim) {
        return joinStrings("", it, delim, "");
    }

    protected String joinStrings(String prefix, Iterator<String> it, String delim, String suffix) {
        StringBuilder builder = new StringBuilder(prefix);

        if (it.hasNext()) {
            builder.append(it.next());
            while (it.hasNext()) {
                builder.append(delim);
                builder.append(it.next());
            }
        }

        builder.append(suffix);

        return builder.toString();
    }

    protected void printLineToChat(ICommandSender sender, String line) {
        sender.addChatMessage(new ChatComponentText(line));
    }
}
