package me.deltaorion.siegestats.controller.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.siegestats.Permissions;
import me.deltaorion.siegestats.SiegeStatsPlugin;
import me.deltaorion.siegestats.StringUtil;
import me.deltaorion.siegestats.model.SiegeKill;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.model.killer.Participant;
import me.deltaorion.siegestats.service.SiegeService;
import me.deltaorion.siegestats.view.report.CSVReportGenerator;
import me.deltaorion.siegestats.view.report.PasteBinPasteTransmitter;
import me.deltaorion.siegestats.view.report.PasteTransmitter;
import me.deltaorion.siegestats.view.report.ReportGenerator;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ReportCommand extends FunctionalCommand {

    private final SiegeStatsPlugin plugin;
    private final File reportDir;
    private final SiegeService siegeManager;

    protected ReportCommand(SiegeStatsPlugin plugin, File reportDir, SiegeService siegeManager) {
        super(Permissions.REPORT_COMMAND);
        this.plugin = plugin;
        this.reportDir = reportDir;
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

        final StatSiege clone = siege.clone();
        command.getSender().sendMessage("Writing Siege Report for '"+siege.getDefenderNationName()+"'");
        new BukkitRunnable() {
            @Override
            public void run() {
                command.getSender().sendMessage("Starting Runnable");
                String ordinalText = "";
                if(num > 0) {
                    ordinalText = StringUtil.getOrdinal(num+1) + " ";
                }

                String name = (ordinalText + "Siege of "+clone.getBesieged().getLastName()).replace(' ','_');
                try {
                    if(!reportDir.exists())
                        reportDir.mkdir();

                    File file = new File(reportDir,name+".csv");
                    if (!file.exists())
                        file.createNewFile();

                    FileWriter writer = new FileWriter(file);
                    ReportGenerator generator = new CSVReportGenerator();
                    generator.generate(writer,clone,num);
                    command.getSender().sendMessage("Successfully Saved Report '"+file.getName());
                    command.getSender().sendMessage("Uploading to pastebin... ");
                    PasteTransmitter transmitter = new PasteBinPasteTransmitter();
                    String response = transmitter.send(new FileInputStream(file));
                    command.getSender().sendMessage(ChatColor.GOLD + response);
                } catch (IOException e) {
                    command.getSender().sendMessage("Could not generate report '"+e.getMessage()+"'");
                }
            }
        }.runTaskAsynchronously(plugin);

    }
}
