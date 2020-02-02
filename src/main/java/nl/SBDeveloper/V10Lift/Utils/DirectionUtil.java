package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DirectionUtil {

    @Nullable
    public static BlockFace getDirection(@Nonnull Block block) {
        if (block.getBlockData() instanceof Directional && block.getType().toString().contains("WALL_SIGN")) {
            Directional dir = (Directional) block.getBlockData();
            return dir.getFacing();
        }
        return null;
    }

    public static void setDirection(@Nonnull Block block, BlockFace blockFace) {
        if (block.getBlockData() instanceof Directional && block.getType().toString().contains("WALL_SIGN")) {
            BlockData bd = block.getBlockData();
            Directional dir = (Directional) bd;
            dir.setFacing(blockFace);
            block.setBlockData(bd);
        }
    }
}
