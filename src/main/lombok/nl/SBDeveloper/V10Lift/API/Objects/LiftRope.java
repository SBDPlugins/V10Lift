package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
public class LiftRope {
    private Material type;
    private BlockFace face;
    private String world;
    private int x;
    private int minY;
    private int maxY;
    private int z;
    private int currently;

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
                currently == liftRope.currently &&
                type == liftRope.type &&
                face == liftRope.face &&
                Objects.equals(world, liftRope.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, face, world, x, minY, maxY, z, currently);
    }

    @Override
    public String toString() {
        return "LiftRope{" +
                "type=" + type +
                ", face=" + face +
                ", world='" + world + '\'' +
                ", x=" + x +
                ", minY=" + minY +
                ", maxY=" + maxY +
                ", z=" + z +
                ", currently=" + currently +
                '}';
    }
}
