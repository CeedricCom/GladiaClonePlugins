package me.deltaorion.towntier.towntiers;

import org.bukkit.Material;

public class NationTier {
    private final int tier;
    private final int regionalCapitals;
    private final float doubleContributionPercentage;
    private final float extraMCMMOExperience;
    private final float gunPriceReduction;
    private final float vehiclePriceReduction;
    private final boolean announceOnLevelUp;
    private final int amountOfGenerators;
    private final Material viewMaterial;

    public NationTier(int tier, int regionalCapitals, float doubleContributionPercentage, float extraMCMMOExperience, float gunPriceReduction, float vehiclePriceReduction,boolean announceOnLevelUp,int amountOfGenerators, Material viewMaterial) {
        this.tier = tier;
        this.regionalCapitals = regionalCapitals;
        this.doubleContributionPercentage = doubleContributionPercentage;
        this.extraMCMMOExperience = extraMCMMOExperience;
        this.gunPriceReduction = gunPriceReduction;
        this.vehiclePriceReduction = vehiclePriceReduction;
        this.announceOnLevelUp = announceOnLevelUp;
        this.viewMaterial = viewMaterial;
        this.amountOfGenerators = amountOfGenerators;
    }

    public int getTier() {
        return tier;
    }

    public int getRegionalCapitals() {
        return regionalCapitals;
    }

    public float getDoubleContributionPercentage() {
        return doubleContributionPercentage;
    }

    public float getExtraMCMMOExperience() {
        return extraMCMMOExperience;
    }

    public float getGunPriceReduction() {
        return gunPriceReduction;
    }

    public float getVehiclePriceReduction() {
        return vehiclePriceReduction;
    }

    public boolean isAnnounceOnLevelUp() {
        return announceOnLevelUp;
    }

    public Material getViewMaterial() {
        return viewMaterial;
    }

    public int getAmountOfGenerators() {
        return amountOfGenerators;
    }
}
