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
        if (!(o instanceof LiftSign)) {
            if (!(o instanceof LiftBlock))
                return false;
            LiftBlock other = (LiftBlock) o;
            return world.equals(other.getWorld()) &&
                    x == other.getX() &&
                    y == other.getY() &&
                    z == other.getZ();
        }
        LiftSign other = (LiftSign) o;
        return world.equals(other.world) &&
                x == other.x &&
                y == other.y &&
                z == other.z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
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
