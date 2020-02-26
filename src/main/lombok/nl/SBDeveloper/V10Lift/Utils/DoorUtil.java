package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class DoorUtil {

    /* Gate codes sponsored by MrWouter <3 */
    @SuppressWarnings("deprecation")
    public static boolean openDoor(Block b) {
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
    @SuppressWarnings("deprecation")
    public static boolean closeDoor(Block b) {
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

}