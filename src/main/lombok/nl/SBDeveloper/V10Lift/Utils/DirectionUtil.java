package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DirectionUtil {

    @Nullable
    public static BlockFace getDirection(@Nonnull Block block) {
        if (!XMaterial.isNewVersion()) return null;
        if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            org.bukkit.block.data.Directional dir = (org.bukkit.block.data.Directional) block.getBlockData();
            return dir.getFacing();
        }
        return null;
    }

    public static void setDirection(@Nonnull Block block, BlockFace blockFace) {
        if (!XMaterial.isNewVersion()) return;
        if (blockFace != null && block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.Directional dir = (org.bukkit.block.data.Directional) bd;
            dir.setFacing(blockFace);
            block.setBlockData(bd);
        }
    }

    @Nullable
    public static String getBisected(@Nonnull Block block) {
        if (!XMaterial.isNewVersion()) return null;
        if (block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
            org.bukkit.block.data.Bisected bis = (org.bukkit.block.data.Bisected) block.getBlockData();
            return bis.getHalf().toString();
        }
        return null;
    }

    public static void setBisected(@Nonnull Block block, String bisected) {
        if (!XMaterial.isNewVersion()) return;
        if (bisected != null && block.getBlockData() instanceof org.bukkit.block.data.Bisected) {

            org.bukkit.block.data.Bisected.Half half;
            try {
                half = org.bukkit.block.data.Bisected.Half.valueOf(bisected);
            } catch (IllegalArgumentException e) {
                return;
            }

            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.Bisected bis = (org.bukkit.block.data.Bisected) bd;
            bis.setHalf(half);
            block.setBlockData(bd);
        }
    }
}
