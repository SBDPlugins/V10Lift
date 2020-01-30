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
}
