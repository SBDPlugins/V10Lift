package nl.SBDeveloper.V10Lift.sbutils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class SQLiteDB {
    private final String dbName;
    private HikariDataSource source;
    private Connection con;

    /**
     * Initialize a new connection
     *
     * @param dbName The database name
     */
    public SQLiteDB(String dbName) {
        this.dbName = dbName;

        File dbFile = new File(V10LiftPlugin.getInstance().getDataFolder(), dbName + ".db");

        if (!dbFile.exists()) {
            try {
                Bukkit.getLogger().info("[V10Lift] Generating the " + dbName + ".db!");
                if (!dbFile.createNewFile()) {
                    Bukkit.getLogger().severe("[V10Lift] Couldn't generate the " + dbName + ".db!");
                    return;
                }
            } catch (IOException e) {
                Bukkit.getLogger().info("[V10Lift] Couldn't generate the " + dbName + ".db!");
                return;
            }
        }

        HikariConfig config = new HikariConfig();
        config.setPoolName("V10Lift");
        config.setUsername(null);
        config.setPassword(null);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setConnectionTestQuery("SELECT 1");
        config.setMaximumPoolSize(1);

        Properties prop = new Properties();
        prop.setProperty("date_string_format", "yyyy-MM-dd HH:mm:ss");

        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
        config.setDataSourceProperties(prop);
        this.source = new HikariDataSource(config);

        try {
            this.con = this.source.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the connection, to execute queries
     *
     * CREATE TABLE -> execute()
     * SELECT -> executeQuery()
     * UPDATE -> executeUpdate()
     *
     * @return Connection
     */
    public Connection getConnection() {
        return this.con;
    }

    /**
     * Close the connection
     */
    public void closeSource() {
        Bukkit.getLogger().info("[V10Lift] Closing the database connection for " + dbName + ".db!");
        try {
            this.con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.source.close();
    }
}
