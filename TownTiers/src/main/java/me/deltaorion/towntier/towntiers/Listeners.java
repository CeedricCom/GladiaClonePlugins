package me.deltaorion.towntier.towntiers;

import com.gmail.goosius.siegewar.SiegeController;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.event.*;
import com.palmergames.bukkit.towny.event.player.PlayerEntersIntoTownBorderEvent;
import com.palmergames.bukkit.towny.event.player.PlayerExitsFromTownBorderEvent;
import com.palmergames.bukkit.towny.event.statusscreen.NationStatusScreenEvent;
import com.palmergames.bukkit.towny.event.statusscreen.TownStatusScreenEvent;
import com.palmergames.bukkit.towny.event.town.TownLeaveEvent;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.permissions.PermissionNodes;
import com.palmergames.bukkit.towny.permissions.TownyPermissionSource;
import me.deltaorion.towntier.towntiers.data.PlayerData;
import me.deltaorion.towntier.towntiers.data.TownTierData;
import me.deltaorion.towntier.towntiers.townyutils.Settings;
import me.deltaorion.towntier.towntiers.townyutils.TownSpawn;
import me.deltaorion.towntier.towntiers.townyutils.TownyUtils;
import me.nik.combatplus.CombatPlus;
import me.nik.combatplus.modules.impl.EnderpearlCooldown;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Listeners implements Listener {

    private static final NamespacedKey giveSpeedKey;
    private static final NamespacedKey giveHasteKey;
    private static final NamespacedKey hasteReasonKey;
    private static final NamespacedKey speedReasonKey;
    private static final NamespacedKey enderPearlTipKey;
    private static final NamespacedKey fallTipKey;
    private final TownyPermissionSource permSource;

    public Listeners() {
        permSource = TownyUniverse.getInstance().getPermissionSource();
    }

    static {
        giveSpeedKey = new NamespacedKey(TownTiers.getInstance(), "TownTiers.townSpeed");
        giveHasteKey = new NamespacedKey(TownTiers.getInstance(), "TownTiers.townHaste");
        hasteReasonKey = new NamespacedKey(TownTiers.getInstance(), "TownTiers.hasteCauseTown");
        speedReasonKey = new NamespacedKey(TownTiers.getInstance(), "TownTiers.speedCauseTown");
        enderPearlTipKey = new NamespacedKey(TownTiers.getInstance(), "TownTiers.epearlTipKey");
        fallTipKey = new NamespacedKey(TownTiers.getInstance(), "TownTiers.fallTipKey");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TownTiers.getInstance().playerJoin(event.getPlayer());
    }

    @EventHandler
    public void onInventory(InventoryInteractEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        Inventory viewedInventory = p.getViewedInventory();
        if (viewedInventory != null) {
            if (viewedInventory.equals(event.getInventory())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        Inventory viewedInventory = p.getViewedInventory();
        if (viewedInventory != null) {
            if (viewedInventory.equals(event.getInventory())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        PlayerData p = TownTiers.getInstance().getPlayerData(player);
        Inventory viewedInventory = p.getViewedInventory();
        if (viewedInventory != null) {
            if (viewedInventory.equals(event.getInventory())) {
                event.setCancelled(true);
                long now = System.currentTimeMillis();
                if (now - p.getLastAction() < 200) {
                    return;
                }
                p.setLastAction(now);
                ItemStack identifier = viewedInventory.getItem(0);
                if (identifier != null && event.getCurrentItem() != null) {
                    ItemMeta identifierMeta = identifier.getItemMeta();
                    if (identifierMeta.getPersistentDataContainer().has(GUImanager.inventorySep, PersistentDataType.STRING)) {
                        String identifierKey = identifierMeta.getPersistentDataContainer().get(GUImanager.inventorySep, PersistentDataType.STRING);
                        boolean isTown = identifier.getItemMeta().isUnbreakable();
                        if (identifierKey == null) {
                            return;
                        }
                        switch (identifierKey) {
                            case "Inventory.Main":
                                handleMainInput(player, viewedInventory, event.getCurrentItem(), event.getSlot());
                                break;
                            case "Inventory.ViewTiers":
                                handleTeirInput(player, event.getCurrentItem(), event.getSlot(), isTown);
                                break;
                            case "Inventory.MoneyConversion":
                                handleGoldInput(player, event.getCurrentItem(), event.getSlot(), isTown);
                                break;
                            case "Inventory.XPConversion":
                                handleXPInput(player, event.getCurrentItem(), event.getSlot(), isTown);
                                break;
                            case "Inventory.Generator":
                                int gen = identifierMeta.getPersistentDataContainer().get(GUImanager.generatorSelectionKey, PersistentDataType.INTEGER);
                                handleGeneratorInput(player, event.getCurrentItem(), event.getSlot(), isTown, gen);
                                break;
                            case "Inventory.GeneratorSelect":
                                handleGeneratorSelectInput(player, event.getCurrentItem(), event.getSlot(), isTown);
                            case "Inventory.Settings":
                                handleSettingsInput(player, event.getCurrentItem(), event.getSlot());
                        }
                    }
                }

            }
        }
    }

    public void handleSettingsInput(Player player, ItemStack currentItem, int slot) {
        Town town = TownyUtils.getTownFromPlayer(player);
        if (town == null) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You must be in a town to do this");
            return;
        }
        TownTier tier = TownTiers.getInstance().getTierFromTown(town);
        if (currentItem.getType().equals(Material.BEACON)) {
            int currentAmount = TownyUtils.getMetaDataFromTown(town, Settings.townBankPercentage);
            if (currentAmount < 0) {
                currentAmount = 0;
            }
            if (currentAmount + 10 > tier.getTownMissionBankPercentage()) {
                currentAmount = 0;
            } else {
                currentAmount += 10;
            }
            TownyUtils.updateTownMetaData(town, currentAmount, Settings.townBankPercentage);
            player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 1.5f, 1f);
            GUImanager.constructSettingsGUI(player, town);
        } else if (currentItem.getType().equals(Material.BARRIER)) {
            player.closeInventory();
            return;
        } else if (currentItem.getType().equals(Material.GOLDEN_PICKAXE)) {
            if (tier.getHasteInClaims() == 0) {
                player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
                return;
            }
            int currentHaste = TownyUtils.getMetaDataFromTown(town, Settings.hasteInClaims);
            if (currentHaste < 0) {
                currentHaste = tier.getHasteInClaims();
            }
            if (currentHaste + 1 > tier.getHasteInClaims()) {
                currentHaste = 0;
            } else {
                currentHaste += 1;
            }
            TownyUtils.updateTownMetaData(town, currentHaste, Settings.hasteInClaims);
            int speed = Math.min(tier.getSpeedInClaims(), TownyUtils.getMetaDataFromTown(town, Settings.speedInClaims));
            if (speed < 0) {
                speed = tier.getSpeedInClaims();
            }
            for (Player p : TownyAPI.getInstance().getOnlinePlayersInTown(town)) {
                if (TownyUtils.playerInOwnTown(p)) {
                    handlePotionEffectRemoval(p);
                    handlePotionEffects(p, speed, currentHaste);
                }
            }
            player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 1.5f, 1f);
            GUImanager.constructSettingsGUI(player, town);
        } else if (currentItem.getType().equals(Material.FEATHER)) {
            if (tier.getSpeedInClaims() == 0) {
                player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
                return;
            }
            int currentSpeed = TownyUtils.getMetaDataFromTown(town, Settings.speedInClaims);
            if (currentSpeed < 0) {
                currentSpeed = tier.getHasteInClaims();
            }
            if (currentSpeed + 1 > tier.getSpeedInClaims()) {
                currentSpeed = 0;
            } else {
                currentSpeed += 1;
            }
            int haste = Math.min(tier.getHasteInClaims(), TownyUtils.getMetaDataFromTown(town, Settings.hasteInClaims));
            if (haste < 0) {
                haste = tier.getHasteInClaims();
            }
            for (Player p : TownyAPI.getInstance().getOnlinePlayersInTown(town)) {
                if (TownyUtils.playerInOwnTown(p)) {
                    handlePotionEffectRemoval(p);
                    handlePotionEffects(p, currentSpeed, haste);
                }
            }
            TownyUtils.updateTownMetaData(town, currentSpeed, Settings.speedInClaims);
            player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 1.5f, 1f);
            GUImanager.constructSettingsGUI(player, town);
        } else if (currentItem.getType().equals(Material.ENDER_PEARL)) {
            //if the town has this meta data than pearls are disabled
            //if the town does not have this metadata then currentpearls is true thus pearls are not lost
            boolean currentPearls = !town.hasMeta(Settings.pearlClaims.getKey());
            if (tier.isPearlsLost()) {
                player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            } else {
                if (currentPearls) {
                    TownyUtils.updateTownMetaData(town, 0, Settings.pearlClaims);
                } else {
                    TownyUtils.removeMetaDataFromTown(town, Settings.pearlClaims);
                }
                player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 1.5f, 1f);
                GUImanager.constructSettingsGUI(player, town);
            }
        } else if (currentItem.getType().equals(Material.COOKED_BEEF)) {
            boolean currentPearls = !town.hasMeta(Settings.hungerClaims.getKey());
            if (tier.isHungerInClaims()) {
                player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            } else {
                if (currentPearls) {
                    TownyUtils.updateTownMetaData(town, 0, Settings.hungerClaims);
                } else {
                    TownyUtils.removeMetaDataFromTown(town, Settings.hungerClaims);
                }
                player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 1.5f, 1f);
                GUImanager.constructSettingsGUI(player, town);
            }
        } else if (currentItem.getType().equals(Material.WITHER_SKELETON_SKULL)) {
            boolean currentFall = !town.hasMeta(Settings.noFallClaims.getKey());
            if (tier.isFallDamageInClaims()) {
                player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 1.5f, 1f);
            } else {
                if (currentFall) {
                    TownyUtils.updateTownMetaData(town, 0, Settings.noFallClaims);
                } else {
                    TownyUtils.removeMetaDataFromTown(town, Settings.noFallClaims);
                }
                player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 1.5f, 1f);
                GUImanager.constructSettingsGUI(player, town);
            }
        }
    }

    public void handleGeneratorSelectInput(Player player, ItemStack currentItem, int slot, boolean isTown) {
        if (currentItem.getType().equals(Material.BARRIER)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You do not have a town");
                    return;
                }
                GUImanager.constructMainGUI(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You do not have a nation");
                    return;
                }
                GUImanager.constructMainGUI(player, null, nation, false);
            }
        } else if (currentItem.getType().equals(Material.EMERALD)) {
            int amount = 0;
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a town");
                    player.closeInventory();
                    return;
                }
                amount = TownTiers.getInstance().collectFromAll(town);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a nation");
                    player.closeInventory();
                    return;
                }
                amount = TownTiers.getInstance().collectFromAll(nation);
            }
            if (amount <= 0) {
                player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                return;
            } else {
                GUImanager.constructGeneratorSelectionGUI(player, isTown);
                player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);
                player.sendMessage(ChatColor.GOLD + "You collected " + ChatColor.YELLOW + amount + ChatColor.GOLD + " from all generators!");
            }
        } else if (currentItem.getType().equals(Material.CHEST)) {
            ItemMeta currentMeta = currentItem.getItemMeta();
            if (currentMeta.getPersistentDataContainer().has(GUImanager.generatorSelectionKey, PersistentDataType.INTEGER)) {
                int generator = currentMeta.getPersistentDataContainer().get(GUImanager.generatorSelectionKey, PersistentDataType.INTEGER);
                if (isTown) {
                    Town town = TownyUtils.getTownFromPlayer(player);
                    if (town == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a town");
                        player.closeInventory();
                        return;
                    }
                    GUImanager.constructGeneratorGUI(player, true, town, null, generator);
                } else {
                    Nation nation = TownyUtils.getNationFromPlayer(player);
                    if (nation == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a nation");
                        player.closeInventory();
                        return;
                    }
                    GUImanager.constructGeneratorGUI(player, false, null, nation, generator);
                }
            }
        } else if (currentItem.getType().equals(Material.LIME_STAINED_GLASS_PANE)) {
            ItemMeta currentMeta = currentItem.getItemMeta();
            if (currentMeta.getPersistentDataContainer().has(GUImanager.generatorSelectionKey, PersistentDataType.INTEGER)) {
                int gen = currentMeta.getPersistentDataContainer().get(GUImanager.generatorSelectionKey, PersistentDataType.INTEGER);
                if (isTown) {
                    Town town = TownyUtils.getTownFromPlayer(player);
                    if (town == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a town");
                        player.closeInventory();
                        return;
                    }
                    int lowestGen = TownTiers.getInstance().lowestNationGen();
                    if (SetExpFix.getTotalExperience(player) > lowestGen) {
                        TownTiers.getInstance().setGeneratorLevel(town, 0, gen);
                        player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 2f, 1f);
                        GUImanager.constructGeneratorSelectionGUI(player, isTown);
                    } else {
                        player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                        player.sendMessage(ChatColor.GOLD + "You do not have enough XP you only have " + ChatColor.YELLOW + SetExpFix.getTotalExperience(player));
                        return;
                    }
                    TownTiers.getInstance().setGeneratorLevel(town, 0, gen);
                    player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 2f, 1f);
                    GUImanager.constructGeneratorSelectionGUI(player, isTown);
                } else {
                    Nation nation = TownyUtils.getNationFromPlayer(player);
                    if (nation == null) {
                        player.sendMessage(ChatColor.RED + "You are not in a nation");
                        player.closeInventory();
                        return;
                    }
                    int lowestGen = TownTiers.getInstance().lowestTownGen();
                    if (SetExpFix.getTotalExperience(player) > lowestGen) {
                        TownTiers.getInstance().setGeneratorLevel(nation, 0, gen);
                        player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 2f, 1f);
                        GUImanager.constructGeneratorSelectionGUI(player, isTown);
                    } else {
                        player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                        player.sendMessage(ChatColor.GOLD + "You do not have enough XP you only have " + ChatColor.YELLOW + SetExpFix.getTotalExperience(player));
                        return;
                    }

                }
            }
        } else if (currentItem.getType().equals(Material.YELLOW_STAINED_GLASS_PANE)) {
            player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
        }
    }

    public void handleMainInput(Player player, Inventory inventory, ItemStack clicked, int slot) {
        if (slot == 22) {
            boolean isTown = clicked.getItemMeta().isUnbreakable();
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                GUImanager.constructTierInformationMenu(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                GUImanager.constructTierInformationMenu(player, null, nation, false);
            }
        } else if (clicked.getType().equals(Material.COMPARATOR)) {
            if (inventory.getItem(22) == null) {
                player.sendMessage(ChatColor.RED + "Something went wrong!");
                player.closeInventory();
                return;
            }
            boolean isTown = inventory.getItem(22).getItemMeta().isUnbreakable();
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (town == null || resident == null) {
                    player.sendMessage(ChatColor.RED + "You must be in a town to do this");
                    player.closeInventory();
                    return;
                }
                if (town.getMayor().equals(resident)) {
                    GUImanager.constructSettingsGUI(player, town);
                }
            }
        } else if (clicked.getType().equals(Material.CHEST)) {
            if (inventory.getItem(22) == null) {
                player.sendMessage(ChatColor.RED + "Something went wrong!");
                player.closeInventory();
                return;
            }
            boolean isTown = inventory.getItem(22).getItemMeta().isUnbreakable();
            GUImanager.constructGeneratorSelectionGUI(player, isTown);
        } else if (clicked.getType().equals(Material.GOLD_INGOT)) {
            boolean isTown = inventory.getItem(22).getItemMeta().isUnbreakable();
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                GUImanager.constructConversionMenu(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                GUImanager.constructConversionMenu(player, null, nation, false);
            }
        } else if (clicked.getType().equals(Material.EXPERIENCE_BOTTLE)) {
            boolean isTown = inventory.getItem(22).getItemMeta().isUnbreakable();
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You must be in a town to do this");
                    return;
                }
                GUImanager.constructXpConversionMenu(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.RED + "You must be in a nation to do this");
                    return;
                }
                GUImanager.constructXpConversionMenu(player, null, nation, false);
            }
        } else if (clicked.getType().equals(Material.BARRIER)) {
            player.closeInventory();
        }
    }

    public void handleTeirInput(Player player, ItemStack clicked, int slot, boolean isTown) {
        if (clicked.getType().equals(Material.BARRIER)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.closeInventory();
                    return;
                }
                GUImanager.constructMainGUI(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.closeInventory();
                    return;
                }
                GUImanager.constructMainGUI(player, null, nation, false);
            }
        }
    }

    public void handleGeneratorInput(Player player, ItemStack clicked, int slot, boolean isTown, int gen) {
        if (clicked.getType().equals(Material.BARRIER)) {
            Town town = null;
            Nation nation = null;
            if (isTown) {
                town = TownyUtils.getTownFromPlayer(player);
            } else {
                nation = TownyUtils.getNationFromPlayer(player);
            }
            GUImanager.constructGeneratorSelectionGUI(player, isTown);
        } else if (clicked.getType().equals(Material.CHEST)) {
            Town town = null;
            Nation nation = null;
            if (isTown) {
                town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a town!");
                    player.closeInventory();
                    return;
                }
                Generator generator = TownTiers.getInstance().getGeneratorFromTown(town, gen);
                if (generator == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int amount = TownTiers.getInstance().getAmountInStorage(generator, town, gen);
                if (amount <= 0) {
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                } else {
                    TownTiers.getInstance().addXPTown(town, amount);
                    TownyUtils.updateTownMetaData(town, System.currentTimeMillis(), Generator.getCollections().get(gen));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);
                    player.sendMessage(ChatColor.GOLD + "Collected " + ChatColor.YELLOW + amount + " TP");
                    GUImanager.constructGeneratorGUI(player, true, town, null, gen);
                }
            } else {
                nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.sendMessage(ChatColor.RED + "You are not in a nation!");
                    player.closeInventory();
                    return;
                }
                Generator generator = TownTiers.getInstance().getGeneratorFromNation(nation, gen);
                if (generator == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int amount = Math.max(0, TownTiers.getInstance().getAmountInStorage(generator, nation, gen));
                if (amount <= 0) {
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                } else {
                    TownyUtils.updateNationMetaData(nation, System.currentTimeMillis(), Generator.getCollections().get(gen));
                    TownTiers.getInstance().addXPNation(nation, amount);
                    player.playSound(player.getLocation(), TownTiers.getInstance().getSuccessSound(), 2f, 1f);
                    player.sendMessage(ChatColor.GOLD + "Collected " + ChatColor.YELLOW + amount + " NP");
                    GUImanager.constructGeneratorGUI(player, false, null, nation, gen);
                }
            }
        } else if (clicked.getType().equals(Material.DIAMOND)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a town!");
                    player.closeInventory();
                    return;
                }
                Generator generator = TownTiers.getInstance().getGeneratorFromTown(town, gen);
                if (generator == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int cost = generator.getCostToUpgrade();
                if (cost > SetExpFix.getTotalExperience(player)) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough XP you only have " + ChatColor.YELLOW + SetExpFix.getTotalExperience(player));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                Generator nextGen = TownTiers.getInstance().getGeneratorFromNumber(generator.getLevel() + 1, true);
                if (nextGen == null) {
                    player.sendMessage(ChatColor.GOLD + "Your generator is maxed!");
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                player.giveExp(-cost);
                TownTiers.getInstance().setGeneratorLevel(town, generator.getLevel() + 1, gen);
                player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 3f, 1f);
                player.sendMessage(ChatColor.GOLD + "Successfully Upgraded Town Generator!");
                GUImanager.constructGeneratorGUI(player, true, town, null, gen);

            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a town!");
                    player.closeInventory();
                    return;
                }
                Generator generator = TownTiers.getInstance().getGeneratorFromNation(nation, gen);
                if (generator == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int cost = generator.getCostToUpgrade();
                if (cost > SetExpFix.getTotalExperience(player)) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough XP you only have " + ChatColor.YELLOW + SetExpFix.getTotalExperience(player));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                Generator nextGen = TownTiers.getInstance().getGeneratorFromNumber(generator.getLevel() + 1, true);
                if (nextGen == null) {
                    player.sendMessage(ChatColor.GOLD + "Your generator is maxed!");
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                player.giveExp(-cost);
                TownTiers.getInstance().setGeneratorLevel(nation, generator.getLevel() + 1, gen);
                player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 3f, 1f);
                player.sendMessage(ChatColor.GOLD + "Successfully Upgraded Town Generator!");
                GUImanager.constructGeneratorGUI(player, false, null, nation, gen);
            }
        }
    }

    public void handleGoldInput(Player player, ItemStack clicked, int slot, boolean isTown) {
        if (clicked.getType().equals(Material.BARRIER)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.closeInventory();
                    return;
                }
                GUImanager.constructMainGUI(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.closeInventory();
                    return;
                }
                GUImanager.constructMainGUI(player, null, nation, false);
            }
        } else if (clicked.getType().equals(Material.GOLD_INGOT)) {
            if (clicked.getItemMeta().getPersistentDataContainer().has(GUImanager.conversionAmountKey, PersistentDataType.INTEGER)) {
                int amount = clicked.getItemMeta().getPersistentDataContainer().get(GUImanager.conversionAmountKey, PersistentDataType.INTEGER);
                if (isTown) {
                    Town town = TownyUtils.getTownFromPlayer(player);
                    if (town == null) {
                        player.closeInventory();
                        return;
                    }
                    TownTiers.getInstance().tradeMoney(player, town, amount);
                } else {
                    Nation nation = TownyUtils.getNationFromPlayer(player);
                    if (nation == null) {
                        player.closeInventory();
                        return;
                    }
                    TownTiers.getInstance().tradeMoney(player, nation, amount);
                }
            }
        } else if (clicked.getType().equals(Material.CHEST)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (town == null || resident == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a town!");
                    player.closeInventory();
                    return;
                }

                Convertor convertor = TownTiers.getInstance().getConvertorGold(town);
                if (convertor == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int cost = convertor.getCostToUpgrade();
                if (cost > resident.getAccount().getHoldingBalance()) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough money you only have $" + ChatColor.YELLOW + String.format("%.2f", resident.getAccount().getHoldingBalance()));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                Convertor nextCon = TownTiers.getInstance().getConvertorGold(convertor.getLevel() + 1);
                if (nextCon == null) {
                    player.sendMessage(ChatColor.GOLD + "Your convertor is maxed!");
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                resident.getAccount().withdraw(convertor.getCostToUpgrade(), "Paid for a better convertor");
                TownTiers.getInstance().addConvertorLevelGold(town);
                player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 3f, 1f);
                player.sendMessage(ChatColor.GOLD + "Successfully Upgraded Gold Convertor!");
                GUImanager.constructConversionMenu(player, town, null, true);

            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (nation == null || resident == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a nation!");
                    player.closeInventory();
                    return;
                }

                Convertor convertor = TownTiers.getInstance().getConvertorGold(nation);
                if (convertor == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int cost = convertor.getCostToUpgrade();
                if (cost > resident.getAccount().getHoldingBalance()) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough money you only have $" + ChatColor.YELLOW + String.format("%.2f", resident.getAccount().getHoldingBalance()));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                Convertor nextCon = TownTiers.getInstance().getConvertorGold(convertor.getLevel() + 1);
                if (nextCon == null) {
                    player.sendMessage(ChatColor.GOLD + "Your convertor is maxed!");
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                resident.getAccount().withdraw(convertor.getCostToUpgrade(), "Paid for a better convertor");
                TownTiers.getInstance().addConvertorLevelGold(nation);
                player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 3f, 1f);
                player.sendMessage(ChatColor.GOLD + "Successfully Upgraded Gold Convertor!");
                GUImanager.constructConversionMenu(player, null, nation, false);

            }

        }
    }

    public void handleXPInput(Player player, ItemStack clicked, int slot, boolean isTown) {
        if (clicked.getType().equals(Material.BARRIER)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    player.closeInventory();
                    return;
                }
                GUImanager.constructMainGUI(player, town, null, true);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                if (nation == null) {
                    player.closeInventory();
                    return;
                }
                GUImanager.constructMainGUI(player, null, nation, false);
            }
        } else if (clicked.getType().equals(Material.EXPERIENCE_BOTTLE)) {
            if (clicked.getItemMeta().getPersistentDataContainer().has(GUImanager.conversionAmountKey, PersistentDataType.INTEGER)) {
                int amount = clicked.getItemMeta().getPersistentDataContainer().get(GUImanager.conversionAmountKey, PersistentDataType.INTEGER);
                if (isTown) {
                    Town town = TownyUtils.getTownFromPlayer(player);
                    if (town == null) {
                        player.closeInventory();
                        return;
                    }
                    TownTiers.getInstance().tradeXP(player, town, amount);
                } else {
                    Nation nation = TownyUtils.getNationFromPlayer(player);
                    if (nation == null) {
                        player.closeInventory();
                        return;
                    }
                    TownTiers.getInstance().tradeXP(player, nation, amount);
                }
            }
        } else if (clicked.getType().equals(Material.CHEST)) {
            if (isTown) {
                Town town = TownyUtils.getTownFromPlayer(player);
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (town == null || resident == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a town!");
                    player.closeInventory();
                    return;
                }
                Convertor convertor = TownTiers.getInstance().getConvertorXP(town);
                if (convertor == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int cost = convertor.getCostToUpgrade();
                if (cost > resident.getAccount().getHoldingBalance()) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough money you only have $" + ChatColor.YELLOW + String.format("%.2f", resident.getAccount().getHoldingBalance()));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                Convertor nextCon = TownTiers.getInstance().getConvertorXP(convertor.getLevel() + 1);
                if (nextCon == null) {
                    player.sendMessage(ChatColor.GOLD + "Your convertor is maxed!");
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                resident.getAccount().withdraw(convertor.getCostToUpgrade(), "Paid for a better convertor");
                TownTiers.getInstance().addConvertorLevelXP(town);
                player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 3f, 1f);
                player.sendMessage(ChatColor.GOLD + "Successfully Upgraded XP Generator!");
                GUImanager.constructXpConversionMenu(player, town, null, isTown);
            } else {
                Nation nation = TownyUtils.getNationFromPlayer(player);
                Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                if (nation == null || resident == null) {
                    player.sendMessage(ChatColor.RED + "You do not have a town!");
                    player.closeInventory();
                    return;
                }
                Convertor convertor = TownTiers.getInstance().getConvertorXP(nation);
                if (convertor == null) {
                    player.sendMessage(ChatColor.RED + "Something went wrong!");
                    player.closeInventory();
                    return;
                }
                int cost = convertor.getCostToUpgrade();
                if (cost > resident.getAccount().getHoldingBalance()) {
                    player.sendMessage(ChatColor.GOLD + "You do not have enough money you only have $" + ChatColor.YELLOW + String.format("%.2f", resident.getAccount().getHoldingBalance()));
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                Convertor nextCon = TownTiers.getInstance().getConvertorXP(convertor.getLevel() + 1);
                if (nextCon == null) {
                    player.sendMessage(ChatColor.GOLD + "Your convertor is maxed!");
                    player.playSound(player.getLocation(), TownTiers.getInstance().getUnableSound(), 2f, 1f);
                    return;
                }
                resident.getAccount().withdraw(convertor.getCostToUpgrade(), "Paid for a better convertor");
                TownTiers.getInstance().addConvertorLevelXP(nation);
                player.playSound(player.getLocation(), TownTiers.getInstance().getRewardSound(), 3f, 1f);
                player.sendMessage(ChatColor.GOLD + "Successfully Upgraded XP Generator!");
                GUImanager.constructXpConversionMenu(player, null, nation, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (TownTiers.getInstance().inCombat(event.getPlayer())) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "You cannot use this command in combat!");
            return;
        }
        String[] args = event.getMessage().split(" ");
        if (args.length > 2) {
            if (args[0].equalsIgnoreCase("/t") || args[0].equalsIgnoreCase("/town")) {
                if (args[1].equalsIgnoreCase("spawn")) {
                    int spawnNumber = 0;
                    try {
                        spawnNumber = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        return;
                    }
                    int maxSpawnNumber = TownTiers.getInstance().getHighestTownTier().getExtraTownSpawns();
                    if (spawnNumber > 1 && spawnNumber < (2 + maxSpawnNumber)) {
                        Town town = TownyUtils.getTownFromPlayer(event.getPlayer());
                        Resident resident = TownyUniverse.getInstance().getResident(event.getPlayer().getName());
                        if (town == null || resident == null) {
                            event.getPlayer().sendMessage(ChatColor.GOLD + "You must be in a town to do this! Use " + ChatColor.YELLOW + "/t new [town]");
                            event.setCancelled(true);
                            return;
                        }
                        int extraTownSpawns = TownTiers.getInstance().getTierFromTown(town).getExtraTownSpawns();
                        if (extraTownSpawns > 0) {
                            if (spawnNumber - 1 <= extraTownSpawns) {
                                TownSpawn spawns = TownTiers.getInstance().getTownSpawns().get(town);
                                if (spawns == null) {
                                    event.getPlayer().sendMessage(ChatColor.GOLD + "You have not set this spawn. Use" + ChatColor.YELLOW + " /t set spawn " + spawnNumber);
                                    event.setCancelled(true);
                                    return;
                                } else {
                                    Location spawn = spawns.getSpawns().get(spawnNumber);
                                    if (spawn == null) {
                                        event.setCancelled(true);
                                        event.getPlayer().sendMessage(ChatColor.GOLD + "You have not set this spawn. Use" + ChatColor.YELLOW + " /t set spawn " + spawnNumber);
                                        return;
                                    }
                                    if (!TownyUtils.locationInPlayersTown(event.getPlayer(), spawn)) {
                                        event.getPlayer().sendMessage(ChatColor.GOLD + "Location is not in your town!");
                                        event.setCancelled(true);
                                        return;
                                    }
                                    event.getPlayer().teleport(spawn, PlayerTeleportEvent.TeleportCause.COMMAND);
                                    event.getPlayer().sendMessage(ChatColor.GRAY + "Successfully Teleported to /t spawn 2 - Use /t set spawn 2,3,4 etc for more");
                                    event.setCancelled(true);
                                    return;
                                }
                            } else {
                                event.getPlayer().sendMessage(ChatColor.GOLD + "You only have " + ChatColor.YELLOW + extraTownSpawns + ChatColor.GOLD + " extra town spawns. Use" + ChatColor.YELLOW + "/tiers town");
                                event.setCancelled(true);
                                return;
                            }
                        } else {
                            event.getPlayer().sendMessage(ChatColor.GOLD + "You have not unlocked Multiple Town Spawns - Use " + ChatColor.YELLOW + "/tiers town");
                            event.setCancelled(true);
                            return;
                        }
                    } else if (spawnNumber == 1) {
                        event.getPlayer().sendMessage(ChatColor.GOLD + "Use " + ChatColor.YELLOW + "/t spawn " + ChatColor.GOLD + "for your normal t spawn");
                        event.setCancelled(true);
                        return;
                    }


                } else if (args[1].equalsIgnoreCase("set")) {
                    if (args[2].equalsIgnoreCase("spawn")) {
                        if (args.length > 3) {
                            Town town = TownyUtils.getTownFromPlayer(event.getPlayer());
                            Resident resident = TownyUniverse.getInstance().getResident(event.getPlayer().getName());
                            if (town == null || resident == null) {
                                event.getPlayer().sendMessage(ChatColor.GOLD + "You must be in a town to do this! Use " + ChatColor.YELLOW + "/t new [town]");
                                event.setCancelled(true);
                                return;
                            }

                            int spawnNumber = 0;
                            try {
                                spawnNumber = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                return;
                            }

                            int extraTownSpawns = TownTiers.getInstance().getTierFromTown(town).getExtraTownSpawns();
                            int maxSpawnNumber = TownTiers.getInstance().getHighestTownTier().getExtraTownSpawns();
                            if (spawnNumber > 1 && spawnNumber < (2 + maxSpawnNumber)) {
                                if (permSource.testPermission(event.getPlayer(), PermissionNodes.TOWNY_COMMAND_TOWN_SET_SPAWN.getNode())) {
                                    if (extraTownSpawns > 0) {
                                        if (spawnNumber - 1 <= extraTownSpawns) {
                                            Location location = event.getPlayer().getLocation();
                                            if (!TownyUtils.playerInOwnTown(event.getPlayer())) {
                                                event.setCancelled(true);
                                                event.getPlayer().sendMessage(ChatColor.GOLD + "You must be in your own town to do this!");
                                                return;
                                            }
                                            TownSpawn spawns = TownTiers.getInstance().getTownSpawns().get(town);
                                            if (spawns == null) {
                                                spawns = new TownSpawn(town);
                                                TownTiers.getInstance().getTownSpawns().put(town, spawns);
                                            }
                                            if (spawns.getSpawns().containsKey(spawnNumber)) {
                                                spawns.getSpawns().replace(spawnNumber, location);
                                            } else {
                                                spawns.getSpawns().put(spawnNumber, location);
                                            }
                                            event.setCancelled(true);
                                            event.getPlayer().sendMessage(ChatColor.GOLD + "Successfully Set Town Spawn " + ChatColor.YELLOW + spawnNumber);
                                        } else {
                                            event.getPlayer().sendMessage(ChatColor.GOLD + "You only have " + ChatColor.YELLOW + extraTownSpawns + ChatColor.GOLD + " extra town spawns. Use" + ChatColor.YELLOW + "/tiers town");
                                            event.setCancelled(true);
                                            return;
                                        }
                                    } else {
                                        event.getPlayer().sendMessage(ChatColor.GOLD + "You have not unlocked Multiple Town Spawns - Use " + ChatColor.YELLOW + "/tiers town");
                                        event.setCancelled(true);
                                        return;
                                    }
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.GOLD + "You must be mayor to do this!");
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("/n") || args[0].equalsIgnoreCase("/nation")) {
                if (args[1].equalsIgnoreCase("set")) {
                    if (args[2].equalsIgnoreCase("rc") || args[2].equalsIgnoreCase("regionalcapital")) {
                        StringBuilder newCommand = new StringBuilder("/rc ");
                        for (int i = 1; i < args.length; i++) {
                            newCommand.append(args[i]);
                            newCommand.append(" ");
                        }
                        System.out.println("NEW COMMAND: " + newCommand.toString());
                        event.getPlayer().chat(newCommand.toString());
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClaims(PlayerEntersIntoTownBorderEvent event) {
        Town town = TownyUtils.getTownFromPlayer(event.getPlayer());
        Player player = event.getPlayer();
        if (town == null) {
            return;
        }
        if (event.getEnteredTown().equals(town)) {
            TownTier tier = TownTiers.getInstance().getTierFromTown(town);
            int haste = Math.min(tier.getHasteInClaims(), TownyUtils.getMetaDataFromTown(town, Settings.hasteInClaims));
            if (haste < 0) {
                haste = tier.getHasteInClaims();
            }
            int speed = Math.min(tier.getSpeedInClaims(), TownyUtils.getMetaDataFromTown(town, Settings.speedInClaims));
            if (speed < 0) {
                speed = tier.getSpeedInClaims();
            }

            //player has greater speed and enters - mark for lesser speed later if in claims
            //player drinks greater speed in claims - mark for lesser speed later if in claims
            //player doesnt have greater speed give immediantly
            //town levels up
            //town levels down
            handlePotionEffects(player, speed, haste);
        }
    }

    @EventHandler
    public void onTownLeave(PlayerExitsFromTownBorderEvent event) {
        Town town = TownyUtils.getTownFromPlayer(event.getPlayer());
        Player player = event.getPlayer();
        if (town == null) {
            return;
        }
        if (event.getLeftTown().equals(town)) {
            handlePotionEffectRemoval(player);
        }
    }

    @EventHandler
    public void onTown(TownRemoveResidentEvent event) {
        if (event.getResident().getPlayer() != null) {
            handlePotionEffectRemoval(event.getResident().getPlayer());
        }
    }

    @EventHandler
    public void onPotionEnd(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getCause().equals(EntityPotionEffectEvent.Cause.EXPIRATION)) {
                if (event.getOldEffect() != null) {
                    if (event.getOldEffect().getType().equals(PotionEffectType.SPEED)) {
                        if (player.getPersistentDataContainer().has(giveSpeedKey, PersistentDataType.INTEGER)) {
                            if (TownyUtils.playerInOwnTown(player)) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000, player.getPersistentDataContainer().get(giveSpeedKey, PersistentDataType.INTEGER)));
                                player.getPersistentDataContainer().set(speedReasonKey, PersistentDataType.INTEGER, 0);
                            }
                            player.getPersistentDataContainer().remove(giveSpeedKey);
                        }
                    } else if (event.getOldEffect().getType().equals(PotionEffectType.FAST_DIGGING)) {
                        if (player.getPersistentDataContainer().has(giveHasteKey, PersistentDataType.INTEGER)) {
                            if (TownyUtils.playerInOwnTown(player)) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 100000, player.getPersistentDataContainer().get(giveHasteKey, PersistentDataType.INTEGER)));
                                player.getPersistentDataContainer().set(hasteReasonKey, PersistentDataType.INTEGER, 0);
                            }
                            player.getPersistentDataContainer().remove(giveHasteKey);
                        }
                    }
                }
            } else {
                if (event.getNewEffect() != null) {
                    if (event.getNewEffect().getType().equals(PotionEffectType.SPEED) && event.getNewEffect().getDuration() < 100000 && player.getPersistentDataContainer().has(speedReasonKey, PersistentDataType.INTEGER)) {
                        player.getPersistentDataContainer().remove(speedReasonKey);
                        player.getPersistentDataContainer().set(giveSpeedKey, PersistentDataType.INTEGER, 1);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                // What you want to schedule goes here
                                player.removePotionEffect(PotionEffectType.SPEED);
                                player.addPotionEffect(event.getNewEffect());
                            }

                        }.runTaskLater(TownTiers.getInstance(), 1L);
                    } else if (event.getNewEffect().getType().equals(PotionEffectType.FAST_DIGGING) && event.getNewEffect().getDuration() < 100000 && player.getPersistentDataContainer().has(hasteReasonKey, PersistentDataType.INTEGER)) {
                        player.getPersistentDataContainer().remove(hasteReasonKey);
                        player.getPersistentDataContainer().set(giveHasteKey, PersistentDataType.INTEGER, 1);
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                // What you want to schedule goes here
                                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                                player.addPotionEffect(event.getNewEffect());
                            }

                        }.runTaskLater(TownTiers.getInstance(), 1L);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPearl(PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) {
            Town town = TownyUtils.getTownFromPlayer(event.getPlayer());
            if (town == null) {
                return;
            }
            TownTier tier = TownTiers.getInstance().getTierFromTown(town);
            if (!tier.isPearlsLost()) {
                if (Dependency.SIEGEWAR.isActive()) {
                    if (SiegeController.hasActiveSiege(town)) {
                        if (!event.getPlayer().getPersistentDataContainer().has(enderPearlTipKey, PersistentDataType.INTEGER)) {
                            event.getPlayer().getPersistentDataContainer().set(enderPearlTipKey, PersistentDataType.INTEGER, 0);
                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e[Tip] &6Enderpearl Claims Perk only works when your town is not under siege!"));
                        }
                        return;
                    }
                }
                boolean currentPearls = !town.hasMeta(Settings.pearlClaims.getKey());
                if (currentPearls) {
                    if (TownyUtils.locationInPlayersTown(event.getPlayer(), event.getTo())) {
                        event.getPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                        new BukkitRunnable() {

                            @Override
                            public void run() {
                                if (!Dependency.COMBATPLUS.isActive())
                                    return;

                                EnderpearlCooldown cooldown = (EnderpearlCooldown) CombatPlus.getInstance().getModule(EnderpearlCooldown.class);
                                System.out.println("Set Cooldown to 0");
                                cooldown.setCooldown(event.getPlayer().getUniqueId(),0);
                            }
                        }.runTaskLater(TownTiers.getInstance(), 1L);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getFoodLevel() >= 0) {
                return;
            }
            Player player = (Player) event.getEntity();
            Town town = TownyUtils.getTownFromPlayer(player);
            if (town == null) {
                return;
            }
            if (!TownTiers.getInstance().getTierFromTown(town).isHungerInClaims()) {
                boolean currentHunger = !town.hasMeta(Settings.hungerClaims.getKey());
                if (currentHunger) {
                    if (Dependency.SIEGEWAR.isActive()) {
                        if (SiegeController.hasActiveSiege(town)) {
                            return;
                        }
                    }
                    if (TownyUtils.playerInOwnTown(player)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                Player player = (Player) event.getEntity();
                Town town = TownyUtils.getTownFromPlayer(player);
                if (town == null) {
                    return;
                }
                if (!TownTiers.getInstance().getTierFromTown(town).isFallDamageInClaims()) {
                    if (Dependency.SIEGEWAR.isActive()) {
                        if (SiegeController.hasActiveSiege(town)) {
                            if (!player.getPersistentDataContainer().has(fallTipKey, PersistentDataType.INTEGER)) {
                                player.sendMessage(ChatColor.YELLOW + "[Tip] " + ChatColor.GOLD + "You can take fall damage in your claims if it is under siege!");
                                player.getPersistentDataContainer().set(fallTipKey, PersistentDataType.INTEGER, 1);
                            }
                            return;
                        }
                    }
                    boolean currentFall = !town.hasMeta(Settings.noFallClaims.getKey());
                    if (currentFall) {
                        if (TownyUtils.playerInOwnTown(player)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    /*
    public void onTeleport(TownSpawnEvent event) {
        Town town = event.getToTown();
        Resident resident = TownyUniverse.getInstance().getResident(event.getPlayer().getName());
        Town playerTown = TownyUtils.getTownFromPlayer(event.getPlayer());
        int regionalCapital = TownyUtils.getMetaDataFromTown(town, TownTierData.getRegionalCapitalField());
        Nation nation = TownyUtils.getNationFromPlayer(event.getPlayer());
        if (nation == null || playerTown == null || resident == null) {
            return;
        }
        if (permSource.testPermission(event.getPlayer(), PermissionNodes.TOWNY_COMMAND_TOWNYADMIN.getNode())) {
            return;
        }
        NationTier tier = TownTiers.getInstance().getTierFromNation(nation);
        if (regionalCapital <= tier.getRegionalCapitals() && regionalCapital > 0) {
            //stuff in here
        } else {
            if (!town.equals(playerTown)) {
                if (tier.getRegionalCapitals() <= 0) {
                    event.setCancelMessage(ChatColor.RED + "Town Spawn Forbidden - Nation has not unlocked regional capitals - /tiers nation");
                }
                ArrayList<Town> regionalCapitals = new ArrayList<>();
                for (Town nt : nation.getTowns()) {
                    if (nt.hasMeta(TownTierData.getRegionalCapitalField().getKey())) {
                        regionalCapitals.add(nt);
                    }
                }
                StringBuilder rcList = new StringBuilder();
                if (regionalCapitals.size() == 0) {
                    event.setCancelMessage(ChatColor.RED + "You may only teleport to regional capitals in your nation! - None Set(/n set rc)");
                } else {
                    for (int i = 0; i < regionalCapitals.size(); i++) {
                        rcList.append(regionalCapitals.get(i));
                        if (i < regionalCapitals.size() - 1) {
                            rcList.append(",");
                        }
                    }
                    event.setCancelMessage(ChatColor.RED + "You may only teleport to regional capitals in your nation! - Regional Capitals [" + ChatColor.YELLOW + rcList.toString() + ChatColor.RED + "]");
                }
            }
        }
    }

     */

    @EventHandler
    public void onTownDelete(PreDeleteTownEvent event) {
        for (TownTierData townTierData : TownTiers.getInstance().getTownData()) {
            if (townTierData.isTown() && !townTierData.isLegacy()) {
                if (townTierData.getUniqueID().equals(event.getTown().getUUID())) {
                    townTierData.setLegacy(true);
                    townTierData.setMayor(event.getTown().getMayor().getUUID());
                    townTierData.setXP(Math.max(townTierData.getXP(), 0));
                    townTierData.setTier(Math.max(townTierData.getTier(), 0));
                    int maxPop = Math.max(1, TownyUtils.getMetaDataFromTown(event.getTown(), TownTierData.getMostAmountResidentsField()));
                    townTierData.setMaxPop(maxPop);
                }
            }
        }
    }

    @EventHandler
    public void onTownDelete(PreDeleteNationEvent event) {
        for (TownTierData townTierData : TownTiers.getInstance().getTownData()) {
            if (!townTierData.isTown() && !townTierData.isLegacy()) {
                if (townTierData.getUniqueID().equals(event.getNation().getUUID())) {
                    townTierData.setLegacy(true);
                    townTierData.setMayor(event.getNation().getKing().getUUID());
                    int maxPop = Math.max(1, TownyUtils.getMetaDataFromNation(event.getNation(), TownTierData.getMostAmountResidentsField()));
                    townTierData.setMaxPop(maxPop);
                }
            }
        }
    }

    @EventHandler
    public void onResident(TownAddResidentEvent event) {
        int prevMax = TownyUtils.getMetaDataFromTown(event.getTown(), TownTierData.getMostAmountResidentsField());
        if (event.getTown().getResidents().size() > prevMax) {
            TownyUtils.updateTownMetaData(event.getTown(), event.getTown().getResidents().size(), TownTierData.getMostAmountResidentsField());
        }
        if (event.getTown().hasNation()) {
            Nation nation = null;
            try {
                nation = event.getTown().getNation();
                int nPrevMax = TownyUtils.getMetaDataFromNation(nation, TownTierData.getMostAmountResidentsField());
                if (nation.getResidents().size() > nPrevMax) {
                    TownyUtils.updateNationMetaData(nation, nation.getResidents().size(), TownTierData.getMostAmountResidentsField());
                }
            } catch (NotRegisteredException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onTown(NationAddTownEvent event) {
        int prevMax = TownyUtils.getMetaDataFromNation(event.getNation(), TownTierData.getMostAmountResidentsField());
        if (prevMax > event.getNation().getResidents().size()) {
            TownyUtils.updateNationMetaData(event.getNation(), event.getNation().getResidents().size(), TownTierData.getMostAmountResidentsField());
        }
    }


    public static void handlePotionEffects(Player player, int speed, int haste) {
        if (speed > 0) {
            int currentSpeed = -5000;
            PotionEffect playerSpeed = player.getPotionEffect(PotionEffectType.SPEED);
            if (playerSpeed != null) {
                currentSpeed = playerSpeed.getAmplifier();
            }
            if (currentSpeed > -1000) {
                player.getPersistentDataContainer().set(giveSpeedKey, PersistentDataType.INTEGER, haste - 1);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, speed - 1));
                player.getPersistentDataContainer().set(speedReasonKey, PersistentDataType.INTEGER, speed - 1);
            }
        }
        if (haste > 0) {
            int currentSpeed = -5000;
            PotionEffect playerSpeed = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
            if (playerSpeed != null) {
                currentSpeed = playerSpeed.getAmplifier();
            }
            if (currentSpeed > -1000) {
                player.getPersistentDataContainer().set(giveHasteKey, PersistentDataType.INTEGER, haste - 1);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1000000, haste - 1));
                player.getPersistentDataContainer().set(hasteReasonKey, PersistentDataType.INTEGER, 0);
            }
        }
    }

    public static void handlePotionEffectRemoval(Player player) {
        PotionEffect currentSpeed = player.getPotionEffect(PotionEffectType.SPEED);
        PotionEffect currentHaste = player.getPotionEffect(PotionEffectType.FAST_DIGGING);
        if (currentHaste != null) {
            if (player.getPersistentDataContainer().has(hasteReasonKey, PersistentDataType.INTEGER)) {
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.getPersistentDataContainer().remove(hasteReasonKey);
            }
        }
        if (currentSpeed != null) {
            if (player.getPersistentDataContainer().has(speedReasonKey, PersistentDataType.INTEGER)) {
                player.removePotionEffect(PotionEffectType.SPEED);
                player.getPersistentDataContainer().remove(speedReasonKey);
            }
        }
    }

    @EventHandler
    public void newTown(NewTownEvent event) {
        TownTiers.getInstance().addTierDataNation(0);
        TownTiers.getInstance().addGeneratorData(1);
    }

    @EventHandler
    public void newNation(NewNationEvent event) {
        TownTiers.getInstance().addTierDataTown(0);
        TownTiers.getInstance().addGeneratorData(1);
    }

    @EventHandler
    public void onVehicle(VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            Player player = (Player) event.getEntered();
            handlePotionEffectRemoval(player);
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onTownScreen(TownStatusScreenEvent event) {
        TownTier tier = TownTiers.getInstance().getTierFromTown(event.getTown());
        String tierProgress = null;
        if(TownTiers.getInstance().getTownTiers().size() == tier.getTier()-1) {
            tierProgress = ChatColor.GREEN + "Max Tier";
        } else {
            tierProgress = ChatColor.DARK_GREEN+"| Town Points: "
                    +ChatColor.GREEN+TownTiers.getInstance().getTownXP(event.getTown())+"/"+TownTiers.getInstance().getXpToNextLevel(tier.getTier(), true);
        }
        event.addLine(ChatColor.DARK_GREEN+"Tier: "+ChatColor.GREEN+tier.getTier() + " "+tierProgress);
        event.addLine(ChatColor.DARK_GREEN+" > Use "+ChatColor.GREEN+" /tiers town "+ChatColor.DARK_GREEN+"to level up your town and earn amazing rewards!");
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onTownScreen(NationStatusScreenEvent event) {
        NationTier tier = TownTiers.getInstance().getTierFromNation(event.getNation());
        String tierProgress = null;
        if(TownTiers.getInstance().getTownTiers().size() == tier.getTier()-1) {
            tierProgress = ChatColor.GREEN + "Max Tier";
        } else {
            tierProgress = ChatColor.DARK_GREEN+"| Town Points: "
                    +ChatColor.GREEN+TownTiers.getInstance().getNationXP(event.getNation())+"/"+TownTiers.getInstance().getXpToNextLevel(tier.getTier(), false);
        }
        event.addLine(ChatColor.DARK_GREEN+"Tier: "+ChatColor.GREEN+tier.getTier() + " "+tierProgress);
        event.addLine(ChatColor.DARK_GREEN+" > Use "+ChatColor.GREEN+" /tiers nation "+ChatColor.DARK_GREEN+"to level up your nation and earn amazing rewards!");
    }
}
