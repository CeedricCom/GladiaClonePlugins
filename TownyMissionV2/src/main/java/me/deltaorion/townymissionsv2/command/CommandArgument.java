package me.deltaorion.townymissionsv2.command;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.util.DurationParser;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.function.Function;

public class CommandArgument {

    private final TownyMissionsV2 plugin;
    private final int index;
    private final String arg;

    public CommandArgument(TownyMissionsV2 plugin, int index, String arg) {
        this.arg = arg;
        this.index = index;
        this.plugin = plugin;
    }

    public int asInt() throws CommandException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new CommandException(Message.COMMAND_NOT_INTEGER.getMessage());
        }
    }

    public int asIntOrElse(Function<String,Integer> orElse) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return orElse.apply(arg);
        }
    }

    public int asIntOrDefault(int def) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public float asFloat() throws CommandException {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw new CommandException(Message.COMMAND_NOT_NUMBER.getMessage());
        }
    }

    public float asFloatOrElse(Function<String,Float> orElse) {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            return orElse.apply(arg);
        }
    }

    public float asFloatOrDefault(float def) {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public double asDouble() throws CommandException {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new CommandException(Message.COMMAND_NOT_NUMBER.getMessage());
        }
    }

    public double asDoubleOrElse(Function<String,Double> orElse) {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            return orElse.apply(arg);
        }
    }

    public double asDoubleOrDefault(double def) {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean asBoolean() throws CommandException {
        try {
            return parseBoolean(arg);
        } catch (IllegalArgumentException e) {
            throw new CommandException(Message.COMMAND_NOT_BOOLEAN.getMessage());
        }
    }

    public boolean asBooleanOrElse(Function<String,Boolean> orElse) {
        try {
            return parseBoolean(arg);
        } catch (IllegalArgumentException e) {
            return orElse.apply(arg);
        }
    }

    public boolean asBooleanOrDefault(boolean def) {
        try {
            return parseBoolean(arg);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public <T extends Enum<T>> T asEnum(Class<T> type, String name) throws CommandException {
        try {
            return Enum.valueOf(type, arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CommandException(Message.COMMAND_NOT_ENUM.getMessage(name));
        }
    }

    public <T extends Enum<T>> T asEnumOrDefault(Class<T> type, T def) throws CommandException {
        try {
            return Enum.valueOf(type, arg.toUpperCase());
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public Duration asDuration() throws CommandException {
        try {
            return DurationParser.parseDuration(arg);
        } catch (IllegalArgumentException e) {
            throw new CommandException(Message.COMMAND_INVALID_DURATION.getMessage());
        }
    }

    public Duration asDurationOrElse(Function<String,Duration> orElse) throws CommandException {
        try {
            return DurationParser.parseDuration(arg);
        } catch (IllegalArgumentException e) {
            return orElse.apply(arg);
        }
    }

    public Duration asDuration(Duration def) throws CommandException {
        try {
            return DurationParser.parseDuration(arg);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public Player asPlayer() throws CommandException {
        Player player = plugin.getServer().getPlayer(arg);
        if(player==null) {
            throw new CommandException(Message.COMMAND_NOT_PLAYER.getMessage());
        } else {
            return player;
        }
    }

    public Player asPlayerOrElse(Function<String,Player> orElse) throws CommandException {
        Player player = plugin.getServer().getPlayer(arg);
        if(player==null) {
            throw new CommandException(Message.COMMAND_NOT_PLAYER.getMessage());
        } else {
            return orElse.apply(arg);
        }
    }

    public Player asPlayerOrDef(Player def) throws CommandException {
        Player player = plugin.getServer().getPlayer(arg);
        if(player==null) {
            return def;
        } else {
            return player;
        }
    }

    public <T> T parse(Class<T> to, CommandParser parser) throws CommandException {
        return parser.parse(arg);
    }

    public boolean matches(String string) {
        return this.arg.equalsIgnoreCase(string);
    }
    public String asString() {
        return this.arg.toLowerCase();
    }

    public int getIndex() {
        return this.index;
    }

    public String toString() {
        return this.arg;
    }

    private boolean parseBoolean(String arg) {
        if(arg.equalsIgnoreCase("yes") || arg.equalsIgnoreCase("true")) {
            return true;
        } else if(arg.equalsIgnoreCase("no") || arg.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
