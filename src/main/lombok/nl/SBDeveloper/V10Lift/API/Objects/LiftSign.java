package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor
public class LiftSign {
    private String world;
    private int x;
    private int z;
    private int y;
    private String oldText = null;
    private byte type;
    private byte state;

    public LiftSign(String world, int x, int y, int z, byte type, byte state) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiftSign liftSign = (LiftSign) o;
        return x == liftSign.x &&
                z == liftSign.z &&
                y == liftSign.y &&
                type == liftSign.type &&
                state == liftSign.state &&
                Objects.equals(world, liftSign.world) &&
                Objects.equals(oldText, liftSign.oldText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, x, z, y, oldText, type, state);
    }

    @Override
    public String toString() {
        return "LiftSign{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", z=" + z +
                ", y=" + y +
                ", oldText='" + oldText + '\'' +
                ", type=" + type +
                ", state=" + state +
                '}';
    }
}
