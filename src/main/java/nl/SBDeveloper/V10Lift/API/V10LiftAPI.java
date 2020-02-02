package nl.SBDeveloper.V10Lift.API;

import nl.SBDeveloper.V10Lift.API.Objects.*;
import nl.SBDeveloper.V10Lift.API.Runnables.DoorCloser;
import nl.SBDeveloper.V10Lift.API.Runnables.MoveLift;
import nl.SBDeveloper.V10Lift.Managers.AntiCopyBlockManager;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.Managers.ForbiddenBlockManager;
import nl.SBDeveloper.V10Lift.Utils.LocationSerializer;
import nl.SBDeveloper.V10Lift.Utils.XMaterial;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;

public class V10LiftAPI {
    /* Load managers... */
    private static ForbiddenBlockManager fbm;
    private static AntiCopyBlockManager acbm;

    public V10LiftAPI() {
        fbm = new ForbiddenBlockManager();
        acbm = new AntiCopyBlockManager();
    }

    /**
     * Get the ForbiddenBlockManager, to check if a material is forbidden
     *
     * @return {@link ForbiddenBlockManager}
     */
    public ForbiddenBlockManager getFBM() {
        return fbm;
    }

    /**
     * Get the AntiCopyBlockManager, to check if we can copy a material
     *
     * @return {@link AntiCopyBlockManager}
     */
    public AntiCopyBlockManager getACBM() {
        return acbm;
    }

    /* Private API methods */
    private void sortFloors(@Nonnull Lift lift) {
        Bukkit.getLogger().info("[V10Lift] Sorting floors for lift...");
        ArrayList<Map.Entry<String, Floor>> as = new ArrayList<>(lift.getFloors().entrySet());
        as.sort(Comparator.comparingInt(o -> o.getValue().getY()));
        Iterator<Map.Entry<String, Floor>> iter = as.iterator();
        lift.getFloors().clear();
        Map.Entry<String, Floor> e;
        while (iter.hasNext()) {
            e = iter.next();
            lift.getFloors().put(e.getKey(), e.getValue());
        }
    }

    private void startLift(String liftName) {
        Bukkit.getLogger().info("[V10Lift] Starting lift " + liftName);
        if (!DataManager.containsMovingTask(liftName)) {
            Lift lift = DataManager.getLift(liftName);
            DataManager.addMovingTask(liftName, Bukkit.getScheduler().scheduleSyncRepeatingTask(V10LiftPlugin.getInstance(), new MoveLift(liftName, lift.getSpeed()), lift.getSpeed(), lift.getSpeed()));
        }
    }

    /* API methods */

    /**
     * Create a new Lift
     *
     * @param p The player [owner] of the lift
     * @param liftName The name of the lift
     * @return true if created, false if null or already exists
     */
    public boolean createLift(Player p, String liftName) {
        Bukkit.getLogger().info("[V10Lift] Creating lift " + liftName);
        if (p == null || liftName == null || DataManager.containsLift(liftName)) return false;

        //TODO Add defaults to config
        DataManager.addLift(liftName, new Lift(p.getUniqueId(), 16, true));
        return true;
    }

    /**
     * Remove a lift
     *
     * @param liftName The name of the lift
     * @return true if removed, false if null or doesn't exists
     */
    public boolean removeLift(String liftName) {
        Bukkit.getLogger().info("[V10Lift] Removing lift " + liftName);
        if (liftName == null || !DataManager.containsLift(liftName)) return false;

        //TODO Remove lift from all data maps

        //TODO Stop movingtask if running

        DataManager.removeLift(liftName);
        return true;
    }

    /**
     * Rename a lift
     *
     * @param liftName The name of the lift
     * @param newName The new name of the lift
     */
    public void renameLift(String liftName, String newName) {
        Bukkit.getLogger().info("[V10Lift] Renaming lift " + liftName);
        if (liftName == null || newName == null || !DataManager.containsLift(liftName)) return;

        Lift lift = DataManager.getLift(liftName);
        DataManager.removeLift(liftName);
        DataManager.addLift(newName, lift);
        for (LiftSign ls : lift.getSigns()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setDefective").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
            BlockState bs = block.getState();
            if (!(bs instanceof Sign)) continue;
            Sign si = (Sign) bs;
            si.setLine(1, newName);
            si.update();
        }
    }

