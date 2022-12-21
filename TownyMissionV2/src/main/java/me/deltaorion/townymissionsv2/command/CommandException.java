package me.deltaorion.townymissionsv2.command;

public class CommandException extends Exception {

    public CommandException(String errMessage) {
        super(errMessage);
    }

    public CommandException(String errMessage, Throwable e) {
        super(errMessage,e);
    }
}
