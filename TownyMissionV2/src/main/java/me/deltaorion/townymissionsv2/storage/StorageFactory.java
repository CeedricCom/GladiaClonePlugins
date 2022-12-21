package me.deltaorion.townymissionsv2.storage;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.storage.connection.SqliteConnectionFactory;

public class StorageFactory {

    private final static String DATABASE_NAME = "TownyMissions_V2";

    public static ConnectionFactory getConnection(StorageType type, TownyMissionsV2 plugin) {
        switch (type) {
            case SQLITE:
                return new SqliteConnectionFactory(plugin.getDataFolder().toPath().resolve("storage").resolve(DATABASE_NAME + "." + type.getFileExtension()));

            default:
                throw new IllegalStateException("Could not find relevant implementation for storage type '"+type+"'");
        }
    }
}
