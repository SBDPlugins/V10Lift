package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import nl.SBDeveloper.V10Lift.API.Runnables.DoorCloser;

import java.util.*;

public class Lift {
    @Getter @Setter private String worldName;
    @Getter @Setter private int y;
    @Getter private ArrayList<UUID> owners;
    @Getter private ArrayList<String> whitelist;
    @Getter private TreeSet<LiftBlock> blocks;
    @Getter private LinkedHashMap<String, Floor> floors;
    @Getter private ArrayList<LiftSign> signs;
    @Getter private ArrayList<LiftBlock> inputs;
    @Getter private ArrayList<LiftBlock> offlineInputs;
    @Getter @Setter private LinkedHashMap<String, Floor> queue;
    @Getter private ArrayList<LiftRope> ropes;
    @Getter private ArrayList<V10Entity> toMove;
    @Getter @Setter private int speed;
    @Getter @Setter private boolean realistic;
    @Getter @Setter private boolean offline;
    @Getter @Setter private boolean sound;
    @Getter @Setter private boolean defective;
    @Getter @Setter private String signText;
    @Getter @Setter private int counter;
    @Getter @Setter private Floor doorOpen;
    @Getter @Setter private DoorCloser doorCloser;

    public Lift(ArrayList<UUID> owners, int speed, boolean realistic) {
        this.owners = owners;
        this.speed = speed;
        this.realistic = realistic;
        this.blocks = new TreeSet<>();
        this.signs = new ArrayList<>();
        this.whitelist = new ArrayList<>();
        this.floors = new LinkedHashMap<>();
        this.inputs = new ArrayList<>();
        this.offlineInputs = new ArrayList<>();
        this.queue = new LinkedHashMap<>();
        this.ropes = new ArrayList<>();
        this.toMove = new ArrayList<>();
        this.offline = false;
        this.sound = true;
        this.defective = false;
        this.counter = 0;
    }

    public Lift(UUID owner, int speed, boolean realistic) {
        new Lift(new ArrayList<>(Collections.singletonList(owner)), speed, realistic);
    }
}
