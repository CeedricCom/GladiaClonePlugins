package me.deltaorion.consumescrolls.command;

import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.consumescrolls.ConsumeScrollPlugin;
import me.deltaorion.consumescrolls.config.ConfigurationException;

public class ReloadPluginCommand extends FunctionalCommand {

    private final ConsumeScrollPlugin plugin;

    public ReloadPluginCommand(ConsumeScrollPlugin plugin) {
        super(Permissions.RELOAD);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {
        try {
            plugin.reloadConfiguration();
            sentCommand.getSender().sendMessage("Successfully reloaded config");
        } catch (ConfigurationException e) {
            sentCommand.getSender().sendMessage("Unable to reload config: "+e.getMessage());
        }
    }
}
