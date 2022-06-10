package nl.SBDeveloper.V10Lift.listeners;

import nl.SBDeveloper.V10Lift.api.objects.Lift;
import nl.SBDeveloper.V10Lift.api.objects.LiftBlock;
import nl.SBDeveloper.V10Lift.managers.DataManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityDamageListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) return;

        Entity entity = e.getEntity();
        Location loc;
        if (e instanceof LivingEntity) {
            loc = ((LivingEntity) entity).getEyeLocation();
        } else {
            loc = entity.getLocation();
        }

        if (loc.getWorld() == null) return;

        String world = loc.getWorld().getName();
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        for (Lift lift : DataManager.getLifts().values()) {
            for (LiftBlock lb : lift.getBlocks()) {
                if (world.equals(lb.getWorld()) && x == lb.getX() && y == lb.getY() && z == lb.getZ()) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
}
