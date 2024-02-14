package me.deltaorion.siegestats.service;

import com.gmail.goosius.siegewar.SiegeController;
import com.gmail.goosius.siegewar.enums.SiegeSide;
import com.gmail.goosius.siegewar.enums.SiegeWarPermissionNodes;
import com.gmail.goosius.siegewar.objects.BattleSession;
import com.gmail.goosius.siegewar.objects.Siege;
import com.gmail.goosius.siegewar.settings.SiegeWarSettings;
import com.gmail.goosius.siegewar.utils.SiegeWarAllegianceUtil;
import com.gmail.goosius.siegewar.utils.SiegeWarDistanceUtil;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.permissions.TownyPerms;
import me.deltaorion.common.command.CommandException;
import me.deltaorion.siegestats.model.SiegeKill;
import me.deltaorion.siegestats.model.SiegeTown;
import me.deltaorion.siegestats.model.StatSiege;
import me.deltaorion.siegestats.model.killer.Participant;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SiegeService {

    private final Map<UUID, SiegeTown> siegeTowns;
    private static final String NATION_POINTS_NODE = SiegeWarPermissionNodes.SIEGEWAR_NATION_SIEGE_BATTLE_POINTS.getNode();
    private static final String TOWN_POINTS_NODE = SiegeWarPermissionNodes.SIEGEWAR_TOWN_SIEGE_BATTLE_POINTS.getNode();

    public SiegeService() {
        this.siegeTowns = new HashMap<>();
    }

    public StatSiege createSiege(Siege siege) {
        SiegeTown town = getOrMakeTown(siege.getTown());
        town.setName(siege.getTown().getName());
        String defenderName = getDefenderName(siege);
        StatSiege statSiege = new StatSiege(town,siege.getAttacker().getUUID(),siege.getAttacker().getName(),System.currentTimeMillis(),defenderName);
        town.addSiege(statSiege);
        return statSiege;
    }

    private String getDefenderName(Siege siege) {
        try {
            Nation defender = siege.getTown().getNation();
            return defender.getName();
        } catch (NotRegisteredException e) {
            return siege.getTown().getName();
        }
    }

    public void addTown(SiegeTown town) {
        this.siegeTowns.put(town.getUniqueId(),town);
    }

    public Collection<SiegeTown> getTowns() {
        return siegeTowns.values();
    }

    public Collection<StatSiege> getAllSieges() {
        List<StatSiege> allSieges = new ArrayList<>();
        for(SiegeTown town : siegeTowns.values()) {
            allSieges.addAll(town.getSieges());
        }

        return allSieges;
    }

    @NotNull
    public SiegeTown getTown(Town town) {
        return getTown(town.getUUID());
    }

    @Nullable
    public SiegeTown getTown(UUID uniqueId) {
        return siegeTowns.get(uniqueId);
    }

    @NotNull
    public SiegeTown getTown(Siege siege) {
        return getTown(siege.getTown());
    }

    @NotNull
    public SiegeTown getOrMakeTown(Town town) {
        SiegeTown siegeTown = siegeTowns.get(town.getUUID());
        if(siegeTown==null) {
            siegeTown = new SiegeTown(town.getUUID(),town.getName());
            siegeTowns.put(town.getUUID(),siegeTown);
        }

        return siegeTown;
    }

    public SiegeTown getTownByName(String name) {
        Town town = TownyUniverse.getInstance().getTown(name);
        SiegeTown siegeTown = null;
        if(town==null) {
            try {
                siegeTown = getTown(UUID.fromString(name));
            } catch (IllegalArgumentException e) {
                return null;
            }
        } else {
            siegeTown = getOrMakeTown(town);
        }

        return siegeTown;
    }

    public void evaluateDamage(Entity damager, Player victim, String cause, double damage) {
        StatSiege siege = getSiege(victim);
        if(siege==null)
            return;

        Participant killer = siege.getOrMake(damager,cause);
        killer.addDamage(damage);
    }

    public void evaluateKill(Entity killer, Player victim, Location location, String cause) {
        StatSiege siege = getSiege(victim);
        if(siege==null)
            return;

        addKillStat(siege,killer,victim,location,cause);
    }

    private StatSiege getSiege(Player victim) {
        if(!isSWEnabledAndIsThisAWarAllowedWorld(victim.getWorld()))
            return null;

        try {
            Resident deadResident = TownyUniverse.getInstance().getResident(victim.getName());
            // Weed out invalid residents, residents without a town, and players who cannot collect Points in a Siege.
            if (deadResident == null || !deadResident.hasTown() || playerIsMissingSiegePointsNodes(deadResident))
                return null;

            Town deadResidentTown = deadResident.getTown();
            Siege siege = findAValidSiege(victim, deadResidentTown);

            // If player is confirmed as close to one or more sieges in which they are
            // eligible to be involved, apply siege point penalty for the nearest one, and
            // keep inventory
            if(siege==null)
                return null;

            if(!BattleSession.getBattleSession().isActive())
                return null;

            SiegeTown siegeTown = getOrMakeTown(siege.getTown());
            StatSiege statSiege = siegeTown.getLatestSiege();
            if(statSiege==null) {
                statSiege = createSiege(siege);
            }

            return statSiege;

        } catch (NotRegisteredException e) {
            return null;
        }
    }

    public void addKillStat(StatSiege statSiege, Entity attacker, Player victim, Location location, String reason) {
        Participant killer = statSiege.getOrMake(attacker,reason);
        Participant deadPlayer = statSiege.getOrMake(victim);

        SiegeSide side = getSide(statSiege.getSiege(),victim);
        statSiege.addKill(new SiegeKill(killer,side,deadPlayer,location));
    }

    private SiegeSide getSide(Siege siege, Player victim) {
        SiegeSide side = SiegeSide.NOBODY;
        Resident resident = TownyUniverse.getInstance().getResident(victim.getName());
        if(resident==null)
            return side;

        side = SiegeSide.getPlayerSiegeSide(siege, victim);

        return side;
    }

    private boolean isSWEnabledAndIsThisAWarAllowedWorld(World world) {
        return SiegeWarSettings.getWarSiegeEnabled() && TownyAPI.getInstance().getTownyWorld(world.getName()).isWarAllowed();
    }

    private boolean playerIsMissingSiegePointsNodes(Resident resident) {
        Map<String,Boolean> perms = TownyPerms.getResidentPerms(resident);
        boolean townNode = perms.getOrDefault(TOWN_POINTS_NODE,false);
        boolean nationNode = perms.getOrDefault(NATION_POINTS_NODE,false);
        boolean nationNode2 = perms.getOrDefault("siegewar.nation.siege.*",false);
        boolean townNode2 = perms.getOrDefault("siegewar.town.siege.*",false);

        return !townNode && !nationNode && !nationNode2 && !townNode2;
    }

    private Siege findAValidSiege(Player deadPlayer, Town deadResidentTown) {
        Siege nearestSiege = null;
        double smallestDistanceToSiege = 0;

        //Find nearest eligible siege
        for (Siege candidateSiege : SiegeController.getSieges()) {

            //Skip if siege is not active
            if (!candidateSiege.getStatus().isActive())
                continue;

            //Skip if player is not is siege-zone
            if(!SiegeWarDistanceUtil.isInSiegeZone(deadPlayer, candidateSiege))
                continue;

            //Is player an attacker or defender in this siege?
            if(SiegeSide.getPlayerSiegeSide(candidateSiege, deadPlayer) == SiegeSide.NOBODY)

                continue;

            //Set nearestSiege if it is 1st viable one OR closer than smallestDistanceToSiege.
            double candidateSiegeDistanceToPlayer = deadPlayer.getLocation().distance(candidateSiege.getFlagLocation());
            if (nearestSiege == null || candidateSiegeDistanceToPlayer < smallestDistanceToSiege) {
                nearestSiege = candidateSiege;
                smallestDistanceToSiege = candidateSiegeDistanceToPlayer;
            }
        }
        return nearestSiege;
    }


}