    /**
     * Add a block to a lift
     * Use {@link nl.SBDeveloper.V10Lift.API.V10LiftAPI#sortLiftBlocks(String liftName)} after!
     *
     * @param liftName The name of the lift
     * @param block The block
     * @return 0 if added, -1 if null or doesn't exists, -2 if forbidden, -3 if already added
     */
    public int addBlockToLift(String liftName, Block block) {
        if (liftName == null || block == null || !DataManager.containsLift(liftName)) return -1;
        Lift lift = DataManager.getLift(liftName);
        return addBlockToLift(lift.getBlocks(), block);
    }

    /**
     * Add a block to a lift
     * Use {@link nl.SBDeveloper.V10Lift.API.V10LiftAPI#sortLiftBlocks(String liftName)} after!
     *
     * @param blocks The blockset
     * @param block The block
     * @return 0 if added, -1 if null or doesn't exists, -2 if forbidden, -3 if already added
     */
    public int addBlockToLift(Set<LiftBlock> blocks, @Nonnull Block block) {
        Bukkit.getLogger().info("[V10Lift] Adding Block to lift...");
        Material type = block.getType();
        LiftBlock lb;
        if (type.toString().contains("SIGN")) {
            //SIGN
            if (XMaterial.isNewVersion()) {
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, ((Sign) block.getState()).getLines());
            } else {
                Bukkit.getLogger().info("Using deprecated method! " + block.getState().getRawData());
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData(), ((Sign) block.getState()).getLines());
            }
        } else {
            if (XMaterial.isNewVersion()) {
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type);
            } else {
                Bukkit.getLogger().info("Using deprecated method! " + block.getState().getRawData());
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData());
            }
        }
        return addBlockToLift(blocks, lb);
    }

    /**
     * Add a block to a lift
     * Use {@link nl.SBDeveloper.V10Lift.API.V10LiftAPI#sortLiftBlocks(String liftName)} after!
     *
     * @param blocks The blockset
     * @param block The LiftBlock
     * @return 0 if added, -1 if null or doesn't exists, -2 if forbidden, -3 if already added
     */
    public int addBlockToLift(@Nonnull Set<LiftBlock> blocks, @Nonnull LiftBlock block) {
        Bukkit.getLogger().info("[V10Lift] Adding Block to lift 2...");
        if (getFBM().isForbidden(block.getMat())) return -2;
        if (blocks.contains(block)) return -3;
        blocks.add(block);
        return 0;
    }

    /**
     * Remove a block from a lift
     * Use {@link nl.SBDeveloper.V10Lift.API.V10LiftAPI#sortLiftBlocks(String liftName)} after!
     *
     * @param liftName The name of the lift
     * @param block The block
     * @return 0 if removed, -1 if null or doesn't exists, -2 if not added
     */
    public int removeBlockFromLift(String liftName, Block block) {
        if (liftName == null || block == null || !DataManager.containsLift(liftName)) return -1;
        Lift lift = DataManager.getLift(liftName);
        Material type = block.getType();
        LiftBlock lb;
        if (type.toString().contains("SIGN")) {
            //SIGN
            if (XMaterial.isNewVersion()) {
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, ((Sign) block.getState()).getLines());
            } else {
                Bukkit.getLogger().info("Using deprecated method! " + block.getState().getRawData());
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData(), ((Sign) block.getState()).getLines());
            }
        } else {
            if (XMaterial.isNewVersion()) {
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type);
            } else {
                Bukkit.getLogger().info("Using deprecated method! " + block.getState().getRawData());
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData());
            }
        }
        if (!lift.getBlocks().contains(lb)) return -2;
        lift.getBlocks().remove(lb);
        return 0;
    }

    /**
     * Switch a block at a lift
     * Use {@link nl.SBDeveloper.V10Lift.API.V10LiftAPI#sortLiftBlocks(String liftName)} after!
     *
     * @param liftName The name of the lift
     * @param block The block
     * @return 0 if added, 1 if removed, -1 if null or doesn't exists, -2 if not added
     */
    public int switchBlockAtLift(String liftName, Block block) {
        Bukkit.getLogger().info("[V10Lift] Switching block at lift...");
        if  (liftName == null || block == null || !DataManager.containsLift(liftName)) return -1;
        return switchBlockAtLift(DataManager.getLift(liftName).getBlocks(), block);
    }

    /**
     * Switch a block at a lift
     * Use {@link nl.SBDeveloper.V10Lift.API.V10LiftAPI#sortLiftBlocks(String liftName)} after!
     *
     * @param blocks The blockset
     * @param block The block
     * @return 0 if added, 1 if removed, -1 if null or doesn't exists, -2 if not added
     */
    public int switchBlockAtLift(TreeSet<LiftBlock> blocks, Block block) {
        Bukkit.getLogger().info("[V10Lift] Switching block at lift 2...");
        if  (blocks == null || block == null) return -1;
        Material type = block.getType();
        if (getFBM().isForbidden(type)) return -2;
        LiftBlock lb;
        if (type.toString().contains("SIGN")) {
            //SIGN
            if (XMaterial.isNewVersion()) {
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, ((Sign) block.getState()).getLines());
            } else {
                Bukkit.getLogger().info("Using deprecated method! " + block.getState().getRawData());
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData(), ((Sign) block.getState()).getLines());
            }
        } else {
            if (XMaterial.isNewVersion()) {
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type);
            } else {
                Bukkit.getLogger().info("Using deprecated method! " + block.getState().getRawData());
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData());
            }
        }
        if (blocks.contains(lb)) {
            blocks.remove(lb);
            return 1;
        }
        blocks.add(lb);
        return 0;
    }

    /**
     * Sort the blocks of a lift.
     * Use this after they have been modified.
     *
     * @param liftName The name of the lift
     */
    public void sortLiftBlocks(String liftName) {
        Bukkit.getLogger().info("[V10Lift] Sorting blocks at lift...");
        if (liftName != null && DataManager.containsLift(liftName)) {
            Lift lift = DataManager.getLift(liftName);
            if (lift.getWorldName() == null) lift.setWorldName(lift.getBlocks().first().getWorld());
            World world = Bukkit.getWorld(lift.getWorldName());
            if (world == null) return;
            lift.setY(world.getMaxHeight());
            for (LiftBlock lb : lift.getBlocks()) {
                if (lb.getY() < lift.getY()) {
                    lift.setY(lb.getY());
                    lift.setWorldName(lb.getWorld());
                }
            }
        }
    }

    /**
     * Open the door
     *
     * @param liftName The name of the lift
     * @return true/false
     */
    public boolean openDoor(String liftName) {
        if (liftName == null || !DataManager.containsLift(liftName)) return false;

        Lift lift = DataManager.getLift(liftName);

        if (lift.getQueue() != null) return false;

        Floor f = null;
        for (Floor fl : lift.getFloors().values()) {
            if (fl.getY() == lift.getY() && fl.getWorld().equals(lift.getWorldName())) {
                f = fl;
                break;
            }
        }

        if (f == null) return false;

        if (lift.getDoorOpen() != null && !closeDoor(liftName)) return false;

        for (LiftBlock lb : f.getDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            block.setType(Material.AIR);
        }

        lift.setDoorOpen(f);

        if (lift.isRealistic()) {
            lift.setDoorCloser(new DoorCloser(liftName));
            //TODO Add defaults (doorclosetime) to config
            long doorCloseTime = 5 * 20;
            int pid = Bukkit.getScheduler().scheduleSyncRepeatingTask(V10LiftPlugin.getInstance(), lift.getDoorCloser(), doorCloseTime, doorCloseTime);
            lift.getDoorCloser().setPid(pid);
        }
        return true;
    }

    /**
     * Open the door
     *
     * @param lift The lift
     * @param liftName The name of the lift
     * @param f The floor
     * @return true/false
     */
    public boolean openDoor(Lift lift, String liftName, Floor f) {
        Bukkit.getLogger().info("[V10Lift] Opening door...");
        if (lift == null || liftName == null || f == null) return false;
        if (lift.getDoorOpen() != null && !closeDoor(liftName)) return false;

        for (LiftBlock lb : f.getDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            block.setType(Material.AIR);
        }

        lift.setDoorOpen(f);

        if (lift.isRealistic()) {
            lift.setDoorCloser(new DoorCloser(liftName));
            //TODO Add defaults (doorclosetime) to config
            long doorCloseTime = 5 * 20;
            int pid = Bukkit.getScheduler().scheduleSyncRepeatingTask(V10LiftPlugin.getInstance(), lift.getDoorCloser(), doorCloseTime, doorCloseTime);
            lift.getDoorCloser().setPid(pid);
        }
        return true;
    }

    /**
     * Close a lift door
     *
     * @param liftName The name of the lift
     * @return true if door was closed, false if else.
     */
    public boolean closeDoor(String liftName) {
        Bukkit.getLogger().info("[V10Lift] Closing door...");
        if (liftName == null || !DataManager.containsLift(liftName)) return false;

        Lift lift = DataManager.getLift(liftName);

        boolean blocked = false;
        if (lift.getDoorOpen() == null) {
            return true;
        }

        if (lift.isRealistic()) {
            for (LiftBlock lb : lift.getDoorOpen().getDoorBlocks()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at closeDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                for (Entity ent : block.getChunk().getEntities()) {
                    Location loc = ent.getLocation();
                    if (loc.getBlockX() == lb.getX() && loc.getBlockY() == lb.getY() && loc.getBlockZ() == lb.getZ()) {
                        blocked = true;
                        break;
                    }
                }
                if (blocked) break;
            }
        }

        if (!blocked) {
            for (LiftBlock lb : lift.getDoorOpen().getDoorBlocks()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at closeDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                BlockState state = block.getState();
                state.setType(lb.getMat());
                if (!XMaterial.isNewVersion()) {
                    state.setRawData(lb.getData());
                }
                state.update(true);
            }
            lift.setDoorOpen(null);
            if (lift.getDoorCloser() != null) lift.getDoorCloser().stop();
        }

        return !blocked;
    }

    /**
     * To check if a lift has an open door.
     *
     * @param liftName The name of the lift
     * @return true if open, false if else
     */
    public boolean hasDoorOpen(String liftName) {
        Bukkit.getLogger().info("[V10Lift] Opening door...");
        return (liftName != null && DataManager.containsLift(liftName)) && DataManager.getLift(liftName).getDoorOpen() != null;
    }

    /**
     * Adds a new floor to a lift
     *
     * @param liftName The name of the lift
     * @param floorName The name of the floor
     * @param floor The floor object
     * @return 0 if added, -1 if null or doesn't exists, -2 if height is to high, -3 if floor already exists
     */
    public int addFloor(String liftName, String floorName, Floor floor) {
        Bukkit.getLogger().info("[V10Lift] Adding door to " + floorName);
        if (liftName == null || floorName == null || floor == null || !DataManager.containsLift(liftName) || floor.getWorld() == null) return -1;
        if (floor.getY() > Objects.requireNonNull(Bukkit.getServer().getWorld(floor.getWorld()), "World is null at addNewFloor!").getMaxHeight()) return -2;
        if (floorName.length() > 13) floorName = floorName.substring(0, 13).trim();
        Lift lift = DataManager.getLift(liftName);
        if (lift.getFloors().containsKey(floorName) || lift.getFloors().containsValue(floor)) return -3;

        lift.getFloors().put(floorName, floor);
        sortFloors(lift);
        return 0;
    }

    /**
     * Removes a floor from a lift
     *
     * @param liftName The name of the lift
     * @param floorName The name of the floor
     * @return true if removed, false if null or doesn't exists
     */
    public boolean removeFloor(String liftName, String floorName) {
        Bukkit.getLogger().info("[V10Lift] Removing floor at " + liftName);
        if (liftName == null || floorName == null || !DataManager.containsLift(liftName)) return false;
        Lift lift = DataManager.getLift(liftName);
        if (!lift.getFloors().containsKey(floorName)) return false;

        lift.getFloors().remove(floorName);
        lift.getInputs().removeIf(liftBlock -> liftBlock.getFloor().equals(floorName));
        return true;
    }

    /**
     * Rename a floor from a lift
     *
     * @param liftName The name of the lift
     * @param oldName The old name of the floor
     * @param newName The new name of the floor
     * @return 0 if renamed, -1 if null or doesn't exists, -2 if floor doesn't exists, -3 if floor already exists
     */
    public int renameFloor(String liftName, String oldName, String newName) {
        Bukkit.getLogger().info("[V10Lift] Renaming floor at " + liftName);
        if (liftName == null || oldName == null || newName == null || !DataManager.containsLift(liftName)) return -1;
        Lift lift = DataManager.getLift(liftName);
        if (!lift.getFloors().containsKey(oldName)) return -2;
        if (newName.length() > 13) newName = newName.substring(0, 13).trim();
        if (lift.getFloors().containsKey(newName)) return -3;

        Floor f = lift.getFloors().get(oldName);
        lift.getFloors().remove(oldName);
        lift.getFloors().put(newName, f);
        sortFloors(lift);
        Iterator<LiftBlock> liter = lift.getInputs().iterator();
        LiftBlock lb;
        ArrayList<LiftBlock> newBlocks = new ArrayList<>();
        while (liter.hasNext()) {
            lb = liter.next();
            if (lb.getFloor().equals(oldName)) {
                liter.remove();
                newBlocks.add(new LiftBlock(lb.getWorld(), lb.getX(), lb.getY(), lb.getZ(), newName));
            }
        }
        newBlocks.forEach(nlb -> lift.getInputs().add(nlb));
        return 0;
    }

    /**
     * Check if a lift is defective
     *
     * @param liftName The name of the lift
     * @return true/false
     */
    public boolean isDefective(String liftName) {
        if (liftName == null || !DataManager.containsLift(liftName)) return false;
        return DataManager.getLift(liftName).isDefective();
    }

    /**
     * Set a lift to (not) defective
     *
     * @param liftName The name of the lift
     * @param state true/false
     * @return 0 if set, -1 if null or doesn't exists, -2 if same state, -3 if no signs, -4 if wrong sign
     */
    public int setDefective(String liftName, boolean state) {
        Bukkit.getLogger().info("[V10Lift] Set defective");
        if (liftName == null || !DataManager.containsLift(liftName)) return -1;
        Lift lift = DataManager.getLift(liftName);
        boolean oldState = lift.isDefective();
        if (oldState == state) return -2;
        lift.setDefective(state);
        Iterator<LiftSign> liter = lift.getSigns().iterator();
        if (state) {
            //SET DEFECTIVE
            int max = lift.getSigns().size();
            if (max == 0) return -3;
            LiftSign ls;
            if (max == 1) {
                //If one sign, update that one.
                ls = liter.next();
            } else {
                //If multiple signs, get random one.
                int r = new Random().nextInt(max);
                for (int i = 0; i < r; i++) {
                    liter.next();
                }
                ls = liter.next();
            }

            //Update sign
            Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setDefective").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
            BlockState bs = block.getState();
            if (!(bs instanceof Sign)) {
                Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                liter.remove();
                return -4;
            }

            Sign s = (Sign) bs;
            ls.setOldText(s.getLine(3));
            //TODO Add defaults to config
            s.setLine(3, ChatColor.MAGIC + "Defect!");
            s.update();

            //Update all other signs
            for (LiftBlock lb : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setDefective").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;

                s = (Sign) bs;
                lift.setSignText(s.getLine(3));
                s.setLine(3, ChatColor.MAGIC + "Defect!");
                s.update();
            }
        } else {
            LiftSign ls;
            BlockState bs;
            Sign s;
            while (liter.hasNext()) {
                ls = liter.next();
                bs = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setDefective").getBlockAt(ls.getX(), ls.getY(), ls.getZ()).getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(bs.getBlock().getLocation()));
                    liter.remove();
                    continue;
                }
                s = (Sign) bs;
                if (s.getLine(3).equals(ChatColor.MAGIC + "Defect!")) {
                    s.setLine(3, ls.getOldText());
                    s.update();
                    ls.setOldText(null);
                    for (LiftBlock lb : lift.getBlocks()) {
                        bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setDefective").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                        if (!(bs instanceof Sign)) continue;

                        s = (Sign) bs;
                        s.setLine(3, lift.getSignText());
                        s.update();
                        lift.setSignText(null);
                    }
                }
            }
        }
        return 0;
    }

    /**
     * Get the whitelist of a lift
     *
     * @param liftName The name of the lift
     * @param floorName The name of the floor
     * @return list with UUIDs of the players
     */
    public HashSet<UUID> getWhitelist(String liftName, String floorName) {
        HashSet<UUID> ret = new HashSet<>();
        if (liftName != null && floorName != null && DataManager.containsLift(liftName)) {
            Lift lift = DataManager.getLift(liftName);
            if (lift.getFloors().containsKey(floorName)) {
                ret = lift.getFloors().get(floorName).getWhitelist();
            }
        }
        return ret;
    }

    /**
     * Check if a lift is offline
     *
     * @param liftName The name of the lift
     * @return true/false
     */
    public boolean isOffline(String liftName) {
        if (liftName == null || !DataManager.containsLift(liftName)) return false;
        return DataManager.getLift(liftName).isOffline();
    }

    /**
     * Set a lift to (not) offline
     *
     * @param liftName The name of the lift
     * @param state true/false
     * @return 0 if set, -1 if null or doesn't exists, -2 if same state
     */
    public int setOffline(String liftName, boolean state) {
        if (liftName == null || !DataManager.containsLift(liftName)) return -1;
        Lift lift = DataManager.getLift(liftName);
        boolean oldState = lift.isOffline();
        if (oldState == state) return -2;
        lift.setOffline(state);
        Iterator<LiftSign> liter = lift.getSigns().iterator();
        BlockState bs;
        Sign sign;
        if (state) {
            for (LiftBlock lb : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setOffline").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                //TODO Add defaults
                if (!sign.getLine(0).equalsIgnoreCase("[v10lift]")) continue;
                sign.setLine(3, ChatColor.RED + "Disabled");
                sign.update();
            }

            while (liter.hasNext()) {
                LiftSign ls = liter.next();
                bs = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setOffline").getBlockAt(ls.getX(), ls.getY(), ls.getZ()).getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(bs.getBlock().getLocation()));
                    liter.remove();
                    continue;
                }
                sign = (Sign) bs;
                ls.setOldText(sign.getLine(3));
                //TODO Add defaults
                sign.setLine(3, ChatColor.RED + "Disabled");
                sign.update();
            }
        } else {
            for (LiftBlock lb : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setOffline").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                //TODO Add defaults
                if (!sign.getLine(0).equalsIgnoreCase("[v10lift]")) continue;
                sign.setLine(3, "");
                sign.update();
            }

            while (liter.hasNext()) {
                LiftSign ls = liter.next();
                bs = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setOffline").getBlockAt(ls.getX(), ls.getY(), ls.getZ()).getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(bs.getBlock().getLocation()));
                    liter.remove();
                    continue;
                }
                sign = (Sign) bs;
                sign.setLine(3, ls.getOldText());
                sign.update();
                ls.setOldText(null);
            }
        }
        return 0;
    }

    /**
     * Check if a lift contains the block as rope
     *
     * @param liftName The name of the lift
     * @param block The block
     *
     * @return true/false
     */
    public boolean containsRope(String liftName, Block block) {
        if (liftName == null || block == null || !DataManager.containsLift(liftName)) return false;

        Lift lift = DataManager.getLift(liftName);
        if (lift.getRopes().isEmpty()) return false;

        String world = block.getWorld().getName();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();

        for (LiftRope rope : lift.getRopes()) {
            if (x != rope.getX() || z != rope.getZ()) continue;
            if (y >= rope.getMinY() && y <= rope.getMaxY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a block is a rope
     *
     * @param b The block
     *
     * @return true/false
     */
    public boolean isRope(Block b) {
        for (String lift : DataManager.getLifts().keySet()) {
            if (containsRope(lift, b)) return true;
        }
        return false;
    }

    /**
     * Send info about a lift to a player
     * @param p The player
     * @param liftName The name of the lift
     */
    public void sendLiftInfo(Player p, String liftName) {
        sendLiftInfo(p, liftName, DataManager.getLift(liftName));
    }

    /**
     * Send info about a lift to a player
     * @param p The player
     * @param liftName The name of the lift
     * @param lift The lift
     */
    public void sendLiftInfo(@Nonnull Player p, String liftName, @Nonnull Lift lift) {
        if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            p.sendMessage(ChatColor.RED + "You don't have the permission to check this lift!");
        } else {
            p.sendMessage(ChatColor.GOLD + "Elevator: " + ChatColor.YELLOW + liftName);
            p.sendMessage(ChatColor.GOLD + "Settings:");
            p.sendMessage(ChatColor.GREEN + "  Speed: " + ChatColor.YELLOW + lift.getSpeed());
            p.sendMessage(ChatColor.GREEN + "  Realistic Mode: " + ChatColor.YELLOW + lift.isRealistic());
            p.sendMessage(ChatColor.GREEN + "  Malfunction: " + ChatColor.YELLOW + lift.isDefective());
            p.sendMessage(ChatColor.GOLD + "Floors:");
            if (lift.getFloors().isEmpty()) {
                p.sendMessage(ChatColor.RED + "None.");
            } else {
                for (Map.Entry<String, Floor> entry : lift.getFloors().entrySet()) {
                    p.sendMessage(ChatColor.GREEN + "  " + entry.getKey() + ":");
                    Floor f = entry.getValue();
                    p.sendMessage(ChatColor.YELLOW + "    World: " + ChatColor.GREEN + f.getWorld());
                    p.sendMessage(ChatColor.YELLOW + "    Height: " + ChatColor.GREEN + f.getY());
                    p.sendMessage(ChatColor.YELLOW + "    Whitelist:");
                    if (f.getWhitelist().isEmpty()) {
                        p.sendMessage(ChatColor.GOLD + "      None.");
                    } else {
                        ChatColor color = ChatColor.DARK_PURPLE;
                        Iterator<UUID> iter = f.getWhitelist().iterator();
                        StringBuilder sb = new StringBuilder();
                        sb.append("      ").append(color).append(Bukkit.getOfflinePlayer(iter.next()).getName());
                        while (iter.hasNext()) {
                            if (color == ChatColor.DARK_PURPLE) {
                                color = ChatColor.LIGHT_PURPLE;
                            } else {
                                color = ChatColor.DARK_PURPLE;
                            }
                            sb.append(ChatColor.AQUA).append(", ").append(color).append(Bukkit.getOfflinePlayer(iter.next()).getName());
                        }
                        p.sendMessage(sb.toString());
                    }
                }
            }
        }
    }

    /**
     * Add a rope to a lift
     *
     * @param lift The name of the lift
     * @param world The world
     * @param x The x-pos
     * @param minY The min y-pos
     * @param maxY The max y-pos
     * @param z The z-pos
     * @return 0 if added, -1 if null or doesn't exists, -2 if not same mat, -3 if already a rope, -4 if forbidden material
     */
    public int addRope(String lift, World world, int x, int minY, int maxY, int z) {
        Bukkit.getLogger().info("[V10Lift] Adding rope to " + lift);
        if (lift == null || !DataManager.containsLift(lift) || world == null) return -1;

        boolean change = minY > maxY;
        Block block = world.getBlockAt(x, minY, z);
        if (isRope(block)) return -3;
        Material mat = block.getType();
        if (getFBM().isForbidden(mat)) return -4;

        for (int i = minY + 1; i <= maxY; i++) {
            block = world.getBlockAt(x, i, z);
            if (isRope(block)) return -3;
            if (block.getType() != mat) return -2;
        }
        DataManager.getLift(lift).getRopes().add(new LiftRope(mat, world.getName(), x, minY, maxY, z));
        return 0;
    }

    /**
     * Remove a rope from a lift
     *
     * @param lift The name of the lift
     * @param block The block
     *
     * @return true/false
     */
    public boolean removeRope(String lift, Block block) {
        Bukkit.getLogger().info("[V10Lift] Removing rope from " + lift);
        if (lift == null || block == null || !DataManager.containsLift(lift) || !containsRope(lift, block)) return false;

        String world = block.getWorld().getName();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        Iterator<LiftRope> riter = DataManager.getLift(lift).getRopes().iterator();
        while (riter.hasNext()) {
            LiftRope rope = riter.next();
            if (x != rope.getX() || z != rope.getZ()) continue;
            if (world.equals(rope.getWorld())) {
                if (y >= rope.getMinY() && y <= rope.getMaxY()) {
                    riter.remove();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Set the queue of a lift
     *
     * @param liftName The name of the lift
     * @param queue The queue
     * @return true/false
     */
    public boolean setQueue(String liftName, LinkedHashMap<String, Floor> queue) {
        if (liftName == null || queue == null || !DataManager.containsLift(liftName)) return false;

        Lift lift = DataManager.getLift(liftName);
        lift.setQueue(new LinkedHashMap<>());
        for (Map.Entry<String, Floor> e : queue.entrySet()) {
            addToQueue(liftName, e.getValue(), e.getKey());
        }
        return true;
    }

    /**
     * Add a location to the queue
     *
     * @param lift The name of the lift
     * @param y The y-pos
     * @param world The world
     *
     * @return true/false
     */
    public boolean addToQueue(String lift, int y, World world) {
        return addToQueue(lift, y, world, null);
    }

    /**
     * Add a location to the queue
     *
     * @param lift The name of the lift
     * @param y The y-pos
     * @param world The world
     * @param floorName The name of the flor
     *
     * @return true/false
     */
    public boolean addToQueue(String lift, int y, @Nonnull World world, String floorName) {
        Bukkit.getLogger().info("[V10Lift] Adding to queue: " + lift);
        return addToQueue(lift, new Floor(y, world.getName()), floorName);
    }

    /**
     * Add a location to the queue
     *
     * @param lift The name of the lift
     * @param floor The {@link Floor}
     * @param floorName The name of the flor
     *
     * @return true/false
     */
    public boolean addToQueue(String lift, Floor floor, String floorName) {
        Bukkit.getLogger().info("[V10Lift] Adding to queue: " + lift);
        if (lift == null || floor == null || !DataManager.containsLift(lift)) return false;

        Lift l = DataManager.getLift(lift);
        if (l.getQueue() == null) {
            l.setQueue(new LinkedHashMap<>());
        }

        if (!l.getQueue().containsValue(floor)) {
            if (floorName == null) {
                floorName = ChatColor.MAGIC + "-----";
                for (Map.Entry<String, Floor> e : l.getFloors().entrySet()) {
                    if (e.getValue().equals(floor)) {
                        floorName = e.getKey();
                        floor = e.getValue();
                        break;
                    }
                }
            }

            l.getQueue().put(floorName, floor);
            startLift(lift);
            return true;
        }
        return false;
    }
}
