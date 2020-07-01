package nl.SBDeveloper.V10Lift.managers;

import nl.SBDeveloper.V10Lift.api.objects.Lift;
import nl.SBDeveloper.V10Lift.api.objects.LiftBlock;

import java.util.*;

public class DataManager {
    /* A manager for general HashMaps */
    private static LinkedHashMap<String, Lift> lifts = new LinkedHashMap<>();
    private static LinkedHashMap<UUID, TreeSet<LiftBlock>> builds = new LinkedHashMap<>();
    private static LinkedHashMap<UUID, String> editors = new LinkedHashMap<>();
    private static LinkedHashMap<UUID, String> inputEdits = new LinkedHashMap<>();
    private static ArrayList<UUID> inputRemoves = new ArrayList<>();
    private static ArrayList<UUID> offlineEdits = new ArrayList<>();
    private static ArrayList<UUID> offlineRemoves = new ArrayList<>();
    private static ArrayList<UUID> builder = new ArrayList<>();
    private static LinkedHashMap<UUID, LiftBlock> ropeEdits = new LinkedHashMap<>();
    private static ArrayList<UUID> ropeRemoves = new ArrayList<>();
    private static HashMap<UUID, String> doorEdits = new HashMap<>();
    private static ArrayList<UUID> whoisReq = new ArrayList<>();
    private static HashMap<String, Integer> movingTasks = new HashMap<>();

    /* HashMap methods */

    // //
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

    public static LinkedHashMap<String, Lift> getLifts() { return lifts; }

    // //
    public static boolean containsPlayer(UUID player) {
        return builds.containsKey(player);
    }

    public static void addPlayer(UUID player) {
        builds.put(player, new TreeSet<>());
    }

    public static void removePlayer(UUID player) {
        builds.remove(player);
    }

    public static TreeSet<LiftBlock> getPlayer(UUID player) {
        return builds.get(player);
    }

    // //
    public static boolean containsInputEditsPlayer(UUID player) {
        return inputEdits.containsKey(player);
    }

    public static void addInputEditsPlayer(UUID player, String liftName) {
        inputEdits.put(player, liftName);
    }

    public static void removeInputEditsPlayer(UUID player) {
        inputEdits.remove(player);
    }

    public static String getInputEditsPlayer(UUID player) {
        return inputEdits.get(player);
    }

    // //
    public static boolean containsRopeEditPlayer(UUID player) {
        return ropeEdits.containsKey(player);
    }

    public static void addRopeEditPlayer(UUID player, LiftBlock liftBlock) {
        ropeEdits.put(player, liftBlock);
    }

    public static void removeRopeEditPlayer(UUID player) {
        ropeEdits.remove(player);
    }

    public static LiftBlock getRopeEditPlayer(UUID player) {
        return ropeEdits.get(player);
    }

    // //
    public static boolean containsEditPlayer(UUID player) {
        return editors.containsKey(player);
    }

    public static boolean containsEditLift(String liftName) {
        return editors.containsValue(liftName);
    }

    public static void addEditPlayer(UUID player, String liftName) {
        editors.put(player, liftName);
    }

    public static void removeEditPlayer(UUID player) {
        editors.remove(player);
    }

    public static String getEditPlayer(UUID player) {
        return editors.get(player);
    }

    public static LinkedHashMap<UUID, String> getEditors() { return editors; }

    // //
    public static void addMovingTask(String liftName, int taskid) {
        movingTasks.put(liftName, taskid);
    }

    public static void removeMovingTask(String liftName) {
        movingTasks.remove(liftName);
    }

    public static boolean containsMovingTask(String liftName) {
        return movingTasks.containsKey(liftName);
    }

    public static int getMovingTask(String liftName) {
        return movingTasks.get(liftName);
    }

    public static void clearMovingTasks() { movingTasks.clear(); }

    // //
    public static boolean containsDoorEditPlayer(UUID player) {
        return doorEdits.containsKey(player);
    }

    public static void addDoorEditPlayer(UUID player, String floorName) {
        doorEdits.put(player, floorName);
    }

    public static void removeDoorEditPlayer(UUID player) {
        doorEdits.remove(player);
    }

    public static String getDoorEditPlayer(UUID player) {
        return doorEdits.get(player);
    }

    /* ArrayList methods */

    // //
    public static boolean containsOfflineEditsPlayer(UUID player) {
        return offlineEdits.contains(player);
    }

    public static void addOfflineEditsPlayer(UUID player) {
        offlineEdits.add(player);
    }

    public static void removeOfflineEditsPlayer(UUID player) {
        offlineEdits.remove(player);
    }

    // //
    public static boolean containsOfflineRemovesPlayer(UUID player) {
        return offlineRemoves.contains(player);
    }

    public static void addOfflineRemovesPlayer(UUID player) {
        offlineRemoves.add(player);
    }

    public static void removeOfflineRemovesPlayer(UUID player) {
        offlineRemoves.remove(player);
    }

    // //
    public static boolean containsBuilderPlayer(UUID player) {
        return builder.contains(player);
    }

    public static void addBuilderPlayer(UUID player) {
        builder.add(player);
    }

    public static void removeBuilderPlayer(UUID player) {
        builder.remove(player);
    }

    // //
    public static boolean containsRopeRemovesPlayer(UUID player) {
        return ropeRemoves.contains(player);
    }

    public static void addRopeRemovesPlayer(UUID player) {
        ropeRemoves.add(player);
    }

    public static void removeRopeRemovesPlayer(UUID player) {
        ropeRemoves.remove(player);
    }

    // //
    public static boolean containsInputRemovesPlayer(UUID player) {
        return inputRemoves.contains(player);
    }

    public static void addInputRemovesPlayer(UUID player) {
        inputRemoves.add(player);
    }

    public static void removeInputRemovesPlayer(UUID player) {
        inputRemoves.remove(player);
    }

    // //
    public static boolean containsWhoisREQPlayer(UUID player) {
        return whoisReq.contains(player);
    }

    public static void addWhoisREQPlayer(UUID player) {
        whoisReq.add(player);
    }

    public static void removeWhoisREQPlayer(UUID player) {
        whoisReq.remove(player);
    }
}
