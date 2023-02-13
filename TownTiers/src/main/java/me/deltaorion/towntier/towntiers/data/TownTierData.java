package me.deltaorion.towntier.towntiers.data;

import com.palmergames.bukkit.towny.object.metadata.BooleanDataField;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;

import java.util.UUID;

public class TownTierData {
    private final UUID uniqueID;
    private final boolean town;
    private int XP;
    private int tier;
    private String name;
    private boolean legacy;
    private UUID mayor;
    private int maxPop;
    private int yearOfDeath = 2000;

    private final static IntegerDataField tierField;
    private final static IntegerDataField xpField;
    private final static IntegerDataField extraClaimsHanded;
    private final static IntegerDataField regionalCapitalField;
    private final static IntegerDataField regionalCapitalCount;
    private final static IntegerDataField mostAmountResidentsField;

    static {
        tierField = new IntegerDataField("TownyTiers.tierField");
        xpField = new IntegerDataField("TownyTiers.xpField");
        extraClaimsHanded = new IntegerDataField("TownyTiers.extraClaimsField");
        regionalCapitalField = new IntegerDataField("TownyTiers.regionalCapital");
        regionalCapitalCount = new IntegerDataField("TownyTiers.regionalCapitalAmount");
        mostAmountResidentsField = new IntegerDataField("TownyTiers.mostAmountResidentsField");
    }

    public TownTierData(UUID uniqueID, boolean town, int XP, int tier, String name,boolean legacy) {
        this.uniqueID = uniqueID;
        this.town = town;
        this.XP = XP;
        this.tier = tier;
        this.name = name;
        this.legacy = legacy;
    }
    public TownTierData(UUID uniqueID, boolean town, int XP, int tier, String name,boolean legacy,UUID mayor, int maxPop) {
        this.uniqueID = uniqueID;
        this.town = town;
        this.XP = XP;
        this.tier = tier;
        this.name = name;
        this.legacy = legacy;
        this.mayor = mayor;
        this.maxPop = maxPop;
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public boolean isTown() {
        return town;
    }

    public int getXP() {
        return XP;
    }

    public void setXP(int XP) {
        this.XP = XP;
    }
    public void addXP(int XP) {
        this.XP += XP;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
    public void addTier(int tier) {
        this.tier+=tier;
    }

    public static IntegerDataField getTierField() {
        return tierField;
    }

    public static IntegerDataField getXpField() {
        return xpField;
    }

    public static IntegerDataField getExtraClaimsHanded() {
        return extraClaimsHanded;
    }

    public static IntegerDataField getRegionalCapitalField() {
        return regionalCapitalField;
    }

    public static IntegerDataField getRegionalCapitalCount() {
        return regionalCapitalCount;
    }

    public String getName() {
        return name;
    }

    public boolean isLegacy() {
        return legacy;
    }

    public void setLegacy(boolean legacy) {
        this.legacy = legacy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getMayor() {
        return mayor;
    }

    public void setMayor(UUID mayor) {
        this.mayor = mayor;
    }

    public static IntegerDataField getMostAmountResidentsField() {
        return mostAmountResidentsField;
    }

    public int getMaxPop() {
        return maxPop;
    }

    public int getYearOfDeath() {
        return yearOfDeath;
    }

    public void setMaxPop(int maxPop) {
        this.maxPop = maxPop;
    }

    public void setYearOfDeath(int yearOfDeath) {
        this.yearOfDeath = yearOfDeath;
    }
}
