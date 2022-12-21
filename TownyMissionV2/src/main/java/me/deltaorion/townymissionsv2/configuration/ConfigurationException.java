package me.deltaorion.townymissionsv2.configuration;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String key, Object value, String message) {
        super(message + System.lineSeparator() + "Issue Here -> " + "'" + key + ": "+value + "'");
    }

    public ConfigurationException(ConfigurationSection section, String key) {
        super("Configuration Missing Key: "+section.getCurrentPath()+"."+key);
    }

}
