package me.deltaorion.consumescrolls.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.consumescrolls.ConsumeScrollGenerator;
import me.deltaorion.consumescrolls.ScrollDefinition;
import me.deltaorion.consumescrolls.ScrollPool;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class ConsumeScrollCommand extends FunctionalCommand {

    private final ScrollPool pool;
    private final ConsumeScrollGenerator generator;

    public ConsumeScrollCommand(ScrollPool pool, ConsumeScrollGenerator generator) {
        super("ConsumeScrolls.Admin");
        this.pool = pool;
        this.generator = generator;
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        String scrollName = command.getArgOrFail(0).asString();
        ScrollDefinition scroll = pool.getScroll(scrollName);
        if(scroll==null)
            throw new CommandException("Unknown scroll '"+scrollName+"'");

        generator.give(Bukkit.getPlayer(command.getSender().getUniqueId()),scroll);
        command.getSender().sendMessage("Gave scroll "+scrollName);
    }
}
