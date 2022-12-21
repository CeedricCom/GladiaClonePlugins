package me.deltaorion.townymissionsv2.command;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.configuration.Message;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ArgumentList {

    private final List<CommandArgument> argumentList;
    private final String label;
    private final TownyMissionsV2 plugin;
    private String usage;

    public ArgumentList(TownyMissionsV2 plugin, String[] args, String label) {
        this.label = label;
        this.plugin = plugin;
        this.argumentList = new ArrayList<>();
        this.usage = "";

        for(int i=0;i<args.length;i++) {
            argumentList.add(new CommandArgument(plugin,i,args[i]));
        }
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    private ArgumentList(TownyMissionsV2 plugin, List<CommandArgument> argumentList, String label,String usage) {
        this.label = label;
        this.plugin = plugin;
        this.argumentList = argumentList;
        this.usage = usage;
    }

    @Nullable
    public CommandArgument getArgNullable(int index) {
        if(invalidArg(index))
            return null;

        return argumentList.get(index);
    }

    public CommandArgument getArgOrFail(int index) throws CommandException {
        if(invalidArg(index)) {
            throw new CommandException(Message.COMMAND_BAD_USAGE.getMessage(this.usage));
        }

        return argumentList.get(index);
    }

    public CommandArgument getArgOrNothing(int index) throws CommandException {
        if(invalidArg(index))
            return new CommandArgument(plugin,index,"");

        return argumentList.get(index);
    }

    public boolean hasArg(int index) {
        return !invalidArg(index);
    }

    public String getRawArg(int index) {
        return argumentList.get(index).asString();
    }

    public List<CommandArgument> args() {
        return argumentList;
    }

    private boolean invalidArg(int arg) {
        return arg >= argumentList.size();
    }

    public String getTyped(int index) {

        index = Math.min(index,argumentList.size()-1);

        StringBuilder typed = new StringBuilder("/");
        typed.append(label);
        typed.append(" ");
        for(int i=0;i<=index;i++) {
            typed.append(argumentList.get(index).asString());
            typed.append(" ");
        }

        return typed.toString();
    }

    public ArgumentList reduce() {
        List<CommandArgument> copy = new ArrayList<>(argumentList);
        if(copy.size()>0) {
            copy.remove(0);
        }

        return new ArgumentList(plugin,copy,label,usage);
    }

    public String getLabel() {
        return label;
    }
}
