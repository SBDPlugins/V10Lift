package nl.SBDeveloper.V10Lift.API.Runnables;

import nl.SBDeveloper.V10Lift.API.Objects.Floor;
import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.API.Objects.LiftBlock;
import nl.SBDeveloper.V10Lift.API.Objects.LiftRope;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MoveLift implements Runnable {

    private final String liftName;
    private final long speed;
    private final int ft;

    public MoveLift(String liftName, long speed) {
        this.liftName = liftName;
        this.speed = speed;

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
        Lift lift = DataManager.getLift(liftName);
        if (lift == null) {
            stopMe();
            return;
        }

        if (lift.getQueue().isEmpty() || lift.isOffline()) {
            stopMe();
            return;
        }

        if (DataManager.containsEditLift(liftName)) return;

        if (lift.getCounter() > 0) {
            lift.setCounter(lift.getCounter() - 1);
            return;
        }

        LiftBlock lb = lift.getBlocks().first();
        World w = Bukkit.getWorld(lb.getWorld());
        if (w == null) {
            lift.setCounter(ft);
            return;
        }

        Location loc = new Location(w, lb.getX(), lb.getY(), lb.getZ());
        if (!loc.getChunk().isLoaded()) {
            lift.setCounter(ft);
            return;
        }

        //TODO Add defaults to config (chanceOfDefect)
        double changeOfDefect = 0.0D;
        if (changeOfDefect > 0.0D) {
            int y = new Random().nextInt(100);
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

        ArrayList<LiftBlock> tb = new ArrayList<>();

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

        String tmpw = lift.getWorldName();
        if (up) {
            if (!V10LiftPlugin.getAPI().closeDoor(liftName)) return;

            //MOVE ROPES
            for (LiftRope rope : lift.getRopes()) {
                if (rope.getCurrentWorld().equals(rope.getStartWorld()) && rope.getCurrently() > rope.getMaxY()) {
                    Bukkit.getLogger().info("[V10Lift] Lift " + liftName + " reaches the upper rope end but won't stop!!");
                    V10LiftPlugin.getAPI().setDefective(liftName, true);
                    lift.getToMove().clear();
                    quiter.remove();
                    return;
                }
                Block block = Objects.requireNonNull(Bukkit.getWorld(rope.getCurrentWorld()), "World is null at MoveLift").getBlockAt(rope.getX(), rope.getCurrently(), rope.getZ());
                block.setType(Material.AIR);
                rope.setCurrently(rope.getCurrently() + 1);
            }

            Iterator<LiftBlock> iter = lift.getBlocks().iterator();
            while (iter.hasNext()) {
                LiftBlock lblock = iter.next();
                if (V10LiftPlugin.getAPI().getACBM().isAntiCopy(lblock.getMat())) {
                    tb.add(lblock);
                    iter.remove();
                    Block block = Objects.requireNonNull(Bukkit.getWorld(lblock.getWorld()), "World is null at MoveLift").getBlockAt(lblock.getX(), lblock.getY(), lblock.getZ());
                    block.setType(Material.AIR);
                    lblock.setY(lblock.getY() + 1);
                }
            }

            boolean wc = false;
            for (LiftBlock lib : lift.getBlocks().descendingSet()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                if (lib.getMat() == Material.CHEST || lib.getMat() == Material.TRAPPED_CHEST && lib.serializedItemStacks == null) {
                    Chest c = (Chest) block.getState();
                    Inventory inv = c.getInventory();
                    ItemStack[] isa = inv.getContents();
                    boolean by = false;
                    lib.serializedItemStacks = new Map[isa.length];
                    for (int i = 0; i < isa.length; i++) {
                        ItemStack is = isa[i];
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
                Block b = Objects.requireNonNull(Bukkit.getWorld(lib.getWorld()), "World is null at MoveLift").getBlockAt(lib.getX(), lib.getY(), lib.getZ());
                b.setType(lib.getMat());
                lb = lift.getBlocks().first();
            }
        }
    }

    private void stopMe() {
        Bukkit.getServer().getScheduler().cancelTask(DataManager.getMovingTask(liftName));
        DataManager.removeMovingTask(liftName);
    }
}
