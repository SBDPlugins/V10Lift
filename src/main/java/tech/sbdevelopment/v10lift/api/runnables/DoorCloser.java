package tech.sbdevelopment.v10lift.api.runnables;

import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.managers.DataManager;
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
        if (V10LiftAPI.getInstance().closeDoor(liftName)) stop();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        if (DataManager.containsLift(liftName)) DataManager.getLift(liftName).setDoorCloser(null);
    }
}
