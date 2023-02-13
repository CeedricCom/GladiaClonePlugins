package me.deltaorion.towntier.towntiers.commands;

import me.deltaorion.towntier.towntiers.SetExpFix;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExperienceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            sender.sendMessage(ChatColor.YELLOW+player.getName()+ChatColor.GOLD+" has "+ChatColor.YELLOW+ SetExpFix.getTotalExperience(player) +ChatColor.GOLD+" Experience ("+ChatColor.YELLOW+player.getLevel()+" Levels"+ChatColor.GOLD+")");
        }
        return true;
    }
}
