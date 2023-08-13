package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
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
    private BlockFace face;
    private String world;
    private int x;
    private int minY;
    private int maxY;
    private int z;
    private int currently;

    /**
     * Construct a new liftrope
     *
     * @param type  The material of the rope
     * @param face  The face of the rope
     * @param world The world
     * @param x     The x-pos
     * @param minY  The starting x-pos
     * @param maxY  The stopping x-pos
     * @param z     The z-pos
     */
    public LiftRope(Material type, BlockFace face, String world, int x, int minY, int maxY, int z) {
        this.type = type;
        this.face = face;
        this.world = world;
        this.x = x;
        this.minY = minY;
        this.maxY = maxY;
        this.z = z;
        this.currently = minY;
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
