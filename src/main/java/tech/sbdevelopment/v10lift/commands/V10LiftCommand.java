package tech.sbdevelopment.v10lift.commands;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.api.objects.Floor;
import tech.sbdevelopment.v10lift.api.objects.Lift;
import tech.sbdevelopment.v10lift.api.objects.LiftBlock;
import tech.sbdevelopment.v10lift.api.objects.LiftSign;
import tech.sbdevelopment.v10lift.managers.DataManager;
import tech.sbdevelopment.v10lift.managers.VaultManager;
import tech.sbdevelopment.v10lift.sbutils.LocationSerializer;
import tech.sbdevelopment.v10lift.utils.ConfigUtil;

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
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return createCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("delete") && args.length == 2) {
            //v10lift delete <Name>
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return deleteCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("edit") && (args.length == 1 || args.length == 2)) {
            //v10lift edit <Name>
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return editCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("floor") && (args.length == 3 || args.length == 4)) {
            //v10lift floor add <Name> || v10lift floor del <Name> || v10lift floor rename <Old> <New>
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return floorCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("input") && (args.length == 2 || args.length == 3)) {
            //v10lift input add <Floor name> || v10lift input del
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return inputCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("offline") && args.length == 2) {
            //v10lift offline add || v10lift offline del
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return offlineCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("rename") && args.length == 2) {
            //v10lift rename <New name>
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return renameCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("build") && args.length == 1) {
            //v10lift build
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return buildCommand(sender);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("rope") && args.length == 2) {
            //v10lift rope add || v10lift rope del
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return ropeCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("door") && (args.length == 1 || args.length == 2)) {
            //v10lift door <Name> || v10lift door
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return doorCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("whitelist") && (args.length == 3 || args.length == 4)) {
            //v10lift whitelist add <Player> <Floor> || v10lift whitelist del <Player> <Floor>
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return whitelistCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("whois") && (args.length == 1 || args.length == 2)) {
            //v10lift whois || v10lift whois <Name>
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return whoisCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("speed") && (args.length == 2 || args.length == 3)) {
            //v10lift speed <Speed> [Name]
            if (args.length == 2 && !(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return speedCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("sound") && args.length == 1) {
            //v10lift sound
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return soundCommand(sender);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("realistic") && args.length == 1) {
            //v10lift realistic
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return realisticCommand(sender);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("abort") && args.length == 1) {
            //v10lift abort
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "General.PlayerOnly");
                return true;
            }
            if (sender.hasPermission("v10lift.build") || sender.hasPermission("v10lift.admin")) {
                return abortCommand(sender);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("reload") && args.length == 1) {
            //v10lift reload
            if (sender.hasPermission("v10lift.reload") || sender.hasPermission("v10lift.admin")) {
                return reloadCommand(sender);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("repair") && args.length == 2) {
            //v10lift repair <Name>
            if (sender.hasPermission("v10lift.repair") || sender.hasPermission("v10lift.admin")) {
                return repairCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("disable") && args.length == 2) {
            //v10lift disable <Name>
            if (sender.hasPermission("v10lift.disable") || sender.hasPermission("v10lift.admin")) {
                return disableCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("start")) {
            //v10lift start <Name> <Floor>
            if (sender.hasPermission("v10lift.start") || sender.hasPermission("v10lift.admin")) {
                return startCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            //v10lift stop <Name>
            if (sender.hasPermission("v10lift.stop") || sender.hasPermission("v10lift.admin")) {
                return stopCommand(sender, args);
            } else {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
            }
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean disableCommand(CommandSender sender, @Nonnull String[] args) {
        String liftName = args[1];
        if (!DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);

        if (lift.isDefective()) {
            ConfigUtil.sendMessage(sender, "Disable.AlreadyDefective");
            return true;
        }

        V10LiftAPI.getInstance().setDefective(liftName, true);
        ConfigUtil.sendMessage(sender, "Disable.Disabled");
        return true;
    }

    private boolean stopCommand(CommandSender sender, @Nonnull String[] args) {
        String liftName;
        if (args.length == 1 && sender instanceof Player) {
            //v10lift stop -> Get liftName from loc and floorName from sign
            Player p = (Player) sender;
            liftName = V10LiftAPI.getInstance().getLiftByLocation(p.getLocation());
        } else if (args.length == 1) {
            ConfigUtil.sendMessage(sender, "Stop.NonPlayer");
            return true;
        } else {
            liftName = args[1];
        }

        if (liftName == null || !DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);
        if (!lift.getQueue().isEmpty()) lift.getQueue().clear();

        if (!DataManager.containsMovingTask(liftName)) {
            ConfigUtil.sendMessage(sender, "Stop.NoMovingTasks", Collections.singletonMap("%Name%", liftName));
            return true;
        }

        Bukkit.getScheduler().cancelTask(DataManager.getMovingTask(liftName));
        DataManager.removeMovingTask(liftName);
        ConfigUtil.sendMessage(sender, "Stop.Stopped", Collections.singletonMap("%Name%", liftName));
        return true;
    }

    private boolean startCommand(CommandSender sender, @Nonnull String[] args) {
        String liftName;
        if (args.length == 1 && sender instanceof Player) {
            //v10lift start -> Get liftName from loc and floorName from sign
            Player p = (Player) sender;
            liftName = V10LiftAPI.getInstance().getLiftByLocation(p.getLocation());
        } else if (args.length == 1) {
            ConfigUtil.sendMessage(sender, "Start.NonPlayer");
            return true;
        } else {
            liftName = args[1];
        }

        if (liftName == null || !DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);

        String floorName = null;
        if (args.length == 1 || args.length == 2) {
            for (LiftBlock lb : lift.getBlocks()) {
                Block block = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at start command").getBlockAt(lb.getX(), lb.getY(), lb.getZ());
                if (block.getState() instanceof Sign) {
                    Sign sign = (Sign) block.getState();
                    String f = ChatColor.stripColor(sign.getLine(3));
                    if (lift.getFloors().containsKey(f)) {
                        floorName = f;
                    }
                }
            }
        } else {
            floorName = args[2];
        }

        if (floorName == null || !lift.getFloors().containsKey(floorName)) {
            if (floorName == null) floorName = "null";
            ConfigUtil.sendMessage(sender, "Start.FloorDoesntExists", Collections.singletonMap("%Name%", floorName));
            return true;
        }

        Floor f = lift.getFloors().get(floorName);
        V10LiftAPI.getInstance().addToQueue(liftName, f, floorName);
        ConfigUtil.sendMessage(sender, "Start.Started", Collections.singletonMap("%Name%", liftName));
        return true;
    }

    private boolean reloadCommand(CommandSender sender) {
        for (Map.Entry<String, Lift> e : DataManager.getLifts().entrySet()) {
            String lift = e.getKey();
            if (DataManager.containsMovingTask(lift)) {
                Bukkit.getScheduler().cancelTask(DataManager.getMovingTask(lift));
            }

            e.getValue().setQueue(null);
            V10LiftAPI.getInstance().sortLiftBlocks(lift);
        }

        DataManager.clearMovingTasks();
        V10LiftPlugin.getSConfig().reloadConfig();
        try {
            V10LiftPlugin.getDBManager().save();
            V10LiftPlugin.getDBManager().load();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ConfigUtil.sendMessage(sender, "Reload.Reloaded");
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
            V10LiftAPI.getInstance().sortLiftBlocks(DataManager.getEditPlayer(p.getUniqueId()));
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
            ConfigUtil.sendMessage(sender, "Abort.Cancelled");
        } else {
            ConfigUtil.sendMessage(sender, "Abort.NothingToCancel");
        }
        return true;
    }

    private boolean repairCommand(CommandSender sender, @Nonnull String[] args) {
        String liftName = args[1];
        if (!DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);

        if (!lift.isDefective()) {
            ConfigUtil.sendMessage(sender, "Repair.NotDefective");
            return true;
        }

        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (!p.hasPermission("v10lift.admin")) {
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
                        Map<String, String> replacements = new HashMap<>();
                        replacements.put("%Amount%", String.valueOf(masterAmount));
                        replacements.put("%ItemName%", masterItem.toString().toLowerCase());
                        ConfigUtil.sendMessage(sender, "Repair.ItemsNeeded", replacements);
                        return true;
                    }
                    p.getInventory().remove(new ItemStack(masterItem, masterAmount));
                }
            }
        }
        V10LiftAPI.getInstance().setDefective(liftName, false);
        ConfigUtil.sendMessage(sender, "Repair.Repaired");
        return true;
    }

    private boolean realisticCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        lift.setRealistic(!lift.isRealistic());
        if (lift.isRealistic()) {
            ConfigUtil.sendMessage(sender, "Realistic.TurnedOn");
        } else {
            ConfigUtil.sendMessage(sender, "Realistic.TurnedOff");
        }
        return true;
    }

    private boolean soundCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        lift.setSound(!lift.isSound());
        if (lift.isSound()) {
            ConfigUtil.sendMessage(sender, "Sound.TurnedOn");
        } else {
            ConfigUtil.sendMessage(sender, "Sound.TurnedOff");
        }
        return true;
    }

    private boolean speedCommand(CommandSender sender, @Nonnull String[] args) {
        Lift lift;
        if (args.length == 3) {
            if (!DataManager.containsLift(args[2])) {
                ConfigUtil.sendMessage(sender, "General.DoesntExists");
                return true;
            }
            lift = DataManager.getLift(args[2]);
        } else {
            Player p = (Player) sender;
            if (!DataManager.containsEditPlayer(p.getUniqueId())) {
                ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
                return true;
            }

            lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        }

        try {
            int speed = Integer.parseInt(args[1]);
            lift.setSpeed(speed);
            if (lift.getSpeed() < 1) lift.setSpeed(1);
            ConfigUtil.sendMessage(sender, "Speed.Changed");
        } catch (NumberFormatException e) {
            ConfigUtil.sendMessage(sender, "Speed.WrongSpeed", Collections.singletonMap("%Speed%", args[1]));
        }
        return true;
    }

    private boolean whoisCommand(CommandSender sender, @Nonnull String[] args) {
        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                ConfigUtil.sendMessage(sender, "Whois.UseWithoutName");
                return true;
            }

            //Without name
            Player p = (Player) sender;
            DataManager.addWhoisREQPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "Whois.WithoutName");
        } else {
            String liftName = args[1];
            if (!DataManager.containsLift(liftName)) {
                ConfigUtil.sendMessage(sender, "Whois.DoesntExists");
            } else {
                V10LiftAPI.getInstance().sendLiftInfo(sender, liftName);
            }
        }
        return true;
    }

    private boolean whitelistCommand(CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        boolean isGroup = false;
        String wgn = null;
        UUID wpu = null;
        if (args[2].startsWith("g:")) {
            if (!V10LiftPlugin.isVaultEnabled()) {
                ConfigUtil.sendMessage(sender, "Whitelist.Group.VaultNotFound");
                return true;
            }
            isGroup = true;
            wgn = args[2].replace("g:", "");
            if (!VaultManager.isGroup(wgn)) {
                ConfigUtil.sendMessage(sender, "Whitelist.Group.NotFound", Collections.singletonMap("%Name%", wgn));
                return true;
            }
        } else {
            OfflinePlayer wp = Bukkit.getOfflinePlayer(args[2]);
            wpu = wp.getUniqueId();
            if (!wp.hasPlayedBefore()) {
                ConfigUtil.sendMessage(sender, "Whitelist.Player.NotFound", Collections.singletonMap("%Name%", args[2]));
                return true;
            }
        }

        String floor = null;
        if (args.length < 4) {
            Block b = p.getLocation().getBlock();
            Floor f = new Floor(b.getY() - 1, Objects.requireNonNull(b.getWorld(), "World was null at whitelistCommand").getName());
            if (!lift.getFloors().containsValue(f)) {
                ConfigUtil.sendMessage(sender, "General.DetectionFailed");
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
                ConfigUtil.sendMessage(sender, "General.FloorDoesntExists", Collections.singletonMap("%Name%", args[3]));
                return true;
            }
        }

        Floor f = lift.getFloors().get(floor);
        if (args[1].equalsIgnoreCase("add")) {
            if (isGroup) {
                if (f.getGroupWhitelist().contains(wgn)) {
                    ConfigUtil.sendMessage(sender, "Whitelist.Group.AlreadyContains");
                } else {
                    f.getGroupWhitelist().add(wgn);
                    ConfigUtil.sendMessage(sender, "Whitelist.Player.Added");
                }
            } else {
                if (f.getUserWhitelist().contains(wpu)) {
                    ConfigUtil.sendMessage(sender, "Whitelist.Group.AlreadyContains");
                } else {
                    f.getUserWhitelist().add(wpu);
                    ConfigUtil.sendMessage(sender, "Whitelist.Player.Added");
                }
            }
        } else if (args[1].equalsIgnoreCase("del")) {
            if (isGroup) {
                if (!f.getGroupWhitelist().contains(wgn)) {
                    ConfigUtil.sendMessage(sender, "Whitelist.Group.DoesntContains");
                } else {
                    f.getGroupWhitelist().remove(wgn);
                    ConfigUtil.sendMessage(sender, "Whitelist.Group.Removed");
                }
            } else {
                if (!f.getUserWhitelist().contains(wpu)) {
                    ConfigUtil.sendMessage(sender, "Whitelist.Player.DoesntContains");
                } else {
                    f.getUserWhitelist().remove(wpu);
                    ConfigUtil.sendMessage(sender, "Whitelist.User.Removed");
                }
            }
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean doorCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        if (DataManager.containsDoorEditPlayer(p.getUniqueId())) {
            DataManager.removeDoorEditPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "Door.Disabled");
            return true;
        }

        Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
        String floor = null;
        if (args.length < 2) {
            Location loc = p.getLocation();
            Floor f = new Floor(loc.getBlockY() - 1, Objects.requireNonNull(loc.getWorld(), "World was null at doorCommand").getName());
            if (!lift.getFloors().containsValue(f)) {
                ConfigUtil.sendMessage(sender, "General.DetectionFailed");
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
                ConfigUtil.sendMessage(sender, "General.FloorDoesntExists", Collections.singletonMap("%Name", args[2]));
                return true;
            }
        }
        DataManager.addDoorEditPlayer(p.getUniqueId(), floor);
        ConfigUtil.sendMessage(sender, "Door.Enabled");
        return true;
    }

    private boolean ropeCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        if (args[1].equalsIgnoreCase("add")) {
            if (DataManager.containsRopeEditPlayer(p.getUniqueId()) || DataManager.containsRopeRemovesPlayer(p.getUniqueId())) {
                ConfigUtil.sendMessage(sender, "Rope.StillAdjusting");
                return true;
            }
            DataManager.addRopeEditPlayer(p.getUniqueId(), null);
            ConfigUtil.sendMessage(sender, "Rope.Add");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (DataManager.containsRopeEditPlayer(p.getUniqueId()) || DataManager.containsRopeRemovesPlayer(p.getUniqueId())) {
                ConfigUtil.sendMessage(sender, "Rope.StillAdjusting");
                return true;
            }
            DataManager.addRopeRemovesPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "Rope.Delete");
        }
        return true;
    }

    private boolean buildCommand(CommandSender sender) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        if (DataManager.containsBuilderPlayer(p.getUniqueId())) {
            DataManager.removeBuilderPlayer(p.getUniqueId());
            V10LiftAPI.getInstance().sortLiftBlocks(DataManager.getEditPlayer(p.getUniqueId()));
            ConfigUtil.sendMessage(sender, "Build.Disabled");
        } else {
            DataManager.addBuilderPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "Build.Enabled");
        }
        return true;
    }

    private boolean renameCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Bukkit.dispatchCommand(sender, "v10lift edit");
        V10LiftAPI.getInstance().renameLift(liftName, args[1]);
        Bukkit.dispatchCommand(sender, "v10lift edit " + args[1]);

        ConfigUtil.sendMessage(sender, "Rename.Renamed");
        return true;
    }

    private boolean offlineCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);
        if (args[1].equalsIgnoreCase("add")) {
            if (DataManager.containsOfflineEditsPlayer(p.getUniqueId()) || DataManager.containsOfflineRemovesPlayer(p.getUniqueId())) {
                ConfigUtil.sendMessage(sender, "OfflineInput.StillAdjusting");
                return true;
            }

            DataManager.addOfflineEditsPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "OfflineInput.RightClick");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (lift.getOfflineInputs().isEmpty()) {
                ConfigUtil.sendMessage(sender, "OfflineInput.NothingToRemove");
                return true;
            }

            if (DataManager.containsOfflineEditsPlayer(p.getUniqueId()) || DataManager.containsOfflineRemovesPlayer(p.getUniqueId())) {
                ConfigUtil.sendMessage(sender, "OfflineInput.StillAdjusting");
                return true;
            }

            DataManager.addOfflineRemovesPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "OfflineInput.RightClick");
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean inputCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(liftName);
        if (args[1].equalsIgnoreCase("add")) {
            String floor = null;
            if (args.length < 3) {
                Block b = p.getLocation().getBlock();
                Floor f = new Floor(b.getY() - 1, b.getWorld().getName());
                if (!lift.getFloors().containsValue(f)) {
                    ConfigUtil.sendMessage(sender, "General.DetectionFailed");
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
                ConfigUtil.sendMessage(sender, "Input.StillAdjusting");
                return true;
            }

            DataManager.addInputEditsPlayer(p.getUniqueId(), Objects.requireNonNull(floor, "Floor is null at input add command"));
            ConfigUtil.sendMessage(sender, "Input.RightClick");
        } else if (args[1].equalsIgnoreCase("del")) {
            if (lift.getInputs().isEmpty()) {
                ConfigUtil.sendMessage(sender, "Input.NothingToRemove");
                return true;
            }

            if (DataManager.containsInputEditsPlayer(p.getUniqueId()) || DataManager.containsInputRemovesPlayer(p.getUniqueId())) {
                ConfigUtil.sendMessage(sender, "Input.StillAdjusting");
                return true;
            }

            DataManager.addInputRemovesPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(sender, "Input.RightClick");
        } else {
            return helpCommand(sender);
        }
        return true;
    }

    private boolean floorCommand(CommandSender sender, String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsEditPlayer(p.getUniqueId())) {
            ConfigUtil.sendMessage(sender, "General.SwitchOnEdit");
            return true;
        }

        String liftName = DataManager.getEditPlayer(p.getUniqueId());
        if (!DataManager.containsLift(liftName)) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }
        if (args[1].equalsIgnoreCase("add")) {
            Block b = p.getLocation().getBlock();
            String floorName = args[2];
            int response = V10LiftAPI.getInstance().addFloor(liftName, floorName, new Floor(b.getY() - 1, b.getWorld().getName()));
            switch (response) {
                case 0:
                    ConfigUtil.sendMessage(sender, "Floor.Added");
                    break;
                case -2:
                    ConfigUtil.sendMessage(sender, "Floor.ToHigh");
                    break;
                case -3:
                    ConfigUtil.sendMessage(sender, "Floor.AlreadyExists");
                    break;
                default:
                    ConfigUtil.sendMessage(sender, "General.InternalError");
                    break;
            }
        } else if (args[1].equalsIgnoreCase("del")) {
            String floorName = args[2];
            if (!V10LiftAPI.getInstance().removeFloor(liftName, floorName)) {
                ConfigUtil.sendMessage(sender, "General.InternalError");
            } else {
                ConfigUtil.sendMessage(sender, "Floor.Removed");
            }
        } else if (args[1].equalsIgnoreCase("rename")) {
            if (args.length < 4) {
                ConfigUtil.sendMessage(sender, "General.IncorrectUsage", Collections.singletonMap("%Command%", "/v10lift floor rename <Old name> <New name>"));
                return true;
            }

            String floorName = args[2];
            String newFloorName = args[3];
            int response = V10LiftAPI.getInstance().renameFloor(liftName, floorName, newFloorName);
            switch (response) {
                case 0:
                    ConfigUtil.sendMessage(sender, "Floor.Renamed");
                    break;
                case -2:
                    ConfigUtil.sendMessage(sender, "Floor.DoesntExists");
                    break;
                case -3:
                    ConfigUtil.sendMessage(sender, "Floor.AlreadyExists");
                    break;
                default:
                    ConfigUtil.sendMessage(sender, "General.InternalError");
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
                    ConfigUtil.sendMessage(sender, "General.DoesntExists");
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
                    V10LiftAPI.getInstance().sortLiftBlocks(liftName);
                }
                DataManager.removeRopeEditPlayer(p.getUniqueId());
                DataManager.removeRopeRemovesPlayer(p.getUniqueId());
                DataManager.removeDoorEditPlayer(p.getUniqueId());

                V10LiftPlugin.getDBManager().saveLift(liftName, lift, false);

                BlockState bs;
                Sign sign;
                for (LiftBlock lb : lift.getBlocks()) {
                    bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at edit command").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                    if (!(bs instanceof Sign)) continue;
                    sign = (Sign) bs;
                    if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) continue;
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
                ConfigUtil.sendMessage(sender, "Edit.TurnedOff");
            } else {
                ConfigUtil.sendMessage(sender, "Edit.StillInEditMode");
                return true;
            }
        } else {
            //TURN ON
            if (args.length < 2) {
                ConfigUtil.sendMessage(sender, "General.IncorrectUsage", Collections.singletonMap("%Command%", "/v10lift edit <Name>"));
                return true;
            }

            if (!DataManager.containsLift(args[1])) {
                ConfigUtil.sendMessage(sender, "General.DoesntExists");
                return true;
            }

            Lift lift = DataManager.getLift(args[1]);
            if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
                ConfigUtil.sendMessage(sender, "General.NoPermission");
                return true;
            }

            DataManager.addEditPlayer(p.getUniqueId(), args[1]);
            BlockState bs;
            Sign sign;
            for (LiftBlock lb : lift.getBlocks()) {
                bs = Objects.requireNonNull(Bukkit.getWorld(lb.getWorld()), "World is null at edit command").getBlockAt(lb.getX(), lb.getY(), lb.getZ()).getState();
                if (!(bs instanceof Sign)) continue;
                sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) continue;
                sign.setLine(3, ConfigUtil.getConfigText("MaintenanceText"));
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
                sign.setLine(3, ConfigUtil.getConfigText("MaintenanceText"));
                sign.update();
            }
            ConfigUtil.sendMessage(sender, "Edit.TurnedOn");
        }
        return true;
    }

    private boolean deleteCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (!DataManager.containsLift(args[1])) {
            ConfigUtil.sendMessage(sender, "General.DoesntExists");
            return true;
        }

        Lift lift = DataManager.getLift(args[1]);
        if (!lift.getOwners().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            ConfigUtil.sendMessage(sender, "General.NoPermission");
            return true;
        }

        if (!V10LiftAPI.getInstance().removeLift(args[1])) {
            ConfigUtil.sendMessage(sender, "Delete.NotRemoved", Collections.singletonMap("%Name%", args[1]));
            return true;
        }

        ConfigUtil.sendMessage(sender, "Delete.Removed", Collections.singletonMap("%Name%", args[1]));
        return true;
    }

    private boolean createCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        Player p = (Player) sender;
        if (DataManager.containsPlayer(p.getUniqueId())) {
            //Already building!!
            if (args.length < 2) {
                ConfigUtil.sendMessage(sender, "General.IncorrectUsage", Collections.singletonMap("%Command%", "/v10lift create <Name>"));
                return true;
            }

            TreeSet<LiftBlock> blocks = DataManager.getPlayer(p.getUniqueId());
            if (blocks.isEmpty()) {
                ConfigUtil.sendMessage(sender, "Create.NoBlocks");
                return true;
            }

            if (!V10LiftAPI.getInstance().createLift(p, args[1])) {
                ConfigUtil.sendMessage(sender, "General.AlreadyExists");
                return true;
            }

            TreeSet<LiftBlock> blcks = DataManager.getLift(args[1]).getBlocks();

            blocks.forEach(block -> V10LiftAPI.getInstance().addBlockToLift(blcks, block));
            V10LiftAPI.getInstance().sortLiftBlocks(args[1]);
            DataManager.removePlayer(p.getUniqueId());
            ConfigUtil.sendMessage(p, "Create.Created", Collections.singletonMap("%Name%", args[1]));
            p.performCommand("v10lift edit " + args[1]);
        } else {
            //Not building yet!!
            DataManager.addPlayer(p.getUniqueId());
            ConfigUtil.sendMessage(p, "Create.AddBlocks");
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
        sender.sendMessage("§6/v10lift rename <New name>§f: Rename a lift.");
        sender.sendMessage("§6/v10lift abort§f: Abort your action.");
        sender.sendMessage("§6/v10lift whois [Name]§f: See information about a lift.");
        sender.sendMessage("§6/v10lift edit [Name]§f: Edit a lift.");
        sender.sendMessage("§6/v10lift floor <add/del/rename> <Name> [New name]§f: Add/remove/rename a floor.");
        sender.sendMessage("§6/v10lift input <add/del> [Floorname]§f: Add/remove an input.");
        sender.sendMessage("§6/v10lift offline <add/del>§f: Add/remove an offline input.");
        sender.sendMessage("§6/v10lift build§f: Add/remove blocks to/from a cab.");
        sender.sendMessage("§6/v10lift rope <add/del>§f: Add/remove a rope.");
        sender.sendMessage("§6/v10lift door§f: Add doors to a lift.");
        sender.sendMessage("§6/v10lift speed <New speed>§f: Change the speed of a lift.");
        sender.sendMessage("§6/v10lift realistic§f: Toggle realistic mode.");
        sender.sendMessage("§6/v10lift repair§f: Repair a lift.");
        sender.sendMessage("§6/v10lift whitelist <add/del> <Player/Group> [Floorname]§f: Add/remove someone of the whitelist. Use g:<Groupname> for a group.");
        sender.sendMessage("§6/v10lift start [Name] [Floor]§f: Start a lift to a floor.");
        sender.sendMessage("§6/v10lift stop [Name]§f: Stop a lift.");
        sender.sendMessage("§6/v10lift disable <Name>§f: Disable a lift.");
        sender.sendMessage("§6/v10lift repair <Name>§f: Repair a lift.");
        return true;
    }

}
