package nl.SBDeveloper.V10Lift.Managers;

import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class DataManager {
    /* A manager for general HashMaps */
    private static LinkedHashMap<String, Lift> lifts = new LinkedHashMap<>();
    private static LinkedHashMap<UUID, ArrayList<Block>> builds = new LinkedHashMap<>();

    public static void addLift(String liftName, Lift lift) {
        lifts.put(liftName, lift);
    }

    public static void removeLift(String liftName) {
        lifts.remove(liftName);
    }

    public static boolean containsLift(String liftName) {
        return lifts.containsKey(liftName);
    }

    public static Lift getLift(String liftName) {
        return lifts.get(liftName);
    }

    public static LinkedHashMap<String, Lift> getLifts() {
        return lifts;
    }

    public static boolean containsPlayer(UUID player) {
        return builds.containsKey(player);
    }

    public static void addPlayer(UUID player) {
        builds.put(player, new ArrayList<>());
    }

    public static void removePlayer(UUID player) {
        builds.remove(player);
    }

    public static ArrayList<Block> getPlayer(UUID player) {
        return builds.get(player);
    }
}
