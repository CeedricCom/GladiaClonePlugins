package me.deltaorion.townymissionsv2.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.KeyAlreadyRegisteredException;
import com.palmergames.bukkit.towny.object.*;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.LongDataField;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import me.deltaorion.townymissionsv2.player.ContributeType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TownyUtil {

    public static Government getGovernment(UUID uuid) {
        Town town = TownyUniverse.getInstance().getTown(uuid);
        if(town!=null)
            return town;

        return TownyUniverse.getInstance().getNation(uuid);
    }

    public static Government getGovernment(String name) {
        Town town = TownyUniverse.getInstance().getTown(name);
        if(town==null) {
            return TownyUniverse.getInstance().getNation(name);
        } else {
            return town;
        }
    }

    public static List<String> getGovernmentTabCompletions() {
        List<String> completions = new ArrayList<>();
        for(Town town : TownyUniverse.getInstance().getTowns()) {
            completions.add(town.getName());
        }

        for(Nation nation : TownyUniverse.getInstance().getNations()) {
            completions.add(nation.getName());
        }

        return completions;
    }

    public static <T> void updateMetaData(TownyObject object, CustomDataField<T> dataField, T value) {

        try {
            TownyAPI.getInstance().registerCustomDataField(dataField);
        } catch (KeyAlreadyRegisteredException ignored) {

        }

        if(object.hasMeta(dataField.getKey())) {
            CustomDataField cdf = object.getMetadata(dataField.getKey());
            cdf.setValue(value);
        } else {
            dataField.setValue(value);
            object.addMetaData(dataField.clone());
        }
    }

    public static <T> T getMetaData(TownyObject object, CustomDataField<T> dataField, T def) {
        if (object.hasMeta(dataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = object.getMetadata(dataField.getKey());
            // Check that it's an IntegerDataField
            return (T) cdf.getValue();
        }
        // Return a default value
        return def;
    }

    public static void removeMetaData(TownyObject object, CustomDataField<?> dataField) {
        object.removeMetaData(dataField);
    }

    public static ContributeType getType(Government government) {
        if(government instanceof Town)
            return ContributeType.TOWN;

        if(government instanceof Nation)
            return ContributeType.NATION;

        return null;
    }

    public static List<Government> getGovernments() {
        List<Government> governments = new ArrayList<>();
        governments.addAll(TownyUniverse.getInstance().getNations());
        governments.addAll(TownyUniverse.getInstance().getTowns());

        return governments;
    }
}
