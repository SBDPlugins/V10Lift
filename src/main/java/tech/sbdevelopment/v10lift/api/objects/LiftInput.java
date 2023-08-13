package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;

/**
 * A lift input object
 */
@Getter
@NoArgsConstructor
@ToString
public class LiftInput implements Comparable<LiftInput> {
    private String world;
    private int x;
    private int y;
    private int z;

    private String floor;

    /**
     * Create a new lift input
     *
     * @param world The world
     * @param x     The x-pos
     * @param y     The y-pos
     * @param z     The z-pos
     * @param floor The floor
     */
    public LiftInput(String world, int x, int y, int z, String floor) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.floor = floor;
    }

    @Override
    public int compareTo(@Nonnull LiftInput lb) {
        int ret = Integer.compare(y, lb.y);
        if (ret == 0) ret = Integer.compare(x, lb.x);
        if (ret == 0) ret = Integer.compare(z, lb.z);

        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiftInput)) return false;
        LiftInput other = (LiftInput) o;
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
