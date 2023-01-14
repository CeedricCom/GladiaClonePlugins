package me.deltaorion.documenter;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;

public class DocumentationCommand extends FunctionalCommand {

    private final Documenter plugin;

    protected DocumentationCommand(Documenter plugin) {
        super("Documentor.admin");
        this.plugin = plugin;
        this.registerArgument("crates",new CrateDocumentCommand(this.plugin));
    }

    @Override
    public void commandLogic(SentCommand sentCommand) throws CommandException {

    }
}
