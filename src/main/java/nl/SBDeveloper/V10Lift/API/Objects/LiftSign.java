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
    private final byte type;
    private byte state;

    public LiftSign(String world, int x, int y, int z, byte type, byte state) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.state = state;
    }
}
