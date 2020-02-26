package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected;

import javax.annotation.Nonnull;

public class DoorUtil {

    /* Gate codes sponsored by MrWouter <3 */
    public static boolean openDoor(@Nonnull Block b) {
        if (b.getType() == XMaterial.IRON_DOOR.parseMaterial()) XSound.BLOCK_IRON_DOOR_OPEN.playSound(b.getLocation());
        if (b.getType().toString().contains("DOOR") && b.getType() != XMaterial.IRON_DOOR.parseMaterial()) XSound.BLOCK_WOODEN_DOOR_OPEN.playSound(b.getLocation());
        if (b.getType().toString().contains("GATE")) XSound.BLOCK_FENCE_GATE_OPEN.playSound(b.getLocation());
        if (XMaterial.isNewVersion()) {
            org.bukkit.block.data.BlockData blockData = b.getBlockData();
            if (isOpenable(b)) {
                org.bukkit.block.data.Openable op = (org.bukkit.block.data.Openable) blockData;
                if (op.isOpen()) {
                    return false;
                }
                op.setOpen(true);
                b.setBlockData(blockData);
                return true;
            }
        } else {
            BlockState state = b.getState();
            if (isOpenable(b)) {
                org.bukkit.material.Openable openable = (org.bukkit.material.Openable) state.getData();
                if (openable.isOpen()) {
                    return false;
                }
                openable.setOpen(true);
                state.setData((org.bukkit.material.MaterialData) openable);
                state.update();
                return true;
            }
        }
        return false;
    }

    /* Gate codes sponsored by MrWouter <3 */
    public static boolean closeDoor(@Nonnull Block b) {
        if (b.getType() == XMaterial.IRON_DOOR.parseMaterial()) XSound.BLOCK_IRON_DOOR_CLOSE.playSound(b.getLocation());
        if (b.getType().toString().contains("DOOR") && b.getType() != XMaterial.IRON_DOOR.parseMaterial()) XSound.BLOCK_WOODEN_DOOR_CLOSE.playSound(b.getLocation());
        if (b.getType().toString().contains("GATE")) XSound.BLOCK_FENCE_GATE_CLOSE.playSound(b.getLocation());
        if (XMaterial.isNewVersion()) {
            org.bukkit.block.data.BlockData blockData = b.getBlockData();
            if (isOpenable(b)) {
                org.bukkit.block.data.Openable op = (org.bukkit.block.data.Openable) blockData;
                if (!op.isOpen()) {
                    return false;
                }
                op.setOpen(false);
                b.setBlockData(blockData);
                return true;
            }
        } else {
            BlockState state = b.getState();
            if (isOpenable(b)) {
                org.bukkit.material.Openable openable = (org.bukkit.material.Openable) state.getData();
                if (!openable.isOpen()) {
                    return false;
                }
                openable.setOpen(false);
                state.setData((org.bukkit.material.MaterialData) openable);
                state.update();
                return true;
            }
        }
        return false;
    }

    /* Gate codes sponsored by MrWouter <3 */
    public static boolean isOpenable(Block b) {
        if (b == null) {
            return false;
        }
        if (XMaterial.isNewVersion()) {
            return b.getBlockData() instanceof org.bukkit.block.data.Openable;
        } else {
            return b.getState().getData() instanceof org.bukkit.material.Openable;
        }
    }

    public static Location getLowerLocationOfDoor(@Nonnull Block block) {
        if (!isDoor(block)) return block.getLocation();

        if (XMaterial.isNewVersion()) {
            org.bukkit.block.data.type.Door door = (org.bukkit.block.data.type.Door) block.getBlockData();
            Location lower;
            if (door.getHalf() == Bisected.Half.TOP) {
                lower = block.getLocation().subtract(0, 1, 0);
            } else {
                if (!door.isOpen()) {
                    lower = block.getLocation().subtract(0, 1, 0);
                    if (isOpenable(lower.getBlock()))
                        return lower;
                    else return block.getLocation();
                }
                lower = block.getLocation();
            }
            return lower;
        } else {
            org.bukkit.material.Door door = (org.bukkit.material.Door) block.getState().getData();
            Location lower;
            if (door.isTopHalf()) {
                lower = block.getLocation().subtract(0, 1, 0);
            } else {
                if (!door.isOpen()) {
                    lower = block.getLocation().subtract(0, 1, 0);
                    if (isOpenable(lower.getBlock()))
                        return lower;
                    else return block.getLocation();
                }
                lower = block.getLocation();
            }
            return lower;
        }
    }

    public static boolean isDoor(Block b) {
        if (b == null) {
            return false;
        }
        if (XMaterial.isNewVersion()) {
            return b.getBlockData() instanceof org.bukkit.block.data.type.Door;
        } else {
            return b.getState().getData() instanceof org.bukkit.material.Door;
        }
    }

}