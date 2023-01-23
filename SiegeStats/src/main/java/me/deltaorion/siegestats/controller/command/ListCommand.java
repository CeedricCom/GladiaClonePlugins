package me.deltaorion.siegestats.controller.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.service.SiegeService;
import me.deltaorion.siegestats.view.command.SiegeCommandView;

public class ListCommand extends FunctionalCommand {

    private final SiegeCommandView view = new SiegeCommandView();
    private final SiegeService siegeManager;

    protected ListCommand(SiegeService siegeManager) {
        super(Permissions.LIST_COMMAND);
        this.siegeManager = siegeManager;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        if(command.argCount()==0) {
            displayAll(command);
            return;
        }

        displayTown(command);
    }

    private void displayTown(SentCommand command) throws CommandException {
        SiegeTown town = siegeManager.getTownByName(command.getArgOrFail(0).asString());
        if(town==null)
            throw new CommandException("Could not find town");

        view.displayTown(command.getSender(),town);
    }

    private void displayAll(SentCommand command) {
        view.displayAll(command.getSender(),siegeManager.getTowns());
    }
}
