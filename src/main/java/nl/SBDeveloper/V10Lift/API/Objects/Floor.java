package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class Floor {
    private String world;
    private int y;
    private ArrayList<LiftBlock> doorBlocks;
    private ArrayList<UUID> whitelist;
}
