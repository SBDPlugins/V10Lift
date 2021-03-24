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
import nl.SBDeveloper.V10Lift.sbutils.UpdateManager;
import nl.SBDeveloper.V10Lift.sbutils.YamlFile;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

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

        //Load the messages
        messages = new YamlFile("messages");
        messages.loadDefaults();

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
            Bukkit.getLogger().info("[V10Lift] Loading Vault hook for group whitelist support.");
            vault = true;
            VaultManager.setupPermissions();
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
        metrics.addCustomChart(new Metrics.SingleLineChart("lifts", () -> DataManager.getLifts().size()));

        //Load the update checker
        if (!getSConfig().getFile().contains("CheckUpdates") || getSConfig().getFile().getBoolean("CheckUpdates")) {
            UpdateManager manager = new UpdateManager(this, 72317, UpdateManager.CheckType.SPIGOT);

            manager.handleResponse((versionResponse, version) -> {
                if (versionResponse == UpdateManager.VersionResponse.FOUND_NEW) {
                    Bukkit.getLogger().warning("[V10Lift] There is a new version available! Current: " + this.getDescription().getVersion() + " New: " + version);
                    Bukkit.getLogger().info("[V10Lift] Trying to download...");

                    manager.handleDownloadResponse((downloadResponse, path) -> {
                        if (downloadResponse == UpdateManager.DownloadResponse.DONE) {
                            Bukkit.getLogger().info("[V10Lift] Update done! After a restart, it should be loaded.");
                        } else if (downloadResponse == UpdateManager.DownloadResponse.UNAVAILABLE) {
                            Bukkit.getLogger().warning("[V10Lift] Couldn't download the update, because it's not a Spigot resource.");
                        } else if (downloadResponse == UpdateManager.DownloadResponse.ERROR) {
                            Bukkit.getLogger().severe("[V10Lift] Unable to download the newest file.");
                        }
                    }).runUpdate();
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
