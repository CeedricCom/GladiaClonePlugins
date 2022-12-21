package me.deltaorion.townymissionsv2.command;

public enum CommandPermissions {

    GENERIC("TownyMissions.User"),
    ADMIN("TownyMissions.Admin"),
    MAYOR("TownyMissions.Mayor"),
    OPERATOR("TownyMissions.Operator"),
    ADMIN_RELOAD("TownyMissions.Admin.Reload"),
    ADMIN_GIVE_MISSION("TownyMissions.Admin.Mission.Add"),
    ADMIN_REMOVE_MISSION("TownyMissions.Admin.Mission.Remove"),
    ADMIN_COMPLETE_MISSION("TownyMissions.Admin.Mission.Complete"),
    ADMIN_COMPLETE_GOAL("TownyMissions.Admin.Goal.Complete"),
    ADMIN_SET_STAGE("TownyMissions.Admin.Goal.Stage"),
    ADMIN_ADD_COOLDOWN("TownyMissions.Admin.Cooldown.Add"),
    ADMIN_REMOVE_COOLDOWN("TownyMissions.Admin.Cooldown.Remove"),
    ADMIN_TICK("TownyMissions.Admin.Tick"),
    DATABASE_SAVE("TownyMissions.Admin.Database"),
    ;

    private final String perm;

    CommandPermissions(String perm) {
        this.perm = perm;
    }

    public String getPerm() {
        return perm;
    }
}
