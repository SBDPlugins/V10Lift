package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Objects;

/**
 * A liftrope object, for a rope in the lift.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LiftRope {
    private Material type;
    //If it's Directional
    private BlockFace face;
    //If it's Openable
    private boolean open;
    private String world;
    private int x;
    private int minY;
    private int maxY;
    private int z;
    private int currently;

    /**
     * Construct a new liftrope
     *
     * @param block The block
     * @param minY  The starting y-pos
     * @param maxY  The stopping y-pos
     */
    public LiftRope(Block block, int minY, int maxY) {
        this.type = block.getType();
        this.world = block.getWorld().getName();
        this.x = block.getX();
        this.minY = minY;
        this.maxY = maxY;
        this.z = block.getZ();
        this.currently = minY;
        if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            this.face = ((org.bukkit.block.data.Directional) block.getBlockData()).getFacing();
        }
        if (block.getBlockData() instanceof org.bukkit.block.data.Openable) {
            this.open = ((org.bukkit.block.data.Openable) block.getBlockData()).isOpen();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiftRope liftRope = (LiftRope) o;
        return x == liftRope.x &&
                minY == liftRope.minY &&
                maxY == liftRope.maxY &&
                z == liftRope.z &&
                Objects.equals(world, liftRope.world);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + world.hashCode();
        result = prime * result + x;
        result = prime * result + minY;
        result = prime * result + maxY;
        result = prime * result + z;
        return result;
    }
}
