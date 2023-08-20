package tech.sbdevelopment.v10lift.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.objects.Lift;
import tech.sbdevelopment.v10lift.api.objects.LiftSign;
import tech.sbdevelopment.v10lift.managers.DataManager;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * The Main API class, for all the API methods
 */
public class V10LiftAPI {
    @Getter
    private static final List<Lift> lifts = new ArrayList<>();

    /**
     * Create a new Lift
     *
     * @param p        The player [owner] of the lift
     * @param liftName The name of the lift
     * @return true if created, false if null or already exists
     */
    public boolean createLift(@Nonnull Player p, @Nonnull String liftName) {
        if (isLift(liftName)) return false;

        lifts.add(new Lift(p.getUniqueId(), V10LiftPlugin.getSConfig().getFile().getInt("DefaultSpeed"), V10LiftPlugin.getSConfig().getFile().getBoolean("DefaultRealistic")));
        return true;
    }

    /**
     * Remove a lift
     *
     * @param liftName The name of the lift
     * @return true if removed, false if null or doesn't exists
     */
    public boolean deleteLift(@Nonnull String liftName) {
        Optional<Lift> liftOpt = getLift(liftName);
        if (liftOpt.isEmpty()) return false;

        Iterator<Map.Entry<UUID, String>> iter = DataManager.getEditors().entrySet().iterator();
        HashSet<UUID> activeEdits = new HashSet<>();
        while (iter.hasNext()) {
            Map.Entry<UUID, String> entry = iter.next();
            if (entry.getValue().equals(liftName)) {
                activeEdits.add(entry.getKey());
                iter.remove();
            }
        }

        for (UUID puuid : activeEdits) {
            DataManager.removeInputEditsPlayer(puuid);
            DataManager.removeInputRemovesPlayer(puuid);
            DataManager.removeOfflineEditsPlayer(puuid);
            DataManager.removeOfflineRemovesPlayer(puuid);
            DataManager.removeBuilderPlayer(puuid);
            DataManager.removeRopeEditPlayer(puuid);
            DataManager.removeRopeRemovesPlayer(puuid);
            DataManager.removeDoorEditPlayer(puuid);
        }

        if (DataManager.containsMovingTask(liftName)) {
            Bukkit.getScheduler().cancelTask(DataManager.getMovingTask(liftName));
            DataManager.removeMovingTask(liftName);
        }

        lifts.remove(liftOpt.get());
        V10LiftPlugin.getDBManager().remove(liftName);
        return true;
    }

    public boolean isLift(@Nonnull String name) {
        return lifts.stream().anyMatch(lift -> lift.getName().equalsIgnoreCase(name));
    }

    public Optional<Lift> getLift(@Nonnull String name) {
        return lifts.stream().filter(lift -> lift.getName().equalsIgnoreCase(name)).findFirst();
    }

    /**
     * Get the name of a lift by a location (checking for cab blocks)
     *
     * @param loc The location you want to check for
     * @return The liftname
     */
    public Optional<Lift> getLift(@Nonnull Location loc) {
        return lifts.stream().filter(lift -> lift.getBlocks().stream().anyMatch(block -> block.getWorld().equals(loc.getWorld().getName()) && block.getX() == loc.getBlockX() && block.getZ() == loc.getBlockZ())).findFirst();
    }

    /**
     * Rename a lift
     *
     * @param liftName The name of the lift
     * @param newName  The new name of the lift
     */
    public boolean renameLift(@Nonnull String liftName, @Nonnull String newName) {
        Optional<Lift> liftOpt = getLift(liftName);
        if (liftOpt.isEmpty() || isLift(newName)) return false;

        liftOpt.get().setName(newName);
        for (LiftSign ls : liftOpt.get().getSigns()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at renameLift").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
            BlockState bs = block.getState();
            if (!(bs instanceof Sign)) continue;
            Sign si = (Sign) bs;
            si.setLine(1, newName);
            si.update();
        }
        return true;
    }

    /**
     * Check if a block is a rope
     *
     * @param b The block
     * @return true/false
     */
    public boolean isRope(Block b) {
        return getLifts().stream().anyMatch(lift -> lift.containsRope(b));
    }
}
