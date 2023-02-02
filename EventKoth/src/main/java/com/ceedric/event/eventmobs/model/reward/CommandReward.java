package com.ceedric.event.eventmobs.model.reward;

import me.deltaorion.common.locale.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandReward implements Reward {

    private final String command;
    private final String name;

    public CommandReward(String command, String name) {
        this.command = command;
        this.name = name;
    }

    @Override
    public void giveReward(Player player) {
        String cmd = Message.valueOf(command).toString(player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    @Override
    public String getName() {
        return name;
    }
}
