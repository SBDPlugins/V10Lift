package tech.sbdevelopment.v10lift.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStateUtil {
    @Nullable
    public static BlockFace getDirection(@Nonnull Block block) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            org.bukkit.block.data.Directional dir = (org.bukkit.block.data.Directional) block.getBlockData();
            return dir.getFacing();
        }
        return null;
    }

    public static void setDirection(@Nonnull Block block, @Nonnull BlockFace blockFace) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            org.bukkit.block.data.BlockData bd = block.getBlockData();
            org.bukkit.block.data.Directional dir = (org.bukkit.block.data.Directional) bd;
            dir.setFacing(blockFace);
            block.setBlockData(bd);
        }
    }

    @Nullable
    public static String getBisected(@Nonnull Block block) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
            org.bukkit.block.data.Bisected bis = (org.bukkit.block.data.Bisected) block.getBlockData();
            return bis.getHalf().toString();
        }
        return null;
    }

    public static void setBisected(@Nonnull Block block, String bisected) {
        if (bisected != null && block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
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

    @Nullable
    public static String getSlabType(@Nonnull Block block) {
        if (block.getBlockData() instanceof org.bukkit.block.data.type.Slab) {
            org.bukkit.block.data.type.Slab slab = (org.bukkit.block.data.type.Slab) block.getBlockData();
            return slab.getType().toString();
        }
        return null;
    }

    public static void setSlabType(@Nonnull Block block, @Nonnull String slabtype) {
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

    @Nullable
    public static Boolean isOpen(@Nonnull Block block) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Openable) {
            org.bukkit.block.data.Openable trapdoor = (org.bukkit.block.data.Openable) block.getBlockData();
            return trapdoor.isOpen();
        }
        return null;
    }

    public static void setOpen(@Nonnull Block block, boolean state) {
        if (block.getBlockData() instanceof org.bukkit.block.data.Openable) {
            org.bukkit.block.data.Openable trapdoor = (org.bukkit.block.data.Openable) block.getBlockData();
            trapdoor.setOpen(state);
        }
    }
}
