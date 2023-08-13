package tech.sbdevelopment.v10lift.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;

public class BlockStateUtil {
    public static void setDirection(@Nonnull Block block, BlockFace blockFace) {
        if (blockFace == null) return;
        if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.Directional dir = (org.bukkit.block.data.Directional) bd;
            dir.setFacing(blockFace);
            block.setBlockData(bd);
        }
    }

    public static void setBisected(@Nonnull Block block, String bisected) {
        if (bisected == null) return;
        if (block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
            org.bukkit.block.data.Bisected.Half half;
            try {
                half = org.bukkit.block.data.Bisected.Half.valueOf(bisected);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }

            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.Bisected bis = (org.bukkit.block.data.Bisected) bd;
            bis.setHalf(half);
            block.setBlockData(bd);
        }
    }

    public static void setSlabType(@Nonnull Block block, String slabtype) {
        if (slabtype == null) return;
        if (block.getBlockData() instanceof org.bukkit.block.data.type.Slab) {
            org.bukkit.block.data.type.Slab.Type type;
            try {
                type = org.bukkit.block.data.type.Slab.Type.valueOf(slabtype);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }

            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.type.Slab slab = (org.bukkit.block.data.type.Slab) bd;
            slab.setType(type);
            block.setBlockData(bd);
        }
    }

    public static void setOpen(@Nonnull Block block, Boolean state) {
        if (state == null) return;
        if (block.getBlockData() instanceof org.bukkit.block.data.Openable) {
            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.Openable openable = (org.bukkit.block.data.Openable) bd;
            openable.setOpen(state);
            block.setBlockData(bd);
        }
    }
}
