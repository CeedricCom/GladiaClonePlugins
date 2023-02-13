package me.deltaorion.towntier.towntiers.data;

import me.deltaorion.towntier.towntiers.TownTiers;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataManager {
    public DataManager(String fileName) {
        this.fileName = fileName;
        saveDefaultConfig();
    }


    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String fileName;

    public void reloadConfig() {
        if(configFile==null) {
            configFile = new File(TownTiers.getInstance().getDataFolder(), fileName);
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile);
        InputStream defaultStream = TownTiers.getInstance().getResource(fileName);
        if(defaultStream!=null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if(dataConfig==null) {
            reloadConfig();
        }
        return dataConfig;
    }

    public void saveConfig() {
        if(dataConfig==null || configFile ==null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void saveDefaultConfig() {

        if(configFile == null) {
            File file = new File(String.valueOf(TownTiers.getInstance().getDataFolder().getAbsoluteFile()));
            file.mkdir();

            configFile = new File(TownTiers.getInstance().getDataFolder(), fileName);
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(!configFile.exists()) {
            TownTiers.getInstance().saveResource(fileName,false);
        }
    }
    public void reset() {
        configFile = new File(TownTiers.getInstance().getDataFolder(), fileName);
        try {
            configFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}