package me.deltaorion.townymissionsv2.bearer;

import com.google.common.base.Preconditions;
import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.deltaorion.townymissionsv2.configuration.Message;
import me.deltaorion.townymissionsv2.display.MinecraftSound;
import me.deltaorion.townymissionsv2.player.MissionPlayer;
import me.deltaorion.townymissionsv2.storage.Saveable;
import me.deltaorion.townymissionsv2.util.TownyUtil;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.Consumer;

public class GovernmentMissionBearer extends AbstractMissionBearer implements Saveable {

    private final Government government;

    public GovernmentMissionBearer(Government government) {
        this.government = government;
    }

    private GovernmentMissionBearer(Government government, UUID primaryMission, long cooldownStart, Duration cooldownDuration) {
        this.government = government;
        setPrimaryMission(primaryMission);
        setCooldown(cooldownDuration);
        setCooldownStart(cooldownStart);
    }

    @Override
    public UUID getUniqueID() {
        return government.getUUID();
    }

    @Override
    public void sendMessage(String text) {
        forEachOnline(resident -> {
            resident.getPlayer().sendMessage(text);
        });
    }

    @Override
    public void playSound(MinecraftSound sound) {
        forEachOnline(resident -> {
            sound.playSound(resident.getPlayer());
        });
    }

    @Override
    public String getCompletionMessage() {
        return Message.GOVERNMENT_COMPLETION.getMessage(getLeader().getName(),government.getName(),getGovernmentName());
    }

    @Override
    public boolean shouldBroadcast() {
        return true;
    }

    @Override
    public void sendTitle(String title, String subTitle) {
        forEachOnline(resident -> {
            resident.getPlayer().sendTitle(title,subTitle,10,40,10);
        });
    }

    @Override
    public String getName() {
        if(isTown()) {
            return "Town";
        } else {
            return "Nation";
        }
    }

    @Override
    public boolean contains(MissionPlayer player) {
        return government.getResidents().contains(player.getResident());
    }


    private void forEachOnline(Consumer<Resident> consumer) {
        for(Resident resident : government.getResidents()) {
            if(resident.isOnline()) {
                consumer.accept(resident);
            }
        }
    }

    public boolean isTown() {
        return government instanceof Town;
    }

    public String getGovernmentName() {
        if(isTown()) {
            return "Town";
        } else {
            return "Nation";
        }
    }

    public Resident getLeader() {
        if(isTown()) {
            Town town = (Town) government;
            return town.getMayor();
        } else {
            Nation nation = (Nation) government;
            return nation.getKing();
        }
    }

    //INSERT INTO MissionBearer VALUES('UUID','cooldown start','cooldown duration','primary mission','bearer type');

    @Override
    public void loadParameters(PreparedStatement statement) throws SQLException {
        statement.setString(1,government.getUUID().toString());
        statement.setLong(2,getCooldownStart());

        if(getCooldown()==null) {
            statement.setNull(3, Types.NULL);
        } else {
            statement.setLong(3,getCooldown().toMillis());
        }

        if(getPrimaryMission()==null) {
            statement.setNull(4,Types.NULL);
        } else {
            statement.setString(4,getPrimaryMission().getUniqueID().toString());
        }

        statement.setString(5,BearerType.GOVERNMENT.toString());
        statement.addBatch();
    }

    public static MissionBearer fromSave(String uuidString, @Nullable String primaryMissionString, long cooldownStart,long cooldownDuration) {
        Preconditions.checkNotNull(uuidString);

        UUID uuid = null;

        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not load mission bearer '"+uuidString+"' from database as the UUID is not a valid UUID!");
            return null;
        }

        UUID primaryMission = null;

        try {
            if(primaryMissionString != null) {
                primaryMission = UUID.fromString(primaryMissionString);
            }
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().severe("Could not load mission bearer '"+uuidString+"' from the database as primary mission UUID '"+primaryMissionString+"' is not a valid UUID!");
            return null;
        }

        Government government = TownyUtil.getGovernment(uuid);

        if(government==null)
            return null;

        Duration cooldown = Duration.of(cooldownDuration,ChronoUnit.MILLIS);

        return new GovernmentMissionBearer(government,primaryMission,cooldownStart,cooldown);

    }

}
