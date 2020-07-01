package nl.SBDeveloper.V10Lift.api.runnables;

import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import nl.SBDeveloper.V10Lift.api.objects.*;
import nl.SBDeveloper.V10Lift.managers.DataManager;
import nl.SBDeveloper.V10Lift.sbutils.LocationSerializer;
import nl.SBDeveloper.V10Lift.utils.ConfigUtil;
import nl.SBDeveloper.V10Lift.utils.DirectionUtil;
import nl.SBDeveloper.V10Lift.utils.XMaterial;
import nl.SBDeveloper.V10Lift.utils.XSound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Supplier;

/** The MoveLift runnable, used for moving a lift. */
public class MoveLift implements Runnable {

    /* Packet teleportation method */
    private final Method[] methods = ((Supplier<Method[]>) () -> {
       try {
           Method getHandle = Class.forName(Bukkit.getServer().getClass().getPackage().getName() + ".entity.CraftEntity").getDeclaredMethod("getHandle");
           return new Method[] {
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
        Iterator<LiftBlock> iter;
        ArrayList<LiftBlock> tb = new ArrayList<>();
        Block block = null;
        World world;
        Location loc;
        BlockState bs;
        boolean by;
        int y;
        Chest c;
        V10Entity v10ent;
        Iterator<V10Entity> veiter;
        Sign sign;
        LiftBlock lb;
        Lift lift;
        LiftSign ls;
        Inventory inv;
        ItemStack is;
        ItemStack[] isa;

        lift = DataManager.getLift(liftName);
        if (lift == null) {
            stopMe();
            return;
        }

        if (lift.getQueue().isEmpty() || lift.isOffline()) {
            lift.setQueue(null);
            stopMe();
            return;
        }

        if (DataManager.containsEditLift(liftName) || lift.isDefective()) return;

        if (lift.getCounter() > 0) {
            lift.setCounter(lift.getCounter() - 1);
            return;
        }

        lb = lift.getBlocks().first();
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

        double changeOfDefect = V10LiftPlugin.getSConfig().getFile().getDouble("DefectRate");
        if (changeOfDefect > 0.0D) {
            y = new Random().nextInt(100);
            double chance;
            if (y < 100) {
                long co = new Random().nextLong();
                if (co < 0) co = -co;
                chance = Double.parseDouble(y + "." + co);
            } else {
                chance = y;
            }

            if (chance < changeOfDefect) {
                V10LiftPlugin.getAPI().setDefective(liftName, true);
                return;
            }
        }

        Iterator<Map.Entry<String, Floor>> quiter = lift.getQueue().entrySet().iterator();
        Map.Entry<String, Floor> floor = quiter.next();
        Floor to = floor.getValue();
        String fl = floor.getKey();
        boolean up = false;
        boolean down = false;
        if (lift.getY() < to.getY()) {
            up = true;
        } else if (lift.getY() > to.getY()) {
            down = true;
        }

        if (up) {
            if (!V10LiftPlugin.getAPI().closeDoor(liftName)) return;

            //MOVE ROPES
            for (LiftRope rope : lift.getRopes()) {
                if (rope.getCurrently() > rope.getMaxY()) {
                    Bukkit.getLogger().info("[V10Lift] Lift " + liftName + " reaches the upper rope end but won't stop!! 1");
                    V10LiftPlugin.getAPI().setDefective(liftName, true);
                    lift.getToMove().clear();
                    quiter.remove();
                    return;
                }
                world = Objects.requireNonNull(Bukkit.getWorld(rope.getWorld()), "World is null at MoveLift");
                block = world.getBlockAt(rope.getX(), rope.getCurrently(), rope.getZ());
                block.setType(Material.AIR);
                rope.setCurrently(rope.getCurrently() + 1);
            }

            iter = lift.getBlocks().iterator();
            while (iter.hasNext()) {
                lb = iter.next();
                if (V10LiftPlugin.getAPI().getACBM().isAntiCopy(lb.getMat())) {
                    tb.add(lb);
                    iter.remove();
                    block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at MoveLift").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                    block.setType(Material.AIR);
                    lb.setY(lb.getY() + 1);
                }
            }

            for (LiftBlock lib : lift.getBlocks().descendingSet()) {
                world = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift");
                block = world.getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                if ((lib.getMat() == Material.CHEST || lib.getMat() == Material.TRAPPED_CHEST) && lib.serializedItemStacks == null) {
                    c = (Chest) block.getState();
                    inv = c.getInventory();
                    isa = inv.getContents();
                    by = false;
                    lib.serializedItemStacks = new Map[isa.length];
                    for (int i = 0; i < isa.length; i++) {
                        is = isa[i];
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
                lib.setY(lib.getY() + 1);
                block = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                BlockState state = block.getState();
                if (lib.getMat() == null) lib.setMat(Material.AIR);
                state.setType(lib.getMat());
                if (!XMaterial.isNewVersion()) {
                    state.setRawData(lib.getData());
                }
                state.update(true);
                if (XMaterial.isNewVersion()) {
                    DirectionUtil.setDirection(block, lib.getFace());
                    DirectionUtil.setBisected(block, lib.getBisected());
                    DirectionUtil.setSlabType(block, lib.getSlabtype());
                }
                lb = lift.getBlocks().first();
                for (Entity ent : Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ()).getChunk().getEntities()) {
                    v10ent = new V10Entity(ent.getUniqueId(), null, 0, 0, 0, 0);
                    if (lift.getToMove().contains(v10ent)) continue;
                    loc = ent.getLocation();
                    y = loc.getBlockY();
                    if (y == lib.getY()) {
                        by = true;
                    } else if (y + 1 == lib.getY()) {
                        by = true;
                        y++;
                    } else {
                        by = false;
                    }

                    if (by && loc.getBlockX() == lib.getX() && loc.getBlockZ() == lib.getZ()) {
                        loc.setY(loc.getY() + 1);
                        if (V10LiftPlugin.getSConfig().getFile().getBoolean("PacketTeleport")) {
                            try {
                                methods[1].invoke(methods[0].invoke(ent), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
                            } catch (Exception ex) {
                                Bukkit.getLogger().severe("[V10Lift] PacketTeleportation is enabled, but couldn't get the method.");
                            }
                        } else {
                            ent.teleport(loc);
                        }
                    }
                }
            }
            veiter = lift.getToMove().iterator();
            while (veiter.hasNext()) {
                v10ent = veiter.next();
                if (v10ent.getStep() > 0) {
                    v10ent.moveUp();
                    if (v10ent.getStep() > 16) {
                        veiter.remove();
                    }
                }
                v10ent.setStep((short) (v10ent.getStep() + 1));
            }
            for (LiftBlock lib : tb) {
                block = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                BlockState state = block.getState();
                if (lib.getMat() == null) lib.setMat(Material.AIR);
                state.setType(lib.getMat());
                if (!XMaterial.isNewVersion()) {
                    state.setRawData(lib.getData());
                }
                state.update(true);
                if (XMaterial.isNewVersion()) {
                    DirectionUtil.setDirection(block, lib.getFace());
                    DirectionUtil.setBisected(block, lib.getBisected());
                    DirectionUtil.setSlabType(block, lib.getSlabtype());
                }
                lift.getBlocks().add(lib);
                if (lib.getSignLines() != null) {
                    bs = block.getState();
                    if (bs instanceof Sign) {
                        sign = (Sign) bs;
                        for (int i = 0; i < 3; i++) {
                            sign.setLine(i, lib.getSignLines()[i]);
                            if (i == 0 && lib.getSignLines()[i].equalsIgnoreCase("[v10lift]") && lib.getSignLines()[1].equals(liftName)) {
                                sign.setLine(1, liftName);
                                sign.setLine(3, ChatColor.GOLD + fl);
                            }
                        }
                        sign.update();
                    }
                }
            }

            lift.setY(lift.getY() + 1);
            Iterator<LiftSign> liter = lift.getSigns().iterator();
            while (liter.hasNext()) {
                ls = liter.next();
                if (ls.getState() == 1) continue;
                block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at MoveLift").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    liter.remove();
                    continue;
                }
                sign = (Sign) bs;
                if (ls.getType() == 0) {
                    sign.setLine(3, ConfigUtil.getConfigText("UpText"));
                } else {
                    sign.setLine(3, ChatColor.GRAY + ChatColor.stripColor(sign.getLine(3)));
                }
                sign.update();
                ls.setState((byte) 1);
            }
        } else if (down) {
            if (!V10LiftPlugin.getAPI().closeDoor(liftName)) return;

            iter = lift.getBlocks().iterator();
            while (iter.hasNext()) {
                lb = iter.next();
                if (V10LiftPlugin.getAPI().getACBM().isAntiCopy(lb.getMat())) {
                    tb.add(lb);
                    iter.remove();
                    block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at MoveLift").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                    block.setType(Material.AIR);
                    lb.setY(lb.getY() - 1);
                }
            }

            for (LiftBlock lib : lift.getBlocks()) {
                block = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                if ((lib.getMat() == Material.CHEST || lib.getMat() == Material.TRAPPED_CHEST) && lib.serializedItemStacks == null) {
                    c = (Chest) block.getState();
                    inv = c.getInventory();
                    isa = inv.getContents();
                    by = false;
                    lib.serializedItemStacks = new Map[isa.length];
                    for (int i = 0; i < isa.length; i++) {
                        is = isa[i];
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
                lib.setY(lib.getY() - 1);
                y = lib.getY();
                block = world.getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                BlockState state = block.getState();
                if (lib.getMat() == null) lib.setMat(Material.AIR);
                state.setType(lib.getMat());
                if (!XMaterial.isNewVersion()) {
                    state.setRawData(lib.getData());
                }
                state.update(true);
                if (XMaterial.isNewVersion()) {
                    DirectionUtil.setDirection(block, lib.getFace());
                    DirectionUtil.setBisected(block, lib.getBisected());
                    DirectionUtil.setSlabType(block, lib.getSlabtype());
                }
            }
            veiter = lift.getToMove().iterator();
            while (veiter.hasNext()) {
                v10ent = veiter.next();
                if (v10ent.getStep() > 0) {
                    v10ent.moveDown();
                    if (v10ent.getStep() > 16) {
                        veiter.remove();
                    }
                }
                v10ent.setStep((short) (v10ent.getStep() + 1));
            }
            for (LiftBlock lib : tb) {
                block = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                BlockState state = block.getState();
                if (lib.getMat() == null) lib.setMat(Material.AIR);
                state.setType(lib.getMat());
                if (!XMaterial.isNewVersion()) {
                    state.setRawData(lib.getData());
                }
                state.update(true);
                if (XMaterial.isNewVersion()) {
                    DirectionUtil.setDirection(block, lib.getFace());
                    DirectionUtil.setBisected(block, lib.getBisected());
                    DirectionUtil.setSlabType(block, lib.getSlabtype());
                }
                lift.getBlocks().add(lib);
                if (lib.getSignLines() != null) {
                    bs = block.getState();
                    if (bs instanceof Sign) {
                        sign = (Sign) bs;
                        for (int i = 0; i < 3; i++) {
                            sign.setLine(i, lib.getSignLines()[i]);
                            if (i == 0 && lib.getSignLines()[i].equalsIgnoreCase("[v10lift]") && lib.getSignLines()[1].equals(liftName)) {
                                sign.setLine(1, liftName);
                                sign.setLine(3, ChatColor.GOLD + fl);
                            }
                        }
                        sign.update();
                    }
                }
            }
            lift.setY(lift.getY() - 1);
            Iterator<LiftSign> liter = lift.getSigns().iterator();
            while (liter.hasNext()) {
                ls = liter.next();
                if (ls.getState() == 2) continue;
                block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at MoveLift").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    liter.remove();
                    continue;
                }
                sign = (Sign) bs;
                if (ls.getType() == 0) {
                    sign.setLine(3, ConfigUtil.getConfigText("DownText"));
                } else {
                    sign.setLine(3, ChatColor.GRAY + ChatColor.stripColor(sign.getLine(3)));
                }
                sign.update();
                ls.setState((byte) 2);
            }

            //MOVE ROPES
            for (LiftRope rope : lift.getRopes()) {

                if (rope.getCurrently() < rope.getMinY()) {
                    Bukkit.getLogger().info("[V10Lift] Lift " + liftName + " reaches the upper rope end but won't stop!! 2");
                    V10LiftPlugin.getAPI().setDefective(liftName, true);
                    lift.getToMove().clear();
                    quiter.remove();
                    rope.setCurrently(rope.getCurrently() - 1);
                    block = world.getBlockAt(rope.getX(), rope.getCurrently(), rope.getZ());
                    if (rope.getType() == null) rope.setType(Material.AIR);
                    block.setType(rope.getType());
                    if (XMaterial.isNewVersion()) {
                        DirectionUtil.setDirection(block, rope.getFace());
                    } else {
                        BlockState state = block.getState();
                        org.bukkit.material.Ladder ladder = new org.bukkit.material.Ladder(rope.getType());
                        ladder.setFacingDirection(rope.getFace());
                        state.setData(ladder);
                        state.update(true);
                    }
                    return;
                }
                world = Objects.requireNonNull(Bukkit.getWorld(rope.getWorld()), "World is null at MoveLift");
                rope.setCurrently(rope.getCurrently() - 1);
                block = world.getBlockAt(rope.getX(), rope.getCurrently(), rope.getZ());
                if (rope.getType() == null) rope.setType(Material.AIR);
                block.setType(rope.getType());
                if (XMaterial.isNewVersion()) {
                    DirectionUtil.setDirection(block, rope.getFace());
                } else {
                    BlockState state = block.getState();
                    org.bukkit.material.Ladder ladder = new org.bukkit.material.Ladder(rope.getType());
                    ladder.setFacingDirection(rope.getFace());
                    state.setData(ladder);
                    state.update(true);
                }
            }
        } else {
            lift.getToMove().clear();
            quiter.remove();
            bs = null;
            for (LiftBlock lib : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ()).getState();
                if (!(bs instanceof Sign)) {
                    if (bs instanceof Chest && lib.serializedItemStacks != null) {
                        isa = new ItemStack[lib.serializedItemStacks.length];
                        by = false;
                        for (int i = 0; i < lib.serializedItemStacks.length; i++) {
                            if (lib.serializedItemStacks[i] != null) {
                                isa[i] = ItemStack.deserialize(lib.serializedItemStacks[i]);
                                by = true;
                            }
                        }
                        if (by) {
                            c = (Chest) bs;
                            c.getInventory().setContents(isa);
                            c.update();
                        }
                        lib.serializedItemStacks = null;
                    }
                    continue;
                }
                sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase("[v10lift]")) continue;
                sign.setLine(1, liftName);
                sign.setLine(3, ChatColor.GREEN + fl);
                sign.update();
            }
            Iterator<LiftSign> liter = lift.getSigns().iterator();
            while (liter.hasNext()) {
                ls = liter.next();
                if (ls.getState() == 0) continue;
                block = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at MoveLift").getBlockAt(ls.getX(), ls.getY(), ls.getZ());
                bs = block.getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(block.getLocation()));
                    liter.remove();
                    continue;
                }
                sign = (Sign) bs;
                if (ls.getType() == 0) {
                    sign.setLine(3, ChatColor.GREEN + fl);
                } else {
                    String l3 = ChatColor.stripColor(sign.getLine(3));
                    if (!fl.equals(l3)) {
                        sign.setLine(3, ChatColor.GRAY + l3);
                    } else {
                        sign.setLine(3, ChatColor.GREEN + l3);
                    }
                }
                sign.update();
                ls.setState((byte) 0);
            }
            V10LiftPlugin.getAPI().openDoor(lift, liftName, to);
            if (lift.isRealistic()) lift.setCounter(ft);
            if (lift.isSound()) {
                if (block != null) {
                    loc = block.getLocation();
                    XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(loc, 2.0F, 63.0F);
                }
            }
        }
    }

    private void stopMe() {
        Bukkit.getServer().getScheduler().cancelTask(DataManager.getMovingTask(liftName));
        DataManager.removeMovingTask(liftName);
    }
}
