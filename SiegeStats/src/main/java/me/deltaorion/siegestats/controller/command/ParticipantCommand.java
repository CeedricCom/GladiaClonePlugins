package me.deltaorion.siegestats.controller.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.model.killer.Participant;
import me.deltaorion.siegestats.service.SiegeService;
import me.deltaorion.siegestats.view.command.SiegeCommandView;

public class ParticipantCommand extends FunctionalCommand {

    private final SiegeService siegeManager;
    private final SiegeCommandView view = new SiegeCommandView();

    protected ParticipantCommand(SiegeService siegeManager) {
        super(Permissions.PARTICIPANT_COMMAND);
        this.siegeManager = siegeManager;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        SiegeTown town = siegeManager.getTownByName(command.getArgOrFail(1).asString());
        if(town==null)
            throw new CommandException("Could not find town");

        int maxSiege = town.getSieges().size();
        int num = command.getArgOrBlank(2).asIntOrDefault(maxSiege) - 1;
        if(town.getSieges().size()==0)
            throw new CommandException("No sieges exist for this town");

        if(num > town.getSieges().size())
            throw new CommandException("There are only "+town.getSieges().size()+" sieges for this town");

        StatSiege siege = town.getLatestSiege();
        if(num >= 0) {
            siege = town.getSieges().get(num);
        }

        Participant participant = siege.getByName(command.getArgOrFail(0).asString());
        if(participant==null)
            throw new CommandException("Unknown participant");

        view.displayParticipant(command.getSender(), siege,participant);
    }
}
