package nl.SBDeveloper.V10Lift.Managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.Utils.SBSQLiteDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBManager {

    private static SBSQLiteDB data;
    private static Connection con;

    public DBManager(JavaPlugin plugin, String name) {
        data = new SBSQLiteDB(plugin, name);

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
            Gson gson = new Gson();
            Lift lift = gson.fromJson(json, new TypeToken<Lift>(){}.getType());
            DataManager.addLift(liftSet.getString("liftName"), lift);

            Bukkit.getLogger().info("[V10Lift] Loading lift " + liftSet.getString("liftName") + " from data...");
        }
    }

    public void removeFromData() {
        try {
            String query0 = "SELECT * FROM lifts";
            PreparedStatement statement0 = con.prepareStatement(query0);
            ResultSet liftSet = statement0.executeQuery();
            while (liftSet.next()) {
                if (!DataManager.containsLift(liftSet.getString("liftName"))) {
                    Bukkit.getLogger().info("[V10Lift] Removing lift " + liftSet.getString("liftName") + " to data...");

                    String query = "DELETE FROM lifts WHERE liftName = ?";
                    PreparedStatement statement = con.prepareStatement(query);
                    statement.setString(1, liftSet.getString("liftName"));
                    statement.executeUpdate();
                }
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromData(String name) {
        try {
            if (!DataManager.containsLift(name)) {
                Bukkit.getLogger().info("[V10Lift] Removing lift " + name + " to data...");

                String query = "DELETE FROM lifts WHERE liftName = ?";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, name);
                statement.executeUpdate();
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        Gson gson = new Gson();

        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
            byte[] blob = gson.toJson(entry.getValue()).getBytes();

            Bukkit.getLogger().info("[V10Lift] Saving lift " + entry.getKey() + " to data...");

            try {
                String query = "INSERT INTO lifts (liftName, liftData) VALUES (?, ?)";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, entry.getKey());
                statement.setBytes(2, blob);
                statement.executeUpdate();
            } catch (SQLException ignored) {}

            try {
                String query2 = "UPDATE lifts SET liftData = ? WHERE liftName = ?";
                PreparedStatement statement2 = con.prepareStatement(query2);
                statement2.setBytes(1, blob);
                statement2.setString(2, entry.getKey());
                statement2.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        data.closeSource();
    }

}
