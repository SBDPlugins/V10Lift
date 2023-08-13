package tech.sbdevelopment.v10lift.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.api.objects.Floor;
import tech.sbdevelopment.v10lift.api.objects.Lift;
import tech.sbdevelopment.v10lift.api.objects.LiftBlock;
import tech.sbdevelopment.v10lift.api.objects.LiftInput;
import tech.sbdevelopment.v10lift.managers.DataManager;
import tech.sbdevelopment.v10lift.utils.ConfigUtil;
import tech.sbdevelopment.v10lift.utils.DoorUtil;

import java.util.Map;
import java.util.Objects;

public class BlockBreakListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        if (V10LiftAPI.getInstance().isRope(b)) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.RemoveRopeFirst");
            e.setCancelled(true);
            return;
        }

        LiftBlock tlb = new LiftBlock(b);
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
