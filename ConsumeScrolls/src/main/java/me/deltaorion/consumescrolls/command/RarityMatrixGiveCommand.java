package me.deltaorion.consumescrolls.command;

import me.deltaorion.common.command.CommandException;
import me.deltaorion.common.command.FunctionalCommand;
import me.deltaorion.common.command.sent.SentCommand;
import me.deltaorion.consumescrolls.ConsumeScrollGenerator;
import me.deltaorion.consumescrolls.Rarity;
import me.deltaorion.consumescrolls.ScrollDefinition;
import me.deltaorion.consumescrolls.ScrollPool;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class RarityMatrixGiveCommand extends FunctionalCommand {

    private final Random random;
    private final Pattern COLON = Pattern.compile(":");
    private final ScrollPool pool;
    private final ConsumeScrollGenerator generator;

    protected RarityMatrixGiveCommand(ScrollPool pool, ConsumeScrollGenerator generator) {
        super(Permissions.GIVE);
        this.pool = pool;
        this.generator = generator;
        this.random = new Random();
    }

    @Override
    public void commandLogic(SentCommand command) throws CommandException {
        Player player = command.getArgOrFail(0).parse(Player.class);
        Map<Rarity,Integer> matrix = getMatrix(command.reduce(""));
        Rarity rarity = getRandomRarity(matrix);
        ScrollDefinition definition = pool.getRandomScrollByRarity(rarity);
        if(definition==null)
            throw new CommandException("No scroll can be found of rarity'"+rarity+"'");

        generator.give(player, definition);
        command.getSender().sendMessage("Gave '" + definition.getName() + "' to " + player.getName());
    }

    private Map<Rarity, Integer> getMatrix(SentCommand command) throws CommandException {
        Map<Rarity,Integer> matrix = new HashMap<>();
        for(String arg : command.getRawArgs()) {
            String[] split = COLON.split(arg);
            if(split.length<2)
                throw new CommandException("Unknown arg '"+arg+"'");

            Rarity rarity = Rarity.valueOf(split[0].toUpperCase(Locale.ROOT));
            int percentage = Integer.parseInt(split[1]);

            matrix.put(rarity,percentage);
        }

        return matrix;
    }

    private Rarity getRandomRarity(Map<Rarity, Integer> matrix) throws CommandException {
        int r = random.nextInt(100) + 1;
        int sum = 0;
        for(Map.Entry<Rarity,Integer> entry : matrix.entrySet()) {
            sum += entry.getValue();
            if(r <= sum)
                return entry.getKey();
        }

        throw new CommandException("Rarities do not add up to 100");
    }
}
