package nl.SBDeveloper.V10Lift.Commands;

import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class V10LiftCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (args.length == 0) {
            //v10lift
            return helpCommand(sender);
        } else if (args[0].equalsIgnoreCase("info") && args.length == 1) {
            //v10lift info
            return infoCommand(sender);
        } else if (args[0].equalsIgnoreCase("create") && (args.length == 1 || args.length == 2)) {
            //v10lift create || v10lift create <Name>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return createCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
            //v10lift delete <Name>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return deleteCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        }
        return false;
    }

    private boolean deleteCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Please use /v10lift delete <Name>");
            return true;
        }

        if (!DataManager.containsLift(args[1])) {
            sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
            return true;
        }

        Lift lift = DataManager.getLift(args[1]);
        if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to remove that lift.");
        }

        //TODO Fix ignoring of result
        V10LiftPlugin.getAPI().removeLift(args[1]);

        sender.sendMessage(ChatColor.GREEN + "The lift " + args[1] + " is removed successfully!");
        return true;
    }

    private boolean createCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (DataManager.containsPlayer(p.getUniqueId())) {
            //Already building!!
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please use /v10lift create <Name>");
                return true;
            }

            ArrayList<Block> blocks = DataManager.getPlayer(p.getUniqueId());
            if (blocks.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Add blocks first!");
                return true;
            }

            if (!V10LiftPlugin.getAPI().createLift(p, args[1])) {
                sender.sendMessage(ChatColor.RED + "A lift with that name already exists.");
            }

            blocks.forEach(block -> V10LiftPlugin.getAPI().addBlockToLift(args[1], block));
            V10LiftPlugin.getAPI().sortLiftBlocks(args[1]);
            DataManager.removePlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "The lift " + args[1] + " is created successfully!");
            p.performCommand("v10lift edit " + args[1]);
        } else {
            //Not building yet!!
            DataManager.addPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Okay, now add all the blocks from the cab by right-clicking on the blocks.");
            sender.sendMessage(ChatColor.GREEN + "Then type: /v10lift create <Name>");
        }
        return true;
    }

    private boolean infoCommand(@Nonnull CommandSender sender) {
        sender.sendMessage("§1==================================");
        sender.sendMessage("§6V10Lift plugin made by §aSBDeveloper");
        sender.sendMessage("§6Version: " + V10LiftPlugin.getInstance().getDescription().getVersion());
        sender.sendMessage("§6Type /v10lift help for more information!");
        sender.sendMessage("§1==================================");
        return true;
    }

    private boolean helpCommand(@Nonnull CommandSender sender) {
        sender.sendMessage("§8V10Lift commands:");
        sender.sendMessage("§6/v10lift info§f: Gives you information about the plugin.");
        sender.sendMessage("§6/v10lift help§f: Gives you this help page.");
        return true;
    }

}
