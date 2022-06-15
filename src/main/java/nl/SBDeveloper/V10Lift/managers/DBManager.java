package nl.SBDeveloper.V10Lift.managers;

import com.google.gson.Gson;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import nl.SBDeveloper.V10Lift.api.objects.Lift;
import nl.SBDeveloper.V10Lift.sbutils.SQLiteDB;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * The DBManager manages the database
 */
public class DBManager {
    private static final Gson gson = new Gson();

    private static SQLiteDB data;
    private static Connection con;

    /**
     * Construct the database manager
     *
     * @param name The name of the sqlite database file
     */
    public DBManager(String name) {
        data = new SQLiteDB(name);

        try {
            con = data.getConnection();

            String query = "CREATE TABLE IF NOT EXISTS lifts (liftName varchar(255) NOT NULL, liftData blob NOT NULL, UNIQUE (liftName))";
            PreparedStatement statement = con.prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the database from data
     *
     * @throws SQLException If the SQL SELECT fails
     */
    public void load() throws SQLException {
        String query = "SELECT * FROM lifts";
        PreparedStatement statement = con.prepareStatement(query);
        ResultSet liftSet = statement.executeQuery();
        while (liftSet.next()) {
            //Loading a lift...

            /*
             * @todo Fix migrating from 1.12.2- to 1.13+
             * - byte to Facing for signs
             * - Facing opposite for ropes
             * - New materials
             */

            byte[] blob = liftSet.getBytes("liftData");
            String json = new String(blob);

            Lift lift = gson.fromJson(json, Lift.class);
            DataManager.addLift(liftSet.getString("liftName"), lift);

            Bukkit.getLogger().info("[V10Lift] Loading lift " + liftSet.getString("liftName") + " from data...");
        }
    }

    /**
     * Remove a lift from data
     *
     * @param liftName The name of the lift
     */
    public void remove(String liftName) {
        if (!DataManager.containsLift(liftName)) {
            Bukkit.getLogger().info("[V10Lift] Removing lift " + liftName + " to data...");

            Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> {
                try {
                    String query = "DELETE FROM lifts WHERE liftName = ?";
                    PreparedStatement statement = con.prepareStatement(query);
                    statement.setString(1, liftName);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Save all lifts to data
     */
    public void save() {
        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
            saveLift(entry.getKey(), entry.getValue(), true);
        }
    }

    /**
     * Save a lift to data
     *
     * @param liftName The name of the lift
     * @param lift The lift itself
     */
    public void saveLift(String liftName, Lift lift, boolean sync) {
        Bukkit.getLogger().info("[V10Lift] Saving lift " + liftName + " to data...");

        byte[] blob = gson.toJson(lift).getBytes();
        if (sync) {
            updateLift(liftName, blob);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> updateLift(liftName, blob));
        }
    }

    /**
     * Update a lift in data
     *
     * @param liftName The name of the lift
     * @param liftData The JSON blob of the lift object
     */
    private void updateLift(String liftName, byte[] liftData) {
        try {
            String query = "INSERT INTO lifts (liftName, liftData) VALUES (?, ?)";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, liftName);
            statement.setBytes(2, liftData);
            statement.executeUpdate();
        } catch (SQLException ignored) {}

        try {
            String query2 = "UPDATE lifts SET liftData = ? WHERE liftName = ?";
            PreparedStatement statement2 = con.prepareStatement(query2);
            statement2.setBytes(1, liftData);
            statement2.setString(2, liftName);
            statement2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the connection with the database
     */
    public void closeConnection() {
        data.closeSource();
    }
}
