package me.deltaorion.townymissionsv2.command;

public interface CommandParser {

    public <T> T parse(String str) throws CommandException;
}
