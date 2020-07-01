package nl.SBDeveloper.V10Lift.listeners;

import nl.SBDeveloper.V10Lift.api.objects.Floor;
import nl.SBDeveloper.V10Lift.api.objects.Lift;
import nl.SBDeveloper.V10Lift.api.objects.LiftBlock;
import nl.SBDeveloper.V10Lift.managers.DataManager;
import nl.SBDeveloper.V10Lift.utils.ConfigUtil;
import nl.SBDeveloper.V10Lift.utils.DoorUtil;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Map;
import java.util.Objects;

public class BlockBreakListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (V10LiftPlugin.getAPI().isRope(b)) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.RemoveRopeFirst");
            e.setCancelled(true);
            return;
        }

        LiftBlock tlb = new LiftBlock(b.getWorld().getName(), b.getX(), b.getY(), b.getZ(), (String) null);
        for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
            Lift lift = entry.getValue();
            if (lift.getBlocks().contains(tlb)) {
                ConfigUtil.sendMessage(e.getPlayer(), "General.RemoveLiftFirst");
                e.setCancelled(true);
                return;
            }

            for (Floor f : lift.getFloors().values()) {
                if (f.getDoorBlocks().contains(tlb)) {
                    ConfigUtil.sendMessage(e.getPlayer(), "General.RemoveDoorFirst");
                    e.setCancelled(true);
                    return;
                }

                for (LiftBlock lb : f.getRealDoorBlocks()) {
                    Location loc = DoorUtil.getLowerLocationOfDoor(b);
                    if (lb.getWorld().equals(Objects.requireNonNull(loc.getWorld(), "World is null at BlockBreakListener").getName())
                            && lb.getX() == loc.getBlockX()
                            && lb.getY() == loc.getBlockY()
                            && lb.getZ() == loc.getBlockZ()) {
                        ConfigUtil.sendMessage(e.getPlayer(), "General.RemoveDoorFirst");
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            if (!(b.getState() instanceof Sign)) continue;

            if (!lift.getSigns().contains(tlb)) continue;

            if (!lift.getOwners().contains(e.getPlayer().getUniqueId()) && !e.getPlayer().hasPermission("v10lift.admin")) {
                ConfigUtil.sendMessage(e.getPlayer(), "General.NoPermission");
                e.setCancelled(true);
            } else {
                lift.getSigns().remove(tlb);
                ConfigUtil.sendMessage(e.getPlayer(), "LiftSign.Removed");
            }
        }
    }
}
