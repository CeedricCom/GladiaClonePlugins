package me.deltaorion.townymissionsv2.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Saveable {

    public void loadParameters(PreparedStatement statement) throws SQLException;

}
