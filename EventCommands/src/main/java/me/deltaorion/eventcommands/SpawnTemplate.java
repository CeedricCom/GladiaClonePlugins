package me.deltaorion.eventcommands;

public class SpawnTemplate {

    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final double pitch;
    private final double yaw;
    private String commandName;
    private String displayName;

    public SpawnTemplate(String worldName, double x, double y, double z, double pitch, double yaw, String commandName, String displayName) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.commandName = commandName;
        this.displayName = displayName;
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }
}
