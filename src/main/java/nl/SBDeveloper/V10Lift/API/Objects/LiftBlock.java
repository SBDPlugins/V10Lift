package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

import javax.annotation.Nonnull;
import java.util.Map;

public class LiftBlock implements Comparable<LiftBlock> {

    @Getter @Setter private String world;
    @Getter private final int x;
    @Getter @Setter private int y;
    @Getter private final int z;

    //Only used for cabine blocks, because those need caching!
    @Getter private final Material mat;
    @Getter private final byte data;
    @Getter private final String[] signLines;

    //Only used for inputs!
    @Getter private final String floor;
    @Getter @Setter private boolean active = false;

    //Only used for chests
    public Map<String, Object>[] serializedItemStacks = null;

    public LiftBlock(String world, int x, int y, int z, String floor) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = null;
        this.data = 0;
        this.signLines = null;
        this.floor = floor;
    }

    public LiftBlock(String world, int x, int y, int z, Material mat) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.data = 0;
        this.signLines = null;
        this.floor = null;
    }

    public LiftBlock(String world, int x, int y, int z, Material mat, byte data) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.data = data;
        this.signLines = null;
        this.floor = null;
    }

    public LiftBlock(String world, int x, int y, int z, Material mat, String[] signLines) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.data = 0;
        this.signLines = signLines;
        this.floor = null;
    }

    public LiftBlock(String world, int x, int y, int z, Material mat, byte data, String[] signLines) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.data = data;
        this.signLines = signLines;
        this.floor = null;
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
