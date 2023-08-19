package tech.sbdevelopment.v10lift.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import tech.sbdevelopment.v10lift.api.V10LiftAPI;
import tech.sbdevelopment.v10lift.managers.DataManager;

public class WorldEditUtil {
    public static Integer getBlocksInSelection(Player p) {
        LocalSession ls = WorldEdit.getInstance().getSessionManager().get(BukkitAdapter.adapt(p));
        Region region;
        try {
            region = ls.getSelection(BukkitAdapter.adapt(p.getWorld()));
        } catch (IncompleteRegionException e) {
            return -1;
        }
        if (region == null) {
            return -1;
        }

        int failed = 0;
        if (region instanceof Polygonal2DRegion) {
            //Get all blocks in region
            Polygonal2DRegion poly = (Polygonal2DRegion) region;
            for (BlockVector2 bv : poly.getPoints()) {
                for (int y = poly.getMinimumPoint().getBlockY(); y <= poly.getMaximumPoint().getBlockY(); y++) {
                    Block b = p.getWorld().getBlockAt(bv.getBlockX(), y, bv.getBlockZ());
                    if (b.getType() == Material.AIR) continue;

                    if (V10LiftAPI.getInstance().switchBlockAtLift(DataManager.getEditPlayer(p.getUniqueId()), b) != 0) {
                        failed++;
                    }
                }
            }

            return failed;
        } else if (region instanceof CuboidRegion) {
            //Get all blocks in region
            CuboidRegion cuboid = (CuboidRegion) region;
            for (int x = cuboid.getMinimumPoint().getBlockX(); x <= cuboid.getMaximumPoint().getBlockX(); x++) {
                for (int y = cuboid.getMinimumPoint().getBlockY(); y <= cuboid.getMaximumPoint().getBlockY(); y++) {
                    for (int z = cuboid.getMinimumPoint().getBlockZ(); z <= cuboid.getMaximumPoint().getBlockZ(); z++) {
                        Block b = p.getWorld().getBlockAt(x, y, z);
                        if (b.getType() == Material.AIR) continue;

                        if (V10LiftAPI.getInstance().switchBlockAtLift(DataManager.getEditPlayer(p.getUniqueId()), b) != 0) {
                            failed++;
                        }
                    }
                }
            }
            return failed;
        } else {
            return -2;
        }
    }
}
