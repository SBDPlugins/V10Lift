package nl.SBDeveloper.V10Lift.Listeners;

import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.API.Objects.LiftBlock;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.Utils.XMaterial;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import javax.xml.crypto.Data;
import java.util.Map;

public class PlayerInteractListener implements Listener {
    //BUTTON CLICK
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractButton(PlayerInteractEvent e) {
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        if (block == null) return;
        Material button = block.getType();
        if (action == Action.RIGHT_CLICK_BLOCK
            && e.getHand() != EquipmentSlot.OFF_HAND
            && (button.toString().contains("BUTTON") || button == XMaterial.LEVER.parseMaterial())) {
            String world = block.getWorld().getName();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
                Lift lift = entry.getValue();
                for (LiftBlock lb : lift.getOfflineInputs()) {
                    if (world.equals(lb.getWorld()) && x == lb.getX() && y == lb.getY() && z == lb.getZ()) {
                        lb.setActive(!lb.isActive());
                        V10LiftPlugin.getAPI().setDefective(entry.getKey(), lb.isActive());
                        return;
                    }
                }
            }
        }
    }

    //BLOCK ADD
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.OFF_HAND) {
            Player p = e.getPlayer();
            if (DataManager.containsPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                //TODO Fix hashmap for player -> lift (because I need it here)
                int res = V10LiftPlugin.getAPI().switchBlockAtLift(DataManager.getPlayer(p.getUniqueId()), e.getClickedBlock());

            }
        }
    }
}
