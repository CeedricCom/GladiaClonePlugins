package me.deltaorion.towntier.towntiers;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.towntier.towntiers.data.PlayerData;
import me.deltaorion.towntier.towntiers.data.TownTierData;
import me.deltaorion.towntier.towntiers.townyutils.Settings;
import me.deltaorion.towntier.towntiers.townyutils.TownyUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.ArrayList;

public class GUImanager {

    public static NamespacedKey inventorySep;
    public static final NamespacedKey conversionAmountKey;
    public static final NamespacedKey generatorSelectionKey;
    private static final int originalPivot = 19;

    public GUImanager() {

    }
    static {
        inventorySep = new NamespacedKey(TownTiers.getInstance(),"TownTiers.InventorySep");
        conversionAmountKey = new NamespacedKey(TownTiers.getInstance(),"TownTiers.ConversionAmount");
        generatorSelectionKey = new NamespacedKey(TownTiers.getInstance(),"TownTiers.generatorSelection");
    }

    public static void constructMainGUI(Player player, Town town, Nation nation, boolean isTown) {
        if(town==null && nation==null) {
            player.sendMessage(ChatColor.RED+"Something went wrong!");
            player.closeInventory();
            return;
        }
        String title = "National Tiers (NP = Nation Points)";
        if(isTown) {
            title = "Town Tiers (TP = Town Points)";
        }
        Inventory mainGUI = Bukkit.createInventory(null,54,title);

        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.Main");
        if(isTown) {
            identifierMeta.setUnbreakable(true);
            identifierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<10;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=(6-1)*9;i<54;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=1;i<6-1;i++) {
            mainGUI.setItem(i*9,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            mainGUI.setItem(i*9+8,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        mainGUI.setItem(0,identifier);
        ItemStack tierStack = null;
        Generator generator = null;
        Convertor convertorGold = null;
        Convertor convertorXP = null;
        int tier = 0;
        int xp = 0;
        ArrayList<String> perks;
        ArrayList<Generator> allGen;
        if(isTown) {
            tier = TownTiers.getInstance().getTownTeir(town);
            xp = TownTiers.getInstance().getTownXP(town);
            allGen = TownTiers.getInstance().getGeneratorsFromTown(town);
            TownTier townTier;
            if(tier>=TownTiers.getInstance().getTownTiers().size()) {
                townTier=TownTiers.getInstance().getHighestTownTier();
            } else {
                townTier = TownTiers.getInstance().getTownTierFromNumber(tier);
            }
            tierStack = new ItemStack(townTier.getViewMaterial());
            perks = TownTiers.getInstance().getPerksFromTier(townTier);
            convertorGold = TownTiers.getInstance().getConvertorGold(town);
            convertorXP = TownTiers.getInstance().getConvertorXP(town);
            Resident resident = TownyUniverse.getInstance().getResident(player.getName());
            if(resident==null) {
                player.sendMessage(ChatColor.RED+"Something went wrong!");
                player.closeInventory();
                return;
            }
            if(town.getMayor().equals(resident)) {
                ItemStack settingStack = new ItemStack(Material.COMPARATOR);
                ItemMeta settingMeta = settingStack.getItemMeta();
                settingMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Settings");
                ArrayList<String> settingLore = new ArrayList<>();
                settingLore.add("");
                settingLore.add(ChatColor.WHITE+"Use this to toggle certain perks");
                settingLore.add(ChatColor.WHITE+"such as bonus mission town bank");
                settingLore.add(ChatColor.WHITE+"or fall damage in claims");
                settingLore.add("");
                settingLore.add(ChatColor.WHITE+"Left-Click to access settings");
                settingMeta.setLore(settingLore);
                settingStack.setItemMeta(settingMeta);
                mainGUI.setItem(53,settingStack);
            }
        } else {
            tier = TownTiers.getInstance().getNationTeir(nation);
            xp = TownTiers.getInstance().getNationXP(nation);
            allGen = TownTiers.getInstance().getGeneratorsFromNation(nation);
            NationTier nationTier;
            if(tier>=TownTiers.getInstance().getNationTiers().size()) {
                nationTier = TownTiers.getInstance().getHighestNationTier();
            } else {
                nationTier = TownTiers.getInstance().getNationTierFromNumber(tier);
            }
            tierStack = new ItemStack(nationTier.getViewMaterial());
            perks = TownTiers.getInstance().getPerksFromTier(nationTier);
            convertorGold = TownTiers.getInstance().getConvertorGold(nation);
            convertorXP = TownTiers.getInstance().getConvertorXP(nation);
        }
        ItemMeta tierMeta = tierStack.getItemMeta();
        tierMeta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Tier "+tier);
        ArrayList<String> tierLore = new ArrayList<>();
        int xpToNextLevel = TownTiers.getInstance().getXpToNextLevel(tier,false);
        if(isTown) {
            xpToNextLevel = TownTiers.getInstance().getXpToNextLevel(tier,true);
        }
        if(isTown) {
            tierLore.add(ChatColor.WHITE + "" + xp + "/" + xpToNextLevel + " TP");
        } else {
            tierLore.add(ChatColor.WHITE + "" + xp + "/" + xpToNextLevel + " NP");
        }
        tierLore.add("");
        tierLore.add(ChatColor.GOLD+"Perks:");
        for(String perk : perks) {
            tierLore.add(ChatColor.WHITE+" - "+perk);
        }
        tierLore.add("");
        tierLore.add(ChatColor.YELLOW + "Left-Click to view all perks");
        tierMeta.setLore(tierLore);
        tierMeta.addEnchant(Enchantment.DAMAGE_ARTHROPODS,1,false);
        tierMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if(isTown) {
            tierMeta.setUnbreakable(true);
            tierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        tierStack.setItemMeta(tierMeta);
        mainGUI.setItem(22,tierStack);

        int totalInStorage = 0;
        int totalStorage = 0;

        for(int i=0;i<allGen.size();i++) {
            int amountInStorage = 0;
            if(isTown) {
                amountInStorage = TownTiers.getInstance().getAmountInStorage(allGen.get(i),town,i);
            } else {
                amountInStorage = TownTiers.getInstance().getAmountInStorage(allGen.get(i),nation,i);
            }
            totalInStorage+=amountInStorage;
            totalStorage+=allGen.get(i).getStorage();
        }


        ItemStack genStack = new ItemStack(Material.CHEST);
        ItemMeta genMeta = genStack.getItemMeta();
        genMeta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Generator");
        ArrayList<String> genLore = new ArrayList<>();
        genLore.add("");
        genLore.add(ChatColor.WHITE+"Generators create town");
        genLore.add(ChatColor.WHITE+"and nation points over time.");
        genLore.add(ChatColor.WHITE+"Upgrade using XP to earn more");
        genLore.add("");
        if(isTown) {
            genLore.add(ChatColor.GOLD + "In Gens: " + ChatColor.WHITE + totalInStorage + "/" + totalStorage + " TP");
        } else {
            genLore.add(ChatColor.GOLD+"In Gens: "+ChatColor.WHITE+totalInStorage+"/"+totalStorage +" NP");
        }
        genLore.add(ChatColor.GOLD+ "Your XP: "+ChatColor.WHITE +SetExpFix.getTotalExperience(player));
        genLore.add("");
        genLore.add(ChatColor.YELLOW+"Left-Click to view your generators");
        genMeta.setLore(genLore);
        genStack.setItemMeta(genMeta);
        mainGUI.setItem(13,genStack);

        ItemStack moneyConversionStack = new ItemStack(Material.GOLD_INGOT);
        ItemMeta moneyConversionMeta = moneyConversionStack.getItemMeta();
        if(convertorGold==null) {
            player.sendMessage(ChatColor.RED+"Something went wrong!");
            player.closeInventory();
            return;
        }
        float conversionFactorGold = convertorGold.getConversionFactor();
        float conversionFactorXP = convertorXP.getConversionFactor();
        if(isTown) {
            moneyConversionMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Purchase TP");
        } else {
            moneyConversionMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Purchase NP");
        }
        ArrayList<String> moneyConversionLore = new ArrayList<>();
        if(isTown) {
            moneyConversionLore.add(ChatColor.WHITE + "$1 = " + conversionFactorGold+" TP");
        } else {
            moneyConversionLore.add(ChatColor.WHITE + "$1 = " + conversionFactorGold+" NP");
        }
        moneyConversionLore.add("");
        if(isTown) {
            moneyConversionLore.add(ChatColor.YELLOW + "/tiers convert money town [amount]");
        } else {
            moneyConversionLore.add(ChatColor.YELLOW + "/tiers convert money nation [amount]");
        }
        moneyConversionLore.add("");
        moneyConversionLore.add(ChatColor.WHITE+ "Left-Click to convert");
        moneyConversionMeta.setLore(moneyConversionLore);
        moneyConversionStack.setItemMeta(moneyConversionMeta);
        mainGUI.setItem(24,moneyConversionStack);

        ItemStack xpConversionStack = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta xpConversionMeta = xpConversionStack.getItemMeta();
        xpConversionMeta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Trade XP");
        ArrayList<String> xpConversionLore = new ArrayList<>();
        if(isTown) {
            xpConversionLore.add(ChatColor.WHITE + "1XP = " + conversionFactorXP+" TP");
        } else {
            xpConversionLore.add(ChatColor.WHITE + "1XP = " + conversionFactorXP+" NP");
        }
        xpConversionLore.add("");
        if(isTown) {
            xpConversionLore.add(ChatColor.YELLOW + "/tiers convert xp town [amount]");
        } else {
            xpConversionLore.add(ChatColor.YELLOW+"/tiers convert xp nation [amount]");
        }
        xpConversionLore.add("");
        xpConversionLore.add(ChatColor.WHITE+ "Left-Click to convert");
        xpConversionMeta.setLore(xpConversionLore);
        xpConversionStack.setItemMeta(xpConversionMeta);
        mainGUI.setItem(20,xpConversionStack);

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Close");
        closeButton.setItemMeta(closeMeta);
        mainGUI.setItem(49,closeButton);

        int pivot = 40;
        int multiplier = 0;
        int i = 0;
        if(isTown) {
            for (TownTierData tierData : TownTiers.getInstance().getTownData()) {
                if (tierData.isTown() && !tierData.isLegacy()) {
                    try {
                        if (i < 5) {
                            Town tierTown = TownyUniverse.getInstance().getDataSource().getTown(tierData.getUniqueID());
                            ItemStack skull = TownTiers.getSkull(tierTown.getMayor().getUUID());
                            ItemMeta skullMeta = skull.getItemMeta();
                            int r = i+1;
                            skullMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "" + r + ") " + tierTown.getName());
                            ArrayList<String> skullLore = new ArrayList<>();
                            skullLore.add("");
                            skullLore.add(ChatColor.GOLD+"Rank: "+ChatColor.WHITE+""+(i+1));
                            skullLore.add(ChatColor.GOLD + "Mayor: " + ChatColor.WHITE + tierTown.getMayor().getName());
                            skullLore.add(ChatColor.GOLD + "Tier: " + ChatColor.WHITE + Math.max(0,tierData.getTier()));
                            skullLore.add(ChatColor.GOLD + "TP: "  + ChatColor.WHITE+ Math.max(0,tierData.getXP()));
                            skullLore.add("");
                            skullLore.add(ChatColor.YELLOW+"/tiers top town"+ChatColor.WHITE+" for more!");
                            skullMeta.setLore(skullLore);
                            skull.setItemMeta(skullMeta);
                            mainGUI.setItem(pivot + multiplier, skull);
                            multiplier *= -1;
                            if (multiplier < 0) {
                                multiplier--;
                            } else if (multiplier == 0) {
                                multiplier = -1;
                            }
                            i++;
                        }
                    } catch (NotRegisteredException e) {
                        continue;
                    }
                }
            }
        } else {
            for (TownTierData tierData : TownTiers.getInstance().getTownData()) {
                if (!tierData.isTown() && !tierData.isLegacy()) {
                    try {
                        if (i < 5) {
                            Nation tierNation = TownyUniverse.getInstance().getDataSource().getNation(tierData.getUniqueID());
                            ItemStack skull = TownTiers.getSkull(tierNation.getKing().getUUID());
                            ItemMeta skullMeta = skull.getItemMeta();
                            int r = i+1;
                            skullMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "" + r +") " + tierNation.getName());
                            ArrayList<String> skullLore = new ArrayList<>();
                            skullLore.add("");
                            skullLore.add(ChatColor.GOLD+"Rank: "+ChatColor.WHITE+""+(i+1));
                            skullLore.add(ChatColor.GOLD + "King: "+ChatColor.WHITE + tierNation.getKing().getName());
                            skullLore.add(ChatColor.GOLD + "Tier: "+ChatColor.WHITE + Math.max(0,tierData.getTier()));
                            skullLore.add(ChatColor.GOLD + "NP: "+ChatColor.WHITE + Math.max(0,tierData.getXP()));
                            skullLore.add("");
                            skullLore.add(ChatColor.YELLOW+"/tiers top nation"+ChatColor.WHITE+" for more!");
                            skullMeta.setLore(skullLore);
                            skull.setItemMeta(skullMeta);
                            mainGUI.setItem(pivot + multiplier, skull);
                            multiplier *= -1;
                            if (multiplier < 0) {
                                multiplier--;
                            } else if (multiplier == 0) {
                                multiplier = -1;
                            }
                            i++;
                        }
                    } catch (NotRegisteredException e) {
                        continue;
                    }
                }
            }
        }

        PlayerData playerData = TownTiers.getInstance().getPlayerData(player);
        playerData.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);
    }
    public static void constructTierInformationMenu(Player player,Town town, Nation nation,boolean isTown) {
        String title = "National Tiers";
        if(isTown) {
            title = "Town Tiers";
        }
        Inventory mainGUI = Bukkit.createInventory(null,54,title);

        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.ViewTiers");
        if(isTown) {
            identifierMeta.setUnbreakable(true);
            identifierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<10;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=(6-1)*9;i<54;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=1;i<6-1;i++) {
            mainGUI.setItem(i*9,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            mainGUI.setItem(i*9+8,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        mainGUI.setItem(0,identifier);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Back");
        close.setItemMeta(closeMeta);
        mainGUI.setItem(49,close);

        int count = originalPivot;
        if(isTown) {
            for(TownTier tier : TownTiers.getInstance().getTownTiers().values()) {
                if(tier.getTier()==0) {
                    continue;
                }
                ItemStack viewItem = new ItemStack(tier.getViewMaterial());
                ItemMeta viewMeta = viewItem.getItemMeta();
                viewMeta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Tier "+tier.getTier());
                ArrayList<String> perks = TownTiers.getInstance().getPerksFromTier(tier);
                ArrayList<String> viewLore = new ArrayList<>();
                int totalXP = TownTiers.getInstance().getXpToNextLevel(tier.getTier()-1,true);
                viewLore.add(ChatColor.WHITE + "" + totalXP + " TP");
                viewLore.add("");
                viewLore.add(ChatColor.GOLD+""+ChatColor.BOLD+"Perks:");

                for(String perk : perks) {
                    viewLore.add(ChatColor.WHITE + " - "+perk);
                }
                viewMeta.setLore(viewLore);
                viewMeta.addEnchant(Enchantment.DAMAGE_ARTHROPODS,1,false);
                viewMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                viewItem.setItemMeta(viewMeta);

                count++;
                if(count==25) {
                    count=29;
                }
                mainGUI.setItem(count,viewItem);
            }
        } else {

            for(NationTier tier : TownTiers.getInstance().getNationTiers().values()) {
                if(tier.getTier()==0) {
                    continue;
                }
                ItemStack viewItem = new ItemStack(tier.getViewMaterial());
                ItemMeta viewMeta = viewItem.getItemMeta();
                viewMeta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Tier "+tier.getTier());
                ArrayList<String> perks = TownTiers.getInstance().getPerksFromTier(tier);
                ArrayList<String> viewLore = new ArrayList<>();
                int totalXP = TownTiers.getInstance().getXpToNextLevel(tier.getTier()-1,false);
                viewLore.add(ChatColor.WHITE + "" +  totalXP+"NP");
                viewLore.add("");
                viewLore.add(ChatColor.GOLD+"Perks:");

                for(String perk : perks) {
                    viewLore.add(ChatColor.WHITE + " - "+perk);
                }
                viewMeta.setLore(viewLore);
                viewMeta.addEnchant(Enchantment.DAMAGE_ARTHROPODS,1,false);
                viewMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                viewItem.setItemMeta(viewMeta);
                count++;
                if(count>24) {
                    count=29;
                }
                mainGUI.setItem(count,viewItem);
            }
        }
        PlayerData playerData = TownTiers.getInstance().getPlayerData(player);
        playerData.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);
    }
    public static void constructConversionMenu(Player player,Town town, Nation nation, boolean isTown) {
        String title = "Convert Money";
        Inventory mainGUI = Bukkit.createInventory(null,54,title);

        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.MoneyConversion");
        if(isTown) {
            identifierMeta.setUnbreakable(true);
            identifierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<10;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=(6-1)*9;i<54;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=1;i<6-1;i++) {
            mainGUI.setItem(i*9,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            mainGUI.setItem(i*9+8,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        float moneyConversionCost = 0;
        Convertor goldConvertor = null;
        if(isTown) {
            goldConvertor  =TownTiers.getInstance().getConvertorGold(town);
            moneyConversionCost = goldConvertor.getConversionFactor();
        } else {
            goldConvertor = TownTiers.getInstance().getConvertorGold(nation);
            moneyConversionCost = goldConvertor.getConversionFactor();
        }
        mainGUI.setItem(0,identifier);
        ArrayList<String> moneyConversionLore = new ArrayList<>();
        moneyConversionLore.add("");
        moneyConversionLore.add(ChatColor.WHITE+"$1 = "+moneyConversionCost);
        moneyConversionLore.add("");
        if(isTown) {
            moneyConversionLore.add(ChatColor.YELLOW + "/tiers convert money town [amount]");
        } else {
            moneyConversionLore.add(ChatColor.YELLOW+"/tiers convert money nation [amount]");
        }
        ItemStack convertHundred = new ItemStack(Material.GOLD_INGOT);
        ItemMeta hundredMeta = convertHundred.getItemMeta();
        hundredMeta.getPersistentDataContainer().set(conversionAmountKey,PersistentDataType.INTEGER,100);
        int conversionAmount = (int) (100*moneyConversionCost);
        hundredMeta.setDisplayName(ChatColor.BOLD+""+ChatColor.GOLD+"$100 for "+conversionAmount);
        hundredMeta.setLore(moneyConversionLore);
        convertHundred.setItemMeta(hundredMeta);
        mainGUI.setItem(20,convertHundred);

        ItemStack convertThousand = new ItemStack(Material.GOLD_INGOT);
        ItemMeta thousandMeta = convertHundred.getItemMeta();
        thousandMeta.getPersistentDataContainer().set(conversionAmountKey,PersistentDataType.INTEGER,500);

        conversionAmount = (int) (500*moneyConversionCost);
        thousandMeta.setDisplayName(ChatColor.BOLD+""+ChatColor.GOLD+"$500 for "+conversionAmount);
        thousandMeta.setLore(moneyConversionLore);
        convertThousand.setItemMeta(thousandMeta);
        mainGUI.setItem(13,convertThousand);

        ItemStack convertmany = new ItemStack(Material.GOLD_INGOT);
        ItemMeta manyMeta = convertHundred.getItemMeta();
        manyMeta.getPersistentDataContainer().set(conversionAmountKey,PersistentDataType.INTEGER,1000);
        conversionAmount = (int) (1000*moneyConversionCost);
        manyMeta.setDisplayName(ChatColor.BOLD+""+ChatColor.GOLD+"$1000 for "+conversionAmount);
        thousandMeta.setLore(moneyConversionLore);
        manyMeta.setLore(moneyConversionLore);
        convertmany.setItemMeta(manyMeta);
        mainGUI.setItem(24,convertmany);

        ItemStack convertor = new ItemStack(Material.CHEST);
        ItemMeta convertorMeta = convertor.getItemMeta();
        convertorMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Upgrade Converter");
        ArrayList<String> convertorLore = new ArrayList<>();
        convertorLore.add(ChatColor.WHITE+"Upgrading your convertor increases");
        convertorLore.add(ChatColor.WHITE+"the TP from spending money");
        convertorLore.add("");
        convertorLore.add(ChatColor.GOLD+"Current Level: "+ChatColor.WHITE+(goldConvertor.getLevel()+1));
        convertorLore.add(ChatColor.GOLD+"Current Conversion: "+ChatColor.WHITE+goldConvertor.getConversionFactor());
        Convertor nextConvertor = TownTiers.getInstance().getConvertorGold(goldConvertor.getLevel()+1);
        if(nextConvertor==null) {
            convertorLore.add("");
            convertorLore.add(ChatColor.GOLD+"Convertor Maxed");
        } else {
            convertorLore.add("");
            convertorLore.add(ChatColor.GOLD + "Upgrade Cost: " + ChatColor.WHITE+"$"+ goldConvertor.getCostToUpgrade());
            convertorLore.add(ChatColor.GOLD + "Conversion: " + ChatColor.GRAY +goldConvertor.getConversionFactor()+"⇨"+ChatColor.YELLOW+nextConvertor.getConversionFactor());
        }
        convertorMeta.setLore(convertorLore);
        convertor.setItemMeta(convertorMeta);
        mainGUI.setItem(31,convertor);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Close");
        close.setItemMeta(closeMeta);
        mainGUI.setItem(49,close);

        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        p.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);
    }
    public static void constructXpConversionMenu(Player player,Town town,Nation nation,boolean isTown) {
        String title = "Convert XP";
        Inventory mainGUI = Bukkit.createInventory(null,54,title);

        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        if(isTown) {
            identifierMeta.setUnbreakable(true);
            identifierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.XPConversion");
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<10;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=(6-1)*9;i<54;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=1;i<6-1;i++) {
            mainGUI.setItem(i*9,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            mainGUI.setItem(i*9+8,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        mainGUI.setItem(0,identifier);
        float xpConversionAmount = 0;
        Convertor xpConvertor = null;
        if(isTown) {
            xpConvertor = TownTiers.getInstance().getConvertorXP(town);
            xpConversionAmount = xpConvertor.getConversionFactor();
        } else {
            xpConvertor = TownTiers.getInstance().getConvertorXP(nation);
            xpConversionAmount = xpConvertor.getConversionFactor();
        }



        ItemStack convertHundred = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta hundredMeta = convertHundred.getItemMeta();
        hundredMeta.getPersistentDataContainer().set(conversionAmountKey,PersistentDataType.INTEGER,100);
        int conversionAmount = (int) (100*xpConversionAmount);
        hundredMeta.setDisplayName(ChatColor.BOLD+""+ChatColor.GOLD+"100 XP (" + TownTiers.getInstance().getLevel(100) + " lvl) for "+conversionAmount);
        ArrayList<String> XPConversionLore = new ArrayList<>();
        XPConversionLore.add("");
        XPConversionLore.add(ChatColor.WHITE+"1XP = "+xpConversionAmount);
        XPConversionLore.add("");
        if(isTown) {
            XPConversionLore.add(ChatColor.YELLOW + "/tiers convert xp town [amount]");
        } else {
            XPConversionLore.add(ChatColor.YELLOW+"/tiers convert xp nation [amount]");
        }
        hundredMeta.setLore(XPConversionLore);
        convertHundred.setItemMeta(hundredMeta);
        mainGUI.setItem(20,convertHundred);

        ItemStack convertThousand = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta thousandMeta = convertHundred.getItemMeta();
        thousandMeta.getPersistentDataContainer().set(conversionAmountKey,PersistentDataType.INTEGER,500);
        conversionAmount = (int) (500*xpConversionAmount);
        thousandMeta.setDisplayName(ChatColor.BOLD+""+ChatColor.GOLD+"500 XP (" + TownTiers.getInstance().getLevel(500) + " lvl) for "+conversionAmount);
        thousandMeta.setLore(XPConversionLore);
        convertThousand.setItemMeta(thousandMeta);
        mainGUI.setItem(13,convertThousand);

        ItemStack convertmany = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta manyMeta = convertHundred.getItemMeta();
        manyMeta.getPersistentDataContainer().set(conversionAmountKey,PersistentDataType.INTEGER,1000);
        conversionAmount = (int) (1000*xpConversionAmount);
        manyMeta.setDisplayName(ChatColor.BOLD+""+ChatColor.GOLD+"1000 XP (" + TownTiers.getInstance().getLevel(1000) + " lvl) for "+conversionAmount);
        manyMeta.setLore(XPConversionLore);
        convertmany.setItemMeta(manyMeta);
        mainGUI.setItem(24,convertmany);

        ItemStack convertor = new ItemStack(Material.CHEST);
        ItemMeta convertorMeta = convertor.getItemMeta();
        convertorMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Upgrade Converter");
        ArrayList<String> convertorLore = new ArrayList<>();
        convertorLore.add(ChatColor.WHITE+"Upgrading your convertor increases");
        convertorLore.add(ChatColor.WHITE+"the TP from spending money");
        convertorLore.add("");
        convertorLore.add(ChatColor.GOLD+"Current Level: "+ChatColor.WHITE+(xpConvertor.getLevel()+1));
        convertorLore.add(ChatColor.GOLD+"Current Conversion: "+ChatColor.WHITE+(xpConvertor.getConversionFactor()));
        Convertor nextConvertor = TownTiers.getInstance().getConvertorXP(xpConvertor.getLevel()+1);
        if(nextConvertor==null) {
            convertorLore.add("");
            convertorLore.add(ChatColor.GOLD+"Convertor Maxed");
        } else {
            convertorLore.add("");
            convertorLore.add(ChatColor.GOLD + "Upgrade Cost: " + ChatColor.WHITE+"$"+ xpConvertor.getCostToUpgrade());
            convertorLore.add(ChatColor.GOLD + "Conversion: " + ChatColor.GRAY +xpConvertor.getConversionFactor()+"⇨"+ChatColor.YELLOW+nextConvertor.getConversionFactor());
        }
        convertorMeta.setLore(convertorLore);
        convertor.setItemMeta(convertorMeta);
        mainGUI.setItem(31,convertor);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Back");
        close.setItemMeta(closeMeta);
        mainGUI.setItem(49,close);

        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        p.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);
    }
    public static void constructGeneratorGUI(Player player, boolean isTown, Town town, Nation nation, int gen) {
        String title = "Town Points Generator";
        if(!isTown) {
            title = "Nation Points Generator";
        }
        Inventory mainGUI = Bukkit.createInventory(null,54,title);
        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.Generator");
        identifierMeta.getPersistentDataContainer().set(generatorSelectionKey,PersistentDataType.INTEGER,gen);
        if(isTown) {
            identifierMeta.setUnbreakable(true);
            identifierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<54;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        mainGUI.setItem(0,identifier);

        Generator generator = null;
        int amountStorage;
        int currentPoints = 0;
        int tier = 0;
        String genTitle = "Town";
        String ptsTitle = "TP";
        if(isTown) {
            generator = TownTiers.getInstance().getGeneratorFromTown(town,gen);
            amountStorage = TownTiers.getInstance().getAmountInStorage(generator,town,gen);
            currentPoints = Math.max(0,TownTiers.getInstance().getTownXP(town));
            tier = Math.max(0,TownTiers.getInstance().getTownTeir(town));
        } else {
            generator=  TownTiers.getInstance().getGeneratorFromNation(nation,gen);
            amountStorage = TownTiers.getInstance().getAmountInStorage(generator,nation,gen);
            currentPoints = Math.max(0,TownTiers.getInstance().getNationXP(nation));
            tier = Math.max(0,TownTiers.getInstance().getNationTeir(nation));
            genTitle = "National";
            ptsTitle = "NP";
        }
        if (generator == null) {
            player.sendMessage(ChatColor.RED+"Something went wrong!");
            player.closeInventory();
            return;
        }
        ItemStack mainStack = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = mainStack.getItemMeta();
        paperMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Level "+(generator.getLevel()+1)+" Generator");
        ArrayList<String> paperLore = new ArrayList<>();
        paperLore.add("");
        paperLore.add(ChatColor.WHITE+"This generates town points over time");
        paperLore.add(ChatColor.WHITE+"You can upgrade by spending XP");
        paperLore.add("");
        paperLore.add(ChatColor.GOLD+genTitle+" Points: "+ChatColor.WHITE+generator.getXpPerHour()+" TP an hour");
        paperLore.add(ChatColor.GOLD+"Storage: "+ChatColor.WHITE+generator.getStorage());
        paperMeta.setLore(paperLore);
        mainStack.setItemMeta(paperMeta);
        mainGUI.setItem(13,mainStack);

        ItemStack storageStack = new ItemStack(Material.CHEST);
        ItemMeta storageMeta = storageStack.getItemMeta();
        storageMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Storage");
        ArrayList<String> storageLore = new ArrayList<>();
        storageLore.add("");
        storageLore.add(ChatColor.GOLD+"Tier: "+ChatColor.WHITE+tier);
        storageLore.add(ChatColor.GOLD+ptsTitle+": "+ChatColor.WHITE+currentPoints);
        storageLore.add("");
        storageLore.add(ChatColor.GOLD+"Total Storage: "+ChatColor.WHITE+generator.getStorage() + ptsTitle);
        storageLore.add(ChatColor.GOLD+"Amount: "+ChatColor.WHITE+amountStorage+" TP");
        storageLore.add("");
        storageLore.add(ChatColor.YELLOW+"Left-Click to Collect "+amountStorage+" "+ptsTitle);
        storageMeta.setLore(storageLore);
        storageStack.setItemMeta(storageMeta);
        mainGUI.setItem(30,storageStack);

        Generator nextGen = TownTiers.getInstance().getGeneratorFromNumber(generator.getLevel()+1,isTown);
        ItemStack upgradeStack = new ItemStack(Material.DIAMOND);
        ItemMeta upgradeMeta = upgradeStack.getItemMeta();
        upgradeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Upgrade");
        ArrayList<String> upgradeLore = new ArrayList<>();
        if(nextGen==null) {
            upgradeLore.add("");
            upgradeLore.add(ChatColor.GOLD+"Maxed out!");
        } else {
            upgradeLore.add(ChatColor.GOLD+"Your XP: "+ChatColor.WHITE+SetExpFix.getTotalExperience(player));
            upgradeLore.add("");
            upgradeLore.add(ChatColor.GOLD + "Cost: " + ChatColor.WHITE + generator.getCostToUpgrade()+" XP"+ChatColor.GOLD+" ("+ChatColor.WHITE+TownTiers.getInstance().getLevel(generator.getCostToUpgrade()) + " lvl" + ChatColor.GOLD+")");
            upgradeLore.add(ChatColor.GOLD+"Points Per Hour: "+ChatColor.GRAY+generator.getXpPerHour()+"⇨"+ ChatColor.YELLOW+nextGen.getXpPerHour());
            upgradeLore.add(ChatColor.GOLD+"Storage: "+ChatColor.GRAY+generator.getStorage()+"⇨"+ChatColor.YELLOW+nextGen.getStorage());
            upgradeLore.add("");
            upgradeLore.add(ChatColor.YELLOW+"Left-Click to upgrade!");
        }
        upgradeMeta.setLore(upgradeLore);
        upgradeStack.setItemMeta(upgradeMeta);
        mainGUI.setItem(32,upgradeStack);

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Close");
        closeButton.setItemMeta(closeMeta);
        mainGUI.setItem(49,closeButton);

        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        p.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);
    }
    public static void constructGeneratorSelectionGUI(Player player, boolean isTown) {

        int numOfGenerator = 0;
        int unlockedGen = 0;
        Town town = null;
        Nation nation = null;
        ArrayList<Generator> allGen;
        if(isTown) {
            town = TownyUtils.getTownFromPlayer(player);
            if(town==null) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED+"You must be in a town to do this!");
                return;
            }
            allGen = TownTiers.getInstance().getGeneratorsFromTown(town);
            unlockedGen = TownTiers.getInstance().getTierFromTown(town).getAmountOfGenerators();
        } else {
            nation = TownyUtils.getNationFromPlayer(player);
            if(nation==null) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED+"You must be in a nation to do this!");
                return;
            }
            allGen = TownTiers.getInstance().getGeneratorsFromNation(nation);
            unlockedGen = TownTiers.getInstance().getTierFromNation(nation).getAmountOfGenerators();

        }
        String title = "Select Generator";
        Inventory mainGUI = Bukkit.createInventory(null,45,title);
        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        if(isTown) {
            identifierMeta.setUnbreakable(true);
            identifierMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.GeneratorSelect");
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<45;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        mainGUI.setItem(0,identifier);

        ItemStack infoStack = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoStack.getItemMeta();
        infoMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Generators");
        ArrayList<String> infoLore = new ArrayList<>();
        infoLore.add("");
        infoLore.add(ChatColor.WHITE+"Generators collect town and nation");
        infoLore.add(ChatColor.WHITE+"points. Each town can have up to");
        infoLore.add(ChatColor.WHITE+"6 generators one unlocked by nation");
        infoMeta.setLore(infoLore);
        infoStack.setItemMeta(infoMeta);

        int pivot = originalPivot;
        int totalInStorage = 0;
        int totalStorage = 0;

        for(int i=0;i<allGen.size();i++) {
            ItemStack genStack = new ItemStack(Material.CHEST);
            ItemMeta genMeta = genStack.getItemMeta();
            genMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Level "+(allGen.get(i).getLevel()+1)+ " Generator");
            ArrayList<String> genLore = new ArrayList<>();
            genLore.add("");
            int amountInStorage = 0;
            if(isTown) {
                amountInStorage = TownTiers.getInstance().getAmountInStorage(allGen.get(i),town,i);
            } else {
                amountInStorage = TownTiers.getInstance().getAmountInStorage(allGen.get(i),nation,i);
            }
            totalInStorage+=amountInStorage;
            totalStorage+=allGen.get(i).getStorage();
            genLore.add(ChatColor.GOLD+"Amount: "+ChatColor.WHITE+amountInStorage);
            genLore.add(ChatColor.GOLD+"Storage: "+ChatColor.WHITE+allGen.get(i).getStorage());
            if(isTown) {
                genLore.add(ChatColor.GOLD + "TP Per Hour: " + ChatColor.WHITE + allGen.get(i).getXpPerHour());
            } else {
                genLore.add(ChatColor.GOLD + "NP Per Hour: " + ChatColor.WHITE + allGen.get(i).getXpPerHour());
            }
            genLore.add("");
            genLore.add(ChatColor.YELLOW+"Left-Click to view this generator");
            genMeta.setLore(genLore);
            genMeta.getPersistentDataContainer().set(generatorSelectionKey,PersistentDataType.INTEGER,i);
            genStack.setItemMeta(genMeta);
            mainGUI.setItem(pivot,genStack);
            pivot++;
            if(pivot==22) {
                pivot++;
            }
        }
        numOfGenerator = allGen.size();
        int lowestGen = 0;
        if(isTown) {
            lowestGen = TownTiers.getInstance().lowestTownGen();
        } else {
            lowestGen = TownTiers.getInstance().lowestNationGen();
        }
        ArrayList<Integer> locked;
        if(isTown) {
             locked = TownTiers.getInstance().getTownGeneratorUnlocks();
        } else {
            locked = TownTiers.getInstance().getNationGeneratorUnlocks();
        }
        int count = numOfGenerator;
        if(pivot>22) {
            unlockedGen++;
        }
        for(int i=pivot;i<7+originalPivot;i++) {
            if(i==22) {
                i++;
                unlockedGen++;
            }
            if(i<originalPivot+unlockedGen) {
                ItemStack unlockedStack = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
                ItemMeta unlockedMeta = unlockedStack.getItemMeta();
                unlockedMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Unlocked Gen Slot");
                ArrayList<String> unlockedLore = new ArrayList<>();
                unlockedLore.add("");
                unlockedLore.add(ChatColor.GOLD+"Cost: "+ChatColor.WHITE+lowestGen+" XP "+ChatColor.GOLD+"("+ChatColor.WHITE+TownTiers.getInstance().getLevel(lowestGen)+" lvl"+ChatColor.GOLD+")");
                unlockedLore.add("");
                unlockedLore.add(ChatColor.YELLOW+"Left-Click to Buy");
                unlockedMeta.setLore(unlockedLore);
                unlockedMeta.getPersistentDataContainer().set(generatorSelectionKey,PersistentDataType.INTEGER,allGen.size());
                unlockedStack.setItemMeta(unlockedMeta);
                mainGUI.setItem(i,unlockedStack);
            } else {
                ItemStack lockedStack = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                ItemMeta lockedMeta = lockedStack.getItemMeta();
                lockedMeta.setDisplayName(ChatColor.YELLOW+"Unlocked at tier "+locked.get(count));
                lockedStack.setItemMeta(lockedMeta);
                mainGUI.setItem(i,lockedStack);
            }
            count++;
        }

        ItemStack collectAllStack = new ItemStack(Material.EMERALD);
        ItemMeta collectMeta = collectAllStack.getItemMeta();
        collectMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Collect All");
        ArrayList<String> collectLore = new ArrayList<>();
        collectLore.add("");
        collectLore.add(ChatColor.GOLD+"Total Amount: "+ChatColor.WHITE+totalInStorage);
        collectLore.add(ChatColor.GOLD+"Total Storage: "+ChatColor.WHITE+totalStorage);
        collectLore.add(ChatColor.GOLD+"");
        collectLore.add(ChatColor.YELLOW+"Left-Click to Quick Collect!");
        collectMeta.setLore(collectLore);
        collectAllStack.setItemMeta(collectMeta);
        mainGUI.setItem(22,collectAllStack);

        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Close");
        close.setItemMeta(closeMeta);
        mainGUI.setItem(40,close);

        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        p.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);
    }
    public static void constructSettingsGUI(Player player, Town town) {
        if(town==null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED+"You do not have a town!");
            return;
        }
        String title = "Town Settings";
        Inventory mainGUI = Bukkit.createInventory(null,45,title);

        ItemStack identifier = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta identifierMeta = identifier.getItemMeta();
        identifierMeta.getPersistentDataContainer().set(inventorySep, PersistentDataType.STRING,"Inventory.Settings");
        identifier.setItemMeta(identifierMeta);
        for(int i=0;i<10;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=(5-1)*9;i<45;i++) {
            mainGUI.setItem(i,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        for(int i=1;i<5-1;i++) {
            mainGUI.setItem(i*9,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
            mainGUI.setItem(i*9+8,new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        TownTier tier = TownTiers.getInstance().getTierFromTown(town);
        int currentAmount = TownyUtils.getMetaDataFromTown(town, Settings.townBankPercentage);
        if(currentAmount<0) {
            currentAmount = 0;
        }
        mainGUI.setItem(0,identifier);
        ItemStack bankStack = new ItemStack(Material.BEACON);
        ItemMeta bankMeta = bankStack.getItemMeta();
        bankMeta.setDisplayName(ChatColor.GOLD+"Toggle Bank Percent");
        ArrayList<String> bankLore = new ArrayList<>();
        bankLore.add("");
        bankLore.add(ChatColor.WHITE+"This determines how much of");
        bankLore.add(ChatColor.WHITE+"of a town mission goes to the");
        bankLore.add(ChatColor.WHITE+"town bank rather than the");
        bankLore.add(ChatColor.WHITE+"contributors");
        bankLore.add("");
        bankLore.add(ChatColor.GOLD+"Current: "+ChatColor.WHITE+(currentAmount+20)+"%");
        bankMeta.setLore(bankLore);
        bankStack.setItemMeta(bankMeta);
        mainGUI.setItem(19,bankStack);

        int currentHaste = TownyUtils.getMetaDataFromTown(town, Settings.hasteInClaims);
        if(currentHaste<0) {
            currentHaste= tier.getHasteInClaims();
        }
        ItemStack hasteStack = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemMeta hasteMeta = hasteStack.getItemMeta();
        hasteMeta.setDisplayName(ChatColor.GOLD+"Toggle Haste");
        hasteMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        ArrayList<String> hasteLore = new ArrayList<>();
        hasteLore.add("");
        hasteLore.add(ChatColor.WHITE+"This determines the amount");
        hasteLore.add(ChatColor.WHITE+"of permanent haste in claims");
        hasteLore.add("");
        hasteLore.add(ChatColor.GOLD+"Haste: "+ChatColor.WHITE+Math.max(0,currentHaste));
        hasteMeta.setLore(hasteLore);
        hasteStack.setItemMeta(hasteMeta);
        mainGUI.setItem(20,hasteStack);

        int currentSpeed = TownyUtils.getMetaDataFromTown(town, Settings.speedInClaims);
        if(currentAmount<0) {
            currentSpeed = tier.getSpeedInClaims();
        }
        ItemStack speedStack = new ItemStack(Material.FEATHER);
        ItemMeta speedMeta = speedStack.getItemMeta();
        speedMeta.setDisplayName(ChatColor.GOLD+"Toggle Speed");
        ArrayList<String> speedLore = new ArrayList<>();
        speedLore.add("");
        speedLore.add(ChatColor.WHITE+"This determines the amount");
        speedLore.add(ChatColor.WHITE+"of permanent speed in claims");
        speedLore.add("");
        speedLore.add(ChatColor.GOLD+"Speed: "+ChatColor.WHITE+Math.max(0,currentSpeed));
        speedMeta.setLore(speedLore);
        speedStack.setItemMeta(speedMeta);
        mainGUI.setItem(21,speedStack);

        //if the town has this meta data than pearls are disabled
        //if the town does not have this metadata then currentpearls is true thus pearls are not lost
        boolean currentPearls = !town.hasMeta(Settings.pearlClaims.getKey());
        ItemStack pearlStack = new ItemStack(Material.ENDER_PEARL);
        ItemMeta pearlMeta = pearlStack.getItemMeta();
        pearlMeta.setDisplayName(ChatColor.GOLD+"Toggle Pearls");
        ArrayList<String> pearlLore = new ArrayList<>();
        pearlLore.add("");
        pearlLore.add(ChatColor.WHITE+"This determines if pearls");
        pearlLore.add(ChatColor.WHITE+"will be lost in claims");
        pearlLore.add("");
        if(currentPearls) {
            pearlLore.add(ChatColor.WHITE+"Pearls are NOT lost in claims");
        } else {
            pearlLore.add(ChatColor.WHITE+"Pearls are lost in claims");
        }
        pearlMeta.setLore(pearlLore);
        pearlStack.setItemMeta(pearlMeta);
        mainGUI.setItem(23,pearlStack);

        boolean currentHunger = !town.hasMeta(Settings.hungerClaims.getKey());
        ItemStack hungerStack = new ItemStack(Material.COOKED_BEEF);
        ItemMeta hungerMeta = pearlStack.getItemMeta();
        hungerMeta.setDisplayName(ChatColor.GOLD+"Toggle Hunger");
        ArrayList<String> hungerLore = new ArrayList<>();
        hungerLore.add("");
        hungerLore.add(ChatColor.WHITE+"This determines if hunger");
        hungerLore.add(ChatColor.WHITE+"will be lost in claims");
        hungerLore.add("");
        if(currentHunger) {
            hungerLore.add(ChatColor.WHITE+"Hunger is NOT lost in claims");
        } else {
            hungerLore.add(ChatColor.WHITE+"Hunger is lost in claims");
        }
        hungerMeta.setLore(hungerLore);
        hungerStack.setItemMeta(hungerMeta);
        mainGUI.setItem(24,hungerStack);

        boolean currentFall = !town.hasMeta(Settings.noFallClaims.getKey());
        ItemStack fallStack = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta fallMeta = fallStack.getItemMeta();
        fallMeta.setDisplayName(ChatColor.GOLD+"Toggle No-Fall");
        ArrayList<String> fallLore = new ArrayList<>();
        fallLore.add("");
        fallLore.add(ChatColor.WHITE+"This determines if fall");
        fallLore.add(ChatColor.WHITE+"damage will be taken in claims");
        fallLore.add("");
        if(currentFall) {
            fallLore.add(ChatColor.WHITE+"Fall Damage NOT taken in claims");
        } else {
            fallLore.add(ChatColor.WHITE+"Fall Damage is taken in claims");
        }
        fallMeta.setLore(fallLore);
        fallStack.setItemMeta(fallMeta);
        mainGUI.setItem(25,fallStack);

        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.YELLOW+""+ChatColor.BOLD+"Close");
        closeButton.setItemMeta(closeMeta);
        mainGUI.setItem(40,closeButton);

        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        p.setViewedInventory(mainGUI);
        player.openInventory(mainGUI);

    }


}
