package tech.sbdevelopment.v10lift.listeners;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import tech.sbdevelopment.v10lift.V10LiftPlugin;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.api.objects.Floor;
import tech.sbdevelopment.v10lift.api.objects.Lift;
import tech.sbdevelopment.v10lift.api.objects.LiftBlock;
import tech.sbdevelopment.v10lift.api.objects.LiftInput;
import tech.sbdevelopment.v10lift.managers.DataManager;
import tech.sbdevelopment.v10lift.managers.ForbiddenBlockManager;
import tech.sbdevelopment.v10lift.managers.VaultManager;
import tech.sbdevelopment.v10lift.utils.ConfigUtil;
import tech.sbdevelopment.v10lift.utils.DoorUtil;

import java.util.*;

public class PlayerInteractListener implements Listener {
    private final ArrayList<UUID> rightClicked = new ArrayList<>();

    //BUTTON CLICK
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteractButton(PlayerInteractEvent e) {
        Action action = e.getAction();
        Block block = e.getClickedBlock();
        if (block == null) return;

        if (action == Action.RIGHT_CLICK_BLOCK
                && e.getHand() != EquipmentSlot.OFF_HAND) {
            String world = block.getWorld().getName();
            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();

            outer:
            for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
                Lift lift = entry.getValue();

                for (LiftInput lbi : lift.getOfflineInputs()) {
                    if (world.equals(lbi.getWorld()) && x == lbi.getX() && y == lbi.getY() && z == lbi.getZ()) {
                        boolean newState = !lift.isOffline();
                        V10LiftAPI.getInstance().setOffline(entry.getKey(), newState);

                        //Update all offline inputs
                        for (LiftInput li : lift.getOfflineInputs()) {
                            Block b = Bukkit.getWorld(li.getWorld()).getBlockAt(li.getX(), li.getY(), li.getZ());
                            BlockData bd = b.getBlockData();
                            if (!(bd instanceof Powerable)) continue;
                            ((Powerable) bd).setPowered(newState);
                            b.setBlockData(bd);
                        }

                        break outer; //We handled an input, stop!
                    }
                }

                for (LiftInput lbi : lift.getInputs()) {
                    if (world.equals(lbi.getWorld()) && x == lbi.getX() && y == lbi.getY() && z == lbi.getZ()) {
                        V10LiftAPI.getInstance().addToQueue(entry.getKey(), lift.getFloors().get(lbi.getFloor()), lbi.getFloor());
                        e.setCancelled(true);
                        break outer; //We handled an input, stop!
                    }
                }
            }
        }
    }

    //Gamemode adventure left click fix
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerLeftClickSign(PlayerAnimationEvent e) {
        Player p = e.getPlayer();

        if (p.getGameMode() != GameMode.ADVENTURE) return;

        Block lookingBlock = p.getTargetBlock(null, 5);

        BlockState bs = lookingBlock.getState();
        if (!(bs instanceof Sign)) return;

        if (rightClicked.contains(p.getUniqueId())) {
            rightClicked.remove(p.getUniqueId());
            return;
        }

        Sign sign = (Sign) bs;
        if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) return;

        String liftName = sign.getLine(1);
        if (!DataManager.containsLift(liftName)) return;
        Lift lift = DataManager.getLift(liftName);
        if (lift.isOffline()) {
            e.setCancelled(true);
            return;
        }

        if (lift.isDefective()) {
            e.setCancelled(true);
            return;
        }

        if (!lift.getBlocks().contains(new LiftBlock(sign.getBlock())))
            return;
        if (DataManager.containsEditLift(liftName)) return;
        e.setCancelled(true);
        if (lift.isDefective()) return;
        String f = ChatColor.stripColor(sign.getLine(3));

        if (!lift.getFloors().containsKey(f)) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.FloorDoesntExists");
            return;
        }

        Floor floor = lift.getFloors().get(f);
        if (!floor.getUserWhitelist().isEmpty() && !floor.getUserWhitelist().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.NoWhitelistPermission");
            e.setCancelled(true);
            return;
        }

        if (!floor.getGroupWhitelist().isEmpty() && !VaultManager.inAnyGroup(p, floor.getGroupWhitelist()) && !p.hasPermission("v10lift.admin")) {
            ConfigUtil.sendMessage(e.getPlayer(), "General.NoWhitelistPermission");
            e.setCancelled(true);
            return;
        }

        V10LiftAPI.getInstance().addToQueue(liftName, lift.getFloors().get(f), f);
    }

    //BLOCK ADD
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent e) {
        rightClicked.add(e.getPlayer().getUniqueId());

        if (e.getHand() != EquipmentSlot.OFF_HAND && e.getClickedBlock() != null) {
            Player p = e.getPlayer();
            if (DataManager.containsPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                int res = V10LiftAPI.getInstance().switchBlockAtLift(DataManager.getPlayer(p.getUniqueId()), e.getClickedBlock());
                switch (res) {
                    case 0:
                        ConfigUtil.sendMessage(e.getPlayer(), "Build.BlockAdded");
                        break;
                    case 1:
                        ConfigUtil.sendMessage(e.getPlayer(), "Build.BlockRemoved");
                        break;
                    case -2:
                        ConfigUtil.sendMessage(e.getPlayer(), "Build.BlacklistedMaterial", Collections.singletonMap("%Name%", e.getClickedBlock().getType().toString().toLowerCase()));
                        break;
                    default:
                        ConfigUtil.sendMessage(e.getPlayer(), "General.InternalError");
                        break;
                }
            } else if (DataManager.containsInputEditsPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                Block block = e.getClickedBlock();
                LiftInput tlb = new LiftInput(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), DataManager.getInputEditsPlayer(p.getUniqueId()));
                Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
                e.setCancelled(true);
                if (lift.getInputs().contains(tlb)) {
                    ConfigUtil.sendMessage(e.getPlayer(), "Input.AlreadyAdded");
                    return;
                }
                lift.getInputs().add(tlb);
                DataManager.removeInputEditsPlayer(p.getUniqueId());
                ConfigUtil.sendMessage(e.getPlayer(), "Input.Created");
            } else if (DataManager.containsOfflineEditsPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                Block block = e.getClickedBlock();
                LiftInput tlb = new LiftInput(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), null);
                Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
                e.setCancelled(true);
                if (lift.getOfflineInputs().contains(tlb)) {
                    ConfigUtil.sendMessage(e.getPlayer(), "OfflineInput.AlreadyAdded");
                    return;
                }
                lift.getOfflineInputs().add(tlb);
                DataManager.removeOfflineEditsPlayer(p.getUniqueId());
                ConfigUtil.sendMessage(e.getPlayer(), "OfflineInput.Created");
            } else if (DataManager.containsInputRemovesPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                Block block = e.getClickedBlock();
                LiftInput tlb = new LiftInput(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), null);
                Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
                e.setCancelled(true);
                if (lift.getInputs().contains(tlb)) {
                    lift.getInputs().remove(tlb);
                    DataManager.removeInputRemovesPlayer(p.getUniqueId());
                    ConfigUtil.sendMessage(e.getPlayer(), "Input.Removed");
                    return;
                }
                ConfigUtil.sendMessage(e.getPlayer(), "Input.NoInput");
            } else if (DataManager.containsOfflineRemovesPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                Block block = e.getClickedBlock();
                LiftInput tlb = new LiftInput(block.getWorld().getName(), block.getX(), block.getY(), block.getZ(), null);
                Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
                e.setCancelled(true);
                if (lift.getOfflineInputs().contains(tlb)) {
                    lift.getOfflineInputs().remove(tlb);
                    DataManager.removeOfflineRemovesPlayer(p.getUniqueId());
                    ConfigUtil.sendMessage(e.getPlayer(), "OfflineInput.Removed");
                    return;
                }
                ConfigUtil.sendMessage(e.getPlayer(), "OfflineInput.NoInput");
            } else if (DataManager.containsBuilderPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                int res = V10LiftAPI.getInstance().switchBlockAtLift(DataManager.getEditPlayer(p.getUniqueId()), e.getClickedBlock());
                switch (res) {
                    case 0:
                        ConfigUtil.sendMessage(e.getPlayer(), "Build.BlockAdded");
                        break;
                    case 1:
                        ConfigUtil.sendMessage(e.getPlayer(), "Build.BlockRemoved");
                        break;
                    case -2:
                        ConfigUtil.sendMessage(e.getPlayer(), "Build.BlacklistedMaterial", Collections.singletonMap("%Name%", e.getClickedBlock().getType().toString().toLowerCase()));
                        break;
                    default:
                        ConfigUtil.sendMessage(e.getPlayer(), "General.InternalError");
                        break;
                }
            } else if (DataManager.containsRopeEditPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                LiftBlock start = DataManager.getRopeEditPlayer(p.getUniqueId());
                Block now = e.getClickedBlock();
                if (start == null) {
                    ConfigUtil.sendMessage(e.getPlayer(), "Rope.ClickOnEnd");
                    DataManager.addRopeEditPlayer(p.getUniqueId(), new LiftBlock(now));
                } else if (start.equals(new LiftBlock(now))) {
                    DataManager.addRopeEditPlayer(p.getUniqueId(), null);
                    ConfigUtil.sendMessage(e.getPlayer(), "Rope.PartRemoved");
                } else {
                    if (start.getX() != now.getX() || start.getZ() != now.getZ()) {
                        ConfigUtil.sendMessage(e.getPlayer(), "Rope.OnlyUp");
                        return;
                    }
                    int res = V10LiftAPI.getInstance().addRope(DataManager.getEditPlayer(p.getUniqueId()), now.getWorld(), start.getX(), now.getY(), start.getY(), start.getZ());
                    switch (res) {
                        case 0:
                            ConfigUtil.sendMessage(e.getPlayer(), "Rope.Created");
                            break;
                        case -2:
                            ConfigUtil.sendMessage(e.getPlayer(), "Rope.OnlyOneMaterial");
                            break;
                        case -3:
                            ConfigUtil.sendMessage(e.getPlayer(), "Rope.AlreadyARope");
                            break;
                        case -4:
                            ConfigUtil.sendMessage(e.getPlayer(), "Rope.BlacklistedMaterial");
                            break;
                        default:
                            ConfigUtil.sendMessage(e.getPlayer(), "General.InternalError");
                            break;
                    }
                    DataManager.removeRopeEditPlayer(p.getUniqueId());
                }
            } else if (DataManager.containsRopeRemovesPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                Block block = e.getClickedBlock();
                String liftName = DataManager.getEditPlayer(p.getUniqueId());
                if (!V10LiftAPI.getInstance().containsRope(liftName, block)) {
                    ConfigUtil.sendMessage(e.getPlayer(), "Rope.NotARope");
                    return;
                }
                V10LiftAPI.getInstance().removeRope(liftName, block);
                DataManager.removeRopeRemovesPlayer(p.getUniqueId());
                ConfigUtil.sendMessage(e.getPlayer(), "Rope.Removed");
            } else if (DataManager.containsDoorEditPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                Block block = e.getClickedBlock();
                if (ForbiddenBlockManager.isForbidden(block.getType())) {
                    ConfigUtil.sendMessage(e.getPlayer(), "Door.BlacklistedMaterial", Collections.singletonMap("%Name%", e.getClickedBlock().getType().toString().toLowerCase()));
                    return;
                }
                LiftBlock lb = new LiftBlock(block);
                Lift lift = DataManager.getLift(DataManager.getEditPlayer(p.getUniqueId()));
                Floor floor = lift.getFloors().get(DataManager.getDoorEditPlayer(p.getUniqueId()));
                if (DoorUtil.isOpenable(block)) {
                    if (floor.getRealDoorBlocks().contains(lb)) {
                        floor.getRealDoorBlocks().remove(lb);
                        ConfigUtil.sendMessage(e.getPlayer(), "Door.Removed");
                        return;
                    }
                    floor.getRealDoorBlocks().add(lb);
                } else {
                    if (floor.getDoorBlocks().contains(lb)) {
                        floor.getDoorBlocks().remove(lb);
                        ConfigUtil.sendMessage(e.getPlayer(), "Door.Removed");
                        return;
                    }
                    floor.getDoorBlocks().add(lb);
                }
                ConfigUtil.sendMessage(e.getPlayer(), "Door.Created");
            } else if (DataManager.containsWhoisREQPlayer(p.getUniqueId())) {
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                e.setCancelled(true);
                Block block = e.getClickedBlock();
                LiftBlock lb = new LiftBlock(block);
                DataManager.removeWhoisREQPlayer(p.getUniqueId());
                for (Map.Entry<String, Lift> entry : DataManager.getLifts().entrySet()) {
                    Lift lift = entry.getValue();
                    if (lift.getBlocks().contains(lb) || lift.getInputs().contains(lb) || lift.getSigns().contains(lb) || lift.getRopes().contains(lb) || lift.getOfflineInputs().contains(lb)) {
                        V10LiftAPI.getInstance().sendLiftInfo(p, entry.getKey(), lift);
                        return;
                    }
                }
                ConfigUtil.sendMessage(e.getPlayer(), "Whois.NotALift");
            } else {
                Action a = e.getAction();
                if (a != Action.RIGHT_CLICK_BLOCK && a != Action.LEFT_CLICK_BLOCK) return;

                BlockState bs = e.getClickedBlock().getState();
                if (!(bs instanceof Sign)) return;

                Sign sign = (Sign) bs;
                if (!sign.getLine(0).equalsIgnoreCase(ConfigUtil.getConfigText("SignText"))) return;

                String liftName = sign.getLine(1);
                if (!DataManager.containsLift(liftName)) return;
                Lift lift = DataManager.getLift(liftName);
                if (lift.isOffline()) {
                    e.setCancelled(true);
                    return;
                }

                if (lift.isDefective()) {
                    if (sign.getLine(3).equals(ConfigUtil.getConfigText("DefectText")) && p.hasPermission("v10lift.repair") && a == Action.RIGHT_CLICK_BLOCK) {
                        int masterAmount = V10LiftPlugin.getSConfig().getFile().getInt("RepairAmount");
                        Optional<XMaterial> mat = XMaterial.matchXMaterial(Objects.requireNonNull(V10LiftPlugin.getSConfig().getFile().getString("RepairItem"), "RepairItem is null"));
                        if (!mat.isPresent()) {
                            Bukkit.getLogger().severe("[V10Lift] The material for RepairItem is undefined!");
                            return;
                        }
                        Material masterItem = mat.get().parseMaterial();
                        if (masterItem == null) {
                            Bukkit.getLogger().severe("[V10Lift] The material for RepairItem is undefined!");
                            return;
                        }
                        if (p.getGameMode() != GameMode.CREATIVE && masterAmount > 0) {
                            if (!p.getInventory().contains(masterItem)) {
                                Map<String, String> replacements = new HashMap<>();
                                replacements.put("%Amount%", String.valueOf(masterAmount));
                                replacements.put("%ItemName%", masterItem.toString().toLowerCase());
                                ConfigUtil.sendMessage(e.getPlayer(), "Repair.ItemsNeeded", replacements);
                                return;
                            }
                            p.getInventory().remove(new ItemStack(masterItem, masterAmount));
                        }
                        V10LiftAPI.getInstance().setDefective(liftName, false);
                    }
                    e.setCancelled(true);
                    return;
                }

                if (!lift.getBlocks().contains(new LiftBlock(sign.getBlock())))
                    return;
                if (DataManager.containsEditLift(liftName)) return;
                e.setCancelled(true);
                if (lift.isDefective()) return;
                String f = ChatColor.stripColor(sign.getLine(3));
                if (a == Action.RIGHT_CLICK_BLOCK) {
                    Iterator<String> iter = lift.getFloors().keySet().iterator();
                    if (!lift.getFloors().containsKey(f)) {
                        if (!iter.hasNext()) {
                            ConfigUtil.sendMessage(e.getPlayer(), "General.NoFloors");
                            return;
                        }
                        f = iter.next();
                    }
                    while (iter.hasNext()) {
                        if (iter.next().equals(f)) break;
                    }
                    if (!iter.hasNext()) iter = lift.getFloors().keySet().iterator();

                    String f2 = iter.next();
                    Floor floor = lift.getFloors().get(f2);
                    if (lift.getY() == floor.getY()) {
                        sign.setLine(3, ChatColor.GREEN + f2);
                    } else if (!floor.getUserWhitelist().isEmpty() && !floor.getUserWhitelist().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
                        sign.setLine(3, ChatColor.RED + f2);
                    } else if (!floor.getGroupWhitelist().isEmpty() && !VaultManager.inAnyGroup(p, floor.getGroupWhitelist()) && !p.hasPermission("v10lift.admin")) {
                        sign.setLine(3, ChatColor.RED + f2);
                    } else {
                        sign.setLine(3, ChatColor.YELLOW + f2);
                    }
                    sign.update();
                } else {
                    if (!lift.getFloors().containsKey(f)) {
                        ConfigUtil.sendMessage(e.getPlayer(), "General.FloorDoesntExists");
                        return;
                    }

                    Floor floor = lift.getFloors().get(f);
                    if (!floor.getUserWhitelist().isEmpty() && !floor.getUserWhitelist().contains(p.getUniqueId()) && !p.hasPermission("v10lift.admin")) {
                        ConfigUtil.sendMessage(e.getPlayer(), "General.NoWhitelistPermission");
                        e.setCancelled(true);
                        return;
                    }

                    if (!floor.getGroupWhitelist().isEmpty() && !VaultManager.inAnyGroup(p, floor.getGroupWhitelist()) && !p.hasPermission("v10lift.admin")) {
                        ConfigUtil.sendMessage(e.getPlayer(), "General.NoWhitelistPermission");
                        e.setCancelled(true);
                        return;
                    }

                    V10LiftAPI.getInstance().addToQueue(liftName, lift.getFloors().get(f), f);
                }
            }
        }
    }
}
