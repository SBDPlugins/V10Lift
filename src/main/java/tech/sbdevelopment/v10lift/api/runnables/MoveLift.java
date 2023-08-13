package tech.sbdevelopment.v10lift.api.runnables;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.api.enums.LiftDirection;
import tech.sbdevelopment.v10lift.api.objects.*;
import tech.sbdevelopment.v10lift.managers.AntiCopyBlockManager;
import tech.sbdevelopment.v10lift.managers.DataManager;
import tech.sbdevelopment.v10lift.sbutils.LocationSerializer;
import tech.sbdevelopment.v10lift.utils.ConfigUtil;
import tech.sbdevelopment.v10lift.utils.DirectionUtil;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * The MoveLift runnable, used for moving a lift.
 */
public class MoveLift implements Runnable {
    /* Packet teleportation method */
    private final Method[] methods = ((Supplier<Method[]>) () -> {
        try {
            Method getHandle = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity").getDeclaredMethod("getHandle");
            return new Method[]{
                    getHandle, getHandle.getReturnType().getDeclaredMethod("setPositionRotation", double.class, double.class, double.class, float.class, float.class)
            };
        } catch (Exception ex) {
            return null;
        }
    }).get();

    private final String liftName;
    private final int ft;

    public MoveLift(String liftName, long speed) {
        this.liftName = liftName;

        if (speed > 32L) {
            ft = 1;
        } else if (speed > 16L) {
            ft = 2;
        } else if (speed > 8L) {
            ft = 4;
        } else if (speed > 4L) {
            ft = 8;
        } else if (speed > 2L) {
            ft = 16;
        } else if (speed > 1L) {
            ft = 32;
        } else {
            ft = 64;
        }
    }

