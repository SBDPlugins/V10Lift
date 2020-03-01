package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;

/** A liftblock object, for a block in a lift. */
@NoArgsConstructor
public class LiftBlock implements Comparable<LiftBlock> {

    @Getter @Setter private String world;
    @Getter private int x;
    @Getter @Setter private int y;
    @Getter private int z;

    //Only used for cabine blocks, because those need caching!
    @Getter @Setter private Material mat;
    @Getter private byte data;
    @Getter private BlockFace face;
    @Getter private String bisected;
    @Getter private String slabtype;
    @Getter private String[] signLines;

    //Only used for inputs!
    @Getter private String floor;
    @Getter @Setter private boolean active = false;

    //Only used for chests
    public Map<String, Object>[] serializedItemStacks = null;

    /**
     * A floor based liftblock, without material (no caching)
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param floor The floorname of the block
     */
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
        this.slabtype = null;
    }

    /**
     * 1.12 liftblock, with material and data [NO SIGN]
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     * @param data The data of the block
     */
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
        this.slabtype = null;
    }

    /**
     * 1.12 liftblock (signs)
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     * @param data The data of the block
     * @param signLines The lines of the sign
     */
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
        this.slabtype = null;
    }

    /**
     * 1.13 liftblock, without a direction
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     */
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
        this.slabtype = null;
    }

    /**
     * 1.13 liftblock with a direction
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     * @param face The blockface of the block
     */
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
        this.slabtype = null;
    }

    /**
     * 1.13 liftblock, with a direction and a bisected
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     * @param face The blockface of the block
     * @param bisected The bisected of the block
     */
    public LiftBlock(String world, int x, int y, int z, Material mat, BlockFace face, String bisected) {
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
        this.slabtype = null;
    }

    /**
     * 1/13 liftblock (sign)
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     * @param face The blockface of the block
     * @param signLines The lines of the sign
     */
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
        this.slabtype = null;
    }

    /**
     * 1.13 liftblock (slab)
     *
     * @param world The world
     * @param x The x-pos
     * @param y The y-pos
     * @param z The z-pos
     * @param mat The Material of the block
     * @param slabtype The typ of slab (low, high, double)
     */
    public LiftBlock(String world, int x, int y, int z, Material mat, String slabtype) {
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
        this.slabtype = slabtype;
    }

    @Override
    public int compareTo(@Nonnull LiftBlock lb) {
        int ret = Integer.compare(y, lb.y);
        if (ret == 0) ret = Integer.compare(x, lb.x);
        if (ret == 0) ret = Integer.compare(z, lb.z);

        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LiftBlock)) {
            if (!(o instanceof LiftSign)) return false;
            LiftSign other = (LiftSign) o;
            return world.equals(other.getWorld()) &&
                    x == other.getX() &&
                    y == other.getY() &&
                    z == other.getZ();
        }
        LiftBlock other = (LiftBlock) o;
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
        return "LiftBlock{" +
                "world='" + world + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", mat=" + mat +
                ", data=" + data +
                ", face=" + face +
                ", bisected='" + bisected + '\'' +
                ", signLines=" + Arrays.toString(signLines) +
                ", floor='" + floor + '\'' +
                ", active=" + active +
                ", serializedItemStacks=" + Arrays.toString(serializedItemStacks) +
                '}';
    }
}
