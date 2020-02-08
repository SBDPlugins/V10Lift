package nl.SBDeveloper.V10Lift.Listeners;

import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.API.Objects.LiftSign;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
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
        if (!lines[0].equalsIgnoreCase("[v10lift]")) return;

        Player p = e.getPlayer();
        if (lines[1].isEmpty()) {
            p.sendMessage(ChatColor.RED + "No lift name given!");
            return;
        }

        if (!DataManager.containsLift(lines[1])) {
            p.sendMessage(ChatColor.RED + "Lift " + lines[1] + " doesn't exists!");
            return;
        }

        Lift lift = DataManager.getLift(lines[1]);
        if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            p.sendMessage(ChatColor.RED + "You can't do this!");
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
        p.sendMessage(ChatColor.GREEN + "Lift sign created!");
    }

}
