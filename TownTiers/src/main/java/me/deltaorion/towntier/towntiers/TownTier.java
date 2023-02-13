package me.deltaorion.towntier.towntiers;

import org.bukkit.Material;

public class TownTier {
    private final int tier;
    private final int extraClaims;
    private final float townMissionBankPercentage;
    private final float doubleContributionPercentage;
    private final float extraMCMMOExperience;
    private final int extraTownSpawns;
    private final boolean pearlsLost;
    private final int speedInClaims;
    private final int hasteInClaims;
    private final boolean hungerInClaims;
    private final boolean fallDamageInClaims;
    private final boolean announceOnLevelUp;
    private final int amountOfGenerators;
    private final Material viewMaterial;

    public TownTier(int tier, int extraClaims, float townMissionBankPercentage, float doubleContributionPercentage, float extraMCMMOExperience, int extraTownSpawns, boolean pearlsLost, int speedInClaims, int hasteInClaims, boolean announceOnfLevelUp,boolean hungerInClaims,boolean fallDamageInClaims,int amountOfGenerators, Material viewMaterial) {
        this.tier = tier;
        this.extraClaims = extraClaims;
        this.townMissionBankPercentage = townMissionBankPercentage;
        this.doubleContributionPercentage = doubleContributionPercentage;
        this.extraMCMMOExperience = extraMCMMOExperience;
        this.extraTownSpawns = extraTownSpawns;
        this.pearlsLost = pearlsLost;
        this.speedInClaims = speedInClaims;
        this.hasteInClaims = hasteInClaims;
        this.announceOnLevelUp = announceOnfLevelUp;
        this.viewMaterial = viewMaterial;
        this.hungerInClaims = hungerInClaims;
        this.fallDamageInClaims = fallDamageInClaims;
        this.amountOfGenerators = amountOfGenerators;
    }

    public int getTier() {
        return tier;
    }

    public int getExtraClaims() {
        return extraClaims;
    }

    public float getTownMissionBankPercentage() {
        return townMissionBankPercentage;
    }

    public float getDoubleContributionPercentage() {
        return doubleContributionPercentage;
    }

    public float getExtraMCMMOExperience() {
        return extraMCMMOExperience;
    }

    public int getExtraTownSpawns() {
        return extraTownSpawns;
    }


    public boolean isPearlsLost() {
        return pearlsLost;
    }

    public int getSpeedInClaims() {
        return speedInClaims;
    }

    public int getHasteInClaims() {
        return hasteInClaims;
    }

    public boolean isAnnounceOnLevelUp() {
        return announceOnLevelUp;
    }

    public Material getViewMaterial() {
        return viewMaterial;
    }

    public boolean isHungerInClaims() {
        return hungerInClaims;
    }

    public boolean isFallDamageInClaims() {
        return fallDamageInClaims;
    }

    public int getAmountOfGenerators() {
        return amountOfGenerators;
    }
}
