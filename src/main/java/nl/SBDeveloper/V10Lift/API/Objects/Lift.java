package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import nl.SBDeveloper.V10Lift.API.Runnables.DoorCloser;

import java.util.*;

public class Lift {
    @Getter @Setter private String worldName;
    @Getter @Setter private int y;
    @Getter private final HashSet<UUID> owners;
    @Getter @Setter private ArrayList<String> whitelist;
    @Getter private final TreeSet<LiftBlock> blocks = new TreeSet<>();
    @Getter private final LinkedHashMap<String, Floor> floors = new LinkedHashMap<>();
    @Getter private final HashSet<LiftSign> signs = new HashSet<>();
    @Getter private final HashSet<LiftBlock> inputs = new HashSet<>();
    @Getter private HashSet<LiftBlock> offlineInputs = new HashSet<>();
    @Getter @Setter private LinkedHashMap<String, Floor> queue = null;
    @Getter private final HashSet<LiftRope> ropes = new HashSet<>();
    @Getter private final ArrayList<V10Entity> toMove = new ArrayList<>();
    @Getter @Setter private int speed;
    @Getter @Setter private boolean realistic;
    @Getter @Setter private boolean offline = false;
    @Getter @Setter private boolean sound = true;
    @Getter @Setter private boolean defective = false;
    @Getter @Setter private String signText = null;
    @Getter @Setter private int counter = 0;
    @Getter @Setter private Floor doorOpen = null;
    @Getter @Setter private DoorCloser doorCloser = null;

    public Lift(HashSet<UUID> owners, int speed, boolean realistic) {
        this.owners = owners;
        this.speed = speed;
        this.realistic = realistic;
    }

    public Lift(UUID owner, int speed, boolean realistic) {
        HashSet<UUID> hs = new HashSet<>();
        hs.add(owner);
        this.owners = hs;
        this.speed = speed;
        this.realistic = realistic;
    }
}
