package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DirectionUtil {

    @Nullable
    public static BlockFace getDirection(@Nonnull Block block) {
        if (block.getBlockData() instanceof Directional) {
            Directional dir = (Directional) block.getBlockData();
            return dir.getFacing();
        }
        return null;
    }

    public static void setDirection(@Nonnull Block block, BlockFace blockFace) {
        if (blockFace != null && block.getBlockData() instanceof Directional) {
            BlockData bd = block.getBlockData();
            Directional dir = (Directional) bd;
            dir.setFacing(blockFace);
            block.setBlockData(bd);
        }
    }

    @Nullable
    public static String getBisected(@Nonnull Block block) {
        if (block.getBlockData() instanceof Bisected) {
            Bisected bis = (Bisected) block.getBlockData();
            return bis.getHalf().toString();
        }
        return null;
    }

    public static void setBisected(@Nonnull Block block, String bisected) {
        if (bisected != null && block.getBlockData() instanceof Bisected) {

            Bisected.Half half;
            try {
                half = Bisected.Half.valueOf(bisected);
            } catch (IllegalArgumentException e) {
                return;
            }

            BlockData bd = block.getBlockData();
            Bisected bis = (Bisected) bd;
            bis.setHalf(half);
            block.setBlockData(bd);
        }
    }
}
