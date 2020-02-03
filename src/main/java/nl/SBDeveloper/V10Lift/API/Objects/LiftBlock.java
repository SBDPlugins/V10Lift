package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Map;

public class LiftBlock implements Comparable<LiftBlock> {

    @Getter @Setter private String world;
    @Getter private final int x;
    @Getter @Setter private int y;
    @Getter private final int z;

    //Only used for cabine blocks, because those need caching!
    @Getter private final Material mat;
    @Getter private final byte data;
    @Getter private final BlockFace face;
    @Getter private final Object bisected;
    @Getter private final String[] signLines;

    //Only used for inputs!
    @Getter private final String floor;
    @Getter @Setter private boolean active = false;

    //Only used for chests
    public Map<String, Object>[] serializedItemStacks = null;

    /* Floor based liftblock, no material */
    public LiftBlock(String world, int x, int y, int z, String floor) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = null;
        this.data = 0;
        this.face = null;
        this.signLines = null;
        this.floor = floor;
        this.bisected = null;
    }

    /** 1.12 liftblocks **/

    /* 1.12 liftblock (Directional) */
    public LiftBlock(String world, int x, int y, int z, Material mat, byte data) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.face = null;
        this.data = data;
        this.signLines = null;
        this.floor = null;
        this.bisected = null;
    }

    /* 1.12 liftblock (sign) */
    public LiftBlock(String world, int x, int y, int z, Material mat, byte data, String[] signLines) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.face = null;
        this.data = data;
        this.signLines = signLines;
        this.floor = null;
        this.bisected = null;
    }

    /** 1.13 liftblocks **/

    /* 1.13 liftblock (no Dir) */
    public LiftBlock(String world, int x, int y, int z, Material mat) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.face = null;
        this.data = 0;
        this.signLines = null;
        this.floor = null;
        this.bisected = null;
    }

    /* 1.13 liftblock (Directional) */
    public LiftBlock(String world, int x, int y, int z, Material mat, BlockFace face) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.face = face;
        this.data = 0;
        this.signLines = null;
        this.floor = null;
        this.bisected = null;
    }

    /* 1.13 liftblock (dir & bisec) */
    public LiftBlock(String world, int x, int y, int z, Material mat, BlockFace face, Object bisected) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.face = face;
        this.data = 0;
        this.signLines = null;
        this.floor = null;
        this.bisected = bisected;
    }

    /* 1.13 liftblock (sign) */
    public LiftBlock(String world, int x, int y, int z, Material mat, BlockFace face, String[] signLines) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.mat = mat;
        this.face = face;
        this.data = 0;
        this.signLines = signLines;
        this.floor = null;
        this.bisected = null;
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

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field: fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
