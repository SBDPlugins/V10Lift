package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A liftsign object, for an info sign for the lift.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LiftSign {
    private String world;
    private int x;
    private int z;
    private int y;
    private String oldText = null;
    private byte type;
    private byte state;

    /**
     * Construct a new liftsign
     *
     * @param world The world
     * @param x     The x-pos
     * @param y     The y-pos
     * @param z     The z-pos
     * @param type  The type of the sign
     * @param state The state of the sign
     */
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
}
