package me.deltaorion.siegestats.controller.command;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.SiegeStatsPlugin;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.service.SiegeService;
import me.deltaorion.siegestats.view.command.SiegeCommandView;

import java.util.List;
import java.util.UUID;

public class ViewCommand extends FunctionalCommand {

    private final SiegeService siegeManager;
    private final SiegeCommandView view = new SiegeCommandView();

    protected ViewCommand(SiegeService siegeManager) {
        super(Permissions.VIEW_COMMAND);
        this.siegeManager = siegeManager;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        SiegeTown town = siegeManager.getTownByName(command.getArgOrFail(0).asString());
        if(town==null)
            throw new CommandException("Could not find town");

        int maxSiege = town.getSieges().size();
        int num = command.getArgOrBlank(1).asIntOrDefault(maxSiege) - 1;
        if(town.getSieges().size()==0)
            throw new CommandException("No sieges exist for this town");

        if(num > town.getSieges().size())
            throw new CommandException("There are only "+town.getSieges().size()+" sieges for this town");

        StatSiege siege = town.getLatestSiege();
        if(num >= 0) {
            siege = town.getSieges().get(num);
        }

        view.displaySiege(command.getSender(), siege);
    }
}
