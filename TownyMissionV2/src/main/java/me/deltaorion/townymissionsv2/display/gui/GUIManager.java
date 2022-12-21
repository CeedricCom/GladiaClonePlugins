package me.deltaorion.townymissionsv2.display.gui;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.Pane;
import com.github.stefvanschie.inventoryframework.pane.PatternPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Pattern;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import me.deltaorion.townymissionsv2.bearer.MissionBearer;
import me.deltaorion.townymissionsv2.command.CommandPermissions;
import me.deltaorion.townymissionsv2.display.MissionSound;
import me.deltaorion.townymissionsv2.mission.Mission;
import me.deltaorion.townymissionsv2.mission.MissionGoal;
import me.deltaorion.townymissionsv2.mission.goal.CollectiveGoal;
import me.deltaorion.townymissionsv2.mission.goal.ContributableGoal;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.util.DurationParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static me.deltaorion.townymissionsv2.display.gui.ItemUtils.*;
import static me.deltaorion.townymissionsv2.display.gui.UtilityButtons.*;


public class GUIManager {

    private final static Enchantment DUMMY_ENCHANT = Enchantment.DAMAGE_ARTHROPODS;

    public static ChestGui buildMissionDisplayGUI(MissionPlayer player, @Nullable Mission mission, MissionBearer bearer) {

        String title = mission == null ? "" : mission.getTitle();

        ChestGui gui = new ChestGui(6,title);
        Pattern pattern = new Pattern(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "100000001",
                "111111111");

        PatternPane background = new PatternPane(0,0,9,6, Pane.Priority.LOWEST,pattern);
        background.bindItem('1',new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        gui.addPane(background);

        StaticPane pane = new StaticPane(4,0,1,6, Pane.Priority.NORMAL);
        pane.addItem(new GuiItem(getSkullDisplayStack(player)),0,0);
        pane.addItem(getMissionDisplayButton(player,mission,pane,gui,bearer),0,2);
        pane.addItem(getCloseButton(),0,5);

        StaticPane hotbar = new StaticPane(2,5,5,1);
        hotbar.addItem(getViewAllMissionsButton(player,bearer),4,0);
        if(player.getPlayer().hasPermission(CommandPermissions.MAYOR.getPerm())) {
            if(mission!=null) {
                if(bearer.getPrimaryMission()!=mission) {
                    hotbar.addItem(getMakePrimaryButton(bearer,mission),0,0);
                }
            }
        }

        List<GuiItem> itemStacks = new ArrayList<>();
        if(mission!=null) {
            if(mission.getCurrentGoal()!=null) {
                if(mission.getCurrentGoal() instanceof CollectiveGoal) {
                    CollectiveGoal goal = (CollectiveGoal) mission.getCurrentGoal();
                    int left = getLeft(goal.getContributions().size());
                    int fill = getFill(goal.getContributions().size());
                    int len = getLength(goal.getContributions().size());
                    PaginatedPane contribution = new PaginatedPane(left,4,len,1);
                    int count = 1;
                    for(Map.Entry<UUID,Integer> entry : goal.getContributions().entrySet()) {
                        ItemStack itemStack = getSkull(entry.getKey());
                        int finalCount = count;
                        transformMeta(itemStack, meta -> {
                            OfflinePlayer p = Bukkit.getOfflinePlayer(entry.getKey());
                            if(p.getName()!=null) {
                                meta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+p.getName());
                                meta.setLore(ImmutableList.of("",
                                        ChatColor.YELLOW+"Rank: "+ChatColor.WHITE+ finalCount,
                                        ChatColor.YELLOW+"Contribution: "+ChatColor.WHITE+entry.getValue()));
                            }
                        });
                        itemStacks.add(new GuiItem(itemStack));
                        count++;
                    }
                    while(itemStacks.size()<fill) {
                        itemStacks.add(new GuiItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)));
                    }
                    contribution.populateWithGuiItems(itemStacks);
                    gui.addPane(contribution);
                }
            }
        }


        gui.addPane(hotbar);
        gui.addPane(pane);

        gui.setOnGlobalClick(event -> {
            event.setCancelled(true);
        });

        gui.setOnGlobalDrag(event -> {
            event.setCancelled(true);
        });

        return gui;
    }

    private static int getLeft(int size) {
        if(size==0 || size==1) {
            return 4;
        } else if(size<=3) {
            return 3;
        } else {
            return 2;
        }
    }

    private static int getFill(int size) {
        if (size == 0) {
            return 0;
        } else if(size==1) {
            return 1;
        } else if (size <= 3) {
            return 3;
        } else {
            return 5;
        }
    }

    private static int getLength(int size) {
        if (size == 0 || size==1) {
            return 1;
        } else if (size <= 3) {
            return 3;
        } else {
            return 5;
        }
    }

    private static GuiItem getViewAllMissionsButton(MissionPlayer player,MissionBearer bearer) {
        ItemStack itemStack = new ItemStack(Material.OAK_SIGN);
        transformMeta(itemStack, meta -> {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "View All Missions");
        });

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.WHITE + "Left-Click to view all missions");

        itemStack.setLore(lore);
        GuiItem item = new GuiItem(itemStack);
        final Mission primaryMission = bearer.getPrimaryMission();

        item.setAction(event -> {
            ChestGui gui = buildDisplayBearerMissions(player,bearer,pageEvent ->  {
                event.getWhoClicked().closeInventory();
                ChestGui g = buildMissionDisplayGUI(pageEvent.player, pageEvent.mission,bearer);
                g.show(player.getPlayer());
            },mission -> {
                return Objects.equals(primaryMission,mission);
            });
            event.getWhoClicked().closeInventory();
            gui.show(player.getPlayer());
        });

        return item;
    }

    private static ItemStack getSkullDisplayStack(MissionPlayer player) {
        ItemStack skull = getSkull(player.getUuid());
        ItemMeta skullMeta = skull.getItemMeta();
        skullMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Your Contributions");
        ArrayList<String> skullLore = new ArrayList<>();
        skullLore.add(ChatColor.WHITE + "Rewards scale with your contribution!");
        skullMeta.setLore(skullLore);
        skull.setItemMeta(skullMeta);
        return skull;
    }



    protected static GuiItem getMissionDisplayButton(MissionPlayer player, Mission mission, StaticPane update, ChestGui gui,MissionBearer bearer) {
        ItemStack itemStack = getMissionDisplay(player,mission,bearer);
        GuiItem missionDisplay = new GuiItem(itemStack);
        List<String> lore = itemStack.getLore();
        if(mission != null) {
            if ((mission.getCurrentGoal() instanceof ContributableGoal)) {

                lore.add("");
                lore.add(ChatColor.RED+"Note - this will remove the item");
                lore.add(ChatColor.RED + "from your inventory");
                lore.add("");
                lore.add(ChatColor.WHITE + "Left-Click to Contribute");

                itemStack.setLore(lore);
            }

            missionDisplay.setAction(new GUIMissionContributeCommand(mission, player, update, gui,bearer));
        }

        return missionDisplay;
    }

    protected static ItemStack getMissionDisplay(MissionPlayer player, Mission mission, MissionBearer bearer) {
        
        if(mission==null)
            return getNoMissionDisplay(bearer);
        
        ItemStack itemStack = new ItemStack(Material.PAPER);

        transformMeta(itemStack, meta -> {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + mission.getTitle());
        });

        List<String> lore = new ArrayList<>();

        if(!(mission.missionOver() || mission.infiniteDuration()))
            lore.add(ChatColor.WHITE + "Time Remaining: "+getTimeLeft(mission));

        if(mission.missionOver()) {
            lore.add(ChatColor.WHITE+"Mission "+ChatColor.UNDERLINE+"Over");
        }

        lore.add("");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Objectives: ");
        for(MissionGoal goal : mission.getGoals()) {
            String strikethrough = goal.isComplete() ? ChatColor.STRIKETHROUGH.toString() : "";
            lore.add(ChatColor.WHITE + strikethrough + " - "+ChatColor.stripColor(goal.getDisplayText())+": "+goal.getProgress());
            if(goal.equals(mission.getCurrentGoal()))
                break;
        }

        hideAll(itemStack);
        itemStack.addUnsafeEnchantment(DUMMY_ENCHANT,1);
        itemStack.setLore(lore);

        return itemStack;
    }

    private static ItemStack getNoMissionDisplay(MissionBearer bearer) {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        transformMeta(itemStack,meta -> {
            meta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"No Active Mission");
            String status = bearer.onCooldown() ? ("Cooldown: "+getCooldown(bearer)) : "Awaiting Mission";
            meta.setLore(ImmutableList.of("",
                    ChatColor.WHITE+"Status: ",
                    ChatColor.WHITE+" - "+status));
        });

        return itemStack;
    }

    private static String getCooldown(MissionBearer bearer) {
        long finishTime = bearer.getCooldown().toMillis()+bearer.getCooldownStart();
        long timeLeft = finishTime - System.currentTimeMillis();

        return DurationParser.print(Duration.of(timeLeft,ChronoUnit.MILLIS));
    }

    private static String getTimeLeft(Mission mission) {
        long finishTime = mission.getDuration().toMillis() + mission.getStartTime();
        long timeLeft = finishTime - System.currentTimeMillis();

        return DurationParser.print(Duration.of(timeLeft, ChronoUnit.MILLIS));
    }



    public static ChestGui buildAutoContributeSelect(MissionPlayer player, MissionBearer bearer) {
        ChestGui gui  = buildDisplayBearerMissions(player,bearer,pageEvent -> {
            player.getAutoContribute().set(!player.getAutoContribute().isToggled(), pageEvent.mission);
            player.playSound(MissionSound.GUI_SUCCESS.getSound());
            player.getPlayer().closeInventory();
        }, mission -> {
            if(player.getAutoContribute().getMission()==null)
                return false;

            return player.getAutoContribute().getMission().equals(mission);
        });

        StaticPane allPane = new StaticPane(4,1,1,1);
        ItemStack itemStack = new ItemStack(Material.NETHER_STAR,1);
        transformMeta(itemStack,meta -> {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD+"Toggle for ALL");
            meta.setLore(ImmutableList.of(
                    "",
                    ChatColor.WHITE+"This turns on auto-contribute",
                    ChatColor.WHITE+"For All Missions",
                    "",
                    ChatColor.WHITE+"Left-Click to toggle for all"
            ));
        });

        GuiItem item = new GuiItem(itemStack,event -> {
            player.getAutoContribute().set(!player.getAutoContribute().isToggled(),null);
            player.playSound(MissionSound.GUI_SUCCESS.getSound());
            event.getWhoClicked().closeInventory();
        });
        allPane.addItem(item,0,0);

        gui.addPane(allPane);

        return gui;

    }

    private static ChestGui buildDisplayBearerMissions(MissionPlayer player, MissionBearer bearer, Consumer<PageEvent> onSelect, Function<Mission,Boolean> highlight) {

        Preconditions.checkNotNull(bearer);

        ChestGui gui = new ChestGui(6,"Missions for "+bearer.getName());
        Pattern pattern = new Pattern(
                "111111111",
                "100000001",
                "100000001",
                "100000001",
                "100000001",
                "111111111");

        PatternPane background = new PatternPane(0,0,9,6, Pane.Priority.LOWEST,pattern);
        background.bindItem('1',new GuiItem(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)));
        gui.addPane(background);

        PaginatedPane missions = new PaginatedPane(2,2,5,2, Pane.Priority.NORMAL);
        List<GuiItem> items = new ArrayList<>();

        List<Mission> bearerMission = new ArrayList<>(bearer.getMissions());
        bearerMission.sort(new Comparator<Mission>() {
            @Override
            public int compare(Mission o1, Mission o2) {
                return Boolean.compare(o1.missionOver(),o2.missionOver());
            }
        });

        for(Mission mission : bearerMission) {
            ItemStack missionStack = getMissionDisplay(player,mission,bearer);
            List<String> lore = missionStack.getLore();
            lore.add("");
            lore.add(ChatColor.WHITE+"Left-Click to View");
            missionStack.setLore(lore);

            if(!highlight.apply(mission)) {
                missionStack.removeEnchantment(DUMMY_ENCHANT);
            }

            GuiItem guiItem = new GuiItem(missionStack);
            guiItem.setAction(click -> {
                PageEvent event = new PageEvent(click,guiItem,mission,player,gui);
                onSelect.accept(event);
            });
            items.add(guiItem);
        }

        missions.populateWithGuiItems(items);
        gui.addPane(missions);

        StaticPane hotbar = new StaticPane(2,5,5,1);
        hotbar.addItem(getCloseButton(),2,0);
        hotbar.addItem(getNextButton(missions,gui),4,0);
        hotbar.addItem(getBackButton(missions,gui),0,0);

        gui.addPane(hotbar);

        gui.setOnGlobalDrag(event -> {
            event.setCancelled(true);
        });

        gui.setOnGlobalClick(event -> {
            event.setCancelled(true);
        });
        
        return gui;
    }

    private static GuiItem getMakePrimaryButton(MissionBearer bearer, Mission mission) {

        Preconditions.checkNotNull(mission);
        Preconditions.checkNotNull(bearer);

        ItemStack itemStack = new ItemStack(Material.NETHER_STAR);
        transformMeta(itemStack,meta -> {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Make Primary");
            meta.setLore(ImmutableList.of("",
                    ChatColor.WHITE+"The primary mission is the",
                    ChatColor.WHITE+"mission displayed on /t",
                    ChatColor.WHITE+"and on /missions town",
                    "",
                    ChatColor.WHITE+"Left-Click to make primary"));

        });

        GuiItem item = new GuiItem(itemStack);
        item.setAction(event -> {

            if(Objects.equals(bearer.getPrimaryMission(),mission)) {
                MissionSound.GUI_FAIL.playSound((Player) event.getWhoClicked());
            } else {
                bearer.setPrimaryMission(mission.getUniqueID());
                MissionSound.GUI_SUCCESS.playSound((Player) event.getWhoClicked());
            }

        });

        return item;
    }

    public static class PageEvent {
        private final InventoryClickEvent event;
        private final GuiItem item;
        private final Mission mission;
        private final MissionPlayer player;
        private final ChestGui gui;

        public PageEvent(InventoryClickEvent event, GuiItem item, Mission mission, MissionPlayer player, ChestGui gui) {
            this.event = event;
            this.item = item;
            this.mission = mission;
            this.player = player;
            this.gui = gui;
        }

        public InventoryClickEvent getEvent() {
            return event;
        }

        public GuiItem getItem() {
            return item;
        }

        public Mission getMission() {
            return mission;
        }

        public MissionPlayer getPlayer() {
            return player;
        }

        public ChestGui getGui() {
            return gui;
        }
    }




}
