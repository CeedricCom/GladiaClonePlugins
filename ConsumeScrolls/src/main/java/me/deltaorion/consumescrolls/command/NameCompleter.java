package me.deltaorion.consumescrolls.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.command.tabcompletion.CompletionSupplier;
import me.deltaorion.consumescrolls.Rarity;
import me.deltaorion.consumescrolls.ScrollDefinition;
import me.deltaorion.consumescrolls.ScrollPool;

import java.util.ArrayList;
import java.util.List;

public class NameCompleter implements CompletionSupplier {

    private final ScrollPool pool;

    public NameCompleter(ScrollPool pool) {
        this.pool = pool;
    }

    @Override
    public List<String> getCompletions(SentCommand sentCommand) throws CommandException {
        List<String> values = new ArrayList<>();
        for(ScrollDefinition definition : pool.getScrolls()) {
            values.add(definition.getName());
        }

        return values;
    }
}
