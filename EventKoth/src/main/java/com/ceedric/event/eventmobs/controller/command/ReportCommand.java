package com.ceedric.event.eventmobs.controller.command;

import com.ceedric.event.eventmobs.EventMobs;
import com.ceedric.event.eventmobs.Permissions;
import com.ceedric.event.eventmobs.StringUtil;
import com.ceedric.event.eventmobs.model.BossWorld;
import com.ceedric.event.eventmobs.model.participant.BossSide;
import com.ceedric.event.eventmobs.view.paste.PasteBinPasteTransmitter;
import com.ceedric.event.eventmobs.view.paste.PasteTransmitter;
import com.ceedric.event.eventmobs.view.report.CSVReportGenerator;
import com.ceedric.event.eventmobs.view.report.ReportGenerator;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.util.DurationParser;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class ReportCommand extends FunctionalCommand {

    private final EventMobs plugin;

    protected ReportCommand(EventMobs plugin) {
        super(Permissions.SPAWN_COMMAND);
        this.plugin = plugin;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        BossWorld world = plugin.getWorld();
        File reportDir = plugin.getReportDir();

        final BossWorld clone = world.clone();
        command.getSender().sendMessage("Writing Boss Report");
        new BukkitRunnable() {
            @Override
            public void run() {
                command.getSender().sendMessage("Starting Runnable");
                String name = String.valueOf(System.currentTimeMillis());
                try {
                    if(!reportDir.exists())
                        reportDir.mkdir();

                    File file = new File(reportDir,name+".csv");
                    if (!file.exists())
                        file.createNewFile();

                    FileWriter writer = new FileWriter(file);
                    ReportGenerator generator = new CSVReportGenerator();
                    generator.generate(writer,clone);
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
