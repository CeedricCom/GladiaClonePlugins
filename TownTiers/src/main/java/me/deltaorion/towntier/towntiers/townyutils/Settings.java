package me.deltaorion.towntier.towntiers.townyutils;

import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;

public class Settings {
    public static IntegerDataField townBankPercentage;
    public static IntegerDataField hasteInClaims;
    public static IntegerDataField speedInClaims;
    public static IntegerDataField noFallClaims;
    public static IntegerDataField pearlClaims;
    public static IntegerDataField hungerClaims;

    static {
        townBankPercentage = new IntegerDataField("TownTiers.TownBankSettings");
        hasteInClaims = new IntegerDataField("TownTiers.HasteClaimsSetting");
        speedInClaims = new IntegerDataField("TownTiers.SpeedClaimsSetting");
        pearlClaims = new IntegerDataField("TownTiers.pearlClaimsSetting");
        hungerClaims = new IntegerDataField("TownTiers.hungerClaimsSetting");
        noFallClaims = new IntegerDataField("TownTiers.noFallClaimsSetting");
    }
}
