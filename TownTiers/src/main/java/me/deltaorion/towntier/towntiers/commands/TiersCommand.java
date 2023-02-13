package me.deltaorion.towntier.towntiers.commands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.towntier.towntiers.GUImanager;
import me.deltaorion.towntier.towntiers.TownTiers;
import me.deltaorion.towntier.towntiers.data.PlayerData;
import me.deltaorion.towntier.towntiers.data.TownTierData;
import me.deltaorion.towntier.towntiers.townyutils.TownyUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TiersCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length>0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission(TownTiers.getInstance().getOperatorNode())) {
                    TownTiers.getInstance().reloadConfig();
                    TownTiers.getInstance().loadConfig(true);
                    sender.sendMessage("Successfully Reloaded Config");
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    //tiers reset town <name>
                    if (args.length > 2) {
                        if (args[1].equalsIgnoreCase("town")) {
                            Town town = null;
                            try {
                                town = TownyAPI.getInstance().getDataSource().getTown(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Town not found");
                                return true;
                            }
                            TownTiers.getInstance().resetXPTown(town);
                            sender.sendMessage("Successfully Reset XP from " + town.getName());
                        } else if (args[1].equalsIgnoreCase("nation")) {
                            Nation nation = null;
                            try {
                                nation = TownyAPI.getInstance().getDataSource().getNation(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Nation not found");
                                return true;
                            }
                            TownTiers.getInstance().resetXPNation(nation);
                            sender.sendMessage("Successfully Reset XP from " + nation.getName());
                        } else {
                            sender.sendMessage("/tiers reset [town/nation] [town-name/nation-name]");
                            return true;
                        }
                    } else {
                        sender.sendMessage("/tiers reset [town/nation] [town-name/nation-name]");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("settier")) {
                if (sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    //tiers reset town <name>
                    if (args.length > 3) {
                        int tier = 0;
                        try {
                            tier = Math.abs(Integer.parseInt(args[3]));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Expected an Integer");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("town")) {
                            Town town = null;
                            try {
                                town = TownyAPI.getInstance().getDataSource().getTown(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Town not found");
                                return true;
                            }
                            TownTiers.getInstance().setTierTown(town, tier);
                            sender.sendMessage("Successfully Set Tier from " + town.getName() + " to tier " + tier);
                            return true;
                        } else if (args[1].equalsIgnoreCase("nation")) {
                            Nation nation = null;
                            try {
                                nation = TownyAPI.getInstance().getDataSource().getNation(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Nation not found");
                                return true;
                            }
                            TownTiers.getInstance().setTierNation(nation, tier);
                            sender.sendMessage("Successfully Set Tier from " + nation.getName() + " to tier " + tier);
                        } else {
                            sender.sendMessage("/tiers set-tier [town/nation] [town-name/nation-name] <tier>");
                            return true;
                        }
                    } else {
                        sender.sendMessage("/tiers set-tier [town/nation] [town-name/nation-name] <tier>");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("setxp")) {
                if (sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    //tiers reset town <name>
                    if (args.length > 3) {
                        int xp = 0;
                        try {
                            xp = Math.abs(Integer.parseInt(args[3]));
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Expected an Integer");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("town")) {
                            Town town = null;
                            try {
                                town = TownyAPI.getInstance().getDataSource().getTown(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Town not found");
                                return true;
                            }
                            TownTiers.getInstance().setXPTown(town, xp);
                            sender.sendMessage("Successfully Set xp of " + town.getName() + " to " + xp);
                            return true;
                        } else if (args[1].equalsIgnoreCase("nation")) {
                            Nation nation = null;
                            try {
                                nation = TownyAPI.getInstance().getDataSource().getNation(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Nation not found");
                                return true;
                            }
                            TownTiers.getInstance().setXPNation(nation, xp);
                            sender.sendMessage("Successfully Set XP of " + nation.getName() + " to " + xp);
                        } else {
                            sender.sendMessage("/tiers set-xp [town/nation] [town-name/nation-name] <tier>");
                            return true;
                        }
                    } else {
                        sender.sendMessage("/tiers set-xp [town/nation] [town-name/nation-name] <tier>");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("getxp")) {
                if (args.length > 2) {
                    if (args[1].equalsIgnoreCase("town")) {
                        Town town = null;
                        try {
                            town = TownyAPI.getInstance().getDataSource().getTown(args[2]);
                        } catch (NotRegisteredException e) {
                            sender.sendMessage("Town not found");
                            return true;
                        }
                        TownTiers.getInstance().printXpTown(town, sender);
                        return true;
                    } else if (args[1].equalsIgnoreCase("nation")) {
                        Nation nation = null;
                        try {
                            nation = TownyAPI.getInstance().getDataSource().getNation(args[2]);
                        } catch (NotRegisteredException e) {
                            sender.sendMessage("Nation not found");
                            return true;
                        }
                        TownTiers.getInstance().printXpNation(nation, sender);
                        return true;
                    } else {
                        sender.sendMessage("/tiers get-xp [town/nation] [town-name/nation-name]");
                        return true;
                    }
                } else {
                    sender.sendMessage("/tiers get-xp [town/nation] [town-name/nation-name]");
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("addxp")) {
                if (sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    //tiers reset town <name>
                    if (args.length > 3) {
                        int xp = 0;
                        try {
                            xp = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Expected an Integer");
                            return true;
                        }
                        if (args[1].equalsIgnoreCase("town")) {
                            Town town = null;
                            try {
                                town = TownyAPI.getInstance().getDataSource().getTown(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Town not found");
                                return true;
                            }
                            TownTiers.getInstance().addXPTown(town, xp);
                            sender.sendMessage("Successfully added " + xp + " xp to " + town.getName());
                            return true;
                        } else if (args[1].equalsIgnoreCase("nation")) {
                            Nation nation = null;
                            try {
                                nation = TownyAPI.getInstance().getDataSource().getNation(args[2]);
                            } catch (NotRegisteredException e) {
                                sender.sendMessage("Nation not found");
                                return true;
                            }
                            TownTiers.getInstance().addXPNation(nation, xp);
                            sender.sendMessage("Successfully added " + xp + " xp to " + nation.getName());
                        } else {
                            sender.sendMessage("/tiers add-xp [town/nation] [town-name/nation-name] <xp>");
                            return true;
                        }
                    } else {
                        sender.sendMessage("/tiers add-xp [town/nation] [town-name/nation-name] <xp>");
                        return true;
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("town")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Town town = TownyUtils.getTownFromPlayer(player);
                    if(town==null) {
                        sender.sendMessage(ChatColor.GOLD+"You must be in a town to view town tiers. Create a town using "+ChatColor.YELLOW+"/t new [name]");
                        return true;
                    }
                    GUImanager.constructMainGUI(player,town,null,true);
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("nation")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Nation nation = TownyUtils.getNationFromPlayer(player);
                    if (nation == null) {
                        sender.sendMessage(ChatColor.GOLD+"You must be in a nation to view national tiers!");
                        return true;
                    }
                    GUImanager.constructMainGUI(player,null,nation,false);
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("convert")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length > 3) {
                        int amount = 0;
                        try {
                            amount = Integer.parseInt(args[3]);
                            amount = Math.abs(amount);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED+"Expected Integer");
                            sender.sendMessage(ChatColor.GOLD + "/tiers convert " + ChatColor.YELLOW + "[money/xp] [town/nation] [amount]");
                            return true;
                        }
                        if (args[2].equalsIgnoreCase("town")) {
                            Town town = TownyUtils.getTownFromPlayer(player);
                            if (town == null) {
                                sender.sendMessage(ChatColor.GOLD + "You must be in a town to view town tiers. Create a town using " + ChatColor.YELLOW + "/t new [name]");
                                return true;
                            }
                            if(args[1].equalsIgnoreCase("xp")) {
                                TownTiers.getInstance().tradeXP(player,town,amount);
                            } else if(args[1].equalsIgnoreCase("money")) {
                                TownTiers.getInstance().tradeMoney(player,town,amount);
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "/tiers convert " + ChatColor.YELLOW + "[money/xp] [town/nation] [amount]");
                            }
                        } else if (args[2].equalsIgnoreCase("nation")) {
                            Nation nation = TownyUtils.getNationFromPlayer(player);
                            if (nation == null) {
                                sender.sendMessage(ChatColor.GOLD+"You must be in a nation to view national tiers!");
                                return true;
                            }
                            if(args[1].equalsIgnoreCase("xp")) {
                                TownTiers.getInstance().tradeXP(player,nation,amount);
                            } else if(args[1].equalsIgnoreCase("money")) {
                                TownTiers.getInstance().tradeMoney(player,nation,amount);
                            } else {
                                sender.sendMessage(ChatColor.GOLD + "/tiers convert " + ChatColor.YELLOW + "[money/xp] [town/nation] [amount]");
                            }
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "/tiers convert " + ChatColor.YELLOW + "[money/xp] [town/nation] [amount]");
                        }
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "/tiers convert " + ChatColor.YELLOW + "[money/xp] [town/nation] [amount]");
                    }
                }
            } else if(args[0].equalsIgnoreCase("admin")) {
                if(sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    printAdminMenu(sender);
                } else {
                    sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
                }
            } else if(args[0].equalsIgnoreCase("rc")) {
                if(sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    if(args.length>2) {
                        if(args[1].equalsIgnoreCase("remove")) {
                            Town town = TownyUniverse.getInstance().getTown(args[2]);
                            if(town==null) {
                                sender.sendMessage(ChatColor.GOLD+"Unknown Town");
                                sender.sendMessage(ChatColor.GOLD+"Unknown Town");
                                return true;
                            }
                            TownyUtils.removeMetaDataFromTown(town, TownTierData.getRegionalCapitalField());
                        }else {
                            sender.sendMessage(ChatColor.GOLD+"/tiers rc remove "+ChatColor.YELLOW+"[town]");
                        }
                    } else {
                        sender.sendMessage(ChatColor.GOLD+"/tiers rc remove "+ChatColor.YELLOW+"[town]");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"You do not have permission to use this command!");
                }
            } else if (args[0].equalsIgnoreCase("top")) {
                long now = System.currentTimeMillis();
                if(sender instanceof Player) {
                    PlayerData p = TownTiers.getInstance().getPlayerData((Player) sender);
                    if (now - p.getLastAction() < 2000) {
                        sender.sendMessage("Please wait "+TownTiers.getInstance().formatTime(2000-(now-p.getLastAction())));
                        return true;
                    }
                    p.setLastAction(now);
                }
                int page = 0;
                int maxpage = TownTiers.getInstance().getMaxTownPages();
                if (args.length > 2) {
                    try {
                        page = Integer.parseInt(args[2]);
                        page = Math.max(0, page-1);
                        page = Math.min(page, maxpage);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Expected Number");
                        sender.sendMessage(ChatColor.GOLD + "/tiers top " + ChatColor.YELLOW + "[town/nation] <page>");
                        return true;
                    }
                }
                if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("town")) {
                        TownTiers.getInstance().printTopTowns(sender, page);
                    } else if (args[1].equalsIgnoreCase("nation")) {
                        TownTiers.getInstance().printTopNations(sender,page);
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD + "/tiers top " + ChatColor.YELLOW + "[town/nation] <page>");
                }
            } else if(args[0].equalsIgnoreCase("generatoradmin")) {
                if(args.length>4) {
                    int gen = 0;
                    try {
                        gen = Integer.parseInt(args[4]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED+"Expected Integer");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("town")) {
                        Town town  = TownyUniverse.getInstance().getTown(args[2]);
                        if(town==null) {
                            sender.sendMessage(ChatColor.RED+"Unknown Town!");
                            return true;
                        }
                        try {
                            int tier = Integer.parseInt(args[3]);
                            tier = Math.min(TownTiers.getInstance().highestTownGen(),tier);
                            TownTiers.getInstance().setGeneratorLevel(town,tier,gen);
                            sender.sendMessage(ChatColor.GOLD+"Successfully Changed Town Generator Level");
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED+"Expected Integer");
                            sender.sendMessage(ChatColor.GOLD+"/tiers generatoradmin "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name] <level>");
                            return true;
                        }
                    } else if (args[1].equalsIgnoreCase("nation")) {
                        Nation nation = TownyUniverse.getInstance().getNation(args[2]);
                        if(nation==null) {
                            sender.sendMessage(ChatColor.RED+"Unknown Nation!");
                            return true;
                        }
                        try {
                            int tier = Integer.parseInt(args[3]);
                            tier = Math.min(TownTiers.getInstance().highestNationGen(),tier);
                            TownTiers.getInstance().setGeneratorLevel(nation,tier,gen);
                            sender.sendMessage(ChatColor.GOLD+"Successfully Changed Nation Generator Level");
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED+"Expected Integer");
                            sender.sendMessage(ChatColor.GOLD+"/tiers generatoradmin "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name] <level>");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.GOLD+"/tiers generatoradmin "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name] <level>");
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD+"/tiers generatoradmin "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name] <level>");
                }
            } else if(args[0].equalsIgnoreCase("data")) {
                if(args.length>1) {
                    if (sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                        if (args[1].equalsIgnoreCase("town")) {
                            TownTiers.getInstance().printTownData(sender);
                        } else if (args[1].equalsIgnoreCase("nation")) {
                            TownTiers.getInstance().printNationData(sender);
                        } else {
                            sender.sendMessage(ChatColor.GOLD + "/tiers data" + ChatColor.YELLOW + "[town/nation]");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD+"/tiers data"+ChatColor.YELLOW+"[town/nation]");
                }
            } else if(args[0].equalsIgnoreCase("resetconverter")) {
                if(args.length>2) {
                    if (args[1].equalsIgnoreCase("town")) {
                        Town town = TownyUniverse.getInstance().getTown(args[2]);
                        if (town == null) {
                            sender.sendMessage(ChatColor.RED + "Could not find town");
                            return true;
                        }
                        TownTiers.getInstance().resetConvertor(town);
                        sender.sendMessage("Successfully Reset Convertor of "+town.getName());
                    } else if (args[1].equalsIgnoreCase("nation")) {
                        Nation nation = TownyUniverse.getInstance().getNation(args[2]);
                        if(nation==null) {
                            sender.sendMessage(ChatColor.RED+"Could not find nation");
                            return true;
                        }
                        TownTiers.getInstance().resetConvertor(nation);
                        sender.sendMessage("Successfully Reset Convertor of "+nation.getName());
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "/tiers resetconvertor " + ChatColor.YELLOW + " [town/nation] [town-name/nation-name]");
                    }
                } else {
                    sender.sendMessage(ChatColor.GOLD+"/tiers resetconvertor "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name]");
                }
            } else if(args[0].equalsIgnoreCase("settings")) {
                if(sender instanceof Player) {
                    Player player = (Player) sender;
                    Resident resident = TownyUniverse.getInstance().getResident(player.getName());
                    Town town = TownyUtils.getTownFromPlayer(player);
                    if(town==null || resident==null) {
                        player.sendMessage(ChatColor.RED+"You must have a town to do this!");
                        return true;
                    }
                    if (!town.getMayor().equals(resident)) {
                        player.sendMessage(ChatColor.RED+"You must be the mayor to do this!");
                        return true;
                    }
                    GUImanager.constructSettingsGUI(player,town);
                }
            } else if(args[0].equalsIgnoreCase("generatordata")) {
                if(sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                    TownTiers.getInstance().printGeneratorData(sender);
                } else {
                    sender.sendMessage(ChatColor.RED+"You do not have permission to use this command");
                }
            }
            else if(args[0].equalsIgnoreCase("help")) {
                printHelpMenu(sender);
            } else {
                sender.sendMessage(ChatColor.RED+"Unknown Command");
                printHelpMenu(sender);
            }
        } else {
            printHelpMenu(sender);
        }
        return true;
    }
    public void printHelpMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW+"oO---"+ChatColor.GREEN+"Towny Tiers"+ChatColor.YELLOW+"---Oo");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD+"Detailed Information can be found at ");
        sender.sendMessage(ChatColor.GOLD+"/tiers town - " + ChatColor.WHITE+"Access Town Tiers");
        sender.sendMessage(ChatColor.GOLD+"/tiers nation - "+ChatColor.WHITE+"Access National Tiers");
        sender.sendMessage(ChatColor.GOLD+"/tiers settings - "+ChatColor.WHITE+"Toggle town tier settings");
        sender.sendMessage(ChatColor.GOLD+"/tiers top "+ChatColor.YELLOW+"[town/nation] <page> - "+ChatColor.WHITE+"View top towns and nations!");
        sender.sendMessage(ChatColor.GOLD+"/tiers convert "+ChatColor.YELLOW+"[money/xp] [town/nation] [amount] - "+ChatColor.WHITE+" turn money and gold into national/town xp");
        sender.sendMessage(ChatColor.GOLD+"/tiers help - "+ChatColor.WHITE+"Opens this menu");
        if(sender.hasPermission(TownTiers.getInstance().getAdminNode()))  {
            sender.sendMessage(ChatColor.GOLD+"/tiers admin - "+ChatColor.WHITE+"Help for Admin Commands");
        }
    }
    public void printAdminMenu(CommandSender sender) {
        sender.sendMessage(ChatColor.YELLOW+"oO---"+ChatColor.GREEN+"---Oo");
        sender.sendMessage(ChatColor.GOLD+"/tiers reset "+ChatColor.YELLOW+" [town/nation] [town/nation name]");
        sender.sendMessage(ChatColor.GOLD+"/tiers settier "+ChatColor.YELLOW+"[town/nation] [town/nation name] <tier>");
        sender.sendMessage(ChatColor.GOLD+"/tiers addxp "+ChatColor.YELLOW+"[town/nation] [town/nation name] <xp>");
        sender.sendMessage(ChatColor.GOLD+"/tiers setxp "+ChatColor.YELLOW+"[town/nation] [town/nation name] <xp>");
        sender.sendMessage(ChatColor.GOLD+"/tiers rc remove "+ChatColor.YELLOW+"[town]");
        sender.sendMessage(ChatColor.GOLD+"/tiers newyear");
        sender.sendMessage(ChatColor.GOLD+"/tiers generatoradmin "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name] <level> <gen>");
        sender.sendMessage(ChatColor.GOLD+"/tiers resetconverter "+ChatColor.YELLOW+" [town/nation] [town-name/nation-name]");
        sender.sendMessage(ChatColor.GOLD+"/tiers data "+ChatColor.YELLOW+"[town/nation]");
        sender.sendMessage(ChatColor.GOLD+"/tiers generatordata");
        if(sender.hasPermission(TownTiers.getInstance().getOperatorNode())) {
            sender.sendMessage(ChatColor.GOLD+"/tiers reload");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        ArrayList<String> completions = new ArrayList<>();
        if(args.length==1) {
            completions.add("town");
            completions.add("nation");
            completions.add("top");
            completions.add("help");
            completions.add("convert");
            completions.add("settings");
            if(sender.hasPermission(TownTiers.getInstance().getAdminNode())) {
                completions.add("admin");
            }
        } else if(args.length==2 && args[0].equalsIgnoreCase("convert")) {
            completions.add("money");
            completions.add("xp");
        } else if(args.length==3 && args[0].equalsIgnoreCase("convert")) {
            completions.add("town");
            completions.add("nation");
        } else if(args.length==2 && args[0].equalsIgnoreCase("top")) {
            completions.add("town");
            completions.add("nation");
        } else if(args.length==3 && args[0].equalsIgnoreCase("top") && args[1].equalsIgnoreCase("town")) {
            int max = TownTiers.getInstance().getMaxTownPages();
            for(int i=1;i<=max;i++) {
                completions.add(String.valueOf(i));
            }
        } else if(args.length==3 && args[0].equalsIgnoreCase("top") && args[1].equalsIgnoreCase("nation")) {
            int max = TownTiers.getInstance().getMaxNationPages();
            for(int i=1;i<=max;i++) {
                completions.add(String.valueOf(i));
            }
        }
        return completions;
    }
}
