package me.deltaorion.towntier.towntiers.data;

import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private Inventory viewedInventory;
    private int viewedPage;
    private long lastAction;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Inventory getViewedInventory() {
        return viewedInventory;
    }

    public void setViewedInventory(Inventory viewedInventory) {
        this.viewedInventory = viewedInventory;
    }

    public int getViewedPage() {
        return viewedPage;
    }

    public void setViewedPage(int viewedPage) {
        this.viewedPage = viewedPage;
    }

    public long getLastAction() {
        return lastAction;
    }

    public void setLastAction(long lastAction) {
        this.lastAction = lastAction;
    }
}
