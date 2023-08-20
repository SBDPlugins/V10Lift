package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.lists.LiftQueue;
import tech.sbdevelopment.v10lift.api.runnables.DoorCloser;
import tech.sbdevelopment.v10lift.managers.ForbiddenBlockManager;
import tech.sbdevelopment.v10lift.sbutils.LocationSerializer;
import tech.sbdevelopment.v10lift.utils.ConfigUtil;
import tech.sbdevelopment.v10lift.utils.DoorUtil;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A lift object, to create a lift.
 */
@Getter
@NoArgsConstructor
@ToString
public class Lift {
    @Setter
    private String name;
    @Setter
    private String worldName;
    @Setter
    private int y;
    private HashSet<UUID> owners;
    private final TreeSet<LiftBlock> blocks = new TreeSet<>();
    private final LinkedHashMap<String, Floor> floors = new LinkedHashMap<>();
    private final HashSet<LiftSign> signs = new HashSet<>();
    private final HashSet<LiftInput> inputs = new HashSet<>();
    private final HashSet<LiftInput> offlineInputs = new HashSet<>();
    private final LiftQueue queue = new LiftQueue();
    private final HashSet<LiftRope> ropes = new HashSet<>();
    @Setter
    private int speed;
    @Setter
    private boolean realistic;
    private boolean offline = false;
    @Setter
    private boolean sound = true;
    private boolean defective = false;
    @Setter
    private String signText = null;
    @Setter
    private int counter = 0;
    @Setter
    private Floor doorOpen = null;
    @Setter
    private DoorCloser doorCloser = null;

    /**
     * Construct a new Lift with multiple owners
     *
     * @param owners    The owners, by uuid
     * @param speed     The speed, 1 is slowest, higher is faster
     * @param realistic Realistic lift, or not
     */
    public Lift(HashSet<UUID> owners, int speed, boolean realistic) {
        this.owners = owners;
        this.speed = speed;
        this.realistic = realistic;
    }

    /**
     * Construct a new Lift with one owners
     *
     * @param owner     The owner, by uuid
     * @param speed     The speed, 1 is slowest, higher is faster
     * @param realistic Realistic lift, or not
     */
    public Lift(UUID owner, int speed, boolean realistic) {
        HashSet<UUID> hs = new HashSet<>();
        hs.add(owner);
        this.owners = hs;
        this.speed = speed;
        this.realistic = realistic;
    }

    /**
     * Add a block to a lift
     * Use {@link Lift#sortLiftBlocks()} after!
     *
     * @param block    The block
     * @return 0 if added, -2 if forbidden, -3 if already added
     */
    public int addBlock(@Nonnull Block block) {
        return addBlock(getBlocks(), block);
    }

    /**
     * Add a block to a lift
     * Use {@link Lift#sortLiftBlocks()} after!
     *
     * @param blocks The blockset
     * @param block  The block
     * @return 0 if added, -2 if forbidden, -3 if already added
     */
    public int addBlock(@Nonnull Set<LiftBlock> blocks, @Nonnull Block block) {
        return addBlock(blocks, new LiftBlock(block));
    }

    /**
     * Add a block to a lift
     * Use {@link Lift#sortLiftBlocks()} after!
     *
     * @param blocks The blockset
     * @param block  The LiftBlock
     * @return 0 if added, -2 if forbidden, -3 if already added
     */
    public int addBlock(@Nonnull Set<LiftBlock> blocks, @Nonnull LiftBlock block) {
        if (ForbiddenBlockManager.isForbidden(block.getMat())) return -2;
        if (blocks.contains(block)) return -3;
        blocks.add(block);
        return 0;
    }

    /**
     * Remove a block from a lift
     * Use {@link Lift#sortLiftBlocks()} after!
     *
     * @param block    The block
     * @return true if removed, false if doesn't exists
     */
    public boolean removeBlock(@Nonnull Block block) {
        LiftBlock lb = new LiftBlock(block);
        if (!getBlocks().contains(lb)) return false;
        getBlocks().remove(lb);
        return true;
    }

    /**
     * Switch a block at a lift
     * Use {@link Lift#sortLiftBlocks()} after!
     *
     * @param block    The block
     * @return 0 if added, 1 if removed, -2 if not added
     */
    public int switchBlock(@Nonnull Block block) {
        return switchBlock(getBlocks(), block);
    }

