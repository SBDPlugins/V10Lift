package nl.SBDeveloper.V10Lift;

import nl.SBDeveloper.V10Lift.api.V10LiftAPI;
import nl.SBDeveloper.V10Lift.commands.V10LiftCommand;
import nl.SBDeveloper.V10Lift.commands.V10LiftTabCompleter;
import nl.SBDeveloper.V10Lift.listeners.BlockBreakListener;
import nl.SBDeveloper.V10Lift.listeners.EntityDamageListener;
import nl.SBDeveloper.V10Lift.listeners.PlayerInteractListener;
import nl.SBDeveloper.V10Lift.listeners.SignChangeListener;
import nl.SBDeveloper.V10Lift.managers.DBManager;
import nl.SBDeveloper.V10Lift.managers.DataManager;
import nl.SBDeveloper.V10Lift.managers.VaultManager;
import nl.SBDeveloper.V10Lift.sbutils.ConfigUpdater;
import nl.SBDeveloper.V10Lift.sbutils.UpdateManager;
import nl.SBDeveloper.V10Lift.sbutils.YamlFile;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

public class V10LiftPlugin extends JavaPlugin {

    private static V10LiftPlugin instance;
    private static YamlFile config;
    private static DBManager dbManager;
    private static YamlFile messages;
    private static V10LiftAPI api;
    private static boolean vault = false;

    @Override
    public void onEnable() {
        instance = this;

        //Load the config
        config = new YamlFile("config");
        config.loadDefaults();

        //And update config
        try {
            ConfigUpdater.update(this, "config.yml", config.getJavaFile(), Collections.emptyList());
        } catch (IOException e) {
            Bukkit.getLogger().warning("[V10Lift] Couldn't update the config.yml. Please check the stacktrace below.");
            e.printStackTrace();
        }

        //Load the messages
        messages = new YamlFile("messages");
        messages.loadDefaults();

        //Load the database
        dbManager = new DBManager("data");
        try {
            dbManager.load();
        } catch (SQLException e) {
            Bukkit.getLogger().warning("[V10Lift] Couldn't connect to the SQLite database. Please check the stacktrace below.");
            e.printStackTrace();
        }

        //Load the API
        api = new V10LiftAPI();

        //Load vault if found
        if (VaultManager.setupPermissions()) {
            Bukkit.getLogger().info("[V10Lift] Loading Vault hook for group whitelist support.");
            vault = true;
        }

        //Load the command
        getCommand("v10lift").setExecutor(new V10LiftCommand());
        getCommand("v10lift").setTabCompleter(new V10LiftTabCompleter());

        //Register the listeners
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);

        //Load metrics
        Bukkit.getLogger().info("[V10Lift] Loading metrics. Can be disabled in the global bStats config.");
        Metrics metrics = new Metrics(this, 6564);
        metrics.addCustomChart(new SingleLineChart("lifts", () -> DataManager.getLifts().size()));

        //Load the update checker
        if (getSConfig().getFile().getBoolean("UpdateChecker.Enabled")) {
            UpdateManager updateManager = new UpdateManager(this, 72317);

            updateManager.handleResponse((versionResponse, version) -> {
                switch (versionResponse) {
                    case FOUND_NEW:
                        Bukkit.getLogger().warning("[V10Lift] There is a new version available! Current: " + this.getDescription().getVersion() + " New: " + version.get());
                        if (getSConfig().getFile().getBoolean("UpdateChecker.DownloadOnUpdate")) {
                            Bukkit.getLogger().info("[V10Lift] Trying to download the update. This could take some time...");

                            updateManager.handleDownloadResponse((downloadResponse, fileName) -> {
                                switch (downloadResponse) {
                                    case DONE:
                                        Bukkit.getLogger().info("[V10Lift] Update downloaded! If you restart your server, it will be loaded. Filename: " + fileName);
                                        break;
                                    case ERROR:
                                        Bukkit.getLogger().severe("[V10Lift] Something went wrong when trying downloading the latest version.");
                                        break;
                                    case UNAVAILABLE:
                                        Bukkit.getLogger().warning("[V10Lift] Unable to download the latest version.");
                                        break;
                                }
                            }).runUpdate();
                        }
                        break;
                    case LATEST:
                        Bukkit.getLogger().info("[V10Lift] You are running the latest version [" + this.getDescription().getVersion() + "]!");
                        break;
                    case THIS_NEWER:
                        Bukkit.getLogger().info("[V10Lift] You are running a newer version [" + this.getDescription().getVersion() + "]! This is probably fine.");
                        break;
                    case UNAVAILABLE:
                        Bukkit.getLogger().severe("[V10Lift] Unable to perform an update check.");
                        break;
                }
            }).check();
        }

        Bukkit.getLogger().info("[V10Lift] Plugin loaded successfully!");
    }

    @Override
    public void onDisable() {
        dbManager.removeFromData(); //TODO Find a better way, override?

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

    public static YamlFile getMessages() {
        return messages;
    }

    public static V10LiftAPI getAPI() {
        return api;
    }

    public static boolean isVaultEnabled() {
        return vault;
    }
}
