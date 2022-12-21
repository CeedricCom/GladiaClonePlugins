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
import me.deltaorion.townymissionsv2.player.ContributeType;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AutoContributeCommand implements SubCommand {

    private final TownyMissionsV2 plugin;

    public AutoContributeCommand(TownyMissionsV2 plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCommand(TownyMissionsV2 plugin, CommandSender sender, ArgumentList args) throws CommandException {
        MissionPlayer player = plugin.getPlayerManager().getPlayer(sender);
        if(!args.hasArg(0)) {
            player.getAutoContribute().set(!player.getAutoContribute().isToggled(),null);
        } else {
            ContributeType type = args.getArgOrFail(0).asEnum(ContributeType.class,"Government Type");
            Government government = player.getGovernment(type);
            if(government==null)
                throw new CommandException(Message.COMMAND_NOT_IN_GOVERNMENT.getMessage(type));

            MissionBearer bearer = plugin.getMissionManager().getMissionBearer(government.getUUID());
            ChestGui gui = GUIManager.buildAutoContributeSelect(player,bearer);

            gui.show((Player) sender);
        }
    }


    @Override
    public String getDescription() {
        return "Toggles Auto-Contribute Mode. This will automatically contribute items that are picked up";
    }

    @Override
    public String getPermission() {
        return CommandPermissions.GENERIC.getPerm();
    }

    @Override
    public String getUsage() {
        return "/missions autocontribute <town/nation>";
    }


    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
