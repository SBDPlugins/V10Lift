package nl.SBDeveloper.V10Lift.api.runnables;

import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import nl.SBDeveloper.V10Lift.managers.DataManager;
import org.bukkit.Bukkit;

/** The DoorCloser runnable, used for checking if the door can be closed. */
public class DoorCloser implements Runnable {
    private final String liftName;
    private final int taskID;

    public DoorCloser(String liftName) {
        this.liftName = liftName;

        final long doorCloseTime = V10LiftPlugin.getSConfig().getFile().getLong("DoorCloseTime");
        this.taskID = Bukkit.getScheduler().runTaskTimer(V10LiftPlugin.getInstance(), this, doorCloseTime, doorCloseTime).getTaskId();
    }

    @Override
    public void run() {
        if (V10LiftPlugin.getAPI().closeDoor(liftName)) stop();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        if (DataManager.containsLift(liftName)) DataManager.getLift(liftName).setDoorCloser(null);
    }
}
