package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor
public class Floor {
    private String world;
    private int y;
    private ArrayList<LiftBlock> doorBlocks = new ArrayList<>();
    private HashSet<UUID> userWhitelist = new HashSet<>();
    private HashSet<String> groupWhitelist = new HashSet<>();

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

    @Override
    public String toString() {
        return "Floor{" +
                "world='" + world + '\'' +
                ", y=" + y +
                ", doorBlocks=" + doorBlocks +
                ", userWhitelist=" + userWhitelist +
                ", groupWhitelist=" + groupWhitelist +
                '}';
    }
}
