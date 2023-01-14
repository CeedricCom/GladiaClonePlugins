package me.deltaorion.documenter;

import com.hazebyte.crate.api.CrateAPI;
import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CrateDocumentCommand extends FunctionalCommand {

    private final Documenter plugin;

    protected CrateDocumentCommand(Documenter plugin) {
        super("");
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        File file = plugin.getDataFolder().toPath().resolve("crates.txt").toFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try(FileWriter writer = new FileWriter(file)) {

                for(Crate crate : CrateAPI.getCrateRegistrar().getCrates()) {
                    writer.write(crate.getDisplayName()+System.lineSeparator());
                    writer.write("| Chance | Rewards |" + System.lineSeparator());
                    writer.write("| ----------- | ----------------- | " + System.lineSeparator());
                    for(Reward reward : crate.getRewards()) {
                        StringBuilder rewardString = new StringBuilder();
                        int count = 0;
                        for(ItemStack item : reward.getItems()) {
                            command.getSender().sendMessage("Writing: " + item.getType());
                            rewardString.append(item.getAmount()).append("x ").append(item.getI18NDisplayName());
                            if(count<reward.getItems().size()-1) {
                                rewardString.append(", ");
                            }
                            count++;
                        }

                        if(reward.hasDisplayItem()) {
                            rewardString = new StringBuilder(reward.getDisplayItem().getItemMeta().getDisplayName());
                        }
                        writer.write("| "+ String.format("%.2f",reward.getChance()) + " | " + rewardString + " |");
                        writer.write(System.lineSeparator());
                    }
                }
            }

            command.getSender().sendMessage("Docs written to docs.txt");

        } catch (IOException e) {
            throw new CommandException(e.getMessage());
        }


    }

    private String toFriendly(Enum<?> zEnum) {
        String str = zEnum.toString();
        str = str.toLowerCase(Locale.ROOT);
        str = str.replace('_',' ');
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
