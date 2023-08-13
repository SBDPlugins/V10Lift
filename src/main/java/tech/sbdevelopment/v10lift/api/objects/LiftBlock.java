package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A liftblock object, for a block in a lift.
 */
@Getter
@NoArgsConstructor
@ToString
public class LiftBlock implements Comparable<LiftBlock> {
    private String world;
    private int x;
    @Setter
    private int y;
    private int z;

    //Only used for cabine blocks, because those need caching!
    @Setter
    private Material mat;
    @Setter
    private BlockFace face;
    @Setter
    private String bisected;
    @Setter
    private String slabType;
    @Setter
    private String[] signLines;
    @Setter
    private Boolean open;
    //Used for chests
    public Map<String, Object>[] serializedItemStacks;

    /**
     * Create a new liftblock from a block
     *
     * @param block The block
     */
    public LiftBlock(Block block) {
        this.world = block.getWorld().getName();
        this.x = block.getX();
        this.y = block.getY();
        this.z = block.getZ();
        this.mat = block.getType();
        if (block.getBlockData() instanceof org.bukkit.block.data.Directional) {
            this.face = ((org.bukkit.block.data.Directional) block.getBlockData()).getFacing();
        }
        if (block.getBlockData() instanceof org.bukkit.block.data.Bisected) {
            this.bisected = ((org.bukkit.block.data.Bisected) block.getBlockData()).getHalf().name();
        }
        if (block.getBlockData() instanceof org.bukkit.block.data.type.Slab) {
            this.slabType = ((org.bukkit.block.data.type.Slab) block.getBlockData()).getType().name();
        }
        if (block.getState() instanceof org.bukkit.block.Sign) {
            this.signLines = ((org.bukkit.block.Sign) block.getState()).getLines();
        }
        if (block.getBlockData() instanceof org.bukkit.block.data.Openable) {
            this.open = ((org.bukkit.block.data.Openable) block.getBlockData()).isOpen();
        }
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
}
