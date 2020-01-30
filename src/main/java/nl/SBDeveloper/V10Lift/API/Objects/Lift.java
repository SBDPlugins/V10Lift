package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.UUID;

public class Lift {
    @Getter @Setter private String worldName;
    @Getter @Setter private int y;
    @Getter private ArrayList<UUID> owners;
    @Getter private ArrayList<String> whitelist;
    @Getter private ArrayList<LiftBlock> blocks;
    @Getter private LinkedHashMap<String, Floor> floors;
    @Getter private ArrayList<LiftSign> signs;
    @Getter private ArrayList<LiftBlock> inputs;
    @Getter private ArrayList<LiftBlock> offlineInputs;
    @Getter private LinkedHashMap<String, Floor> queue;
    @Getter private ArrayList<LiftRope> ropes;
    @Getter @Setter private int speed;
    @Getter @Setter private boolean realistic;
    @Getter @Setter private boolean offline;
    @Getter @Setter private boolean sound;
    @Getter @Setter private boolean defective;
    @Getter @Setter private String signText;

    public Lift(ArrayList<UUID> owners, int speed, boolean realistic) {
        this.owners = owners;
        this.speed = speed;
        this.realistic = realistic;
        this.blocks = new ArrayList<>();
        this.signs = new ArrayList<>();
        this.whitelist = new ArrayList<>();
        this.floors = new LinkedHashMap<>();
        this.inputs = new ArrayList<>();
        this.offlineInputs = new ArrayList<>();
        this.queue = new LinkedHashMap<>();
        this.ropes = new ArrayList<>();
        this.offline = false;
        this.sound = true;
        this.defective = false;
    }

    public Lift(UUID owner, int speed, boolean realistic) {
        new Lift(new ArrayList<>(Collections.singletonList(owner)), speed, realistic);
    }
}