    @Override
    public void run() {
        //Check if lift exists
        Lift lift = DataManager.getLift(liftName);
        if (lift == null) {
            stop();
            return;
        }

        //If the queue is NOT empty and the lift is NOT offline
        if (lift.getQueue().isEmpty() || lift.isOffline()) {
            lift.setQueue(null);
            stop();
            return;
        }

        //If the lift is NOT in edit mode and the lift is NOT defective
        if (DataManager.containsEditLift(liftName) || lift.isDefective()) return;

        //If the lift is NOT in delay
        if (lift.getCounter() > 0) {
            lift.setCounter(lift.getCounter() - 1);
            return;
        }

        //Check if the chunk of the first block is loaded
        LiftBlock lb = lift.getBlocks().first();
        World world = Bukkit.getWorld(lb.getWorld());
        if (world == null) {
            lift.setCounter(ft);
            return;
        }

        Location loc = new Location(world, lb.getX(), lb.getY(), lb.getZ());
        if (!loc.getChunk().isLoaded()) {
            lift.setCounter(ft);
            return;
        }

        //And if the chunk of the last block is loaded
        lb = lift.getBlocks().last();
        world = Bukkit.getWorld(lb.getWorld());
        if (world == null) {
            lift.setCounter(ft);
            return;
        }

        loc = new Location(world, lb.getX(), lb.getY(), lb.getZ());
        if (!loc.getChunk().isLoaded()) {
            lift.setCounter(ft);
            return;
        }

        //Handle malfunction
        double changeOfDefect = V10LiftPlugin.getSConfig().getFile().getDouble("DefectRate");
        if (changeOfDefect > 0.0D) {
            double chance = ThreadLocalRandom.current().nextDouble(100);
            if (chance < changeOfDefect) {
                V10LiftAPI.getInstance().setDefective(liftName, true);
                return;
            }
        }

        Iterator<Map.Entry<String, Floor>> queueIterator = lift.getQueue().entrySet().iterator();
        Map.Entry<String, Floor> floor = queueIterator.next();
        String floorName = floor.getKey();
        Floor floorTo = floor.getValue();

        LiftDirection direction;
        if (lift.getY() < floorTo.getY()) {
            direction = LiftDirection.UP;
        } else if (lift.getY() > floorTo.getY()) {
            direction = LiftDirection.DOWN;
        } else {
            direction = LiftDirection.STOP;
        }

        List<LiftBlock> antiCopyBlocks = new ArrayList<>();

        if (direction == LiftDirection.UP || direction == LiftDirection.DOWN) {
            if (!V10LiftAPI.getInstance().closeDoor(liftName)) return;

            if (direction == LiftDirection.UP) {
                //MOVE ROPES
                for (LiftRope rope : lift.getRopes()) {
                    if (rope.getCurrently() > rope.getMaxY()) {
                        Bukkit.getLogger().info("[V10Lift] Lift " + liftName + " reaches the upper rope end but won't stop!!");

                        V10LiftAPI.getInstance().setDefective(liftName, true);
                        lift.getToMove().clear();
                        queueIterator.remove();
                        return;
                    }

                    Block currentRopeBlock = Bukkit.getWorld(rope.getWorld()).getBlockAt(rope.getX(), rope.getCurrently(), rope.getZ());
                    currentRopeBlock.setType(Material.AIR);

                    rope.setCurrently(rope.getCurrently() + 1);
                }
            }

            Iterator<LiftBlock> blockIterator = lift.getBlocks().iterator();
            while (blockIterator.hasNext()) {
                LiftBlock liftBlock = blockIterator.next();
                if (AntiCopyBlockManager.isAntiCopy(liftBlock.getMat())) {
                    antiCopyBlocks.add(liftBlock);
                    blockIterator.remove();

                    Block antiCopyBlock = Bukkit.getWorld(liftBlock.getWorld()).getBlockAt(liftBlock.getX(), liftBlock.getY(), liftBlock.getZ());
                    antiCopyBlock.setType(Material.AIR);

                    liftBlock.setY(direction == LiftDirection.UP ? liftBlock.getY() + 1 : liftBlock.getY() - 1);
                }
            }

            Set<LiftBlock> set = direction == LiftDirection.UP ? lift.getBlocks().descendingSet() : lift.getBlocks();
            for (LiftBlock lib : set) {
                Block block = Bukkit.getWorld(lib.getWorld()).getBlockAt(lib.getX(), lib.getY(), lib.getZ());

                if ((lib.getMat() == Material.CHEST || lib.getMat() == Material.TRAPPED_CHEST) && lib.serializedItemStacks == null) {
                    Chest c = (Chest) block.getState();
                    Inventory inv = c.getInventory();
                    ItemStack[] invContents = inv.getContents();
                    boolean by = false;

                    lib.serializedItemStacks = new Map[invContents.length];
                    for (int i = 0; i < invContents.length; i++) {
                        ItemStack is = invContents[i];
                        if (is != null) {
                            by = true;
                            lib.serializedItemStacks[i] = is.serialize();
                        }
                    }

                    if (by) {
                        inv.clear();
                        c.update();
                    } else {
                        lib.serializedItemStacks = null;
                    }
                }

                block.setType(Material.AIR);

                lib.setY(direction == LiftDirection.UP ? lib.getY() + 1 : lib.getY() - 1);
                Block nextBlock = Bukkit.getWorld(lib.getWorld()).getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                if (lib.getMat() == null) lib.setMat(Material.AIR);

                BlockState state = nextBlock.getState();
                state.setType(lib.getMat());
                if (!XMaterial.supports(13)) {
                    state.setRawData(lib.getData());
                }
                state.update(true);

                if (XMaterial.supports(13)) {
                    DirectionUtil.setDirection(nextBlock, lib.getFace());
                    DirectionUtil.setBisected(nextBlock, lib.getBisected());
                    DirectionUtil.setSlabType(nextBlock, lib.getSlabtype());
                }

                if (direction == LiftDirection.UP) { //Teleportation is only required if we go up, for down gravity works fine. ;)
                    for (Entity ent : nextBlock.getChunk().getEntities()) {
                        V10Entity v10ent = new V10Entity(ent.getUniqueId(), null, 0, 0, 0, 0);
                        if (lift.getToMove().contains(v10ent)) continue;

                        Location entLoc = ent.getLocation();
                        if ((entLoc.getBlockY() == lib.getY() || entLoc.getBlockY() + 1 == lib.getY()) && entLoc.getBlockX() == lib.getX() && entLoc.getBlockZ() == lib.getZ()) {
                            entLoc.setY(entLoc.getY() + 1);
                            if (V10LiftPlugin.getSConfig().getFile().getBoolean("PacketTeleport")) {
                                try {
                                    methods[1].invoke(methods[0].invoke(ent), entLoc.getX(), entLoc.getY(), entLoc.getZ(), entLoc.getYaw(), entLoc.getPitch());
                                } catch (Exception ex) {
                                    Bukkit.getLogger().severe("[V10Lift] PacketTeleportation is enabled, but couldn't get the method.");
                                }
                            } else {
                                ent.teleport(entLoc);
                            }
                        }
                    }
                }
            }

            Iterator<V10Entity> toMoveIterator = lift.getToMove().iterator();
            while (toMoveIterator.hasNext()) {
                V10Entity v10ent = toMoveIterator.next();
                if (v10ent.getStep() > 0) {
                    if (direction == LiftDirection.UP) v10ent.moveUp();
                    else v10ent.moveDown();
                    if (v10ent.getStep() > 16) {
                        toMoveIterator.remove();
                    }
                }
                v10ent.setStep((short) (v10ent.getStep() + 1));
            }

            for (LiftBlock lib : antiCopyBlocks) {
                Block block = Bukkit.getWorld(lib.getWorld()).getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                if (lib.getMat() == null) lib.setMat(Material.AIR);

                BlockState state = block.getState();
                state.setType(lib.getMat());
                if (!XMaterial.supports(13)) {
                    state.setRawData(lib.getData());
                }
                state.update(true);

                if (XMaterial.supports(13)) {
                    DirectionUtil.setDirection(block, lib.getFace());
                    DirectionUtil.setBisected(block, lib.getBisected());
                    DirectionUtil.setSlabType(block, lib.getSlabtype());
                }

                lift.getBlocks().add(lib);

                if (lib.getSignLines() != null) {
                    BlockState bs = block.getState();
                    if (bs instanceof Sign) {
                        Sign sign = (Sign) bs;
                        for (int i = 0; i < 3; i++) {
                            sign.setLine(i, lib.getSignLines()[i]);
                            if (i == 0 && lib.getSignLines()[i].equalsIgnoreCase(ConfigUtil.getConfigText("SignText")) && lib.getSignLines()[1].equals(liftName)) {
                                sign.setLine(1, liftName);
                                sign.setLine(3, ChatColor.GOLD + floorName);
                            }
                        }
                        sign.update();
                    }
                }
            }

            lift.setY(direction == LiftDirection.UP ? lift.getY() + 1 : lift.getY() - 1);

            int signState = direction == LiftDirection.UP ? 1 : 2;

            Iterator<LiftSign> signIterator = lift.getSigns().iterator();
            while (signIterator.hasNext()) {
                LiftSign ls = signIterator.next();
                if (ls.getState() == signState) continue;

                Block block = Bukkit.getWorld(ls.getWorld()).getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                BlockState bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    signIterator.remove();
                    continue;
                }

                Sign sign = (Sign) bs;
                if (ls.getType() == 0) {
                    String text = direction == LiftDirection.UP ? ConfigUtil.getConfigText("UpText") : ConfigUtil.getConfigText("DownText");
                    sign.setLine(3, text);
                } else {
                    sign.setLine(3, ChatColor.GRAY + ChatColor.stripColor(sign.getLine(3)));
                }
                sign.update();

                ls.setState((byte) signState);
            }

            if (direction == LiftDirection.DOWN) {
                //MOVE ROPES
                for (LiftRope rope : lift.getRopes()) {
                    boolean stopAfter = false;

                    if (rope.getCurrently() < rope.getMinY()) {
                        Bukkit.getLogger().info("[V10Lift] Lift " + liftName + " reaches the upper rope end but won't stop!!");

                        V10LiftAPI.getInstance().setDefective(liftName, true);
                        lift.getToMove().clear();
                        queueIterator.remove();

                        stopAfter = true;
                    }

                    rope.setCurrently(rope.getCurrently() - 1);

                    Block block = Bukkit.getWorld(rope.getWorld()).getBlockAt(rope.getX(), rope.getCurrently(), rope.getZ());
                    if (rope.getType() == null) rope.setType(Material.AIR);

                    block.setType(rope.getType());
                    if (XMaterial.supports(13)) {
                        DirectionUtil.setDirection(block, rope.getFace());
                    } else {
                        BlockState state = block.getState();
                        org.bukkit.material.Ladder ladder = new org.bukkit.material.Ladder(rope.getType());
                        ladder.setFacingDirection(rope.getFace());
                        state.setData(ladder);
                        state.update(true);
                    }

                    if (stopAfter) return;
                }
            }
        } else {
            lift.getToMove().clear();
            queueIterator.remove();

            for (LiftBlock lib : lift.getBlocks()) {
                BlockState bs = Bukkit.getWorld(lib.getWorld()).getBlockAt(lib.getX(), lib.getY(), lib.getZ()).getState();
                if (!(bs instanceof Sign)) {
                    if (bs instanceof Chest && lib.serializedItemStacks != null) {
                        ItemStack[] isa = new ItemStack[lib.serializedItemStacks.length];
                        boolean by = false;

                        for (int i = 0; i < lib.serializedItemStacks.length; i++) {
                            if (lib.serializedItemStacks[i] != null) {
                                isa[i] = ItemStack.deserialize(lib.serializedItemStacks[i]);
                                by = true;
                            }
                        }

                        if (by) {
                            Chest c = (Chest) bs;
                            c.getInventory().setContents(isa);
                            c.update();
                        }

                        lib.serializedItemStacks = null;
                    }
                    continue;
                }

                Sign sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) continue;
                sign.setLine(1, liftName);
                sign.setLine(3, ChatColor.GREEN + floorName);
                sign.update();
            }

