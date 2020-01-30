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

    public Floor(String world, int y) {
        this.world = world;
        this.y = y;
        this.doorBlocks = new ArrayList<>();
        this.whitelist = new ArrayList<>();
    }
}
