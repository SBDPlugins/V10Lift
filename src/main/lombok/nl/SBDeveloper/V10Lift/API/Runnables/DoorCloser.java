package nl.SBDeveloper.V10Lift.API.Runnables;

import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.Bukkit;

public class DoorCloser implements Runnable {
    private final String liftName;
    private int pid;

    public DoorCloser(String liftName) {
        this.liftName = liftName;
    }

    @Override
    public void run() {
        if (V10LiftPlugin.getAPI().closeDoor(liftName)) stop();
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(pid);
        if (DataManager.containsLift(liftName)) DataManager.getLift(liftName).setDoorCloser(null);
    }
}
