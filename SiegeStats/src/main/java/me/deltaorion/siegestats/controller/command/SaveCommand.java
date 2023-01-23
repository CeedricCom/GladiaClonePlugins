package me.deltaorion.siegestats.controller.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.service.PersistenceManager;

public class SaveCommand extends FunctionalCommand {

    private final PersistenceManager manager;

    protected SaveCommand(PersistenceManager manager) {
        super(Permissions.SAVE_COMMAND);
        registerArgument("save",this::save);
        registerArgument("load",this::load);
        this.manager = manager;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        command.getSender().sendMessage("[save] - saves the database");
        command.getSender().sendMessage("[load] - loads the database - may wipe existing data");
    }

    public void save(SentCommand command) {
        manager.saveAll();
        command.getSender().sendMessage("Successfully saved database");
    }

    public void load(SentCommand command) {
        manager.loadAll();
        command.getSender().sendMessage("Successfully loadded database");
    }
}
