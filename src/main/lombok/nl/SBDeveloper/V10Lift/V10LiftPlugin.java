package nl.SBDeveloper.V10Lift;

import nl.SBDeveloper.V10Lift.API.V10LiftAPI;
import nl.SBDeveloper.V10Lift.Commands.V10LiftCommand;
import nl.SBDeveloper.V10Lift.Commands.V10LiftTabCompleter;
import nl.SBDeveloper.V10Lift.Listeners.BlockBreakListener;
import nl.SBDeveloper.V10Lift.Listeners.EntityDamageListener;
import nl.SBDeveloper.V10Lift.Listeners.PlayerInteractListener;
import nl.SBDeveloper.V10Lift.Listeners.SignChangeListener;
import nl.SBDeveloper.V10Lift.Managers.DBManager;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.Managers.VaultManager;
import nl.SBDevelopment.SBUtilities.Data.YamlFile;
import nl.SBDevelopment.SBUtilities.PrivateManagers.UpdateManager;
import nl.SBDevelopment.SBUtilities.SBUtilities;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;

public class V10LiftPlugin extends JavaPlugin {

    private static V10LiftPlugin instance;
    private static YamlFile config;
    private static DBManager dbManager;
    private static V10LiftAPI api;
    private static boolean vault = false;

    @Override
    public void onEnable() {
        instance = this;

        //Initialize the util
        new SBUtilities(this, "V10Lift");

        //Load the config
        config = new YamlFile("config");
        config.loadDefaults();

        //Load the database
        dbManager = new DBManager("data");
        try {
            dbManager.load();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //Load the API
        api = new V10LiftAPI();

        //Load vault if found
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
            vault = true;
            VaultManager.setupPermissions();
        }

        //Load the command
        Objects.requireNonNull(getCommand("v10lift"), "Internal error! Command not found.").setExecutor(new V10LiftCommand());
        Objects.requireNonNull(getCommand("v10lift"), "Internal error! Command not found.").setTabCompleter(new V10LiftTabCompleter());

        //Register the listeners
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);

        //Load metrics
        Metrics metrics = new Metrics(this, 6564);
        metrics.addCustomChart(new Metrics.SingleLineChart("lifts", () -> DataManager.getLifts().size()));

        //Load the update checker
        if (!getSConfig().getFile().contains("CheckUpdates") || getSConfig().getFile().getBoolean("CheckUpdates")) {
            new UpdateManager(this, 72317, UpdateManager.CheckType.SPIGOT).handleResponse((versionResponse, version) -> {
                if (versionResponse == UpdateManager.VersionResponse.FOUND_NEW) {
                    Bukkit.getLogger().warning("[V10Lift] There is a new version available! Current: " + this.getDescription().getVersion() + " New: " + version);
                } else if (versionResponse == UpdateManager.VersionResponse.LATEST) {
                    Bukkit.getLogger().info("[V10Lift] You are running the latest version [" + this.getDescription().getVersion() + "]!");
                } else if (versionResponse == UpdateManager.VersionResponse.UNAVAILABLE) {
                    Bukkit.getLogger().severe("[V10Lift] Unable to perform an update check.");
                }
            }).check();
        }

        Bukkit.getLogger().info("[V10Lift] Plugin loaded successfully!");
    }

    @Override
    public void onDisable() {
        V10LiftPlugin.getDBManager().removeFromData();

        dbManager.save();
        dbManager.closeConnection();

        instance = null;
    }

    public static V10LiftPlugin getInstance() {
        return instance;
    }

    public static YamlFile getSConfig() {
        return config;
    }

    public static DBManager getDBManager() {
        return dbManager;
    }

    public static V10LiftAPI getAPI() {
        return api;
    }

    public static boolean isVaultEnabled() {
        return vault;
    }
}
