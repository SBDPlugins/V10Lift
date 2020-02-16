package nl.SBDeveloper.V10Lift.Commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.SBDeveloper.V10Lift.API.Objects.Floor;
import nl.SBDeveloper.V10Lift.API.Objects.Lift;
import nl.SBDeveloper.V10Lift.API.Objects.LiftBlock;
import nl.SBDeveloper.V10Lift.API.Objects.LiftSign;
import nl.SBDeveloper.V10Lift.Managers.DataManager;
import nl.SBDeveloper.V10Lift.Utils.ConfigUtil;
import nl.SBDeveloper.V10Lift.Utils.XMaterial;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import nl.SBDevelopment.SBUtilities.Utils.LocationSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.*;

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
                return true;
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
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return deleteCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("edit") && (args.length == 1 || args.length == 2)) {
            //v10lift edit <Name>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return editCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("floor") && (args.length == 3 || args.length == 4)) {
            //v10lift floor add <Name> || v10lift floor del <Name> || v10lift floor rename <Old> <New>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return floorCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("input") && (args.length == 2 || args.length == 3)) {
            //v10lift input add <Floor name> || v10lift input del
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return inputCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("offline") && args.length == 2) {
            //v10lift offline add || v10lift offline del
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return offlineCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("rename") && args.length == 2) {
            //v10lift rename <New name>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return renameCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("build") && args.length == 1) {
            //v10lift build
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return buildCommand(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("rope") && args.length == 2) {
            //v10lift rope add || v10lift rope del
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return ropeCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("door") && (args.length == 1 || args.length == 2)) {
            //v10lift door <Name> || v10lift door
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return doorCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("whitelist") && (args.length == 3 || args.length == 4)) {
            //v10lift whitelist add <Player> <Floor> || v10lift whitelist del <Player> <Floor>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return whitelistCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("whois") && (args.length == 1 || args.length == 2)) {
            //v10lift whois || v10lift whois <Name>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return whoisCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("speed") && args.length == 2) {
            //v10lift speed <Speed>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return speedCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("sound") && args.length == 1) {
            //v10lift sound
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return soundCommand(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("realistic") && args.length == 1) {
            //v10lift realistic
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return realisticCommand(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("abort") && args.length == 1) {
            //v10lift abort
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return abortCommand(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
            //v10lift reload
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.admin")) {
                return reloadCommand(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else if (args[0].equalsIgnoreCase("repair") && args.length == 2) {
            //v10lift repair <Name>
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You have to be a player to do this.");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return repairCommand(sender, args);
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
            }
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean reloadCommand(CommandSender sender) {
        for (Map.Entry<String, Lift> e : DataManager.getLifts().entrySet()) {
            String lift = e.getKey();
            if (DataManager.containsMovingTask(lift)) {
                Bukkit.getScheduler().cancelTask(DataManager.getMovingTask(lift));
            }

            e.getValue().setQueue(null);
            V10LiftPlugin.getAPI().sortLiftBlocks(lift);
        }

        DataManager.clearMovingTasks();
        V10LiftPlugin.getSConfig().reloadConfig();
        try {
            V10LiftPlugin.getDBManager().save();
            V10LiftPlugin.getDBManager().load();
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }

        sender.sendMessage(ChatColor.YELLOW + "Plugin reset successful!");
        return true;
    }

    private boolean abortCommand(CommandSender sender) {
        Player p = (Player) sender;
        boolean abort = false;

        if (DataManager.containsPlayer(p.getUniqueId())) {
            DataManager.removePlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsWhoisREQPlayer(p.getUniqueId())) {
            DataManager.removeWhoisREQPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsInputEditsPlayer(p.getUniqueId())) {
            DataManager.removeInputEditsPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsInputRemovesPlayer(p.getUniqueId())) {
            DataManager.removeInputRemovesPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsOfflineEditsPlayer(p.getUniqueId())) {
            DataManager.removeOfflineEditsPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsOfflineRemovesPlayer(p.getUniqueId())) {
            DataManager.removeOfflineRemovesPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsBuilderPlayer(p.getUniqueId())) {
            DataManager.removeBuilderPlayer(p.getUniqueId());
            V10LiftPlugin.getAPI().sortLiftBlocks(DataManager.getEditPlayer(p.getUniqueId()));
            abort = true;
        }

        if (DataManager.containsRopeEditPlayer(p.getUniqueId())) {
            DataManager.removeRopeEditPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsRopeRemovesPlayer(p.getUniqueId())) {
            DataManager.removeRopeRemovesPlayer(p.getUniqueId());
            abort = true;
        }

        if (DataManager.containsDoorEditPlayer(p.getUniqueId())) {
            DataManager.removeDoorEditPlayer(p.getUniqueId());
            abort = true;
        }

        if (abort) {
            p.sendMessage(ChatColor.GOLD + "Cancelled.");
        } else {
            p.sendMessage(ChatColor.RED + "Oops! You can't cancel anything.");
        }
        return true;
    }

    private boolean repairCommand(CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        String liftName = args[1];
        if (!DataManager.containsLift(liftName)) {
            sender.sendMessage(ChatColor.RED + "Lift " + args[1] + " doesn't exists!");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);

        if (!lift.isDefective()) {
            sender.sendMessage(ChatColor.RED + "This lift isn't defective!");
            return true;
        }

        int masterAmount = V10LiftPlugin.getSConfig().getFile().getInt("MasterRepairAmount");
        Optional<XMaterial> mat = XMaterial.matchXMaterial(Objects.requireNonNull(V10LiftPlugin.getSConfig().getFile().getString("MasterRepairItem"), "MasterRepairItem is null"));
        if (!mat.isPresent()) {
            Bukkit.getLogger().severe("[V10Lift] The material for MasterRepairItem is undefined!");
            return true;
        }
        Material masterItem = mat.get().parseMaterial();
        if (masterItem == null) {
            Bukkit.getLogger().severe("[V10Lift] The material for MasterRepairItem is undefined!");
            return true;
        }
        if (p.getGameMode() != GameMode.CREATIVE && masterAmount > 0) {
            if (!p.getInventory().contains(masterItem)) {
                sender.sendMessage(ChatColor.RED + "You need " + masterAmount + "x " + masterItem.toString().toLowerCase() + "!");
                return true;
            }
            p.getInventory().remove(new ItemStack(masterItem, masterAmount));
        }
        V10LiftPlugin.getAPI().setDefective(liftName, false);
        sender.sendMessage(ChatColor.GREEN + "Lift repaired!");
        return true;
    }

    private boolean realisticCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        lift.setRealistic(!lift.isRealistic());
        sender.sendMessage(ChatColor.GREEN + "Realistic mode turned " + (lift.isSound() ? "on" : "off") + "!");
        return true;
    }

    private boolean soundCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        lift.setSound(!lift.isSound());
        sender.sendMessage(ChatColor.GREEN + "Sound mode turned " + (lift.isSound() ? "on" : "off") + "!");
        return true;
    }

    private boolean speedCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        try {
            int speed = Integer.parseInt(args[1]);
            lift.setSpeed(speed);
            if (lift.getSpeed() < 1) lift.setSpeed(1);
            sender.sendMessage(ChatColor.GREEN + "Lift speed changed!");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Wrong speed: " + args[1]);
        }
        return true;
    }

    private boolean whoisCommand(CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (args.length < 2) {
            //Without name
            DataManager.addWhoisREQPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Now right-click on the block you want to check.");
        } else {
            String liftName = args[1];
            if (!DataManager.containsLift(liftName)) {
                sender.sendMessage(ChatColor.RED + "Lift " + liftName + " not found!");
            } else {
                V10LiftPlugin.getAPI().sendLiftInfo(p, liftName);
            }
        }
        return true;
    }

    private boolean whitelistCommand(CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        OfflinePlayer wp = Bukkit.getOfflinePlayer(args[2]);
        UUID wpu = wp.getUniqueId();

        String floor = null;
        if (args.length < 4) {
            Block b = p.getLocation().getBlock();
            Floor f = new Floor(b.getY() - 1, Objects.requireNonNull(b.getWorld(), "World was null at doorCommand").getName());
            if (!lift.getFloors().containsValue(f)) {
                sender.sendMessage(ChatColor.RED + "Automatic floor detection failed!");
                return true;
            }
            for (Map.Entry<String, Floor> e : lift.getFloors().entrySet()) {
                Floor fl = e.getValue();
                if (fl.equals(f)) {
                    floor = e.getKey();
                    break;
                }
            }
        } else {
            floor = args[3];
            if (!lift.getFloors().containsKey(floor)) {
                sender.sendMessage(ChatColor.RED + "Floor " + args[3] + " not found!");
                return true;
            }
        }

        Floor f = lift.getFloors().get(floor);
        if (args[1].equalsIgnoreCase("add")) {
            if (f.getWhitelist().contains(wpu)) {
                sender.sendMessage(ChatColor.RED + "Whitelist already contains this user!");
            } else {
                f.getWhitelist().add(wpu);
                sender.sendMessage(ChatColor.GREEN + "User added to whitelist!");
            }
        } else if (args[1].equalsIgnoreCase("del")) {
            if (!f.getWhitelist().contains(wpu)) {
                sender.sendMessage(ChatColor.RED + "Whitelist doesn't contain this user!");
            } else {
                f.getWhitelist().remove(wpu);
                sender.sendMessage(ChatColor.GREEN + "User removed from whitelist!");
            }
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean doorCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        if (DataManager.containsDoorEditPlayer(p.getUniqueId())) {
            DataManager.removeDoorEditPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Door editor mode disabled!");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        String floor = null;
        if (args.length < 2) {
            Location loc = p.getLocation();
            Floor f = new Floor(loc.getBlockY() - 1, Objects.requireNonNull(loc.getWorld(), "World was null at doorCommand").getName());
            if (!lift.getFloors().containsValue(f)) {
                sender.sendMessage(ChatColor.RED + "Automatic floor detection failed!");
                return true;
            }
            for (Map.Entry<String, Floor> e : lift.getFloors().entrySet()) {
                Floor fl = e.getValue();
                if (fl.equals(f)) {
                    floor = e.getKey();
                    break;
                }
            }
        } else {
            floor = args[2];
            if (!lift.getFloors().containsKey(floor)) {
                sender.sendMessage(ChatColor.RED + "The floor " + args[2] + " doesn't exists!");
                return true;
            }
        }
        DataManager.addDoorEditPlayer(p.getUniqueId(), floor);
        sender.sendMessage(ChatColor.GREEN + "Now right-click on the door blocks!");
        sender.sendMessage(ChatColor.GREEN + "Then do /v10lift door to save it.");
        return true;
    }

    private boolean ropeCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        if (args[1].equalsIgnoreCase("add")) {
            if (DataManager.containsRopeEditPlayer(p.getUniqueId()) || DataManager.containsRopeRemovesPlayer(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You're still adjusting the emergency stairs.");
                return true;
            }
            DataManager.addRopeEditPlayer(p.getUniqueId(), null);
            sender.sendMessage(ChatColor.GREEN + "Now right-click on the beginning and the end of the emergency stairs.");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (DataManager.containsRopeEditPlayer(p.getUniqueId()) || DataManager.containsRopeRemovesPlayer(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You're still adjusting the emergency stairs.");
                return true;
            }
            DataManager.addRopeRemovesPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Now right-click on the the emergency stairs.");
        }
        return true;
    }

    private boolean buildCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        if (DataManager.containsBuilderPlayer(p.getUniqueId())) {
            DataManager.removeBuilderPlayer(p.getUniqueId());
            V10LiftPlugin.getAPI().sortLiftBlocks(DataManager.getEditPlayer(p.getUniqueId()));
            sender.sendMessage(ChatColor.GREEN + "Construction mode disabled!");
        } else {
            DataManager.addBuilderPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Now right-click on the elevator blocks!");
            sender.sendMessage(ChatColor.GREEN + "Then do /v10lift build to save it!");
        }
        return true;
    }

    private boolean renameCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
            return true;
        }

        V10LiftPlugin.getAPI().renameLift(liftName, args[1]);
        sender.sendMessage(ChatColor.GREEN + "Lift successfully renamed!");
        return true;
    }

    private boolean offlineCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);
        if (args[1].equalsIgnoreCase("add")) {
            if (DataManager.containsOfflineEditsPlayer(p.getUniqueId()) || DataManager.containsOfflineRemovesPlayer(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are still adjusting an input!");
                return true;
            }

            DataManager.addOfflineEditsPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Now right click on the offline input block!");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (lift.getOfflineInputs().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "There is no input to remove!");
                return true;
            }

            if (DataManager.containsOfflineEditsPlayer(p.getUniqueId()) || DataManager.containsOfflineRemovesPlayer(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are still adjusting an input!");
                return true;
            }

            DataManager.addOfflineRemovesPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Now right click on the offline input block!");
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean inputCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);
        if (args[1].equalsIgnoreCase("add")) {
            String floor = null;
            if (args.length < 3) {
                Block b = p.getLocation().getBlock();
                Floor f = new Floor(b.getY() - 1, b.getWorld().getName());
                if (!lift.getFloors().containsValue(f)) {
                    sender.sendMessage(ChatColor.RED + "Automatic floor detection failed!");
                    return true;
                }

                for (Map.Entry<String, Floor> e : lift.getFloors().entrySet()) {
                    Floor fl = e.getValue();
                    if (fl.equals(f)) {
                        floor = e.getKey();
                    }
                }
            } else {
                floor = args[2];
            }

            if (DataManager.containsInputEditsPlayer(p.getUniqueId()) || DataManager.containsInputRemovesPlayer(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are still adjusting an input!");
                return true;
            }

            DataManager.addInputEditsPlayer(p.getUniqueId(), Objects.requireNonNull(floor, "Floor is null at input add command"));
            sender.sendMessage(ChatColor.GREEN + "Now right click on the input block!");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (lift.getInputs().isEmpty()) {
                sender.sendMessage(ChatColor.RED + "There is no input to remove!");
                return true;
            }

            if (DataManager.containsInputEditsPlayer(p.getUniqueId()) || DataManager.containsInputRemovesPlayer(p.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + "You are still adjusting an input!");
                return true;
            }

            DataManager.addInputRemovesPlayer(p.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Now right click on the input block!");
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean floorCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "First switch on the editor mode!");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
            return true;
        }
        if (args[1].equalsIgnoreCase("add")) {
            Block b = p.getLocation().getBlock();
            String floorName = args[2];
            int response = V10LiftPlugin.getAPI().addFloor(liftName, floorName, new Floor(b.getY() - 1, b.getWorld().getName()));
            switch (response) {
                case 0:
                    sender.sendMessage(ChatColor.GREEN + "Floor successfully added!");
                    break;
                case -2:
                    sender.sendMessage(ChatColor.RED + "That floor is too high!");
                    break;
                case -3:
                    sender.sendMessage(ChatColor.RED + "That floor already exists!");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Internal error!");
                    break;
            }
        } else if (args[1].equalsIgnoreCase("del")) {
            String floorName = args[2];
            if (!V10LiftPlugin.getAPI().removeFloor(liftName, floorName)) {
                sender.sendMessage(ChatColor.RED + "Internal error!");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Floor successfully removed!");
            }
        } else if (args[1].equalsIgnoreCase("rename")) {
            if (args.length < 4) {
                sender.sendMessage(ChatColor.RED + "Please use: /v10lift floor rename <Old name> <New name>");
                return true;
            }

            String floorName = args[2];
            String newFloorName = args[3];
            int response = V10LiftPlugin.getAPI().renameFloor(liftName, floorName, newFloorName);
            switch (response) {
                case 0:
                    sender.sendMessage(ChatColor.GREEN + "Floor successfully renamed!");
                    break;
                case -2:
                    sender.sendMessage(ChatColor.RED + "That floor doesn't exists!");
                    break;
                case -3:
                    sender.sendMessage(ChatColor.RED + "That floor already exists!");
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "Internal error!");
                    break;
            }
        } else {
            //args[1] not found!
            return helpCommand(sender);
        }
        return true;
    }

    private boolean editCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (DataManager.containsEditPlayer(p.getUniqueId())) {
            //TURN OFF
            if (args.length < 2) {
                String liftName = DataManager.getEditPlayer(p.getUniqueId());
                if (!DataManager.containsLift(liftName)) {
                    sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
                    return true;
                }

                Lift lift = DataManager.getLift(liftName);
                DataManager.removeEditPlayer(p.getUniqueId());
                DataManager.removeInputEditsPlayer(p.getUniqueId());
                DataManager.removeInputRemovesPlayer(p.getUniqueId());
                DataManager.removeOfflineEditsPlayer(p.getUniqueId());
                DataManager.removeOfflineRemovesPlayer(p.getUniqueId());
                if (DataManager.containsBuilderPlayer(p.getUniqueId())) {
                    DataManager.removeBuilderPlayer(p.getUniqueId());
                    V10LiftPlugin.getAPI().sortLiftBlocks(liftName);
                }
                DataManager.removeRopeEditPlayer(p.getUniqueId());
                DataManager.removeRopeRemovesPlayer(p.getUniqueId());
                DataManager.removeDoorEditPlayer(p.getUniqueId());

                try {
                    V10LiftPlugin.getDBManager().save();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                BlockState bs;
                Sign sign;
                for (LiftBlock lb : lift.getBlocks()) {
                    bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at edit command").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                    if (!(bs instanceof Sign)) continue;
                    sign = (Sign) bs;
                    if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getColored("SignText"))) continue;
                    sign.setLine(3, "");
                    sign.update();
                }

                Iterator<LiftSign> liter = lift.getSigns().iterator();
                while (liter.hasNext()) {
                    LiftSign ls = liter.next();
                    bs = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at edit command").getBlockAt(ls.getX(), ls.getY(), ls.getZ()).getState();
                    if (!(bs instanceof Sign)) {
                        Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(bs.getBlock().getLocation()));
                        liter.remove();
                        continue;
                    }
                    sign = (Sign) bs;
                    sign.setLine(3, ls.getOldText());
                    sign.update();
                    ls.setOldText(null);
                }
                sender.sendMessage(ChatColor.GREEN + "Editor turned off!");
            } else {
                sender.sendMessage(ChatColor.RED + "You are still in editor mode.");
                return true;
            }
        } else {
            //TURN ON
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Please use /v10lift edit <Name>");
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

            DataManager.addEditPlayer(p.getUniqueId(), args[1]);
            BlockState bs;
            Sign sign;
            for (LiftBlock lb : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at edit command").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getColored("SignText"))) continue;
                sign.setLine(3, ChatColor.RED + "Maintenance");
                sign.update();
            }

            Iterator<LiftSign> liter = lift.getSigns().iterator();
            while (liter.hasNext()) {
                LiftSign ls = liter.next();
                bs = Objects.requireNonNull(Bukkit.getWorld(ls.getWorld()), "World is null at edit command").getBlockAt(ls.getX(), ls.getY(), ls.getZ()).getState();
                if (!(bs instanceof Sign)) {
                    Bukkit.getLogger().severe("[V10Lift] Wrong sign removed at: " + LocationSerializer.serialize(bs.getBlock().getLocation()));
                    liter.remove();
                    continue;
                }
                sign = (Sign) bs;
                ls.setOldText(sign.getLine(3));
                sign.setLine(3, ConfigUtil.getColored("MaintenanceText"));
                sign.update();
            }
            sender.sendMessage(ChatColor.GREEN + "Editor turned on!");
        }
        return true;
    }

    private boolean deleteCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsLift(args[1])) {
            sender.sendMessage(ChatColor.RED + "That lift doesn't exists.");
            return true;
        }

        Lift lift = DataManager.getLift(args[1]);
        if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have the permission to remove that lift.");
        }

        if (!V10LiftPlugin.getAPI().removeLift(args[1])) {
            sender.sendMessage(ChatColor.RED + "The lift " + args[1] + " couldn't be removed!");
            return true;
        }

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

            TreeSet<LiftBlock> blocks = DataManager.getPlayer(p.getUniqueId());
            if (blocks.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "Add blocks first!");
                return true;
            }

            if (!V10LiftPlugin.getAPI().createLift(p, args[1])) {
                sender.sendMessage(ChatColor.RED + "A lift with that name already exists.");
            }

            TreeSet<LiftBlock> blcks = DataManager.getLift(args[1]).getBlocks();

            blocks.forEach(block -> V10LiftPlugin.getAPI().addBlockToLift(blcks, block));
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
        sender.sendMessage("§6/v10lift reload§f: Reload the plugin.");

        sender.sendMessage("§6/v10lift create [Name]§f: Create a lift.");
        sender.sendMessage("§6/v10lift delete <Name>§f: Delete a lift.");
        sender.sendMessage("§6/v10lift abort§f: Abort your action.");
        sender.sendMessage("§6/v10lift whois [Name]§f: See information about a lift.");
        sender.sendMessage("§6/v10lift edit <Name>§f: Edit a lift.");
        sender.sendMessage("§6/v10lift floor <add/del/rename> <Name> [New name]§f: Add/remove/rename a floor.");
        sender.sendMessage("§6/v10lift input <add/del> [Floorname]§f: Add/remove a input.");
        sender.sendMessage("§6/v10lift build§f: Add/remove blocks to/from a cab.");
        sender.sendMessage("§6/v10lift rope <add/del>§f: Add/remove a rope.");
        sender.sendMessage("§6/v10lift door§f: Add doors to a lift.");
        sender.sendMessage("§6/v10lift speed <New speed>§f: Change the speed of a lift.");
        sender.sendMessage("§6/v10lift realistic§f: Toggle realistic mode.");
        sender.sendMessage("§6/v10lift repair§f: Repair a lift.");
        sender.sendMessage("§6/v10lift whitelist <add/del> <Player> [Floorname]§f: Add/remove someone of the whitelist.");
        return true;
    }

}
