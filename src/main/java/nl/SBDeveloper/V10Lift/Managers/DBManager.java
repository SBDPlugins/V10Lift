package nl.SBDeveloper.V10Lift.Managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.Utils.SBSQLiteDB;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DBManager {

    private static SBSQLiteDB data;

    public DBManager(JavaPlugin plugin, String name) {
        data = new SBSQLiteDB(plugin, name);

        data.execute("CREATE TABLE IF NOT EXISTS lifts (liftName varchar(255) NOT NULL, liftData blob NOT NULL, UNIQUE (liftName))");
    }

    public void load() throws SQLException {
        ResultSet liftSet = data.execute("SELECT * FROM lifts", new HashMap<>());
        while (liftSet.next()) {
            //Loading a lift...
            byte[] blob = liftSet.getBytes("liftData");
            String json = new String(blob);
            Gson gson = new Gson();
            Lift lift = gson.fromJson(json, new TypeToken<Lift>(){}.getType());
            DataManager.addLift(liftSet.getString("liftName"), lift);
        }
    }

    public void save() {
        HashMap<String, byte[]> inserts = new HashMap<>();
        Gson gson = new Gson();
        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
            inserts.put(entry.getKey(), gson.toJson(entry.getValue()).getBytes());
        }
        //TODO Insert
    }

    public void closeConnection() {
        data.closeSource();
    }

}
