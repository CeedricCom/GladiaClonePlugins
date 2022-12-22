package me.deltaorion.consumescrolls.command;

import me.deltaorion.bukkit.command.tabcompletion.CompletersBukkit;
import me.deltaorion.common.command.Command;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.common.command.tabcompletion.Completers;
import me.deltaorion.common.locale.message.Message;
import me.deltaorion.consumescrolls.ConsumeScrollGenerator;
import me.deltaorion.consumescrolls.Rarity;
import me.deltaorion.consumescrolls.ScrollDefinition;
import me.deltaorion.consumescrolls.ScrollPool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class GiveConsumeScrollCommand extends FunctionalCommand {

    private final ScrollPool pool;
    private final ConsumeScrollGenerator generator;

    public GiveConsumeScrollCommand(ScrollPool pool, ConsumeScrollGenerator generator) {
        super(Permissions.GIVE,"[rarity/name/random]", Message.valueOf("gives a player a scroll"));
        this.pool = pool;
        this.generator = generator;
        addMeta();
    }

    private void addMeta() {
        registerArgument("random",new RandomGiveCommand());
        registerArgument("name",new ScrollGiveCommand());
        registerArgument("rarity",new RarityGiveCommand());
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        for(Map.Entry<String, Command> entry : getFunctions().entrySet()) {
            command.getSender().sendMessage("/ScrollAdmin " + entry.getKey()+": "+entry.getValue().getDescription());
        }
    }

    private class RandomGiveCommand extends FunctionalCommand {
        protected RandomGiveCommand() {
            super("","/scrolladmin give random <player>", Message.valueOf("gives a random scroll based on rarities"));
            registerCompleter(1,CompletersBukkit.BUKKIT_PLAYERS());
        }

        @Override
        public void commandLogic(SentCommand command) throws CommandException {
            Player senderPlayer = Bukkit.getPlayer(command.getSender().getUniqueId());
            Player receiver = command.getArgOrBlank(0).parseOrDefault(Player.class, senderPlayer);

            Rarity rarity = Rarity.getRandom();
            ScrollDefinition definition = pool.getRandomScrollByRarity(rarity);
            if(definition==null)
                throw new CommandException("No scroll can be found of rarity'"+rarity+"'");

            generator.give(receiver, definition);
            receiver.sendMessage("Gave '" + definition.getName() + "' to " + receiver.getName());
        }
    }

    private class RarityGiveCommand extends FunctionalCommand {
        protected RarityGiveCommand() {
            super("","/scrolladmin give rarity <player>", Message.valueOf("gives a random scroll from the selected rarity"));
            registerCompleter(1,Completers.ENUMS(Rarity.class));
            registerCompleter(2,CompletersBukkit.BUKKIT_PLAYERS());
        }

        @Override
        public void commandLogic(SentCommand command) throws CommandException {
            Player senderPlayer = Bukkit.getPlayer(command.getSender().getUniqueId());
            Player receiver = command.getArgOrBlank(1).parseOrDefault(Player.class, senderPlayer);

            Rarity rarity = command.getArgOrBlank(0).asEnum(Rarity.class,"Rarity");

            ScrollDefinition definition = pool.getRandomScrollByRarity(rarity);
            if(definition==null)
                throw new CommandException("No scroll can be found of rarity'"+rarity+"'");

            generator.give(receiver, definition);
            receiver.sendMessage("Gave '" + definition.getName() + "' to " + receiver.getName());
        }
    }

    private class ScrollGiveCommand extends FunctionalCommand {
        protected ScrollGiveCommand() {
            super("","/scrolladmin give random <player>", Message.valueOf("gives a random scroll of the given name"));
            registerCompleter(1,new NameCompleter(pool));
            registerCompleter(2,CompletersBukkit.BUKKIT_PLAYERS());
        }

        @Override
        public void commandLogic(SentCommand command) throws CommandException {
            Player senderPlayer = Bukkit.getPlayer(command.getSender().getUniqueId());
            Player receiver = command.getArgOrBlank(1).parseOrDefault(Player.class, senderPlayer);

            String scrollName = command.getArgOrBlank(0).asString();
            ScrollDefinition definition = pool.getScroll(scrollName);
            if(definition==null)
                throw new CommandException("Could not find scroll '"+scrollName+"'");

            generator.give(receiver, definition);
            receiver.sendMessage("Gave '" + definition.getName() + "' to " + receiver.getName());
        }
    }
}
