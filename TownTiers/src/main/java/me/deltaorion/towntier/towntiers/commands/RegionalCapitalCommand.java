package me.deltaorion.towntier.towntiers.commands;

import com.gmail.goosius.siegewar.metadata.TownMetaDataController;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.confirmations.Confirmation;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.towntier.towntiers.Dependency;
import me.deltaorion.towntier.towntiers.NationTier;
import me.deltaorion.towntier.towntiers.TownTiers;
import me.deltaorion.towntier.towntiers.data.TownTierData;
import me.deltaorion.towntier.towntiers.townyutils.TownyUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionalCapitalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 3) {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (nation == null || resident == null) {
                    player.sendMessage(ChatColor.GOLD + "You must be in a nation to do this!");
                    return true;
                }
                if (!resident.equals(nation.getKing())) {
                    player.sendMessage(ChatColor.GOLD + "You must be king to use this command!");
                    return true;
                }

                Town town = TownyUniverse.getInstance().getTown(args[2]);
                if (town == null) {
                    player.sendMessage(ChatColor.GOLD + "Enter a valid town!");
                    return true;
                }
                NationTier tier = TownTiers.getInstance().getTierFromNation(nation);
                int current = 0;
                try {
                    current = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.GOLD + "/n set rc" + ChatColor.YELLOW + " [town] [number]");
                    return true;
                }
                if (tier.getRegionalCapitals() <= 0) {
                    player.sendMessage(ChatColor.GOLD + "Your nation has not unlocked regional capitals!");
                    return true;
                }
                if (town.isConquered()) {
                    sender.sendMessage(ChatColor.GOLD + "You cannot make a occupied town a regional capital!");
                    return true;
                }
                if (current <= tier.getRegionalCapitals() && current > 0) {
                    final int curr = current;
                    Confirmation.runOnAccept(() -> {
                        if (nation.getAccount().getHoldingBalance() >= TownTiers.getInstance().getRegionalCapitalPrice()) {
                            nation.getAccount().withdraw(TownTiers.getInstance().getRegionalCapitalPrice(), player.getName() + " changed regional capital to " + town.getName());
                        } else {
                            player.sendMessage(ChatColor.GOLD + "Your nations bank needs at least " + ChatColor.YELLOW + "$" + TownTiers.getInstance().getRegionalCapitalPrice() + ChatColor.GOLD + " to do this!");
                            return;
                        }
                        for (Town nt : nation.getTowns()) {
                            if (TownyUtils.getMetaDataFromTown(nt, TownTierData.getRegionalCapitalField()) == curr) {
                                TownyUtils.removeMetaDataFromTown(nt, TownTierData.getRegionalCapitalField());
                            }
                        }
                        TownyUtils.updateTownMetaData(town, curr, TownTierData.getRegionalCapitalField());
                        if (Dependency.SIEGEWAR.isActive()) {
                            TownMetaDataController.setSiegeImmunityEndTime(town, System.currentTimeMillis());
                        }

                        player.sendMessage(ChatColor.GOLD + "Successfully Set " + ChatColor.YELLOW + town.getName() + ChatColor.GOLD + " to a regional capital!");
                    }).setTitle("Doing this will cost $" + TownTiers.getInstance().getRegionalCapitalPrice() + ". Continue?").sendTo(sender);

                } else {
                    player.sendMessage(ChatColor.GOLD + "/n set rc" + ChatColor.YELLOW + " [town] [number]");
                    player.sendMessage(ChatColor.GOLD + "Your Nation only has " + ChatColor.YELLOW + tier.getRegionalCapitals() + ChatColor.GOLD + " Regional Capitals");
                }
                return true;
            } else {
                player.sendMessage(ChatColor.GOLD + "/n set rc" + ChatColor.YELLOW + " [town] [number]");
                return true;
            }
        }
        return true;
    }

}
