package tech.sbdevelopment.v10lift.listeners;

import tech.sbdevelopment.v10lift.api.objects.Lift;
import tech.sbdevelopment.v10lift.api.objects.LiftSign;
import tech.sbdevelopment.v10lift.managers.DataManager;
import tech.sbdevelopment.v10lift.utils.ConfigUtil;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent e) {
        String[] lines = e.getLines();
        if (!lines[0].equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) return;

        Player p = e.getPlayer();
        if (lines[1].isEmpty()) {
            ConfigUtil.sendMessage(e.getPlayer(), "LiftSign.NoName");
            return;
        }

        if (!DataManager.containsLift(lines[1])) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.DoesntExists");
            return;
        }

        Lift lift = DataManager.getLift(lines[1]);
        if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.NoPermission");
            e.setCancelled(true);
            return;
        }

        byte type;
        if (lift.getFloors().containsKey(lines[2])) {
            type = 1;
            e.setLine(3, ChatColor.GRAY + lines[2]);
        } else {
            type = 0;
        }
        e.setLine(2, "");

        Block b = e.getBlock();
        lift.getSigns().add(new LiftSign(b.getWorld().getName(), b.getX(), b.getY(), b.getZ(), type, (byte) 0));
        ConfigUtil.sendMessage(e.getPlayer(), "LiftSign.Created");
    }

}
