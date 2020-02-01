package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter @Setter
public class LiftRope {
    private final Material type;
    private final String startWorld;
    private final String endWorld;
    private final int x;
    private final int minY;
    private final int maxY;
    private final int z;
    private String currentWorld;
    private int currently;

    public LiftRope(Material type, String startWorld, String endWorld, int x, int minY, int maxY, int z) {
        this.type = type;
        this.startWorld = startWorld;
        this.endWorld = endWorld;
        this.x = x;
        this.minY = minY;
        this.maxY = maxY;
        this.z = z;
        this.currently = minY;
        this.currentWorld = endWorld;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof LiftRope)) return false;
        LiftRope other = (LiftRope) obj;
        return getStartWorld().equals(other.getStartWorld())
            && endWorld.equals(other.getEndWorld())
            && getX() == other.getX()
            && getMinY() == other.getMinY()
            && getMaxY() == other.getMaxY()
            && getZ() == other.getZ();
    }
}
