package me.deltaorion.towntier.towntiers.townyutils;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.*;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownyUtils {
    public static long getMetaDataFromTown(Town t, LongDataField longDataField) {
        // Check that the town has the metadata key.
        if (t.hasMeta(longDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = t.getMetadata(longDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof LongDataField) {
                // Cast to IntegerDataField
                LongDataField idf = (LongDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return -1;
    }
    public static boolean playerInOwnTown(Player player) {
        try {
            if(!TownyAPI.getInstance().isWilderness(player.getLocation())) {
                Town town = TownyAPI.getInstance().getTownBlock(player.getLocation()).getTown();
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (resident != null) {
                    if (resident.getTown().equals(town)) {
                        return true;
                    }
                }
            }
        } catch (NotRegisteredException e) {
            return false;
        }
        return false;
    }
    public static boolean locationInPlayersTown(Player player, Location location) {
        try {
            if(!TownyAPI.getInstance().isWilderness(location)) {
                Town town = TownyAPI.getInstance().getTownBlock(location).getTown();
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (resident != null) {
                    if (resident.getTown().equals(town)) {
                        return true;
                    }
                }
            }
        } catch (NotRegisteredException e) {
            return false;
        }
        return false;
    }
    public static Town getTownFromPlayer(Player player) {
        Resident resident= TownyUniverse.getInstance().getResident(player.getUniqueId());
        Town town = null;
        if(resident==null) {
            return null;
        }
        if(resident.hasTown()) {
            try {
                town = resident.getTown();
            } catch (NotRegisteredException e) {
                return null;
            }
        } else {
            return null;
        }
        return town;
    }
    public static Nation getNationFromPlayer(Player player) {
        Nation nation = null;
        try {
            Resident resident = TownyUniverse.getInstance().getResident(player.getUniqueId());
            if(resident==null) {
                return null;
            }
            if(resident.hasTown()) {
                Town town = resident.getTown();
                if(town.hasNation()) {
                    nation = town.getNation();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (NotRegisteredException e) {
            return null;
        }
        return nation;
    }
    public static String getMetaDataFromTown(Town t, StringDataField stringDataField) {
        // Check that the town has the metadata key.
        if (t.hasMeta(stringDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = t.getMetadata(stringDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof StringDataField) {
                // Cast to IntegerDataField
                StringDataField idf = (StringDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return null;
    }
    public static int getMetaDataFromTown(Town t, IntegerDataField integerDataField) {
        // Check that the town has the metadata key.
        if (t.hasMeta(integerDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = t.getMetadata(integerDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof IntegerDataField) {
                // Cast to IntegerDataField
                IntegerDataField idf = (IntegerDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return -1;
    }
    public static void updateTownMetaData(Town t, int newVal, IntegerDataField integerDataField) {
        if(t.hasMeta(integerDataField.getKey())) {
            CustomDataField cdf = t.getMetadata(integerDataField.getKey());
            if(cdf instanceof IntegerDataField) {
                IntegerDataField sdf = (IntegerDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            integerDataField.setValue(newVal);
            t.addMetaData(integerDataField.clone());
        }
    }
    public static int getMetaDataFromNation(Nation n, IntegerDataField integerDataField) {
        // Check that the town has the metadata key.
        if (n.hasMeta(integerDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = n.getMetadata(integerDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof IntegerDataField) {
                // Cast to IntegerDataField
                IntegerDataField idf = (IntegerDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return -1;
    }
    public static void removeMetaDataFromTown(Town town,CustomDataField cdf) {
        town.removeMetaData(cdf);
    }

    public static void updateTownMetaData(Town t, String newVal, StringDataField stringDataField) {
        if(t.hasMeta(stringDataField.getKey())) {
            CustomDataField cdf = t.getMetadata(stringDataField.getKey());
            if(cdf instanceof  StringDataField) {
                StringDataField sdf = (StringDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            stringDataField.setValue(newVal);
            t.addMetaData(stringDataField.clone());
        }
    }

    public static String getResidentMetaData(Resident resident,StringDataField stringDataField) {
        if (resident.hasMeta(stringDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = resident.getMetadata(stringDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof StringDataField) {
                // Cast to IntegerDataField
                StringDataField idf = (StringDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return null;

    }
    public static int getResidentMetaData(Resident resident, IntegerDataField integerDataField) {
        if (resident.hasMeta(integerDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = resident.getMetadata(integerDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof IntegerDataField) {
                // Cast to IntegerDataField
                IntegerDataField idf = (IntegerDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return 0;
    }
    public static long getResidentMetaData(Resident resident,LongDataField longDataField) {
        if (resident.hasMeta(longDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = resident.getMetadata(longDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof LongDataField) {
                // Cast to IntegerDataField
                LongDataField ldf = (LongDataField) cdf;

                // Return the read value which is an integer.
                return ldf.getValue();
            }
        }
        // Return a default value
        return -1;
    }
    public static void updateResidentMetaData(Resident resident, String newVal, StringDataField stringDataField) {
        if(resident.hasMeta(stringDataField.getKey())) {
            CustomDataField cdf = resident.getMetadata(stringDataField.getKey());
            if(cdf instanceof  StringDataField) {
                StringDataField sdf = (StringDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            stringDataField.setValue(newVal);
            resident.addMetaData(stringDataField.clone());
        }
    }
    public static void updateResidentMetaData(Resident resident, int newVal, IntegerDataField integerDataField) {
        if(resident.hasMeta(integerDataField.getKey())) {
            CustomDataField cdf = resident.getMetadata(integerDataField.getKey());
            if(cdf instanceof  IntegerDataField) {
                IntegerDataField sdf = (IntegerDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            integerDataField.setValue(newVal);
            resident.addMetaData(integerDataField.clone());
        }
    }
    public static void updateTownMetaData(Town t, long newVal,LongDataField longDataField) {
        if(t.hasMeta(longDataField.getKey())) {
            CustomDataField cdf = t.getMetadata(longDataField.getKey());
            if(cdf instanceof  LongDataField) {
                LongDataField sdf = (LongDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            longDataField.setValue(newVal);
            t.addMetaData(longDataField.clone());
        }
    }
    public static void updateTownMetaData(Town t, boolean newVal, BooleanDataField booleanDataField) {
        if(t.hasMeta(booleanDataField.getKey())) {
            CustomDataField cdf = t.getMetadata(booleanDataField.getKey());
            if(cdf instanceof BooleanDataField) {
                BooleanDataField sdf = (BooleanDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            booleanDataField.setValue(newVal);
            t.addMetaData(booleanDataField.clone());
        }
    }
    /////////////////////////////////////////////////////////
    public static long getMetaDataFromNation(Nation n, LongDataField longDataField) {
        // Check that the town has the metadata key.
        if (n.hasMeta(longDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = n.getMetadata(longDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof LongDataField) {
                // Cast to IntegerDataField
                LongDataField idf = (LongDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return -1;
    }
    public static String getMetaDataFromNation(Nation n, StringDataField stringDataField) {
        // Check that the town has the metadata key.
        if (n.hasMeta(stringDataField.getKey())) {
            // Get the metadata from the town using the key.
            CustomDataField cdf = n.getMetadata(stringDataField.getKey());
            // Check that it's an IntegerDataField
            if (cdf instanceof StringDataField) {
                // Cast to IntegerDataField
                StringDataField idf = (StringDataField) cdf;

                // Return the read value which is an integer.
                return idf.getValue();
            }
        }

        // Return a default value
        return null;
    }
    public static void removeMetaDataFromNation(Nation n,CustomDataField cdf) {
        n.removeMetaData(cdf);
    }

    public static void updateNationMetaData(Nation n, String newVal, StringDataField stringDataField) {
        if(n.hasMeta(stringDataField.getKey())) {
            CustomDataField cdf = n.getMetadata(stringDataField.getKey());
            if(cdf instanceof  StringDataField) {
                StringDataField sdf = (StringDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            stringDataField.setValue(newVal);
            n.addMetaData(stringDataField.clone());
        }
    }
    public static void updateNationMetaData(Nation n, int newVal, IntegerDataField integerDataField) {
        if(n.hasMeta(integerDataField.getKey())) {
            CustomDataField cdf = n.getMetadata(integerDataField.getKey());
            if(cdf instanceof  IntegerDataField) {
                IntegerDataField sdf = (IntegerDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            integerDataField.setValue(newVal);
            n.addMetaData(integerDataField.clone());
        }
    }
    public static void updateNationMetaData(Nation n, long newVal,LongDataField longDataField) {
        if(n.hasMeta(longDataField.getKey())) {
            CustomDataField cdf = n.getMetadata(longDataField.getKey());
            if(cdf instanceof  LongDataField) {
                LongDataField sdf = (LongDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            longDataField.setValue(newVal);
            n.addMetaData(longDataField.clone());
        }
    }
    public static void updateNationMetaData(Nation n, boolean newVal, BooleanDataField booleanDataField) {
        if(n.hasMeta(booleanDataField.getKey())) {
            CustomDataField cdf = n.getMetadata(booleanDataField.getKey());
            if(cdf instanceof BooleanDataField) {
                BooleanDataField sdf = (BooleanDataField) cdf;
                sdf.setValue(newVal);
            }
        } else {
            booleanDataField.setValue(newVal);
            n.addMetaData(booleanDataField.clone());
        }
    }
}

