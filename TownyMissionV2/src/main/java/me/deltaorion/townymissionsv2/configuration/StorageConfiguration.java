package me.deltaorion.townymissionsv2.configuration;

import com.google.common.io.Resources;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class StorageConfiguration {

    private final static String EXTENSION = ".yml";

    private final Path configurationPath;
    private File configFile;
    private final String fileName;
    private FileConfiguration dataConfig;
    private Plugin plugin;

    public StorageConfiguration(Plugin plugin, String fileName) {
        if (!fileName.endsWith(EXTENSION))
            fileName = fileName + EXTENSION;

        this.configurationPath = plugin.getDataFolder().toPath().resolve(fileName);
        this.configFile = configurationPath.toFile();
        this.plugin = plugin;
        this.fileName = fileName;
        saveDefaultConfig();
    }


    public void reloadConfig() {
        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultConfig = plugin.getResource(fileName);

        if (defaultConfig != null) {
            YamlConfiguration def = null;
            try {
                def = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfig));
                dataConfig.setDefaults(def);
            } catch (Exception e) {
                System.out.println("An Error occurred when loading " + defaultConfig);
            } finally {
                try {
                    defaultConfig.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    public FileConfiguration getConfig() {
        if (dataConfig == null) {
            reloadConfig();
        }
        return dataConfig;
    }

    public void saveConfig() {
        if (dataConfig == null || configFile == null) {
            return;
        }
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {

        if (!configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        if (!configFile.exists()) {
            saveResource(plugin);
        }
    }


    private void saveResource(Plugin plugin) {

        InputStream defaultStream = plugin.getResource(fileName);

        if(defaultStream==null)
            throw new IllegalArgumentException("Config File '"+configurationPath.toAbsolutePath().toString()+"' could not be found and there is no default config specified.");

        File outDirectory = configFile.getParentFile();
        File outFile = configFile;

        if(!outDirectory.exists())
            outDirectory.mkdirs();

        try {
            if (!outFile.exists()) {
                Files.copy(
                        defaultStream,
                        outFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
        } catch (IOException ex) {
            System.out.println( "Could not save " + outFile.getName() + " to " + outFile);
            ex.printStackTrace();
        } finally {
            try {
                defaultStream.close();
            } catch (IOException ignored) {

            }
        }
    }
}