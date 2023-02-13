package me.deltaorion.towntier.towntiers;

import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;

public class Convertor {
    private static final IntegerDataField goldConvertorField;
    private static final IntegerDataField xpConvertorField;

    private final int level;
    private final int costToUpgrade;
    private final float conversionFactor;
    private boolean gold;

    public Convertor(int level, int costToUpgrade, float conversionFactor, boolean gold) {
        this.level = level;
        this.costToUpgrade = costToUpgrade;
        this.conversionFactor = conversionFactor;
        this.gold = gold;
    }

    static {
        goldConvertorField = new IntegerDataField("TownTiers.goldConField");
        xpConvertorField = new IntegerDataField("TownTiers.xpConField");
    }

    public static IntegerDataField getGoldConvertorField() {
        return goldConvertorField;
    }

    public static IntegerDataField getXpConvertorField() {
        return xpConvertorField;
    }

    public int getLevel() {
        return level;
    }

    public int getCostToUpgrade() {
        return costToUpgrade;
    }

    public float getConversionFactor() {
        return conversionFactor;
    }

    public boolean isGold() {
        return gold;
    }

    public void setGold(boolean gold) {
        this.gold = gold;
    }
}
