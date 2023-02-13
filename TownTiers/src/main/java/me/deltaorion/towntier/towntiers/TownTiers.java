package me.deltaorion.towntier.towntiers;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyEconomyHandler;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.KeyAlreadyRegisteredException;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.metadata.IntegerDataField;
import me.deltaorion.towntier.towntiers.commands.ExperienceCommand;
import me.deltaorion.towntier.towntiers.commands.RegionalCapitalCommand;
import me.deltaorion.towntier.towntiers.commands.TiersCommand;
import me.deltaorion.towntier.towntiers.data.DataManager;
import me.deltaorion.towntier.towntiers.data.PlayerData;
import me.deltaorion.towntier.towntiers.data.TownTierData;
import me.deltaorion.towntier.towntiers.townyutils.TownSpawn;
import me.deltaorion.towntier.towntiers.townyutils.TownyUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class TownTiers extends JavaPlugin {

    private final String operatorNode = "TownTiers.operator";
    private final String adminNode = "TownTiers.admin";

    private final HashMap<Integer, Integer> tiersDataTown = new HashMap<>();
    private final HashMap<Integer, Integer> tiersDataNation = new HashMap<>();
    private final HashMap<Integer, Integer> generatorData = new HashMap<>();


    /*
    Town tiers:
      1-10
      Town:
      Rewards:
        Town:
          - Town Spawns: x
          - Extra Claims:x
          - Mission Town Percentage: x
          - double contribution chance: x
          - MCMMO levelling: x
          - gun price reduction: x
          - vehicle reduction: x
          - claim price reduction
          - extra outposts: x
          - medium mission difficulty chance: x
          - hard mission difficulty chance: x

       Nation:
         - Regional Capitals: x
         - double mission contirbution chance:
         - MCMMO levelling:
         - gun price reduction:
         - vehicle reduction:
         - medium mission difficulty chance:
         - hard mission difficulty chance:

         Obtaining:
           - money cost
           - sieges
           - missions

           TODO - ensure legacy towns work/history
                - admin commands - near year, remove history
                - /confirm and cost for regional capitals
                - code in plugin gunshop and have that perk work, no fall damage perk
                - tonwy missions scaling
                - generator
                - integrate siegewar
     */

    private static TownTiers instance;
    private final HashMap<Integer, TownTier> townTiers = new HashMap<>();
    private final HashMap<Integer, NationTier> nationTiers = new HashMap<>();
    private final ArrayList<TownTierData> townData = new ArrayList<>();
    private final HashMap<Town, TownSpawn> townSpawns = new HashMap<>();
    private final ArrayList<Generator> generators = new ArrayList<>();
    private final ArrayList<Convertor> goldConvertors = new ArrayList<>();
    private final ArrayList<Convertor> xpConvertors = new ArrayList<>();

    private float siegeScaling = 0;
    private int siegeA = 0;

    private DataManager townSpawnManager;

    private float conversionPrice = 1;
    private float xpConversionPrice = 1;
    private float nationConversionPrice = 1;
    private float nationXPConversionPrice = 1;
    private float experienceM = 20000;
    private int experienceB = 10000;
    private float experienceMNation = 40000;
    private int experienceBNation = 20000;

    private int defaultSiegePoints = 10000;

    private final Sound successSound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
    private final Sound unableSound = Sound.UI_BUTTON_CLICK;
    private final Sound rewardSound = Sound.ENTITY_PLAYER_LEVELUP;
    private final Sound achievementSound = Sound.UI_TOAST_CHALLENGE_COMPLETE;

    private int regionalCapitalPrice = 2000;

    private DataManager dataManager;
    private TownyAPI townyAPI;

    private final HashMap<UUID, PlayerData> playerData = new HashMap<>();

    private final static IntegerDataField siegeWarChestField = new IntegerDataField("TownTiers.siegewon");


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        getCommand("tiers").setExecutor(new TiersCommand());
        getCommand("rc").setExecutor(new RegionalCapitalCommand());
        getCommand("myxp").setExecutor(new ExperienceCommand());

        //handle dependencies

        townyAPI = TownyAPI.getInstance();

        if (!TownyEconomyHandler.isActive()) {
            System.out.println("No Economy Plugin compatible with towny could be found - Disabling");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }


        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        if (Dependency.MCMMO.isActive()) {
            getServer().getPluginManager().registerEvents(new mcMMOListeners(), this);
        }

        dataManager = new DataManager("data.yml");
        townSpawnManager = new DataManager("spawns.yml");
        registerMetaData();

        loadConfig(false);
        loadTierData();

        new BukkitRunnable() {

            @Override
            public void run() {
                // What you want to schedule goes here
                sortTierList();
            }

        }.runTaskTimerAsynchronously(this, 0L, 6000L);


    }

    @Override
    public void onDisable() {
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
        String command = "ta database save";
        Bukkit.dispatchCommand(console, command);
        saveLegacyData();
    }

    public void loadConfig(boolean reload) {
        nationTiers.clear();
        townTiers.clear();

        regionalCapitalPrice = getConfig().getInt("regional-capital-cost");
        conversionPrice = getConfig().getInt("money-conversion-cost");
        xpConversionPrice = getConfig().getInt("xp-conversion-cost");
        nationConversionPrice = getConfig().getInt("nation-money-conversion-cost");
        nationXPConversionPrice = getConfig().getInt("nation-xp-conversion-cost");
        experienceM = (float) getConfig().getDouble("experience-m");
        experienceB = getConfig().getInt("experience-b");
        experienceMNation = (float) getConfig().getDouble("experience-m-nation");
        experienceBNation = getConfig().getInt("experience-b-nation");

        siegeScaling = (float) getConfig().getDouble("siege-scaling-factor");
        siegeA = getConfig().getInt("siege-scaling-A");

        getConfig().getConfigurationSection("nation-tiers").getKeys(false).forEach(key -> {
            int regionalCapitals = getConfig().getInt("nation-tiers." + key + ".regional-capital");
            float doubleMissionChance = getConfig().getInt("nation-tiers." + key + ".double-mission-contribution");
            float extraMCMMO = getConfig().getInt("nation-tiers." + key + ".extra-mcmmo");
            float gunPriceReduction = getConfig().getInt("nation-tiers." + key + ".gun-price-reduction");
            float vehiclePriceReduction = getConfig().getInt("nation-tiers." + key + ".vehicle-price-reduction");
            float mediumMissionChance = getConfig().getInt("nation-tiers." + key + ".medium-mission-difficulty-chance");
            float hardMissionChance = getConfig().getInt("nation-tiers." + key + ".hard-mission-difficulty-chance");
            int defensiveSlots = getConfig().getInt("nation-tiers." + key + ".defensive-slots");
            int extraAttackSlots = getConfig().getInt("nation-tiers." + key + ".extra-attacking-slots");
            boolean announceOnLevelUp = getConfig().getBoolean("nation-tiers." + key + ".announce-on-level");
            String viewMaterialString = getConfig().getString("nation-tiers." + key + ".view-material");
            viewMaterialString = viewMaterialString.toUpperCase();
            Material viewMaterial = Material.valueOf(viewMaterialString);
            int amountOfGenerators = getConfig().getInt("nation-tiers." + key + ".amount-of-generators");

            int tierLvL = Integer.parseInt(key);
            NationTier tier = new NationTier(tierLvL, regionalCapitals, doubleMissionChance, extraMCMMO, gunPriceReduction, vehiclePriceReduction, announceOnLevelUp, amountOfGenerators, viewMaterial);
            nationTiers.put(tierLvL, tier);
        });
        getConfig().getConfigurationSection("town-tiers").getKeys(false).forEach(key -> {
            int tierLvl = Integer.parseInt(key);
            int extraClaims = getConfig().getInt("town-tiers." + key + ".extra-claims");
            float doubleMissionChance = getConfig().getInt("town-tiers." + key + ".double-mission-contribution");
            float townBankExtraMission = getConfig().getInt("town-tiers." + key + ".town-bank-extra-mission");
            float extraMCMMO = getConfig().getInt("town-tiers." + key + ".extra-mcmmo");
            int extraTownSpawns = getConfig().getInt("town-tiers." + key + ".extra-town-spawns");
            boolean pearlsLost = getConfig().getBoolean("town-tiers." + key + ".pearls-lost");
            boolean hungerInClaims = getConfig().getBoolean("town-tiers." + key + ".hunger-in-claims");
            int speedInClaims = getConfig().getInt("town-tiers." + key + ".speed-in-claims");
            int hasteInClaims = getConfig().getInt("town-tiers." + key + ".haste-in-claims");
            boolean fallDamageClaims = getConfig().getBoolean("town-tiers." + key + ".fall-damage-claims");
            boolean announceOnLevelUp = getConfig().getBoolean("town-tiers." + key + ".announce-on-level");
            int amountOfGenerators = getConfig().getInt("town-tiers." + key + ".amount-of-generators");
            String viewMaterialString = getConfig().getString("town-tiers." + key + ".view-material");
            viewMaterialString = viewMaterialString.toUpperCase();
            Material viewMaterial = Material.valueOf(viewMaterialString);
            TownTier tier = new TownTier(tierLvl, extraClaims, townBankExtraMission, doubleMissionChance, extraMCMMO, extraTownSpawns, pearlsLost, speedInClaims, hasteInClaims, announceOnLevelUp, hungerInClaims, fallDamageClaims, amountOfGenerators, viewMaterial);
            townTiers.put(tierLvl, tier);
        });
        getConfig().getConfigurationSection("generators").getKeys(false).forEach(key -> {
            int level = getConfig().getInt("generators." + key + ".level");
            int storage = getConfig().getInt("generators." + key + ".storage");
            int costToUpgrade = getConfig().getInt("generators." + key + ".cost-to-upgrade");
            int xpPerMinute = getConfig().getInt("generators." + key + ".xp-per-hour");
            boolean town = getConfig().getBoolean("generators." + key + ".town");
            Generator generator = new Generator(storage, level, costToUpgrade, xpPerMinute, town);
            generators.add(generator);
        });
        getConfig().getConfigurationSection("convertor-gold").getKeys(false).forEach(key -> {
            int level = getConfig().getInt("convertor-gold." + key + ".level");
            int costToUpgrade = getConfig().getInt("convertor-gold." + key + ".cost-to-upgrade");
            int conversionFactor = getConfig().getInt("convertor-gold." + key + ".conversion-factor");
            Convertor convertor = new Convertor(level, costToUpgrade, conversionFactor, true);
            goldConvertors.add(convertor);
        });
        getConfig().getConfigurationSection("convertor-xp").getKeys(false).forEach(key -> {
            int level = getConfig().getInt("convertor-xp." + key + ".level");
            int costToUpgrade = getConfig().getInt("convertor-xp." + key + ".cost-to-upgrade");
            float conversionFactor = (float) getConfig().getDouble("convertor-xp." + key + ".conversion-factor");
            Convertor convertor = new Convertor(level, costToUpgrade, conversionFactor, false);
            xpConvertors.add(convertor);
        });
        if (!reload) {
            if (townSpawnManager.getConfig().isConfigurationSection("towns")) {
                townSpawnManager.getConfig().getConfigurationSection("towns").getKeys(false).forEach(key -> {
                    if (townSpawnManager.getConfig().isConfigurationSection("towns." + key + ".spawns")) {
                        UUID uuid = UUID.fromString(key);
                        Town town = TownyUniverse.getInstance().getTown(uuid);
                        TownSpawn townSpawnsM = new TownSpawn(town);
                        townSpawnManager.getConfig().getConfigurationSection("towns." + key + ".spawns").getKeys(false).forEach(number -> {
                            int type = Integer.parseInt(number);
                            String worldName = townSpawnManager.getConfig().getString("towns." + key + ".spawns." + number + ".world");
                            int x = townSpawnManager.getConfig().getInt("towns." + key + ".spawns." + number + ".x");
                            int y = townSpawnManager.getConfig().getInt("towns." + key + ".spawns." + number + ".y");
                            int z = townSpawnManager.getConfig().getInt("towns." + key + ".spawns." + number + ".z");
                            if (worldName != null) {
                                World world = Bukkit.getWorld(worldName);
                                Location location = new Location(world, x + 0.5, y, z + 0.5);
                                townSpawnsM.getSpawns().put(type, location);
                            }

                        });

                        townSpawns.put(town, townSpawnsM);
                    }

                });
            }
        }

    }

    private void loadTierData() {
        for (Town town : TownyUniverse.getInstance().getTowns()) {
            int tier = TownyUtils.getMetaDataFromTown(town, TownTierData.getTierField());
            int xp = TownyUtils.getMetaDataFromTown(town, TownTierData.getXpField());
            TownTierData data = new TownTierData(town.getUUID(), true, xp, tier, town.getName(), false);
            townData.add(data);
        }
        for (Nation nation : TownyUniverse.getInstance().getNations()) {
            int tier = TownyUtils.getMetaDataFromNation(nation, TownTierData.getTierField());
            int xp = TownyUtils.getMetaDataFromNation(nation, TownTierData.getXpField());
            TownTierData data = new TownTierData(nation.getUUID(), false, xp, tier, nation.getName(), false);
            townData.add(data);
        }
        if (dataManager.getConfig().isConfigurationSection("legacy-towns")) {
            dataManager.getConfig().getConfigurationSection("legacy-towns").getKeys(false).forEach(key -> {
                UUID uniqueId = UUID.fromString(key);
                boolean town = dataManager.getConfig().getBoolean("legacy-towns." + key + ".town");
                int xp = dataManager.getConfig().getInt("legacy-towns." + key + ".xp");
                int tier = dataManager.getConfig().getInt("legacy-towns." + key + ".tier");
                String name = dataManager.getConfig().getString("legacy-towns." + key + ".name");
                UUID mayor = UUID.fromString(dataManager.getConfig().getString("legacy-towns." + key + ".mayor"));
                int maxPop = dataManager.getConfig().getInt("legacy-towns." + key + ".max-pop");
                TownTierData data = new TownTierData(uniqueId, town, xp, tier, name, true, mayor, maxPop);
                townData.add(data);
            });
        }
        if (dataManager.getConfig().isConfigurationSection("town-data")) {
            dataManager.getConfig().getConfigurationSection("town-data").getKeys(false).forEach(key -> {
                int tier = Integer.parseInt(key);
                int amount = dataManager.getConfig().getInt("town-data." + key + ".amount");
                tiersDataTown.put(tier, amount);
            });
        }
        if (dataManager.getConfig().isConfigurationSection("nation-data")) {
            dataManager.getConfig().getConfigurationSection("nation-data").getKeys(false).forEach(key -> {
                int tier = Integer.parseInt(key);
                int amount = dataManager.getConfig().getInt("nation-data." + key + ".amount");
                tiersDataNation.put(tier, amount);
            });
        }
        if (dataManager.getConfig().isConfigurationSection("generator-data")) {
            dataManager.getConfig().getConfigurationSection("generator-data").getKeys(false).forEach(key -> {
                int tier = Integer.parseInt(key);
                int amount = dataManager.getConfig().getInt("generator-data." + key + ".amount");
                generatorData.put(tier, amount);
            });
        }
    }

    private void saveLegacyData() {
        for (TownTierData tierData : townData) {
            if (tierData.isLegacy()) {
                String key = tierData.getUniqueID().toString();
                dataManager.getConfig().set("legacy-towns." + key + ".town", tierData.isTown());
                dataManager.getConfig().set("legacy-towns." + key + ".xp", tierData.getXP());
                dataManager.getConfig().set("legacy-towns." + key + ".tier", tierData.getTier());
                dataManager.getConfig().set("legacy-towns." + key + ".name", tierData.getName());
                dataManager.getConfig().set("legacy-towns." + key + ".mayor", tierData.getMayor().toString());
                dataManager.getConfig().set("legacy-towns." + key + ".max-pop", tierData.getMaxPop());
            }
        }
        for (Map.Entry<Integer, Integer> data : tiersDataTown.entrySet()) {
            dataManager.getConfig().set("town-data." + data.getKey() + ".amount", data.getValue());
        }
        for (Map.Entry<Integer, Integer> data : tiersDataNation.entrySet()) {
            dataManager.getConfig().set("nation-data." + data.getKey() + ".amount", data.getValue());
        }
        for (Map.Entry<Integer, Integer> data : generatorData.entrySet()) {
            dataManager.getConfig().set("generator-data." + data.getKey() + ".amount", data.getValue());
        }
        dataManager.saveConfig();
        for (TownSpawn spawn : townSpawns.values()) {
            if (spawn.getTown() != null) {
                String uuid = spawn.getTown().getUUID().toString();
                for (Map.Entry<Integer, Location> entry : spawn.getSpawns().entrySet()) {
                    townSpawnManager.getConfig().set("towns." + uuid + ".spawns." + entry.getKey() + ".world", entry.getValue().getWorld().getName());
                    townSpawnManager.getConfig().set("towns." + uuid + ".spawns." + entry.getKey() + ".x", (int) entry.getValue().getX());
                    townSpawnManager.getConfig().set("towns." + uuid + ".spawns." + entry.getKey() + ".y", (int) entry.getValue().getY());
                    townSpawnManager.getConfig().set("towns." + uuid + ".spawns." + entry.getKey() + ".z", (int) entry.getValue().getZ());
                }
            }
        }
        townSpawnManager.saveConfig();
    }

    private void registerMetaData() {
        try {
            townyAPI.registerCustomDataField(TownTierData.getTierField());
            townyAPI.registerCustomDataField(TownTierData.getXpField());
            townyAPI.registerCustomDataField(TownTierData.getExtraClaimsHanded());
            townyAPI.registerCustomDataField(TownTierData.getRegionalCapitalField());
            townyAPI.registerCustomDataField(TownTierData.getMostAmountResidentsField());
            townyAPI.registerCustomDataField(Convertor.getGoldConvertorField());
            townyAPI.registerCustomDataField(Convertor.getXpConvertorField());
            townyAPI.registerCustomDataField(siegeWarChestField);
            for (int i = 0; i < Generator.getCollections().size(); i++) {
                townyAPI.registerCustomDataField(Generator.getCollections().get(i));
            }
            for (int i = 0; i < Generator.getGeneratorLevels().size(); i++) {
                townyAPI.registerCustomDataField(Generator.getGeneratorLevels().get(i));
            }
        } catch (KeyAlreadyRegisteredException e) {
            e.printStackTrace();
        }
    }

    public void addXPTown(Town town, int xp) {
        int initXp = xp;
        int townxp = TownyUtils.getMetaDataFromTown(town, TownTierData.getXpField());
        int townTier = TownyUtils.getMetaDataFromTown(town, TownTierData.getTierField());
        if (xp <= 0) {
            return;
        }

        if (townxp < 0 || townTier < 0) {
            TownyUtils.updateTownMetaData(town, 0, TownTierData.getXpField());
            TownyUtils.updateTownMetaData(town, 0, TownTierData.getTierField());
            townxp = 0;
            townTier = 0;
            return;
        }
        if (initXp > 0) {
            int netXpToNextTier = getXpToNextLevel(townTier, true) - townxp;
            while (xp >= netXpToNextTier && xp > 0) {
                xp -= netXpToNextTier;
                townTier += 1;
                levelUp(town, townTier);
                townxp = 0;
                netXpToNextTier = getXpToNextLevel(townTier, true) - townxp;
            }
            townxp += xp;
        } else {
            int totalXP = getTotalXPFromTier(townTier, true) + xp;
            totalXP = totalXP + xp;
            setXPTown(town, Math.max(totalXP, 0));
            return;
        }
        TownyUtils.updateTownMetaData(town, townxp, TownTierData.getXpField());
        TownyUtils.updateTownMetaData(town, townTier, TownTierData.getTierField());
        TownTierData tierData = getDataFromTown(town);
        tierData.setTier(townTier);
        tierData.setXP(townxp);
    }

    //admin command
    public void setXPTown(Town town, int xp) {
        float tier = getTierFromXP(xp, true);
        int tierRaw = (int) Math.floor(tier);
        int townxp = xp - getTotalXPFromTier(tierRaw, true);
        int townTier = tierRaw;

        TownyUtils.updateTownMetaData(town, townxp, TownTierData.getXpField());
        TownyUtils.updateTownMetaData(town, townTier, TownTierData.getTierField());
        TownTierData tierData = getDataFromTown(town);
        tierData.setTier(townTier);
        tierData.setXP(townxp);
    }

    //admin command
    public void setTierTown(Town town, int tier) {
        TownyUtils.updateTownMetaData(town, 0, TownTierData.getXpField());
        TownyUtils.updateTownMetaData(town, tier, TownTierData.getTierField());
        TownTierData tierData = getDataFromTown(town);
        tierData.setTier(tier);
        tierData.setXP(0);
    }

    //admin command
    public void resetXPTown(Town town) {
        int tier = Math.max(0, getTownTeir(town));
        removeTierDataTown(tier);
        TownyUtils.updateTownMetaData(town, 0, TownTierData.getXpField());
        TownyUtils.updateTownMetaData(town, 0, TownTierData.getTierField());
        TownTierData tierData = getDataFromTown(town);
        tierData.setTier(0);
        tierData.setXP(0);
        levelDown(town);
    }

    public void printXpNation(Nation nation, CommandSender sender) {
        int tier = Math.max(0, getNationTeir(nation));
        removeTierDataNation(tier);
        int nationxp = TownyUtils.getMetaDataFromNation(nation, TownTierData.getXpField());
        int nationTier = TownyUtils.getMetaDataFromNation(nation, TownTierData.getTierField());
        if (nationxp < 0 || nationTier < 0) {
            nationxp = 0;
            nationTier = 0;
        }
        int totalXP = getTotalXPFromTier(nationTier, false) + nationxp;
        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + nation.getName() + " XP" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage(ChatColor.GOLD + "Nation Tier: " + ChatColor.WHITE + nationTier);
        sender.sendMessage(ChatColor.GOLD + "Nation XP: " + ChatColor.WHITE + nationxp);
        sender.sendMessage(ChatColor.GOLD + "Total XP: " + ChatColor.WHITE + totalXP);
    }

    public void printXpTown(Town town, CommandSender sender) {
        int nationxp = TownyUtils.getMetaDataFromTown(town, TownTierData.getXpField());
        int nationTier = TownyUtils.getMetaDataFromTown(town, TownTierData.getTierField());
        if (nationxp < 0 || nationTier < 0) {
            nationxp = 0;
            nationTier = 0;
        }
        int totalXP = getTotalXPFromTier(nationTier, false) + nationxp;
        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + town.getName() + " XP" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage(ChatColor.GOLD + "Town Tier: " + ChatColor.WHITE + nationTier);
        sender.sendMessage(ChatColor.GOLD + "Town XP: " + ChatColor.WHITE + nationxp);
        sender.sendMessage(ChatColor.GOLD + "Total XP: " + ChatColor.WHITE + totalXP);
    }

    public void addXPNation(Nation nation, int xp) {
        int initXp = xp;
        int nationxp = TownyUtils.getMetaDataFromNation(nation, TownTierData.getXpField());
        int nationTier = TownyUtils.getMetaDataFromNation(nation, TownTierData.getTierField());
        if (xp <= 0) {
            return;
        }

        if (nationxp < 0 || nationTier < 0) {
            TownyUtils.updateNationMetaData(nation, 0, TownTierData.getXpField());
            TownyUtils.updateNationMetaData(nation, 0, TownTierData.getTierField());
            nationTier = 0;
            nationxp = 0;
            return;
        }
        if (initXp > 0) {
            int netXpToNextTier = getXpToNextLevel(nationTier, false) - nationxp;
            while (xp >= netXpToNextTier && xp > 0) {
                xp -= netXpToNextTier;
                nationTier += 1;
                levelUp(nation, nationTier);
                nationxp = 0;
                netXpToNextTier = getXpToNextLevel(nationTier, false) - nationxp;
            }
            nationxp += xp;
        } else {
            int totalXP = getTotalXPFromTier(nationTier, false);
            totalXP = totalXP - xp;
            float tier = getTierFromXP(totalXP, false);
            int tierRaw = (int) Math.floor(tier);
            nationxp = Math.max(xp - getTotalXPFromTier(tierRaw, false), 0);
            nationTier = Math.max(tierRaw, 0);
        }
        TownyUtils.updateNationMetaData(nation, nationxp, TownTierData.getXpField());
        TownyUtils.updateNationMetaData(nation, nationTier, TownTierData.getTierField());
        TownTierData tierData = getDataFromNation(nation);
        tierData.setTier(nationTier);
        tierData.setXP(nationxp);
    }

    public void printTopTowns(CommandSender sender, int page) {

        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + "Top Towns" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage(ChatColor.GRAY + "Rankings updated every 5 minutes");
        Town town = null;
        boolean findPlayer = false;
        int playerRank = -1;
        int playerTotal = -1;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            town = TownyUtils.getTownFromPlayer(player);
            if (town != null) {
                findPlayer = true;
            }
        }
        int count = 0;
        for (int i = 0; i < townData.size(); i++) {
            TownTierData tierData = townData.get(i);
            if (tierData.isTown()) {
                int totalXp = getTotalXPFromTier(tierData.getTier(), true) + tierData.getXP();
                if (tierData.getTier() <= 0) {
                    totalXp = Math.max(0, tierData.getXP());
                }
                if (findPlayer) {
                    if (tierData.getUniqueID().equals(town.getUUID())) {
                        findPlayer = false;
                        playerRank = count + 1;
                        playerTotal = totalXp;
                    }
                }
                if (count < page * 9 + 9 && count >= page * 9) {
                    if (tierData.isLegacy()) {
                        sender.sendMessage(ChatColor.WHITE + "" + (count + 1) + ") " + ChatColor.GREEN + tierData.getName() + " - " + ChatColor.WHITE + totalXp + "" + ChatColor.GREEN + " (legacy)");
                    } else {
                        sender.sendMessage(ChatColor.WHITE + "" + (count + 1) + ") " + ChatColor.GREEN + tierData.getName() + " - " + ChatColor.WHITE + totalXp);
                    }
                }
                count++;
            }
        }
        if (playerRank >= 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "Your Ranking - " + playerRank + ") " + ChatColor.WHITE + playerTotal);
        }
        sender.sendMessage(ChatColor.GOLD + "[Tip] Use " + ChatColor.YELLOW + "/tiers history" + ChatColor.GOLD + " for more detailed info!");
    }

    public void printTopNations(CommandSender sender, int page) {

        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + "Top Nations" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GRAY + "Rankings updated every 5 minutes");
        Nation nation = null;
        boolean findPlayer = false;
        int playerRank = -1;
        int playerTotal = -1;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            nation = TownyUtils.getNationFromPlayer(player);
            if (nation != null) {
                findPlayer = true;
            }
        }
        int count = 0;
        for (int i = 0; i < townData.size(); i++) {
            TownTierData tierData = townData.get(i);
            if (!tierData.isTown()) {
                int totalXp = getTotalXPFromTier(tierData.getTier(), false) + tierData.getXP();
                if (tierData.getTier() <= 0) {
                    totalXp = Math.max(0, tierData.getXP());
                }
                if (findPlayer) {
                    if (tierData.getUniqueID().equals(nation.getUUID())) {
                        findPlayer = false;
                        playerRank = count + 1;
                        playerTotal = totalXp;
                    }
                }
                if (count < page * 9 + 9 && count >= page * 9) {
                    if (tierData.isLegacy()) {
                        sender.sendMessage(ChatColor.WHITE + "" + (count + 1) + ") " + ChatColor.GREEN + tierData.getName() + " - " + ChatColor.WHITE + totalXp + "" + ChatColor.GREEN + " (legacy)");
                    } else {
                        sender.sendMessage(ChatColor.WHITE + "" + (count + 1) + ") " + ChatColor.GREEN + tierData.getName() + " - " + ChatColor.WHITE + totalXp);
                    }
                }
                count++;
            }
        }
        if (playerRank >= 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "Your Ranking - " + playerRank + ") " + ChatColor.WHITE + playerTotal);
        }
        sender.sendMessage(ChatColor.GOLD + "[Tip] Use " + ChatColor.YELLOW + "/tiers history" + ChatColor.GOLD + " for more detailed info!");
    }

    public void sortTierList() {
        townData.sort(new Comparator<TownTierData>() {
            @Override
            public int compare(TownTierData o1, TownTierData o2) {
                int totalO1 = 0;
                if (o1.isTown()) {
                    totalO1 = getTotalXPFromTier(o1.getTier(), true) + o1.getXP();
                } else {
                    totalO1 = getTotalXPFromTier(o1.getTier(), false) + o1.getXP();
                }
                int totalO2 = 0;
                if (o2.isTown()) {
                    totalO2 = getTotalXPFromTier(o2.getTier(), true) + o2.getXP();
                } else {
                    totalO2 = getTotalXPFromTier(o2.getTier(), false) + o2.getXP();
                }
                return Integer.compare(totalO2, totalO1);
            }
        });
    }

    @Deprecated
    public void addSiegeWinNationPoints(Nation nationA, Town townC) {
        /* Nation A sieges nation B at town C
         * Case 1 - c is in nation B
         * Take diff between nation tiers
         * if diff<0  amount = base + base*(Math.max(-1,0.5*diff));
         * if diff>=0 amount = base + base*(Math.min(1, 0.2*diff));
         *
         * Case 2 C is town-less
         * tier = Math.floor(towntier/2);
         * tier = Math.max(nation-tier-1,tier);
         * use above calculation
         */

        if (!Dependency.SIEGEWAR.isActive())
            return;

        NationTier tierA = getTierFromNation(nationA);
        if (townC.hasNation()) {
            try {
                Nation nationB = townC.getNation();
                NationTier tierB = getTierFromNation(nationB);
                float gainB = calculateSiegeGain(tierA.getTier(), tierB.getTier());
                int baseA = (int) Math.floor(siegeA * Math.exp(siegeScaling * tierA.getTier()));
                float gainA = calculateSiegeGain(tierB.getTier(), tierA.getTier());
                float baseB = (int) Math.floor(siegeA * Math.exp(siegeScaling * tierB.getTier()));
                int totalA = (int) (baseA + baseA * gainA);
                int totalB = (int) (baseB + baseB * gainB);
                TownyUtils.updateNationMetaData(nationA, totalA, siegeWarChestField);
                TownyUtils.updateNationMetaData(nationB, totalB, siegeWarChestField);
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
        } else {
            TownTier tier = getTierFromTown(townC);
            int tierB = (int) Math.floor((float) tier.getTier() / 2);
            tierB = Math.min(tierA.getTier() - 1, tierB);
            float gainB = calculateSiegeGain(tierA.getTier(), tierB);
            int baseA = (int) Math.floor(siegeA * Math.exp(siegeScaling * tierA.getTier()));
            float gainA = calculateSiegeGain(tierB, tierA.getTier());
            float baseB = (int) Math.floor(siegeA * Math.exp(siegeScaling * tierB));
            int totalA = (int) (baseA + baseA * gainA);
            int totalB = (int) ((baseB + baseB * gainB) / 4.0f);
            TownyUtils.updateNationMetaData(nationA, totalA, siegeWarChestField);
            TownyUtils.updateTownMetaData(townC, totalB, siegeWarChestField);
        }

    }

    @Deprecated
    private float calculateSiegeGain(int tierA, int tierB) {
        int diff = tierA - tierB;
        if (diff < 0) {
            return (float) Math.max(-1, 0.5 * diff);
        } else {
            return (float) Math.min(1, 0.2 * diff);
        }
    }

    //admin command
    public void setXPNation(Nation nation, int xp) {
        float tier = getTierFromXP(xp, false);
        int tierRaw = (int) Math.floor(tier);
        int nationxp = xp - getTotalXPFromTier(tierRaw, false);

        TownyUtils.updateNationMetaData(nation, nationxp, TownTierData.getXpField());
        TownyUtils.updateNationMetaData(nation, tierRaw, TownTierData.getTierField());
        TownTierData tierData = getDataFromNation(nation);
        tierData.setTier(tierRaw);
        tierData.setXP(nationxp);
    }

    //admin command
    public void setTierNation(Nation nation, int tier) {
        TownyUtils.updateNationMetaData(nation, 0, TownTierData.getXpField());
        TownyUtils.updateNationMetaData(nation, tier, TownTierData.getTierField());
        TownTierData tierData = getDataFromNation(nation);
        tierData.setTier(tier);
        tierData.setXP(0);
    }

    //admin command
    public void resetXPNation(Nation nation) {
        TownyUtils.updateNationMetaData(nation, 0, TownTierData.getXpField());
        TownyUtils.updateNationMetaData(nation, 0, TownTierData.getTierField());
        TownTierData tierData = getDataFromNation(nation);
        tierData.setTier(0);
        tierData.setXP(0);
    }

    public int getXpToNextLevel(int currentTier, boolean town) {
        //Be^km
        // where B=initial, k=
        float m = experienceM;
        int b = experienceB;
        if (!town) {
            m = experienceMNation;
            b = experienceBNation;
        }

        return (int) Math.ceil(b * Math.exp(currentTier * m));
    }

    public int getTotalXPFromTier(int tier, boolean town) {
        //Be^km
        float m = experienceM;
        int b = experienceB;
        if (!town) {
            m = experienceMNation;
            b = experienceBNation;
        }
        return (int) Math.ceil(b * ((1 - Math.exp(m * (tier + 1))) / (1 - Math.exp(m))));
    }

    public float getTierFromXP(int xp, boolean town) {
        float a = (float) experienceB;
        float d = (float) experienceM;
        if (!town) {
            a = experienceBNation;
            d = experienceMNation;
        }
        //A = a
        //d = k
        return (float) Math.ceil(Math.log(1 - (xp / a) * (1 - Math.exp(d))) / d);
        //return (float) ((d-2.0f*a) + (float)Math.sqrt(((float)Math.pow(2*a-d,2)+8*d*xp))/(2*d));
    }

    public TownTierData getDataFromTown(Town town) {
        for (TownTierData tierData : townData) {
            if (town.getUUID().equals(tierData.getUniqueID())) {
                return tierData;
            }
        }
        TownTierData data = new TownTierData(town.getUUID(), true, 0, 0, town.getName(), false);
        townData.add(data);
        return data;
    }

    public TownTierData getDataFromNation(Nation nation) {
        for (TownTierData tierData : townData) {
            if (nation.getUUID().equals(tierData.getUniqueID())) {
                return tierData;
            }
        }
        TownTierData data = new TownTierData(nation.getUUID(), true, 0, 0, nation.getName(), false);
        townData.add(data);
        return data;
    }

    public static TownTiers getInstance() {
        return instance;
    }

    public String getOperatorNode() {
        return operatorNode;
    }

    public String getAdminNode() {
        return adminNode;
    }

    public void playerJoin(Player player) {
        if (!playerData.containsKey(player.getUniqueId())) {
            playerData.put(player.getUniqueId(), new PlayerData(player.getUniqueId()));
        }
    }

    public PlayerData getPlayerData(Player player) {
        if (playerData.containsKey(player.getUniqueId())) {
            return playerData.get(player.getUniqueId());
        } else {
            PlayerData playerD = new PlayerData(player.getUniqueId());
            playerData.put(player.getUniqueId(), playerD);
            return playerD;
        }
    }

    public HashMap<UUID, PlayerData> getPlayerData() {
        return playerData;
    }

    public int getTownTeir(Town town) {
        return Math.max(TownyUtils.getMetaDataFromTown(town, TownTierData.getTierField()), 0);
    }

    public int getNationTeir(Nation nation) {
        return Math.max(TownyUtils.getMetaDataFromNation(nation, TownTierData.getTierField()), 0);
    }

    public int getTownXP(Town town) {
        return Math.max(0, TownyUtils.getMetaDataFromTown(town, TownTierData.getXpField()));
    }

    public int getNationXP(Nation nation) {
        return Math.max(0, TownyUtils.getMetaDataFromNation(nation, TownTierData.getXpField()));
    }

    public ArrayList<String> getPerksFromTier(NationTier tier) {
        ArrayList<String> perks = new ArrayList<>();
        if (tier.getTier() > 0) {
            NationTier lastTier = nationTiers.get(tier.getTier() - 1);
            if (tier.getRegionalCapitals() != lastTier.getRegionalCapitals()) {
                perks.add(tier.getRegionalCapitals() + "x Regional Capitals (/n set rc)");
            }
            if (tier.getDoubleContributionPercentage() != lastTier.getDoubleContributionPercentage()) {
                perks.add(tier.getDoubleContributionPercentage() + "% double mission contributions");
            }
            if (lastTier.getExtraMCMMOExperience() != tier.getExtraMCMMOExperience()) {
                perks.add(tier.getExtraMCMMOExperience() + "% bonus MCMMO XP");
            }
            if (lastTier.getGunPriceReduction() != tier.getGunPriceReduction()) {
                perks.add(tier.getGunPriceReduction() + "% cheaper guns");
            }
            if (lastTier.getVehiclePriceReduction() != tier.getVehiclePriceReduction()) {
                perks.add(tier.getVehiclePriceReduction() + "% cheaper vehicles");
            }
            if (lastTier.getAmountOfGenerators() != tier.getAmountOfGenerators()) {
                perks.add(tier.getAmountOfGenerators() + "x Bonus Generators");
            }
        } else {
            perks.add("1x generator");
        }
        return perks;
    }

    public ArrayList<String> getPerksFromTier(TownTier tier) {
        ArrayList<String> perks = new ArrayList<>();
        if (tier.getTier() > 0) {
            TownTier lastTier = townTiers.get(tier.getTier() - 1);
            if (tier.isPearlsLost() != lastTier.isPearlsLost()) {
                perks.add("Pearls not lost in claims");
            }
            if (tier.getExtraTownSpawns() != lastTier.getExtraTownSpawns()) {
                perks.add(tier.getExtraTownSpawns() + 1 + "x town spawns (/t spawn 2)");
            }
            if (tier.getHasteInClaims() != lastTier.getHasteInClaims()) {
                perks.add("Haste " + getRomanNumeral(tier.getHasteInClaims()) + " in claims");
            }
            if (tier.getSpeedInClaims() != lastTier.getSpeedInClaims()) {
                perks.add("Speed " + getRomanNumeral(tier.getSpeedInClaims()) + " in claims");
            }
            if (lastTier.getDoubleContributionPercentage() != tier.getDoubleContributionPercentage()) {
                perks.add(tier.getDoubleContributionPercentage() + "% double mission contributions");
            }
            if (lastTier.getExtraClaims() != tier.getExtraClaims()) {
                perks.add(tier.getExtraClaims() + "x bonus claims");
            }
            if (lastTier.getExtraMCMMOExperience() != tier.getExtraMCMMOExperience()) {
                perks.add(tier.getExtraMCMMOExperience() + "% bonus MCMMO XP");
            }
            if (lastTier.getTownMissionBankPercentage() != tier.getTownMissionBankPercentage()) {
                perks.add(tier.getTownMissionBankPercentage() + "% extra mission prize to town bank");
            }
            if (lastTier.isHungerInClaims() != tier.isHungerInClaims()) {
                perks.add("Hunger not lost in claims");
            }
            if (lastTier.isFallDamageInClaims() != tier.isFallDamageInClaims()) {
                perks.add("No Fall Damage in Claims");
            }
            if (lastTier.getAmountOfGenerators() != tier.getAmountOfGenerators()) {
                perks.add(tier.getAmountOfGenerators() + "x generators");
            }

        } else {
            perks.add("1x Town Spawn");
            perks.add("1x Generator");
        }
        return perks;
    }

    public int getRegionalCapital(Town town) {
        return TownyUtils.getMetaDataFromTown(town, TownTierData.getRegionalCapitalField());
    }


    public void levelUp(Town town, int tierLvL) {
        TownTier tier = getTownTierFromNumber(tierLvL);
        //Handle Perks

        //Announce to player
        addTierDataTown(tierLvL);
        if (tier != null) {
            //handle perks
            if (tierLvL > 0 && tierLvL < townTiers.size()) {
                TownTier lastTier = getTownTierFromNumber(tierLvL - 1);
                if (lastTier.getExtraClaims() != tier.getExtraClaims()) {
                    int diff = tier.getExtraClaims() - lastTier.getExtraClaims();
                    int claimsHanded = TownyUtils.getMetaDataFromTown(town, TownTierData.getExtraClaimsHanded());
                    claimsHanded = Math.max(claimsHanded, 0);
                    claimsHanded += diff;
                    town.addBonusBlocks(diff);
                    TownyUtils.updateTownMetaData(town, diff, TownTierData.getExtraClaimsHanded());
                }
            }


            if (tier.isAnnounceOnLevelUp()) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + town.getMayor().getName() + ChatColor.GOLD + " and the residents of " + ChatColor.YELLOW + town.getName() + ChatColor.GOLD + " leveled up their town to " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Tier " + tierLvL + ChatColor.GOLD + " using  " + ChatColor.YELLOW + "/tiers town");
            }
            ArrayList<String> perks = getPerksFromTier(tier);
            StringBuilder perkList = new StringBuilder();
            for (int i = 0; i < perks.size(); i++) {
                perkList.append(perks.get(i));
                if (i != perks.size() - 1) {
                    perkList.append(", ");
                }
            }
            for (Player player : TownyAPI.getInstance().getOnlinePlayersInTown(town)) {
                player.sendMessage(ChatColor.GOLD + "Congratulations! Your town levelled up to" + ChatColor.YELLOW + "" + ChatColor.BOLD + " Tier " + tierLvL + " " + ChatColor.GOLD + "earning the following [" + ChatColor.YELLOW + perkList.toString() + ChatColor.GOLD + "]");
                if (tier.getTier() > 7) {
                    player.playSound(player.getLocation(), achievementSound, 2.5f, 1f);
                } else {
                    player.playSound(player.getLocation(), rewardSound, 2.5f, 1f);
                }

                //handle rewards
                int speed = tier.getSpeedInClaims();
                int haste = tier.getHasteInClaims();
                if (TownyUtils.playerInOwnTown(player)) {
                    Listeners.handlePotionEffects(player, speed, haste);
                }
            }
        } else {
            for (Player player : TownyAPI.getInstance().getOnlinePlayersInTown(town)) {
                player.sendMessage(ChatColor.GOLD + "Congratulations! Your town levelled up to tier " + ChatColor.YELLOW + "" + ChatColor.BOLD + tierLvL);
                player.playSound(player.getLocation(), rewardSound, 2.5f, 1f);
            }
        }
    }

    public void levelDown(Town town) {
        int claimsHanded = Math.max(0, TownyUtils.getMetaDataFromTown(town, TownTierData.getExtraClaimsHanded()));
        town.addBonusBlocks(-claimsHanded);
        for (Player player : TownyAPI.getInstance().getOnlinePlayersInTown(town)) {
            Listeners.handlePotionEffectRemoval(player);
        }
    }

    public void levelUp(Nation nation, int tierLvL) {
        NationTier tier = getNationTierFromNumber(tierLvL);
        addTierDataNation(tierLvL);
        if (tier != null) {
            if (tier.isAnnounceOnLevelUp()) {
                Bukkit.broadcastMessage(ChatColor.YELLOW + nation.getKing().getName() + ChatColor.GOLD + " and the people of " + ChatColor.YELLOW + nation.getName() + ChatColor.GOLD + " leveled up their nation to " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Tier " + tierLvL + ChatColor.GOLD + " using " + ChatColor.YELLOW + "/tiers nation");
            }
            ArrayList<String> perks = getPerksFromTier(tier);
            StringBuilder perkList = new StringBuilder();
            for (int i = 0; i < perks.size(); i++) {
                perkList.append(perks.get(i));
                if (i != perks.size() - 1) {
                    perkList.append(", ");
                }
            }
            for (Player player : TownyAPI.getInstance().getOnlinePlayersInNation(nation)) {
                player.sendMessage(ChatColor.GOLD + "Congratulations! Your Nation levelled up to" + ChatColor.YELLOW + "" + ChatColor.BOLD + " Tier " + tierLvL + " " + ChatColor.GOLD + "earning the following [" + ChatColor.YELLOW + perkList.toString() + ChatColor.GOLD + "]");

                if (tier.getTier() >= 4) {
                    player.playSound(player.getLocation(), achievementSound, 2.5f, 1f);
                } else {
                    player.playSound(player.getLocation(), rewardSound, 2.5f, 1f);
                }
            }
        } else {
            for (Player player : TownyAPI.getInstance().getOnlinePlayersInNation(nation)) {
                player.sendMessage(ChatColor.GOLD + "Congratulations! Your Nation levelled up to tier " + ChatColor.YELLOW + "" + ChatColor.BOLD + tierLvL);
                player.playSound(player.getLocation(), rewardSound, 2.5f, 1f);
            }
        }
    }

    public TownTier getTierFromTown(Town town) {
        int tier = getTownTeir(town);
        if (tier >= townTiers.size()) {
            return getHighestTownTier();
        }
        return getTownTierFromNumber(tier);
    }

    public NationTier getTierFromNation(Nation nation) {
        int tier = getNationTeir(nation);
        if (tier >= nationTiers.size()) {
            return getHighestNationTier();
        }
        return getNationTierFromNumber(tier);
    }

    public TownTier getTownTierFromNumber(int i) {
        return townTiers.get(i);
    }

    public NationTier getNationTierFromNumber(int i) {
        return nationTiers.get(i);
    }

    public String getRomanNumeral(int i) {
        if (i == 1) {
            return "I";
        } else if (i == 2) {
            return "II";
        } else if (i == 3) {
            return "III";
        } else if (i == 4) {
            return "IV";
        } else if (i == 5) {
            return "V";
        } else {
            return String.valueOf(i);
        }
    }

    public void tradeXP(Player player, Town town, int xp) {
        if (xp > SetExpFix.getTotalExperience(player)) {
            player.sendMessage(ChatColor.GOLD + "You do not have enough experience, you only have " + SetExpFix.getTotalExperience(player));
            player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            return;
        }
        Convertor convertor = getConvertorXP(town);
        if (convertor == null) {
            player.sendMessage(ChatColor.RED + "Something went wrong!");
            return;
        }
        float conversionFactor = convertor.getConversionFactor();
        player.giveExp(-xp);
        int tXP = (int) (xp * conversionFactor);
        addXPTown(town, tXP);
        player.sendMessage(ChatColor.GOLD + "Successfully Converted " + xp + " to " + tXP + " Town XP");
        player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);
    }

    public void tradeMoney(Player player, Town town, int money) {
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) {
            player.sendMessage(ChatColor.GOLD + "You must be in a town to do this!");
            return;
        }
        Convertor convertor = getConvertorGold(town);
        if (convertor == null) {
            player.sendMessage(ChatColor.RED + "Something went wrong!");
            return;
        }
        float conversionFactor = convertor.getConversionFactor();
        if (money > resident.getAccount().getHoldingBalance()) {
            player.sendMessage(ChatColor.GOLD + "You do not have enough money! You only have $" + String.format("%.2f", resident.getAccount().getHoldingBalance()));
            player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            return;
        }
        resident.getAccount().withdraw(money, "Converted to XP");
        int tXP = (int) (money * conversionFactor);
        addXPTown(town, tXP);
        player.sendMessage(ChatColor.GOLD + "Successfully Converted $" + money + " to " + tXP + " Town XP");
        player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);

    }

    public void tradeXP(Player player, Nation nation, int xp) {
        if (xp == 0) {
            return;
        }
        if (xp > SetExpFix.getTotalExperience(player)) {
            player.sendMessage(ChatColor.GOLD + "You do not have enough experience, you only have " + SetExpFix.getTotalExperience(player));
            player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            return;
        }
        Convertor convertor = getConvertorXP(nation);
        if (convertor == null) {
            player.sendMessage(ChatColor.RED + "Something went wrong!");
            return;
        }
        float conversionFactor = convertor.getConversionFactor();
        player.giveExp(-xp);
        int tXP = (int) (xp * conversionFactor);
        addXPNation(nation, tXP);
        player.sendMessage(ChatColor.GOLD + "Successfully Converted " + xp + " to " + tXP + " National XP");
        player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);

    }

    public void tradeMoney(Player player, Nation nation, int money) {
        if (money == 0) {
            return;
        }
        Resident resident = TownyUniverse.getInstance().getResident(player.getName());
        if (resident == null) {
            player.sendMessage(ChatColor.GOLD + "You must be in a town to do this!");
            return;
        }
        Convertor convertor = getConvertorGold(nation);
        if (convertor == null) {
            player.sendMessage(ChatColor.RED + "Something went wrong!");
            return;
        }
        float conversionFactor = convertor.getConversionFactor();
        if (money > resident.getAccount().getHoldingBalance()) {
            player.sendMessage(ChatColor.GOLD + "You do not have enough money! You only have $" + String.format("%.2f", resident.getAccount().getHoldingBalance()));
            player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            return;
        }
        resident.getAccount().withdraw(money, "Converted to XP");
        int tXP = (int) (money * conversionFactor);
        addXPNation(nation, tXP);
        player.sendMessage(ChatColor.GOLD + "Successfully Converted $" + money + " to " + tXP + " National XP");
        player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);

    }

    public float getConversionPrice() {
        return conversionPrice;
    }

    public float getXpConversionPrice() {
        return xpConversionPrice;
    }

    public HashMap<Integer, TownTier> getTownTiers() {
        return townTiers;
    }

    public HashMap<Integer, NationTier> getNationTiers() {
        return nationTiers;
    }

    public float getNationConversionPrice() {
        return nationConversionPrice;
    }

    public float getNationXPConversionPrice() {
        return nationXPConversionPrice;
    }

    public Sound getSuccessSound() {
        return successSound;
    }

    public Sound getUnableSound() {
        return unableSound;
    }

    public Sound getRewardSound() {
        return rewardSound;
    }

    public TownTier getHighestTownTier() {
        ArrayList<TownTier> values = new ArrayList<>(townTiers.values());
        return values.get(values.size() - 1);
    }

    public NationTier getHighestNationTier() {
        ArrayList<NationTier> values = new ArrayList<>(nationTiers.values());
        return values.get(values.size() - 1);
    }

    public HashMap<Town, TownSpawn> getTownSpawns() {
        return townSpawns;
    }

    public int getMaxTownPages() {
        float count = 0;
        for (TownTierData tierData : townData) {
            if (tierData.isTown()) {
                count++;
            }
        }
        return (int) Math.ceil(count / 9.0f);
    }

    public int getMaxNationPages() {
        float count = 0;
        for (TownTierData tierData : townData) {
            if (!tierData.isTown()) {
                count++;
            }
        }
        return (int) Math.ceil(count / 9.0f);
    }

    public static ItemStack getSkull(UUID stats) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(stats));
        ArrayList<String> skullMetaLore = new ArrayList<>();
        meta.setLore(skullMetaLore);
        skull.setItemMeta(meta);
        return skull;
    }

    public ArrayList<TownTierData> getTownData() {
        return townData;
    }

    public int getRegionalCapitalPrice() {
        return regionalCapitalPrice;
    }

    public Generator getGeneratorFromTown(Town town, int gen) {
        int lvl = TownyUtils.getMetaDataFromTown(town, Generator.getGeneratorLevels().get(gen));
        if (gen == 0) {
            lvl = Math.max(0, lvl);
        }
        for (Generator generator : generators) {
            if (generator.getLevel() == lvl && generator.isTown()) {
                return generator;
            }
        }
        return null;
    }

    public ArrayList<Integer> getTownGeneratorUnlocks() {
        ArrayList<Integer> unlocks = new ArrayList<>();
        TownTier lastGenTier = townTiers.get(0);
        unlocks.add(0);
        for (TownTier tier : townTiers.values()) {
            if (tier.getAmountOfGenerators() != lastGenTier.getAmountOfGenerators()) {
                int diff = tier.getAmountOfGenerators() - lastGenTier.getAmountOfGenerators();
                for (int i = 0; i < diff; i++) {
                    unlocks.add(tier.getTier());
                }
                lastGenTier = tier;
            }
        }
        return unlocks;
    }

    public ArrayList<Integer> getNationGeneratorUnlocks() {
        ArrayList<Integer> unlocks = new ArrayList<>();
        NationTier lastGenTier = nationTiers.get(0);
        unlocks.add(0);
        for (NationTier tier : nationTiers.values()) {
            if (tier.getAmountOfGenerators() != lastGenTier.getAmountOfGenerators()) {
                int diff = tier.getAmountOfGenerators() - lastGenTier.getAmountOfGenerators();
                for (int i = 0; i < diff; i++) {
                    unlocks.add(tier.getTier());
                }
                lastGenTier = tier;
            }
        }
        return unlocks;
    }

    public int collectFromAll(Town town) {
        ArrayList<Generator> allGen = getGeneratorsFromTown(town);
        int total = 0;
        for (int i = 0; i < allGen.size(); i++) {
            total += getAmountInStorage(allGen.get(i), town, i);
            TownyUtils.updateTownMetaData(town, System.currentTimeMillis(), Generator.getCollections().get(i));
        }
        if (total > 0) {
            addXPTown(town, total);
        }
        return total;
    }

    public int collectFromAll(Nation nation) {
        ArrayList<Generator> allGen = getGeneratorsFromNation(nation);
        int total = 0;
        for (int i = 0; i < allGen.size(); i++) {
            total += getAmountInStorage(allGen.get(i), nation, i);
            TownyUtils.updateNationMetaData(nation, System.currentTimeMillis(), Generator.getCollections().get(i));
        }
        if (total > 0) {
            addXPNation(nation, total);
        }
        return total;
    }

    public Generator getGeneratorFromNation(Nation nation, int gen) {
        int lvl = TownyUtils.getMetaDataFromNation(nation, Generator.getGeneratorLevels().get(gen));
        if (gen == 0) {
            lvl = Math.max(lvl, 0);
        }
        for (Generator generator : generators) {
            if (generator.getLevel() == lvl && !generator.isTown()) {
                return generator;
            }
        }
        return null;
    }

    public ArrayList<Generator> getGeneratorsFromTown(Town town) {
        TownTier tier = getTierFromTown(town);
        ArrayList<Generator> gens = new ArrayList<>();
        for (int i = 0; i < tier.getAmountOfGenerators(); i++) {
            Generator gen = getGeneratorFromTown(town, i);
            if (gen != null) {
                gens.add(gen);
            }
        }
        return gens;
    }

    public ArrayList<Generator> getGeneratorsFromNation(Nation nation) {
        NationTier tier = getTierFromNation(nation);
        ArrayList<Generator> gens = new ArrayList<>();
        for (int i = 0; i < tier.getAmountOfGenerators(); i++) {
            Generator gen = getGeneratorFromNation(nation, i);
            if (gen != null) {
                gens.add(gen);
            }
        }
        return gens;
    }

    public Generator getGeneratorFromNumber(int i, boolean town) {
        for (Generator generator : generators) {
            if (generator.getLevel() == i && generator.isTown() == town) {
                return generator;
            }
        }
        return null;
    }

    public int highestTownGen() {
        int highest = 0;
        for (Generator generator : generators) {
            if (generator.getLevel() > highest && generator.isTown()) {
                highest = generator.getLevel();
            }
        }
        return highest;
    }

    public int lowestTownGen() {
        int lowest = generators.get(0).getLevel();
        int cost = generators.get(0).getCostToUpgrade();
        for (Generator generator : generators) {
            if (generator.getLevel() < lowest && generator.isTown()) {
                lowest = generator.getLevel();
            }
        }
        return cost;
    }

    public int highestNationGen() {
        int highest = 0;
        for (Generator generator : generators) {
            if (generator.getLevel() > highest && !generator.isTown()) {
                highest = generator.getLevel();
            }
        }
        return highest;
    }

    public int lowestNationGen() {
        int lowest = generators.get(0).getLevel();
        int cost = generators.get(0).getCostToUpgrade();
        for (Generator generator : generators) {
            if (generator.getLevel() < lowest && !generator.isTown()) {
                lowest = generator.getLevel();
                cost = generator.getCostToUpgrade();
            }
        }
        return cost;
    }

    public void setGeneratorLevel(Town town, int lvl, int gen) {
        TownyUtils.updateTownMetaData(town, lvl, Generator.getGeneratorLevels().get(gen));
        if (lvl == 0) {
            TownyUtils.updateTownMetaData(town, System.currentTimeMillis(), Generator.getCollections().get(gen));
        }
        addGeneratorData(lvl);
    }

    public void setGeneratorLevel(Nation nation, int lvl, int gen) {
        TownyUtils.updateNationMetaData(nation, lvl, Generator.getGeneratorLevels().get(gen));
        if (lvl == 0) {
            TownyUtils.updateNationMetaData(nation, System.currentTimeMillis(), Generator.getCollections().get(gen));
        }
        addGeneratorData(lvl);
    }

    public void addGeneratorLevel(Town town, int gen) {
        int currentLevel = Math.max(0, TownyUtils.getMetaDataFromTown(town, Generator.getGeneratorLevels().get(gen)));
        TownyUtils.updateTownMetaData(town, currentLevel + 1, Generator.getGeneratorLevels().get(gen));
    }

    public void addGeneratorLevel(Nation nation, int gen) {
        int currentLevel = Math.max(0, TownyUtils.getMetaDataFromNation(nation, Generator.getGeneratorLevels().get(gen)));
        TownyUtils.updateNationMetaData(nation, currentLevel + 1, Generator.getGeneratorLevels().get(gen));
    }

    public void addConvertorLevelGold(Town town) {
        int currentLevel = Math.max(0, TownyUtils.getMetaDataFromTown(town, Convertor.getGoldConvertorField()));
        if (currentLevel < getMaxConvertorLevelGold().getLevel()) {
            TownyUtils.updateTownMetaData(town, currentLevel + 1, Convertor.getGoldConvertorField());
        }
    }

    public void addConvertorLevelGold(Nation nation) {
        int currentLevel = Math.max(0, TownyUtils.getMetaDataFromNation(nation, Convertor.getGoldConvertorField()));
        if (currentLevel < getMaxConvertorLevelGold().getLevel()) {
            TownyUtils.updateNationMetaData(nation, currentLevel + 1, Convertor.getGoldConvertorField());
        }
    }

    public void addConvertorLevelXP(Town town) {
        int currentLevel = Math.max(0, TownyUtils.getMetaDataFromTown(town, Convertor.getXpConvertorField()));
        if (currentLevel < getMaxConvertorXP().getLevel()) {
            TownyUtils.updateTownMetaData(town, currentLevel + 1, Convertor.getXpConvertorField());
        }
    }

    public void addConvertorLevelXP(Nation nation) {
        int currentLevel = Math.max(0, TownyUtils.getMetaDataFromNation(nation, Convertor.getXpConvertorField()));
        if (currentLevel < getMaxConvertorXP().getLevel()) {
            TownyUtils.updateNationMetaData(nation, currentLevel + 1, Convertor.getXpConvertorField());
        }
    }

    public Convertor getConvertorGold(Town town) {
        int level = Math.max(0, TownyUtils.getMetaDataFromTown(town, Convertor.getGoldConvertorField()));
        for (int i = 0; i < goldConvertors.size(); i++) {
            if (goldConvertors.get(i).getLevel() == level) {
                return goldConvertors.get(i);
            }
        }
        return null;
    }

    public Convertor getConvertorGold(int convertor) {
        for (int i = 0; i < goldConvertors.size(); i++) {
            if (goldConvertors.get(i).getLevel() == convertor) {
                return goldConvertors.get(i);
            }
        }
        return null;
    }

    public Convertor getConvertorXP(int convertor) {
        for (int i = 0; i < xpConvertors.size(); i++) {
            if (xpConvertors.get(i).getLevel() == convertor) {
                return xpConvertors.get(i);
            }
        }
        return null;
    }

    public Convertor getConvertorGold(Nation nation) {
        int level = Math.max(0, TownyUtils.getMetaDataFromNation(nation, Convertor.getGoldConvertorField()));
        for (int i = 0; i < goldConvertors.size(); i++) {
            if (goldConvertors.get(i).getLevel() == level) {
                return goldConvertors.get(i);
            }
        }
        return null;
    }

    public Convertor getConvertorXP(Town town) {
        int level = Math.max(0, TownyUtils.getMetaDataFromTown(town, Convertor.getXpConvertorField()));
        for (int i = 0; i < xpConvertors.size(); i++) {
            if (xpConvertors.get(i).getLevel() == level) {
                return xpConvertors.get(i);
            }
        }
        return null;
    }

    public Convertor getConvertorXP(Nation nation) {
        int level = Math.max(0, TownyUtils.getMetaDataFromNation(nation, Convertor.getXpConvertorField()));
        for (int i = 0; i < xpConvertors.size(); i++) {
            if (xpConvertors.get(i).getLevel() == level) {
                return xpConvertors.get(i);
            }
        }
        return null;
    }

    public void resetConvertor(Town town) {
        TownyUtils.updateTownMetaData(town, 0, Convertor.getGoldConvertorField());
        TownyUtils.updateTownMetaData(town, 0, Convertor.getXpConvertorField());
    }

    public void resetConvertor(Nation nation) {
        TownyUtils.updateNationMetaData(nation, 0, Convertor.getGoldConvertorField());
        TownyUtils.updateNationMetaData(nation, 0, Convertor.getXpConvertorField());
    }

    public Convertor getMaxConvertorLevelGold() {
        Convertor goldConvertor = goldConvertors.get(0);
        for (int i = 0; i < goldConvertors.size(); i++) {
            if (goldConvertors.get(i).getLevel() > goldConvertor.getLevel()) {
                goldConvertor = goldConvertors.get(i);
            }
        }
        return goldConvertor;
    }

    public Convertor getMaxConvertorXP() {
        Convertor goldConvertor = xpConvertors.get(0);
        for (int i = 0; i < xpConvertors.size(); i++) {
            if (xpConvertors.get(i).getLevel() > goldConvertor.getLevel()) {
                goldConvertor = xpConvertors.get(i);
            }
        }
        return goldConvertor;
    }


    public int getAmountInStorage(Generator generator, Town town, int gen) {
        long lastCollection = TownyUtils.getMetaDataFromTown(town, Generator.getCollections().get(gen));
        if (lastCollection == -1) {
            lastCollection = town.getRegistered();
        }
        long delay = System.currentTimeMillis() - lastCollection;
        float amount = (delay) / (1000f * 60f * 60f) * generator.getXpPerHour();
        return Math.min(generator.getStorage(), (int) amount);
    }

    public int getAmountInStorage(Generator generator, Nation nation, int gen) {
        long lastCollection = TownyUtils.getMetaDataFromNation(nation, Generator.getCollections().get(gen));
        if (lastCollection == -1) {
            lastCollection = nation.getRegistered();
        }
        long delay = System.currentTimeMillis() - lastCollection;
        float amount = (delay) / (1000f * 60f * 60f) * generator.getXpPerHour();
        return Math.min(generator.getStorage(), (int) amount);
    }

    public void addTierDataTown(int tier) {
        if (!tiersDataTown.containsKey(tier)) {
            tiersDataTown.put(tier, 1);
        } else {
            tiersDataTown.replace(tier, tiersDataTown.get(tier) + 1);
        }
        if (tier > 0) {
            removeTierDataTown(tier - 1);
        }
    }

    public void addGeneratorData(int lvl) {
        if (!generatorData.containsKey(lvl)) {
            generatorData.put(lvl, 1);
        } else {
            generatorData.replace(lvl, generatorData.get(lvl) + 1);
        }
    }

    public void removeTierDataTown(int tier) {
        if (tiersDataTown.containsKey(tier)) {
            tiersDataTown.replace(tier, tiersDataTown.get(tier) - 1);
        }
    }

    public void addTierDataNation(int tier) {
        if (!tiersDataNation.containsKey(tier)) {
            tiersDataNation.put(tier, 1);
        } else {
            tiersDataNation.replace(tier, tiersDataNation.get(tier) + 1);
        }
    }

    public void removeTierDataNation(int tier) {
        if (tiersDataNation.containsKey(tier)) {
            tiersDataNation.replace(tier, tiersDataNation.get(tier) - 1);
        }
    }

    public void printTownData(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + "Town Tier Data" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage(ChatColor.GRAY + "towny tiers made by DeltaOrion!");
        for (Map.Entry<Integer, Integer> data : tiersDataTown.entrySet()) {
            sender.sendMessage(ChatColor.GOLD + "Tier " + data.getKey() + ": " + ChatColor.WHITE + data.getValue());
        }
    }

    public void printNationData(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + "Nation Tier Data" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage(ChatColor.GRAY + "towny tiers made by DeltaOrion!");
        for (Map.Entry<Integer, Integer> data : tiersDataNation.entrySet()) {
            sender.sendMessage(ChatColor.GOLD + "Tier " + data.getKey() + ": " + ChatColor.WHITE + data.getValue());
        }
    }

    public void printGeneratorData(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW + "oO---" + ChatColor.GREEN + "Generator Data" + ChatColor.YELLOW + "---Oo");
        sender.sendMessage(ChatColor.GRAY + "towny tiers made by DeltaOrion!");
        for (Map.Entry<Integer, Integer> data : tiersDataNation.entrySet()) {
            sender.sendMessage(ChatColor.GOLD + "Generator Lvl " + data.getKey() + ": " + ChatColor.WHITE + data.getValue());
        }
    }

    public int getLevel(int experience) {
        if (experience <= 352) {
            return (int) (Math.ceil((-6 + Math.sqrt(36 + 4 * experience)) / (2)));
        } else if (experience <= 1507) {
            return (int) (Math.ceil((40.5 + Math.sqrt(1640.25 - 10 * (360 - experience))) / (5)));
        } else {
            return (int) (Math.ceil((162.5 + Math.sqrt(26406.25 - 18 * (2220 - experience))) / (9)));
        }
    }

    public static IntegerDataField getSiegeWarChestField() {
        return siegeWarChestField;
    }

    public String formatTime(long timeMs) {
        String prefix = "d";
        float timeDays = (float) timeMs / (24 * 1000 * 3600);
        if (timeDays < 1) {
            timeDays *= 24;
            prefix = "h";
            if (timeDays < 1) {
                timeDays *= 60;
                prefix = "m";
            }
            if (timeDays < 1) {
                timeDays *= 60;
                prefix = "s";
                if (timeDays < 0) {
                    timeDays = 0;
                }
            }
        }
        return (String.format("%.2f", timeDays) + prefix);
    }

    public boolean inCombat(Player player) {
        if (!Dependency.COMBATLOGX.isActive()) {
            return false;
        }

        ICombatLogX plugin = (ICombatLogX) Dependency.COMBATLOGX.getPlugin();
        ICombatManager combatManager = plugin.getCombatManager();
        return combatManager.isInCombat(player);
    }
}



