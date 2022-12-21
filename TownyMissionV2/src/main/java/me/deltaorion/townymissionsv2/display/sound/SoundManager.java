package me.deltaorion.townymissionsv2.display.sound;

import me.deltaorion.townymissionsv2.configuration.StorageConfiguration;
import me.deltaorion.townymissionsv2.display.MinecraftSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    public static final Map<String,MinecraftSound> sounds = new HashMap<>();
    public static final MinecraftSound PLACEHOLDER = new MinecraftSound(Sound.ENTITY_ENDERMAN_DEATH,1.0f,1.0f);

    public static void loadSounds(Plugin plugin) {
        StorageConfiguration configuration = new StorageConfiguration(plugin,"sounds");
        configuration.getConfig().getKeys(true).forEach(key -> {
            if(!configuration.getConfig().isConfigurationSection(key)) {
                try {
                    sounds.put(key, MinecraftSound.parseSound(configuration.getConfig().getString(key)));
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Trouble Loading Sound '" + key + "' Reason: '" + e.getMessage() + "'");
                }
            }
        });
    }

    public static MinecraftSound getSound(String location) {
        MinecraftSound sound = sounds.get(location);
        if(sound!=null) {
            return sound;
        } else {
            return PLACEHOLDER;
        }
    }
}
