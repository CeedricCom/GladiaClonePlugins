package me.deltaorion.consumescrolls.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.consumescrolls.ConsumeScroll;
import me.deltaorion.consumescrolls.ConsumeScrollGenerator;
import me.deltaorion.consumescrolls.ConsumeScrollPlugin;
import me.deltaorion.consumescrolls.ScrollDefinition;
import me.deltaorion.consumescrolls.reward.ScrollReward;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DocumentationCommand extends FunctionalCommand {

    private final ConsumeScrollPlugin plugin;

    protected DocumentationCommand(ConsumeScrollPlugin plugin) {
        super(Permissions.ADMIN);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        File file = plugin.getDataFolder().toPath().resolve("docs.txt").toFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try(FileWriter writer = new FileWriter(file)) {
                writer.write("| Rarity | Goal | Rewards |" + System.lineSeparator());
                writer.write("| ----------- | ----------------- | ----------------------- |" + System.lineSeparator());
                List<ScrollDefinition> definitions = new ArrayList<>(plugin.getPool().getScrolls());
                definitions.sort(Comparator.comparingInt(o -> o.getRarity().ordinal()));
                for (ScrollDefinition definition : definitions) {
                    command.getSender().sendMessage("Generating for '"+definition.getName());
                    ConsumeScroll scroll = new ConsumeScroll(definition,plugin.getConfiguration().getToolTip());
                    scroll.getFriendlyName();
                    StringBuilder rewardString = new StringBuilder();
                    int count = 0;
                    for(ScrollReward reward : definition.getRewards()) {
                        rewardString.append(ChatColor.stripColor(reward.getName()));
                        if(count<definition.getRewards().size()-1) {
                            rewardString.append(", ");
                        }
                        count++;
                    }

                    writer.write("| "+definition.getRarity()+" | "+
                            definition.getMinGoal()+" - "+definition.getMaxGoal()+" "+scroll.getFriendlyName()+ " | " +
                            rewardString + " |");

                    writer.write(System.lineSeparator());
                }
            }

            command.getSender().sendMessage("Docs written to docs.txt");

        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
