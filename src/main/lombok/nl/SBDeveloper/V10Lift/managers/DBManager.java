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

public class DBManager {
    private static final Gson gson = new Gson();

    private static SQLiteDB data;
    private static Connection con;

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

    public void removeFromData(String name) {
        if (!DataManager.containsLift(name)) {
            Bukkit.getLogger().info("[V10Lift] Removing lift " + name + " to data...");

            Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> {
                try {
                    String query = "DELETE FROM lifts WHERE liftName = ?";
                    PreparedStatement statement = con.prepareStatement(query);
                    statement.setString(1, name);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void save() {
        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
            byte[] blob = gson.toJson(entry.getValue()).getBytes();

            Bukkit.getLogger().info("[V10Lift] Saving lift " + entry.getKey() + " to data...");

            Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> {
                try {
                    String query = "INSERT INTO lifts (liftName, liftData) VALUES (?, ?)";
                    PreparedStatement statement = con.prepareStatement(query);
                    statement.setString(1, entry.getKey());
                    statement.setBytes(2, blob);
                    statement.executeUpdate();
                } catch (SQLException ignored) {
                }
            });

            Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> {
                try {
                    String query2 = "UPDATE lifts SET liftData = ? WHERE liftName = ?";
                    PreparedStatement statement2 = con.prepareStatement(query2);
                    statement2.setBytes(1, blob);
                    statement2.setString(2, entry.getKey());
                    statement2.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void saveLift(String name, Lift lift) {
        byte[] blob = gson.toJson(lift).getBytes();

        Bukkit.getLogger().info("[V10Lift] Saving lift " + name + " to data...");

        Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> {
            try {
                String query = "INSERT INTO lifts (liftName, liftData) VALUES (?, ?)";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, name);
                statement.setBytes(2, blob);
                statement.executeUpdate();
            } catch (SQLException ignored) {
            }
        });

        Bukkit.getScheduler().runTaskAsynchronously(V10LiftPlugin.getInstance(), () -> {
            try {
                String query2 = "UPDATE lifts SET liftData = ? WHERE liftName = ?";
                PreparedStatement statement2 = con.prepareStatement(query2);
                statement2.setBytes(1, blob);
                statement2.setString(2, name);
                statement2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void closeConnection() {
        data.closeSource();
    }
}
