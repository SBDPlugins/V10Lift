package tech.sbdevelopment.v10lift.utils;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;

import javax.annotation.Nonnull;

/* Openable codes sponsored by MrWouter <3 */
public class DoorUtil {
    /**
     * Open a door, with 1.12.x- and 1.13.x+ support
     *
     * @param b The block (door)
     * @return true if opened, false if not opened
     */
    public static boolean openDoor(@Nonnull Block b) {
        if (b.getType() == XMaterial.IRON_DOOR.parseMaterial()) XSound.BLOCK_IRON_DOOR_OPEN.play(b.getLocation());
        if (b.getType().toString().contains("DOOR") && b.getType() != XMaterial.IRON_DOOR.parseMaterial())
            XSound.BLOCK_WOODEN_DOOR_OPEN.play(b.getLocation());
        if (b.getType().toString().contains("GATE")) XSound.BLOCK_FENCE_GATE_OPEN.play(b.getLocation());

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
        return false;
    }

    /**
     * Close a door, with 1.12.x- and 1.13.x+ support
     *
     * @param b The block (door)
     * @return true if opened, false if not opened
     */
    public static boolean closeDoor(@Nonnull Block b) {
        if (b.getType() == XMaterial.IRON_DOOR.parseMaterial()) XSound.BLOCK_IRON_DOOR_CLOSE.play(b.getLocation());
        if (b.getType().toString().contains("DOOR") && b.getType() != XMaterial.IRON_DOOR.parseMaterial())
            XSound.BLOCK_WOODEN_DOOR_CLOSE.play(b.getLocation());
        if (b.getType().toString().contains("GATE")) XSound.BLOCK_FENCE_GATE_CLOSE.play(b.getLocation());

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
        return false;
    }

    /**
     * Check if a block instanceof Openable
     *
     * @param b The block
     * @return true if Openable, false if not
     */
    public static boolean isOpenable(@Nonnull Block b) {
        return b.getBlockData() instanceof org.bukkit.block.data.Openable;
    }

    /**
     * Get the lower location of a door
     *
     * @param block The location of a door
     * @return The lower location of a door
     */
    public static Location getLowerLocationOfDoor(@Nonnull Block block) {
        if (!isDoor(block)) return block.getLocation();

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
    }

    /**
     * Check if a block instanceof Door
     *
     * @param b The block
     * @return true if a Door, false if not
     */
    public static boolean isDoor(@Nonnull Block b) {
        return b.getBlockData() instanceof org.bukkit.block.data.type.Door;
    }
}