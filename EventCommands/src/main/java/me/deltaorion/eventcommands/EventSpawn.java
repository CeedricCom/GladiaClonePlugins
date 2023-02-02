package me.deltaorion.eventcommands;

import org.bukkit.ChatColor;
import org.bukkit.Location;

public class EventSpawn {

    private Location location;
    private String commandName;
    private String displayName;

    public EventSpawn(Location location, String commandName, String displayName) {
        this.location = location;
        this.commandName = commandName;
        this.displayName = displayName;
    }

    public Location getLocation() {
        return location;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&',displayName);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
