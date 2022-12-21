package me.deltaorion.townymissionsv2.display;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftSound {

    public static Pattern PATTERN = Pattern.compile("^[A-Z_]+,[ ]*+[0-3].[0-9]+,[ ]*[0-3].[0-9]+$");
    public static Pattern COMMA_SPACE = Pattern.compile(",[ ]*");

    private final Sound sound;
    private final float pitch;
    private final float volume;

    public MinecraftSound(Sound sound, float pitch, float volume) {
        this.sound = sound;
        this.pitch = pitch;
        this.volume = volume;
    }

    public void playSound(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public static MinecraftSound parseSound(String input) {
        //"LEVEL_UP,3.0,2.0"
        input = input.toUpperCase();

        Matcher matcher = PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("unable to parse sound!");
        }

        String[] inputSplit = COMMA_SPACE.split(input);

        Sound sound;
        float volume;
        float pitch;

        try {
            sound = Sound.valueOf(inputSplit[0]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse sound! Reason: Invalid Sound");
        }

        volume = Float.parseFloat(inputSplit[1]);
        pitch = Float.parseFloat(inputSplit[2]);

        return new MinecraftSound(sound, pitch, volume);
    }

    public String toString() {
        return sound + ", "+volume + ", " + pitch;
    }

}
