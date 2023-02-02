package me.deltaorion.eventcommands.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.locale.message.Message;
import me.deltaorion.eventcommands.EventSpawn;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnCommand extends FunctionalCommand {

    private final EventSpawn spawn;

    protected SpawnCommand(EventSpawn spawn) {
        super("EventCommands.Command.Spawn","/eventspawn "+ spawn.getCommandName(), Message.valueOf("Teleports you to the "+spawn.getDisplayName()+" event"));
        this.spawn = spawn;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        if(command.getSender().isConsole())
            throw new CommandException("Only players may use this command");

        Player player = Bukkit.getPlayer(command.getSender().getUniqueId());
        player.teleport(spawn.getLocation());
        command.getSender().sendMessage("Teleported you to "+spawn.getDisplayName());
    }
}
