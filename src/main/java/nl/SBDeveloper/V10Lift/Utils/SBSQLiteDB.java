package nl.SBDeveloper.V10Lift.Utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.util.DriverDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SBSQLiteDB {

    private HikariDataSource source;

    /**
     * Initialize a new connection
     *
     * @param plugin The plugin (Main class)
     * @param name The database name
     */
    public SBSQLiteDB(@Nonnull Plugin plugin, String name) {
        Bukkit.getLogger().info("[V10Lift] Loading databases...");

        File dbFile = new File(plugin.getDataFolder(), name + ".db");

        if (!dbFile.exists()) {
            try {
                Bukkit.getLogger().info("[V10Lift] Generating the " + name + ".db!");
                dbFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[V10Lift] Couldn't generate the " + name + ".db!");
                e.printStackTrace();
                return;
            }
        }

        DataSource dataSource = new DriverDataSource("jdbc:sqlite:" + dbFile.getAbsolutePath(), "org.sqlite.JDBC", new Properties(), null, null);
        HikariConfig config = new HikariConfig();
        config.setPoolName("SQLiteConnectionPool");
        config.setDataSource(dataSource);

        this.source = new HikariDataSource(config);
    }

    //CREATE TABLE -> execute()
    //SELECT -> executeQuery()
    //UPDATE -> executeUpdate()

    public Connection getConnection() throws SQLException {
        return this.source.getConnection();
    }

    public void closeSource() {
        Bukkit.getLogger().info("[V10Lift] Closing the database connection!");
        this.source.close();
    }
}
