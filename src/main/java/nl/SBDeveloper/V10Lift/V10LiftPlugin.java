package nl.SBDeveloper.V10Lift;

import nl.SBDeveloper.V10Lift.API.V10LiftAPI;
import nl.SBDeveloper.V10Lift.Commands.V10LiftCommand;
import nl.SBDeveloper.V10Lift.Listeners.BlockBreakListener;
import nl.SBDeveloper.V10Lift.Listeners.EntityDamageListener;
import nl.SBDeveloper.V10Lift.Listeners.PlayerInteractListener;
import nl.SBDeveloper.V10Lift.Listeners.SignChangeListener;
import nl.SBDeveloper.V10Lift.Utils.SBSQLiteDB;
import nl.SBDeveloper.V10Lift.Utils.SBYamlFile;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class V10LiftPlugin extends JavaPlugin {

    private static V10LiftPlugin instance;
    private static SBYamlFile config;
    private static SBSQLiteDB data;
    private static V10LiftAPI api;

    @Override
    public void onEnable() {
        instance = this;

        config = new SBYamlFile(this, "config");
        config.loadDefaults();
        data = new SBSQLiteDB(this, "data");

        api = new V10LiftAPI();

        getCommand("v10lift").setExecutor(new V10LiftCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamageListener(), this);

        getLogger().info("[V10Lift] Plugin loaded successfully!");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static V10LiftPlugin getInstance() {
        return instance;
    }

    public static SBYamlFile getSConfig() {
        return config;
    }

    public static SBSQLiteDB getData() {
        return data;
    }

    public static V10LiftAPI getAPI() {
        return api;
    }

}
