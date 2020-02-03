package nl.SBDeveloper.V10Lift;

import nl.SBDeveloper.V10Lift.API.V10LiftAPI;
import nl.SBDeveloper.V10Lift.Commands.V10LiftCommand;
import nl.SBDeveloper.V10Lift.Listeners.BlockBreakListener;
import nl.SBDeveloper.V10Lift.Listeners.EntityDamageListener;
import nl.SBDeveloper.V10Lift.Listeners.PlayerInteractListener;
import nl.SBDeveloper.V10Lift.Listeners.SignChangeListener;
import nl.SBDeveloper.V10Lift.Managers.DBManager;
import nl.SBDeveloper.V10Lift.Utils.SBYamlFile;
import nl.SBDeveloper.V10Lift.Utils.UpdateManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class V10LiftPlugin extends JavaPlugin {

    private static V10LiftPlugin instance;
    private static SBYamlFile config;
    private static DBManager dbManager;
    private static V10LiftAPI api;

    @Override
    public void onEnable() {
        instance = this;

        config = new SBYamlFile(this, "config");
        config.loadDefaults();

        dbManager = new DBManager(this, "data");

        try {
            dbManager.load();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        api = new V10LiftAPI();

        getCommand("v10lift").setExecutor(new V10LiftCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);

        new UpdateManager(this, 72317, UpdateManager.CheckType.SPIGOT).handleResponse((versionResponse, version) -> {
            if (versionResponse == UpdateManager.VersionResponse.FOUND_NEW) {
                Bukkit.getLogger().warning("[V10Lift] There is a new version available! Current: " + this.getDescription().getVersion() + " New: " + version);
            } else if (versionResponse == UpdateManager.VersionResponse.LATEST) {
                Bukkit.getLogger().info("[V10Lift] You are running the latest version [" + this.getDescription().getVersion() + "]!");
            } else if (versionResponse == UpdateManager.VersionResponse.UNAVAILABLE) {
                Bukkit.getLogger().severe("[V10Lift] Unable to perform an update check.");
            }
        }).check();

        Bukkit.getLogger().info("[V10Lift] Plugin loaded successfully!");
    }

    @Override
    public void onDisable() {
        instance = null;
        dbManager.save();
        dbManager.closeConnection();
    }

    public static V10LiftPlugin getInstance() {
        return instance;
    }

    public static SBYamlFile getSConfig() {
        return config;
    }

    public static V10LiftAPI getAPI() {
        return api;
    }

}
