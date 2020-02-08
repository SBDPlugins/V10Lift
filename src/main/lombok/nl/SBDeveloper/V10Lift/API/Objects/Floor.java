package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class Floor {
    private String world;
    private int y;
    private ArrayList<LiftBlock> doorBlocks = new ArrayList<>();
    private HashSet<UUID> whitelist = new HashSet<>();

    public Floor(int y, String world) {
        this.y = y;
        this.world = world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Floor floor = (Floor) o;
        return y == floor.y &&
                Objects.equals(world, floor.world) &&
                Objects.equals(doorBlocks, floor.doorBlocks) &&
                Objects.equals(whitelist, floor.whitelist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, y, doorBlocks, whitelist);
    }

    @Override
    public String toString() {
        return "Floor{" +
                "world='" + world + '\'' +
                ", y=" + y +
                ", doorBlocks=" + doorBlocks +
                ", whitelist=" + whitelist +
                '}';
    }
}