    /**
     * Switch a block at a lift
     * Use {@link Lift#sortLiftBlocks()} after!
     *
     * @param blocks The blockset
     * @param block  The block
     * @return 0 if added, 1 if removed, -2 if not added
     */
    public int switchBlock(@Nonnull TreeSet<LiftBlock> blocks, @Nonnull Block block) {
        if (ForbiddenBlockManager.isForbidden(block.getType())) return -2;
        LiftBlock lb = new LiftBlock(block);
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
     */
    public void sortLiftBlocks() {
        if (getWorldName() == null) setWorldName(getBlocks().first().getWorld());
        World world = Bukkit.getWorld(getWorldName());
        if (world == null) return;
        setY(world.getMaxHeight());
        for (LiftBlock lb : getBlocks()) {
            if (lb.getY() < getY()) {
                setY(lb.getY());
                setWorldName(lb.getWorld());
            }
        }
    }

    /**
     * Open the door
     *
     * @return true/false
     */
    public boolean openDoor() {
        if (getQueue() != null) return false;

        Floor f = null;
        for (Floor fl : getFloors().values()) {
            if (fl.getY() == getY() && fl.getWorld().equals(getWorldName())) {
                f = fl;
                break;
            }
        }

        if (f == null) return false;

        if (getDoorOpen() != null && !closeDoor()) return false;

        for (LiftBlock lb : f.getDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            block.setType(Material.AIR);
        }

        for (LiftBlock lb : f.getRealDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            DoorUtil.openDoor(block);
        }

        setDoorOpen(f);

        if (isRealistic()) setDoorCloser(new DoorCloser(this));
        return true;
    }

    /**
     * Open the door
     *
     * @param f        The floor
     * @return true/false
     */
    public boolean openDoor(@Nonnull Floor f) {
        if (getDoorOpen() != null && !closeDoor()) return false;

        for (LiftBlock lb : f.getDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            block.setType(Material.AIR);
        }

        for (LiftBlock lb : f.getRealDoorBlocks()) {
            Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at openDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
            DoorUtil.openDoor(block);
        }

        setDoorOpen(f);

        if (isRealistic()) setDoorCloser(new DoorCloser(this));
        return true;
    }

    /**
     * Close a lift door
     *
     * @return true if door was closed, false if else.
     */
    public boolean closeDoor() {
        boolean blocked = false;
        if (getDoorOpen() == null) {
            return true;
        }

        if (isRealistic()) {
            for (LiftBlock lb : getDoorOpen().getDoorBlocks()) {
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

            for (LiftBlock lb : getDoorOpen().getRealDoorBlocks()) {
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
            for (LiftBlock lb : getDoorOpen().getDoorBlocks()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at closeDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                BlockState state = block.getState();
                state.setType(lb.getMat());
                state.update(true);
            }

            for (LiftBlock lb : getDoorOpen().getRealDoorBlocks()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at closeDoor").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                DoorUtil.closeDoor(block);
            }

            setDoorOpen(null);
            if (getDoorCloser() != null) getDoorCloser().stop();
        }

        return !blocked;
    }

    /**
     * To check if a lift has an open door.
     *
     * @return true if open, false if else
     */
    public boolean hasDoorOpen() {
        return getDoorOpen() != null;
    }

    private void sortFloors() {
        ArrayList<Map.Entry<String, Floor>> as = new ArrayList<>(getFloors().entrySet());
        as.sort(Comparator.comparingInt(o -> o.getValue().getY()));
        Iterator<Map.Entry<String, Floor>> iter = as.iterator();
        getFloors().clear();
        Map.Entry<String, Floor> e;
        while (iter.hasNext()) {
            e = iter.next();
            getFloors().put(e.getKey(), e.getValue());
        }
    }

    /**
     * Adds a new floor to a lift
     *
     * @param floorName The name of the floor
     * @param floor     The floor object
     * @return 0 if added, -1 if world doesn't exist, -2 if height is too high, -3 if floor already exists
     */
    public int addFloor(@Nonnull String floorName, @Nonnull Floor floor) {
        if (Bukkit.getWorld(floor.getWorld()) == null) return -1;
        if (floor.getY() > Bukkit.getServer().getWorld(floor.getWorld()).getMaxHeight())
            return -2;
        if (floorName.length() > 13) floorName = floorName.substring(0, 13).trim();
        if (getFloors().containsKey(floorName) || getFloors().containsValue(floor)) return -3;

        getFloors().put(floorName, floor);
        sortFloors();
        return 0;
    }

    /**
     * Removes a floor from a lift
     *
     * @param floorName The name of the floor
     * @return true if removed, false if null or doesn't exists
     */
    public boolean removeFloor(@Nonnull String floorName) {
        if (!getFloors().containsKey(floorName)) return false;

        getFloors().remove(floorName);
        getInputs().removeIf(liftBlock -> liftBlock.getFloor().equals(floorName));
        return true;
    }

    /**
     * Rename a floor from a lift
     *
     * @param oldName  The old name of the floor
     * @param newName  The new name of the floor
     * @return 0 if renamed, -2 if floor doesn't exists, -3 if floor already exists
     */
    public int renameFloor(@Nonnull String oldName, @Nonnull String newName) {
        if (!getFloors().containsKey(oldName)) return -2;
        if (newName.length() > 13) newName = newName.substring(0, 13).trim();
        if (getFloors().containsKey(newName)) return -3;

        Floor f = getFloors().get(oldName);
        getFloors().remove(oldName);
        getFloors().put(newName, f);
        sortFloors();
        Iterator<LiftInput> liter = getInputs().iterator();
        LiftInput lb;
        ArrayList<LiftInput> newBlocks = new ArrayList<>();
        while (liter.hasNext()) {
            lb = liter.next();
            if (lb.getFloor().equals(oldName)) {
                liter.remove();
                newBlocks.add(new LiftInput(lb.getWorld(), lb.getX(), lb.getY(), lb.getZ(), newName));
            }
        }
        newBlocks.forEach(nlb -> getInputs().add(nlb));
        return 0;
    }

    /**
     * Set a lift to (not) defective
     *
     * @param state    true/false
     * @return 0 if set, -2 if same state, -3 if no signs, -4 if wrong sign
     */
    public int setDefective(boolean state) {
        if (defective == state) return -2;
        defective = state;

        if (state) {
            //SET DEFECTIVE
            //Update sign
            for (LiftSign ls : getSigns()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at setDefective").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                BlockState bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    return -4;
                }

                Sign s = (Sign) bs;
                ls.setOldText(s.getLine(3));
                s.setLine(3, ConfigUtil.getConfigText("DefectText"));
                s.update();
            }

            //Update all cab signs
            for (LiftBlock lb : getBlocks()) {
                BlockState bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setDefective").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;

                Sign s = (Sign) bs;
                setSignText(s.getLine(3));
                s.setLine(3, ConfigUtil.getConfigText("DefectText"));
                s.update();
            }
        } else {

            //Update sign
            for (LiftSign ls : getSigns()) {
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
            for (LiftBlock lb : getBlocks()) {
                BlockState bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setDefective").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;

                Sign s = (Sign) bs;
                s.setLine(3, getSignText());
                s.update();
            }
            setSignText(null);
        }
        return 0;
    }

    /**
     * Get the userWhitelist of a lift
     *
     * @param floorName The name of the floor
     * @return set with UUIDs of the players
     */
    public HashSet<UUID> getUserWhitelist(@Nonnull String floorName) {
        HashSet<UUID> ret = new HashSet<>();
        if (getFloors().containsKey(floorName)) {
            ret = getFloors().get(floorName).getUserWhitelist();
        }
        return ret;
    }

    /**
     * Get the groupWhitelist of a lift
     *
     * @param floorName The name of the floor
     * @return set with groupnames
     */
    public HashSet<String> getGroupWhitelist(@Nonnull String floorName) {
        HashSet<String> ret = new HashSet<>();
        if (getFloors().containsKey(floorName)) {
            ret = getFloors().get(floorName).getGroupWhitelist();
        }
        return ret;
    }

    /**
     * Set a lift to (not) offline
     *
     * @param state    true/false
     * @return 0 if set, -2 if same state
     */
    public int setOffline(boolean state) {
        if (offline == state) return -2;
        offline = state;
        Iterator<LiftSign> liter = getSigns().iterator();
        BlockState bs;
        Sign sign;
        if (state) {
            for (LiftBlock lb : getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setOffline").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) continue;
                sign.setLine(3, ConfigUtil.getConfigText("DisabledText"));
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
                sign.setLine(3, ConfigUtil.getConfigText("DisabledText"));
                sign.update();
            }
        } else {
            for (LiftBlock lb : getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at setOffline").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) continue;
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
     * @param block    The block
     * @return true/false
     */
    public boolean containsRope(@Nonnull Block block) {
        if (getRopes().isEmpty()) return false;

        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        for (LiftRope rope : getRopes()) {
            if (x != rope.getX() || z != rope.getZ()) continue;
            if (y >= rope.getMinY() && y <= rope.getMaxY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Send info about a lift to a player
     *
     * @param ent      Where you want to send it to
     */
    public void sendInfo(@Nonnull CommandSender ent) {
        ent.sendMessage(ChatColor.GOLD + "Elevator: " + ChatColor.YELLOW + name);
        ent.sendMessage(ChatColor.GOLD + "Settings:");
        ent.sendMessage(ChatColor.GREEN + "  Speed: " + ChatColor.YELLOW + getSpeed());
        ent.sendMessage(ChatColor.GREEN + "  Realistic Mode: " + ChatColor.YELLOW + isRealistic());
        ent.sendMessage(ChatColor.GREEN + "  Malfunction: " + ChatColor.YELLOW + isDefective());
        ent.sendMessage(ChatColor.GOLD + "Floors:");
        if (getFloors().isEmpty()) {
            ent.sendMessage(ChatColor.RED + "None.");
        } else {
            for (Map.Entry<String, Floor> entry : getFloors().entrySet()) {
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
     * @param world The world
     * @param x     The x-pos
     * @param minY  The min y-pos
     * @param maxY  The max y-pos
     * @param z     The z-pos
     * @return 0 if added, -2 if not same mat, -3 if already a rope, -4 if forbidden material
     */
    public int addRope(@Nonnull World world, int x, int minY, int maxY, int z) {
        //minY = maxY, so reverse
        if (minY > maxY) {
            int tempY = minY;
            minY = maxY;
            maxY = tempY;
        }

        Block block = world.getBlockAt(x, minY, z);
        if (V10LiftPlugin.getApi().isRope(block)) return -3;
        Material mat = block.getType();
        if (ForbiddenBlockManager.isForbidden(mat)) return -4;

        for (int i = minY + 1; i <= maxY; i++) {
            block = world.getBlockAt(x, i, z);
            if (V10LiftPlugin.getApi().isRope(block)) return -3;
            if (block.getType() != mat) return -2;
        }

        LiftRope rope = new LiftRope(block, minY, maxY);
        getRopes().add(rope);

        return 0;
    }

    /**
     * Remove a rope from a lift
     *
     * @param block The block
     * @return true/false
     */
    public boolean removeRope(@Nonnull Block block) {
        if (!containsRope(block))
            return false;

        String world = block.getWorld().getName();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        Iterator<LiftRope> riter = getRopes().iterator();
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

//    /**
//     * Set the queue of a lift
//     *
//     * @param liftName The name of the lift
//     * @param queue    The queue
//     * @return true/false
//     */
//    public boolean setQueue(String liftName, LinkedHashMap<String, Floor> queue) {
//        if (liftName == null || queue == null || !DataManager.containsLift(liftName)) return false;
//
//        Lift lift = DataManager.getLift(liftName);
//        lift.setQueue(new LinkedHashMap<>());
//        for (Map.Entry<String, Floor> e : queue.entrySet()) {
//            addToQueue(liftName, e.getValue(), e.getKey());
//        }
//        return true;
//    }
//
//    /**
//     * Add a location to the queue
//     *
//     * @param lift  The name of the lift
//     * @param y     The y-pos
//     * @param world The world
//     * @return true/false
//     */
//    public boolean addToQueue(String lift, int y, World world) {
//        return addToQueue(lift, y, world, null);
//    }
//
//    /**
//     * Add a location to the queue
//     *
//     * @param lift      The name of the lift
//     * @param y         The y-pos
//     * @param world     The world
//     * @param floorName The name of the flor
//     * @return true/false
//     */
//    public boolean addToQueue(String lift, int y, @Nonnull World world, String floorName) {
//        return addToQueue(lift, new Floor(y, world.getName()), floorName);
//    }
//
//    /**
//     * Add a location to the queue
//     *
//     * @param lift      The name of the lift
//     * @param floor     The {@link Floor}
//     * @param floorName The name of the flor
//     * @return true/false
//     */
//    public boolean addToQueue(String lift, Floor floor, String floorName) {
//        if (lift == null || floor == null || !DataManager.containsLift(lift)) return false;
//
//        Lift l = DataManager.getLift(lift);
//        if (l.getQueue() == null) {
//            l.setQueue(new LinkedHashMap<>());
//        }
//
//        if (!l.getQueue().containsValue(floor)) {
//            if (floorName == null) {
//                floorName = ChatColor.MAGIC + "-----";
//                for (Map.Entry<String, Floor> e : l.getFloors().entrySet()) {
//                    if (e.getValue().equals(floor)) {
//                        floorName = e.getKey();
//                        floor = e.getValue();
//                        break;
//                    }
//                }
//            }
//
//            l.getQueue().put(floorName, floor);
//            startLift(lift);
//            return true;
//        }
//        return false;
//    }
//
//    private void start(String liftName) {
//        if (!DataManager.containsMovingTask(liftName)) {
//            Lift lift = DataManager.getLift(liftName);
//            DataManager.addMovingTask(liftName, Bukkit.getScheduler().scheduleSyncRepeatingTask(V10LiftPlugin.getInstance(), new MoveLift(liftName, lift.getSpeed()), lift.getSpeed(), lift.getSpeed()));
//        }
//    }
}