            Block block = null;

            Iterator<LiftSign> signIterator = lift.getSigns().iterator();
            while (signIterator.hasNext()) {
                LiftSign ls = signIterator.next();
                if (ls.getState() == 0) continue;

                block = Bukkit.getWorld(ls.getWorld()).getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                BlockState bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    signIterator.remove();
                    continue;
                }

                Sign sign = (Sign) bs;
                if (ls.getType() == 0) {
                    sign.setLine(3, ChatColor.GREEN + floorName);
                } else {
                    String l3 = ChatColor.stripColor(sign.getLine(3));
                    if (!floorName.equals(l3)) {
                        sign.setLine(3, ChatColor.GRAY + l3);
                    } else {
                        sign.setLine(3, ChatColor.GREEN + l3);
                    }
                }
                sign.update();
                ls.setState((byte) 0);
            }

            V10LiftAPI.getInstance().openDoor(lift, liftName, floorTo);

            if (lift.isRealistic()) lift.setCounter(ft);

            if (lift.isSound() && block != null)
                XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(block.getLocation(), 2.0F, 63.0F);
        }
    }

    private void stop() {
        Bukkit.getServer().getScheduler().cancelTask(DataManager.getMovingTask(liftName));
        DataManager.removeMovingTask(liftName);
    }
}
