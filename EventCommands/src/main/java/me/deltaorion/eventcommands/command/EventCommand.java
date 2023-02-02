package me.deltaorion.eventcommands.command;

import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.eventcommands.EventCommandsPlugin;
import me.deltaorion.eventcommands.EventSpawn;

import java.util.Map;

public class EventCommand extends FunctionalCommand {

    private final EventCommandsPlugin plugin;

    public EventCommand(EventCommandsPlugin plugin) {
        super("EventCommands.Command");
        this.plugin = plugin;
        registerArgument("reload",new ReloadCommand(this.plugin));
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        for(Map.Entry<String, Command>  commandFunction : getFunctions().entrySet()) {
            command.getSender().sendMessage("/"+command.getLabel()+" "+commandFunction.getKey()+": "+commandFunction.getValue().getDescription());
        }
    }


    public void deregister() {
        for(String arg : getCommandArgs()) {
            if(!arg.equals("reload"))
                deregisterArgument(arg);
        }
    }

    public void register(EventSpawn spawn) {
        SpawnCommand command = new SpawnCommand(spawn);
        if(spawn.getCommandName().equals("reload"))
            throw new IllegalArgumentException("Cannot register a command with the name reload");
        registerArgument(spawn.getCommandName(),command);
    }
}
