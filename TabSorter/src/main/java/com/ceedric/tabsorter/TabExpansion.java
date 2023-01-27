package com.ceedric.tabsorter;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TabExpansion extends PlaceholderExpansion implements Relational {
    @Override
    public @NotNull String getIdentifier() {
        return "ceedrictab";
    }

    @Override
    public @NotNull String getAuthor() {
        return "DeltaOrion";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, Player player2, String identifier) {
        String request = getRequest(player,player2,identifier);
        System.out.println(request);
        return request;
    }

    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
        return "Hello World";
    }

    private String getRequest(Player player, Player player2, String identifier) {
        Resident res = TownyUniverse.getInstance().getResident(player.getUniqueId());
        Resident res2 = TownyUniverse.getInstance().getResident(player2.getUniqueId());
        if (res == null || res2 == null)
            return null;

        System.out.println("Res 1: " + res.getName());
        System.out.println("Res 2: " +res2.getName());

        if (!res2.hasTown())
            return "noTown";
        else if (CombatUtil.isSameTown(res, res2))
            return "sameTown";
        else if (CombatUtil.isSameNation(res, res2))
            return "sameNation";
        else if (CombatUtil.isAlly(res, res2))
            return "ally";
        else if (CombatUtil.isEnemy(res, res2))
            return "enemy";
        else
            return "neutral";
    }


}
