package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/** A floor object, for a floor in the lift. */
@Getter @Setter @NoArgsConstructor @ToString
public class Floor {
    private String world;
    private int y;
    private ArrayList<LiftBlock> doorBlocks = new ArrayList<>();
    private ArrayList<LiftBlock> realDoorBlocks = new ArrayList<>();
    private HashSet<UUID> userWhitelist = new HashSet<>();
    private HashSet<String> groupWhitelist = new HashSet<>();

    /**
     * Construct a new Floor
     *
     * @param y The y/height of the floor
     * @param world The world of the floor
     */
    public Floor(int y, String world) {
        this.y = y;
        this.world = world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        if (world == null) {
            if (floor.getWorld() != null) return false;
        } else if (!world.equals(floor.getWorld())) return false;

        return y == floor.getY();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        result = prime * result + y;
        return result;
    }
}
