package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter
public class Floor {
    private String world;
    private int y;
    private ArrayList<LiftBlock> doorBlocks;
    private ArrayList<UUID> whitelist;

    public Floor(int y, String world) {
        this.y = y;
        this.world = world;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Floor other = (Floor) obj;
        if (getWorld() == null) {
            if (other.getWorld() != null) return false;
        } else if (!getWorld().equals(other.getWorld())) {
            return false;
        }
        return getY() == other.getY();
    }
}
