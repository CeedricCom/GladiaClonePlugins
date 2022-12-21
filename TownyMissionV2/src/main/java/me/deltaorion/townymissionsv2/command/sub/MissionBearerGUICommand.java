package me.deltaorion.townymissionsv2.command.sub;

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.palmergames.bukkit.towny.object.Government;
import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.ArgumentList;
import me.deltaorion.townymissionsv2.command.CommandException;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.command.SubCommand;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.display.gui.GUIManager;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MissionBearerGUICommand implements SubCommand {

    private final ContributeType type;

    public MissionBearerGUICommand(ContributeType type) {
        this.type = type;
    }


    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        MissionPlayer player = plugin.getPlayerManager().getPlayer(sender);
        Mission mission = plugin.getMissionManager().getPrimaryMission(player,type);

        Government government = player.getGovernment(type);
        if(government==null)
            throw new CommandException(Message.COMMAND_NOT_IN_GOVERNMENT.getMessage(type.toString().toLowerCase()));

        ChestGui gui = GUIManager.buildMissionDisplayGUI(player,mission,plugin.getMissionManager().getMissionBearer(government.getUUID()));
        gui.show((Player) sender);
    }

    @Override
    public String getDescription() {
        return "Access and Contribute to "+type.toString().toLowerCase()+" missions";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.GENERIC.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions "+type.toString().toLowerCase();
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
