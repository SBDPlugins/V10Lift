package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LiftSign {
    private String world;
    private int x;
    private int z;
    private int y;
    private String oldText;

    public LiftSign(String world, int x, int y, int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
