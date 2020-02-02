package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter @Setter
public class LiftRope {
    private final Material type;
    private final String world;
    private final int x;
    private final int minY;
    private final int maxY;
    private final int z;
    private int currently;

    public LiftRope(Material type, String world, int x, int minY, int maxY, int z) {
        this.type = type;
        this.world = world;
        this.x = x;
        this.minY = minY;
        this.maxY = maxY;
        this.z = z;
        this.currently = minY;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof LiftRope)) return false;
        LiftRope other = (LiftRope) obj;
        return getWorld().equals(other.getWorld())
            && getX() == other.getX()
            && getMinY() == other.getMinY()
            && getMaxY() == other.getMaxY()
            && getZ() == other.getZ();
    }
}
