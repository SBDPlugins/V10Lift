package tech.sbdevelopment.v10lift.api.runnables;

import org.bukkit.Bukkit;
import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.api.objects.Lift;
import tech.sbdevelopment.v10lift.managers.DataManager;

/**
 * The DoorCloser runnable, used for checking if the door can be closed.
 */
public class DoorCloser implements Runnable {
    private final Lift lift;
    private final int taskID;

    public DoorCloser(Lift lift) {
        this.lift = lift;

        final long doorCloseTime = V10LiftPlugin.getSConfig().getFile().getLong("DoorCloseTime");
        this.taskID = Bukkit.getScheduler().runTaskTimer(V10LiftPlugin.getInstance(), this, doorCloseTime, doorCloseTime).getTaskId();
    }

    @Override
    public void run() {
        if (lift.closeDoor()) stop();
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(taskID);
        lift.setDoorCloser(null);
    }
}
