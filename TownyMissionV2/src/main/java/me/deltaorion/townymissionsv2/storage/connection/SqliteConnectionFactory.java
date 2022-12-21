package me.deltaorion.townymissionsv2.storage.connection;

import me.deltaorion.townymissionsv2.TownyMissionsV2;
import me.deltaorion.townymissionsv2.storage.ConnectionFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteConnectionFactory implements ConnectionFactory {

    //single unclosing connection should be fine for sqlite
    private final Path fileName;
    private File file;
    private Connection connection;

    public SqliteConnectionFactory(Path fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getImplementationName() {
        return "Sqlite";
    }

    @Override
    public void init(TownyMissionsV2 plugin) {
        try {
            this.file = new File(fileName.toAbsolutePath().toString());
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shutdown() throws Exception {
        if(this.connection != null) {
            if(!this.connection.isClosed()) {
                connection.close();
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(this.connection == null || this.connection.isClosed()) {
            this.connection = createConnection();
        }

        return connection;
    }

    private Connection createConnection() throws SQLException {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if(con == null)
            throw new RuntimeException("Unable to initialise database connection");

        return con;
    }
}
