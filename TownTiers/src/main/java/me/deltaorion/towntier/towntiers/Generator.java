package me.deltaorion.towntier.towntiers;

//store last collection
//store current generator level

import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import com.palmergames.bukkit.towny.object.metadata.LongDataField;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Generator {

    private final static ArrayList<IntegerDataField> generatorLevels = new ArrayList<>();
    private final static ArrayList<LongDataField> collections = new ArrayList<>();

    private final int storage;
    private final int level;
    private final int costToUpgrade;
    private final int xpPerHour;
    private boolean town;

    static {
        String collectionKey = "TownTiers.lastGeneratorCollection";
        String levelKey = "TownTiers.generatorLevel";
        for(int i=0;i<6;i++) {
            generatorLevels.add(new IntegerDataField(levelKey+i));
            collections.add(new LongDataField(collectionKey+i));
        }

    }

    public Generator(int storage, int level, int costToUpgrade, int xpPerHour,boolean town) {
        this.storage = storage;
        this.level = level;
        this.costToUpgrade = costToUpgrade;
        this.xpPerHour = xpPerHour;
        this.town = town;
    }

    public static ArrayList<IntegerDataField> getGeneratorLevels() {
        return generatorLevels;
    }

    public static ArrayList<LongDataField> getCollections() {
        return collections;
    }

    public int getStorage() {
        return storage;
    }

    public int getLevel() {
        return level;
    }

    public int getCostToUpgrade() {
        return costToUpgrade;
    }

    public int getXpPerHour() {
        return xpPerHour;
    }

    public boolean isTown() {
        return town;
    }
}
