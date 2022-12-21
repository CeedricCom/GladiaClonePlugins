package me.deltaorion.townymissionsv2.storage;

import me.deltaorion.townymissionsv2.TownyMissionsV2;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {

    String getImplementationName();

    void init(TownyMissionsV2 plugin);

    void shutdown() throws Exception;

    Connection getConnection() throws SQLException;
}
