package me.deltaorion.consumescrolls.command;

import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.consumescrolls.ConsumeScrollGenerator;
import me.deltaorion.consumescrolls.ConsumeScrollPlugin;
import me.deltaorion.consumescrolls.ScrollPool;
import org.bukkit.command.defaults.ReloadCommand;

import java.util.Map;

public class ConsumeScrollCommand extends FunctionalCommand {


    public ConsumeScrollCommand(ConsumeScrollPlugin plugin) {
        super(Permissions.ADMIN);
        registerArgument("give",new GiveConsumeScrollCommand(plugin.getPool(),plugin.getGenerator()));
        registerArgument("reload", new ReloadPluginCommand(plugin));
        registerArgument("docs",new DocumentationCommand(plugin));
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        for(Map.Entry<String, Command> entry : getFunctions().entrySet()) {
            command.getSender().sendMessage("/ScrollAdmin " + entry.getKey()+": "+entry.getValue().getDescription());
        }
    }
}
