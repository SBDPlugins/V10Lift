package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LiftRope {
    private String world;
    private int x;
    private int z;
    private int minY;
    private int maxY;

    public LiftRope(String world, int x, int minY, int maxY, int z) {
        this.world = world;
        this.x = x;
        this.minY = minY;
        this.maxY = maxY;
        this.z = z;
    }
}
