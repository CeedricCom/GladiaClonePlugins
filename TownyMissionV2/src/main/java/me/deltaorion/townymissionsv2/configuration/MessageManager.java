package me.deltaorion.townymissionsv2.configuration;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.nio.file.Path;

public class MessageManager {

    public static StorageConfiguration configuration;
    private final static String PLACEHOLDER = "%s";

    public static void loadMessages(JavaPlugin plugin) {
        configuration = new StorageConfiguration(plugin,"messages.yml");
    }

    public static String getMessage(String location, Object... args) {

        if(configuration==null)
            return location;

        if(configuration.getConfig().getString(location) == null)
            return location;

        if(configuration.getConfig().getString(location).equals(""))
            return location;

        String rendered = configuration.getConfig().getString(location);
        if(rendered==null)
            return null;

        return ChatColor.translateAlternateColorCodes('&',substitutePlaceHolders(rendered,args));
    }

    public static String substitutePlaceHolders(String rendered, Object... args) {
        StringBuilder fin = new StringBuilder();
        int i = 0;
        int objCount = 0;
        while(i<rendered.length()-PLACEHOLDER.length()+1) {
            boolean placeholder = isPlaceHolder(rendered,PLACEHOLDER,i);

            Object arg = null;
            if(placeholder) {
                arg = getArg(objCount,args);
                objCount++;
            }

            if(arg==null) {
                fin.append(rendered.charAt(i));
                i++;
            } else {
                fin.append(arg);
                i+=PLACEHOLDER.length();
            }
        }

        while(i<rendered.length()) {
            fin.append(rendered.charAt(i));
            i++;
        }
        return fin.toString();
    }

    private static Object getArg(int objCount, Object[] args) {
        if (objCount < args.length) {
            return args[objCount];
        } else {
            return null;
        }
    }

    private static boolean isPlaceHolder(String rendered, String placeholder, int i) {
        for(int j=0;j<placeholder.length();j++) {
            char placeHolder = placeholder.charAt(j);
            char actual = rendered.charAt(i+j);
            if (actual != placeHolder) {
                return false;
            }
        }
        return true;
    }
}
