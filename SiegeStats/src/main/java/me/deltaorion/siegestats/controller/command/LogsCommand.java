package me.deltaorion.siegestats.controller.command;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.model.killer.Participant;
import me.deltaorion.siegestats.service.SiegeService;
import me.deltaorion.siegestats.view.command.LogsCommandView;
import me.deltaorion.siegestats.view.command.SiegeCommandView;

import java.util.List;
import java.util.UUID;

public class LogsCommand extends FunctionalCommand {

    private final SiegeService siegeManager;
    private final LogsCommandView view = new LogsCommandView();

    protected LogsCommand(SiegeService siegeManager) {
        super(Permissions.LOG_COMMAND);
        this.siegeManager = siegeManager;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        LogType type = command.getArgOrFail(0).asEnum(LogType.class,"Log Type");

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


        switch (type) {
            case KILLS -> {
                view.displayKillLogs(command.getSender(),siege);
            } case DAMAGE -> {
                view.displayDamageLogs(command.getSender(),siege);
            } case DEATHS -> {
                view.displayDeathsLogs(command.getSender(),siege);
            }
        }

    }

    private static enum LogType {
        DAMAGE,
        KILLS,
        DEATHS
        ;
    }
}
