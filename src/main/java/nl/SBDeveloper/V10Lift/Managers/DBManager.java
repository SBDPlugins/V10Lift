package nl.SBDeveloper.V10Lift.Managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.Utils.SBSQLiteDB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBManager {

    private static SBSQLiteDB data;

    public DBManager(JavaPlugin plugin, String name) {
        data = new SBSQLiteDB(plugin, name);

        try {
            String query = "CREATE TABLE IF NOT EXISTS lifts (liftName varchar(255) NOT NULL, liftData blob NOT NULL, UNIQUE (liftName))";
            PreparedStatement statement = data.getConnection().prepareStatement(query);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void load() throws SQLException {
        String query = "SELECT * FROM lifts";
        PreparedStatement statement = data.getConnection().prepareStatement(query);
        ResultSet liftSet = statement.executeQuery();
        while (liftSet.next()) {
            //Loading a lift...
            byte[] blob = liftSet.getBytes("liftData");
            String json = new String(blob);
            Gson gson = new Gson();
            Lift lift = gson.fromJson(json, new TypeToken<Lift>(){}.getType());
            DataManager.addLift(liftSet.getString("liftName"), lift);

            Bukkit.getLogger().info("[V10Lift] Loading lift " + liftSet.getString("liftName") + " from data...");
        }
    }

    public void save() {
        Gson gson = new Gson();
        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {

            byte[] blob = gson.toJson(entry.getValue()).getBytes();

            try {
                String query = "INSERT INTO lifts (liftName, liftData) VALUES (?, ?)";
                PreparedStatement statement = data.getConnection().prepareStatement(query);
                statement.setString(1, entry.getKey());
                statement.setBytes(2, blob);
                statement.executeUpdate();

                String query2 = "UPDATE lifts SET liftData = ? WHERE liftName = ?";
                PreparedStatement statement2 = data.getConnection().prepareStatement(query2);
                statement2.setBytes(1, blob);
                statement2.setString(2, entry.getKey());
                statement2.executeUpdate();

                Bukkit.getLogger().info("[V10Lift] Saving lift " + entry.getKey() + " to data...");
            } catch(SQLException ignored) {}
        }
    }

    public void closeConnection() {
        data.closeSource();
    }

}
