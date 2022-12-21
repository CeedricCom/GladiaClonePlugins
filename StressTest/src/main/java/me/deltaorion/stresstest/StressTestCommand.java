package me.deltaorion.stresstest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class StressTestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("StressTest.admin")) {
            sender.sendMessage("Hey!, You do not have permission to use this command");
            return true;
        }

        if(args.length==0) {
            printHelpMessage(sender);
            return true;
        }

        Teleporter teleporter = StressTest.getInstance().getTeleporter();
        if(args[0].equals("start")) {
            if(args.length==1) {
                sender.sendMessage("enter delay between finish teleport (ticks)");
                return true;
            }

            int ticks = Integer.parseInt(args[1]);
            teleporter.start(ticks);
            sender.sendMessage("Started RTP with '"+ticks+"' delay");
        } else if(args[0].equals("stop")) {
            teleporter.stop();
            sender.sendMessage("Stopped RTP!");
        } else {
            printHelpMessage(sender);
        }

        return true;
    }

    private void printHelpMessage(CommandSender sender) {
        sender.sendMessage("StressTest [start/stop]");
    }
}
