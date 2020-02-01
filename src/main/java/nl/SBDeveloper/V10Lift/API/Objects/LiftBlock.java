package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Map;

public class LiftBlock implements Comparable<LiftBlock> {

    @Getter @Setter private String world;
    @Getter @Setter private int x;
    @Getter @Setter private int y;
    @Getter @Setter private int z;

    //Only used for cabine blocks, because those need caching!
    @Getter @Setter private Material mat;
    @Getter @Setter private String[] signLines;

    //Only used for inputs!
    @Getter @Setter private String floor;
    @Getter @Setter private boolean active = false;

    //Only used for chests
    public Map<String, Object>[] serializedItemStacks = null;

    /**
     * Add lift block with material
     *
     * @param world Worldname
     * @param x x-pos
     * @param y y-pos
     * @param z z-pos
     * @param mat the material
     */
    public LiftBlock(String world, int x, int y, int z, Material mat) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
    }

    public LiftBlock(String world, int x, int y, int z, String floor) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.floor = floor;
    }

    public LiftBlock(String world, int x, int y, int z, Material mat, String[] signLines) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.signLines = signLines;
    }

    @Override
    public int compareTo(@Nonnull LiftBlock lb) {
        int ret = Integer.compare(y, lb.y);
        if (ret == 0) ret = Integer.compare(x, lb.x);
        if (ret == 0) ret = Integer.compare(z, lb.z);

        return ret;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LiftBlock)) {
            if (!(obj instanceof LiftSign)) return false;
            LiftSign other = (LiftSign) obj;
            return getWorld().equals(other.getWorld()) && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
        }
        LiftBlock other = (LiftBlock) obj;
        return getWorld().equals(other.getWorld()) && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
    }
}
