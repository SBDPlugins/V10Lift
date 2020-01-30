package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;

@Getter @Setter
public class LiftBlock implements Comparable<LiftBlock> {

    private String world;
    private int x;
    private int y;
    private int z;

    //Only used for cabine blocks, because those need caching!
    private Material mat;
    private String[] signLines;

    //Only used for inputs!
    private String floor;
    private boolean active = false;

    //Only used for chests
    private LinkedHashMap<Integer, ItemStack> chestContent = new LinkedHashMap<>();

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
}
