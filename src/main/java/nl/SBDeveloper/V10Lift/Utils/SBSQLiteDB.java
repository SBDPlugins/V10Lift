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
        Bukkit.getLogger().info("[SBDBManager] Loading databases...");

        File dbFile = new File(plugin.getDataFolder(), name + ".db");

        if (!dbFile.exists()) {
            try {
                Bukkit.getLogger().info("[SBDBManager] Generating the " + name + ".db!");
                dbFile.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("[SBDBManager] Couldn't generate the " + name + ".db!");
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

    /**
     * Execute queries like CREATE TABLE, ALTER TABLE, ....
     *
     * @param query The query you want to execute
     *
     * @return true/false
     */
    public boolean execute(String query) {
        Connection con = null;
        PreparedStatement statement = null;
        boolean b;

        try {
            con = this.source.getConnection();
            statement = con.prepareStatement(query);
            b = statement.execute();
            return b;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[SBDBManager] SQL exception! Please check the stacktrace below.");
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (con != null) con.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Execute queries like INSERT, UPDATE, DELETE, ...
     *
     * @param query The query you want to execute
     * @param objects The objects you want to insert, in the right order
     *
     * @return true/false
     */
    public boolean execute(String query, LinkedHashMap<Integer, Object> objects) {
        Connection con = null;
        PreparedStatement statement = null;
        int b;
        try {
            con = this.source.getConnection();
            statement = con.prepareStatement(query);
            if (objects != null) {
                for (Map.Entry<Integer, Object> entry : objects.entrySet()) {
                    statement.setObject(entry.getKey(), entry.getValue());
                }
            }
            b = statement.executeUpdate();
            if (b >= 0) return true;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[SBDBManager] SQL exception! Please check the stacktrace below.");
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
                if (con != null) con.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Execute a SELECT query
     *
     * @param query The query you want to execute
     * @param objects The objects you want to insert, in the right order
     * @param requests The objects you want to select from the database
     *
     * @return HashMap<Object, Object> where the first object is the rowname and the second object is the value
     */
    public ResultSet execute(String query, HashMap<Integer, Object> objects, ArrayList<Object> requests) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet set = null;

        try {
            con = this.source.getConnection();
            statement = con.prepareStatement(query);
            if (objects != null) {
                for (Map.Entry<Integer, Object> entry : objects.entrySet()) {
                    statement.setObject(entry.getKey(), entry.getValue());
                }
            }
            set = statement.executeQuery();
            return set;
        } catch (SQLException e) {
            Bukkit.getLogger().severe("[SBDBManager] SQL exception! Please check the stacktrace below.");
            e.printStackTrace();
        } finally {
            try {
                if (set != null) set.close();
                if (statement != null) statement.close();
                if (con != null) con.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        return set;
    }

    public void closeSource() {
        Bukkit.getLogger().info("[SBDBManager] Closing the database connection!");
        this.source.close();
    }
}
