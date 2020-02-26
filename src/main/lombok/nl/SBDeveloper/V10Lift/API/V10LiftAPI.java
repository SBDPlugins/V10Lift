package nl.SBDeveloper.V10Lift.API;

import nl.SBDeveloper.V10Lift.API.Objects.*;
import nl.SBDeveloper.V10Lift.API.Runnables.DoorCloser;
import nl.SBDeveloper.V10Lift.API.Runnables.MoveLift;
import nl.SBDeveloper.V10Lift.Managers.AntiCopyBlockManager;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.Managers.ForbiddenBlockManager;
import nl.SBDeveloper.V10Lift.Utils.ConfigUtil;
import nl.SBDeveloper.V10Lift.Utils.DirectionUtil;
import nl.SBDeveloper.V10Lift.Utils.DoorUtil;
import nl.SBDeveloper.V10Lift.Utils.XMaterial;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import nl.SBDevelopment.SBUtilities.Utils.LocationSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
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
        if (!DataManager.containsMovingTask(liftName)) {
            Lift lift = DataManager.getLift(liftName);
            DataManager.addMovingTask(liftName, Bukkit.getScheduler().scheduleSyncRepeatingTask(V10LiftPlugin.getInstance(), new MoveLift(liftName, lift.getSpeed()), lift.getSpeed(), lift.getSpeed()));
        }
    }

    /* API methods */

    /*
    @todo Fix creating lifts in adventure not working
     */

    /**
     * Create a new Lift
     *
     * @param p The player [owner] of the lift
     * @param liftName The name of the lift
     * @return true if created, false if null or already exists
     */
    public boolean createLift(Player p, String liftName) {
        if (p == null || liftName == null || DataManager.containsLift(liftName)) return false;

        DataManager.addLift(liftName, new Lift(p.getUniqueId(), V10LiftPlugin.getSConfig().getFile().getInt("DefaultSpeed"), V10LiftPlugin.getSConfig().getFile().getBoolean("DefaultRealistic")));
        return true;
    }

    /**
     * Remove a lift
     *
     * @param liftName The name of the lift
     * @return true if removed, false if null or doesn't exists
     */
    public boolean removeLift(String liftName) {
        if (liftName == null || !DataManager.containsLift(liftName)) return false;

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

        DataManager.removeLift(liftName);
        V10LiftPlugin.getDBManager().removeFromData(liftName);
        return true;
    }

    /**
     * Get the name of a lift by a location (checking for cab blocks)
     *
     * @param loc The location you want to check for
     * @return The liftname
     */
    public String getLiftByLocation(Location loc) {
        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
            for (LiftBlock block : entry.getValue().getBlocks()) {
                //Check for world, x and z
                if (block.getWorld().equals(Objects.requireNonNull(loc.getWorld(), "World is null at getLiftByLocation").getName()) && block.getX() == loc.getBlockX() && block.getZ() == loc.getBlockZ()) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    /**
     * Rename a lift
     *
     * @param liftName The name of the lift
     * @param newName The new name of the lift
     */
    public void renameLift(String liftName, String newName) {
        if (liftName == null || newName == null || !DataManager.containsLift(liftName)) return;

        Lift lift = DataManager.getLift(liftName);
        DataManager.removeLift(liftName);
        DataManager.addLift(newName, lift);
        for (LiftSign ls : lift.getSigns()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at renameLift").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
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
        Material type = block.getType();
        LiftBlock lb;
        if (XMaterial.isNewVersion()) {
            if (type.toString().contains("SIGN")) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13 & is sign");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block), ((Sign) block.getState()).getLines());
            } else if (block.getBlockData() instanceof org.bukkit.block.data.Directional && block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13 & bisected");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block), DirectionUtil.getBisected(block));
            } else if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block));
            } else {
                Bukkit.getLogger().info("Block not instanceof Dir 1.13");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type);
            }
        } else {
            if (type.toString().contains("SIGN")) {
                Bukkit.getLogger().info("Block instanceof Dir 1.12 & is sign");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData(), ((Sign) block.getState()).getLines());
            } else {
                Bukkit.getLogger().info("Block no sign 1.12");
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
        if (XMaterial.isNewVersion()) {
            if (type.toString().contains("SIGN")) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13 & is sign");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block), ((Sign) block.getState()).getLines());
            } else if (block.getBlockData() instanceof org.bukkit.block.data.Directional && block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13 & bisected");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block), DirectionUtil.getBisected(block));
            } else if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block));
            } else {
                Bukkit.getLogger().info("Block not instanceof Dir 1.13");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type);
            }
        } else {
            if (type.toString().contains("SIGN")) {
                Bukkit.getLogger().info("Block instanceof Dir 1.12 & is sign");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData(), ((Sign) block.getState()).getLines());
            } else {
                Bukkit.getLogger().info("Block no sign 1.12");
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
        if  (blocks == null || block == null) return -1;
        Material type = block.getType();
        if (getFBM().isForbidden(type)) return -2;
        LiftBlock lb;
        if (XMaterial.isNewVersion()) {
            if (type.toString().contains("SIGN")) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13 & is sign");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block), ((Sign) block.getState()).getLines());
            } else if (block.getBlockData() instanceof org.bukkit.block.data.Directional && block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13 & bisected");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block), DirectionUtil.getBisected(block));
            } else if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
                Bukkit.getLogger().info("Block instanceof Dir 1.13");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, DirectionUtil.getDirection(block));
            } else {
                Bukkit.getLogger().info("Block not instanceof Dir 1.13");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type);
            }
        } else {
            if (type.toString().contains("SIGN")) {
                Bukkit.getLogger().info("Block instanceof Dir 1.12 & is sign");
                lb = new LiftBlock(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), type, block.getState().getRawData(), ((Sign) block.getState()).getLines());
            } else {
                Bukkit.getLogger().info("Block no sign 1.12");
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

        for (LiftBlock lb : f.getRealDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            DoorUtil.openDoor(block);
        }

        lift.setDoorOpen(f);

        if (lift.isRealistic()) {
            lift.setDoorCloser(new DoorCloser(liftName));
            long doorCloseTime = V10LiftPlugin.getSConfig().getFile().getLong("DoorCloseTime");

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
        if (lift == null || liftName == null || f == null) return false;
        if (lift.getDoorOpen() != null && !closeDoor(liftName)) return false;

        for (LiftBlock lb : f.getDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            block.setType(Material.AIR);
        }

        for (LiftBlock lb : f.getRealDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            DoorUtil.openDoor(block);
        }

        lift.setDoorOpen(f);

        if (lift.isRealistic()) {
            lift.setDoorCloser(new DoorCloser(liftName));
            long doorCloseTime = V10LiftPlugin.getSConfig().getFile().getLong("DoorCloseTime");

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

            for (LiftBlock lb : lift.getDoorOpen().getRealDoorBlocks()) {
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
            for (LiftBlock lb : lift.getDoorOpen().getRealDoorBlocks()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at closeDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                DoorUtil.closeDoor(block);
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
        if (liftName == null || !DataManager.containsLift(liftName)) return -1;
        Lift lift = DataManager.getLift(liftName);
        boolean oldState = lift.isDefective();
        if (oldState == state) return -2;
        lift.setDefective(state);
        if (state) {
            //SET DEFECTIVE
            //Update sign
            for (LiftSign ls : lift.getSigns()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setDefective").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                BlockState bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    return -4;
                }

                Sign s = (Sign) bs;
                ls.setOldText(s.getLine(3));
                s.setLine(3, ConfigUtil.getColored("DefectText"));
                s.update();
            }

            //Update all cab signs
            for (LiftBlock lb : lift.getBlocks()) {
                BlockState bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setDefective").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;

                Sign s = (Sign) bs;
                lift.setSignText(s.getLine(3));
                s.setLine(3, ConfigUtil.getColored("DefectText"));
                s.update();
            }
        } else {

            //Update sign
            for (LiftSign ls : lift.getSigns()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setDefective").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                BlockState bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    return -4;
                }

                Sign s = (Sign) bs;
                s.setLine(3, ls.getOldText());
                ls.setOldText(null);
                s.update();
            }

            //Update all cab signs
            for (LiftBlock lb : lift.getBlocks()) {
                BlockState bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setDefective").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;

                Sign s = (Sign) bs;
                s.setLine(3, lift.getSignText());
                s.update();
            }
            lift.setSignText(null);
        }
        return 0;
    }

    /**
     * Get the userWhitelist of a lift
     *
     * @param liftName The name of the lift
     * @param floorName The name of the floor
     * @return list with UUIDs of the players
     */
    public HashSet<UUID> getUserWhitelist(String liftName, String floorName) {
        HashSet<UUID> ret = new HashSet<>();
        if (liftName != null && floorName != null && DataManager.containsLift(liftName)) {
            Lift lift = DataManager.getLift(liftName);
            if (lift.getFloors().containsKey(floorName)) {
                ret = lift.getFloors().get(floorName).getUserWhitelist();
            }
        }
        return ret;
    }

    /**
     * Get the groupWhitelist of a lift
     *
     * @param liftName The name of the lift
     * @param floorName The name of the floor
     * @return list with groupnames
     */
    public HashSet<String> getGroupWhitelist(String liftName, String floorName) {
        HashSet<String> ret = new HashSet<>();
        if (liftName != null && floorName != null && DataManager.containsLift(liftName)) {
            Lift lift = DataManager.getLift(liftName);
            if (lift.getFloors().containsKey(floorName)) {
                ret = lift.getFloors().get(floorName).getGroupWhitelist();
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
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getColored("SignText"))) continue;
                sign.setLine(3, ConfigUtil.getColored("DisabledText"));
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
                sign.setLine(3, ConfigUtil.getColored("DisabledText"));
                sign.update();
            }
        } else {
            for (LiftBlock lb : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setOffline").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getColored("SignText"))) continue;
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
     * @param sender Where you want to send it to
     * @param liftName The name of the lift
     */
    public void sendLiftInfo(CommandSender sender, String liftName) {
        sendLiftInfo(sender, liftName, DataManager.getLift(liftName));
    }

    /**
     * Send info about a lift to a player
     * @param ent Where you want to send it to
     * @param liftName The name of the lift
     * @param lift The lift
     */
    public void sendLiftInfo(@Nonnull CommandSender ent, String liftName, @Nonnull Lift lift) {

        ent.sendMessage(ChatColor.GOLD + "Elevator: " + ChatColor.YELLOW + liftName);
        ent.sendMessage(ChatColor.GOLD + "Settings:");
        ent.sendMessage(ChatColor.GREEN + "  Speed: " + ChatColor.YELLOW + lift.getSpeed());
        ent.sendMessage(ChatColor.GREEN + "  Realistic Mode: " + ChatColor.YELLOW + lift.isRealistic());
        ent.sendMessage(ChatColor.GREEN + "  Malfunction: " + ChatColor.YELLOW + lift.isDefective());
        ent.sendMessage(ChatColor.GOLD + "Floors:");
        if (lift.getFloors().isEmpty()) {
            ent.sendMessage(ChatColor.RED + "None.");
        } else {
            for (Map.Entry<String, Floor> entry : lift.getFloors().entrySet()) {
                ent.sendMessage(ChatColor.GREEN + "  " + entry.getKey() + ":");
                Floor f = entry.getValue();
                ent.sendMessage(ChatColor.YELLOW + "    World: " + ChatColor.GREEN + f.getWorld());
                ent.sendMessage(ChatColor.YELLOW + "    Height: " + ChatColor.GREEN + f.getY());
                ent.sendMessage(ChatColor.YELLOW + "    Whitelist:");
                if (f.getUserWhitelist().isEmpty() && f.getGroupWhitelist().isEmpty()) {
                    ent.sendMessage(ChatColor.GOLD + "      None.");
                } else {
                    ChatColor color = ChatColor.DARK_PURPLE;
                    Iterator<UUID> iter = f.getUserWhitelist().iterator();
                    Iterator<String> iter2 = f.getGroupWhitelist().iterator();
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
                    while (iter2.hasNext()) {
                        if (color == ChatColor.DARK_PURPLE) {
                            color = ChatColor.LIGHT_PURPLE;
                        } else {
                            color = ChatColor.DARK_PURPLE;
                        }
                        sb.append(ChatColor.AQUA).append(", ").append(color).append("Group: ").append(iter2.next());
                    }
                    ent.sendMessage(sb.toString());
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
        if (lift == null || !DataManager.containsLift(lift) || world == null) return -1;

        //minY = maxY, so reverse
        if (minY > maxY) {
            int tempY = minY;
            minY = maxY;
            maxY = tempY;
        }

        Block block = world.getBlockAt(x, minY, z);
        if (isRope(block)) return -3;
        Material mat = block.getType();
        if (getFBM().isForbidden(mat)) return -4;

        for (int i = minY + 1; i <= maxY; i++) {
            block = world.getBlockAt(x, i, z);
            if (isRope(block)) return -3;
            if (block.getType() != mat) return -2;
        }

        BlockFace face;
        if (XMaterial.isNewVersion()) {
            face = DirectionUtil.getDirection(block);
        } else {
            BlockState state = block.getState();
            org.bukkit.material.Ladder ladder = (org.bukkit.material.Ladder) state.getData();
            face = ladder.getAttachedFace();
        }

        LiftRope rope = new LiftRope(mat, face, world.getName(), x, minY, maxY, z);
        DataManager.getLift(lift).getRopes().add(rope);

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
