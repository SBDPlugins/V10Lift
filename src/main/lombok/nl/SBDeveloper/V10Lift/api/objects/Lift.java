package nl.SBDeveloper.V10Lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.SBDeveloper.V10Lift.api.runnables.DoorCloser;

import java.util.*;

/** A lift object, to create a lift. */
@NoArgsConstructor
public class Lift {
    @Getter @Setter private String worldName;
    @Getter @Setter private int y;
    @Getter private HashSet<UUID> owners;
    //@Getter @Setter private ArrayList<String> whitelist;
    @Getter private final TreeSet<LiftBlock> blocks = new TreeSet<>();
    @Getter private final LinkedHashMap<String, Floor> floors = new LinkedHashMap<>();
    @Getter private final HashSet<LiftSign> signs = new HashSet<>();
    @Getter private final HashSet<LiftBlock> inputs = new HashSet<>();
    @Getter private HashSet<LiftBlock> offlineInputs = new HashSet<>();
    @Getter @Setter private LinkedHashMap<String, Floor> queue = null;
    @Getter private final HashSet<LiftRope> ropes = new HashSet<>();
    @Getter private transient final ArrayList<V10Entity> toMove = new ArrayList<>();
    @Getter @Setter private int speed;
    @Getter @Setter private boolean realistic;
    @Getter @Setter private boolean offline = false;
    @Getter @Setter private boolean sound = true;
    @Getter @Setter private boolean defective = false;
    @Getter @Setter private String signText = null;
    @Getter @Setter private int counter = 0;
    @Getter @Setter private Floor doorOpen = null;
    @Getter @Setter private DoorCloser doorCloser = null;

    /**
     * Construct a new Lift with multiple owners
     *
     * @param owners The owners, by uuid
     * @param speed The speed, 1 is slowest, higher is faster
     * @param realistic Realistic lift, or not
     */
    public Lift(HashSet<UUID> owners, int speed, boolean realistic) {
        this.owners = owners;
        this.speed = speed;
        this.realistic = realistic;
    }

    /**
     * Construct a new Lift with one owners
     *
     * @param owner The owner, by uuid
     * @param speed The speed, 1 is slowest, higher is faster
     * @param realistic Realistic lift, or not
     */
    public Lift(UUID owner, int speed, boolean realistic) {
        HashSet<UUID> hs = new HashSet<>();
        hs.add(owner);
        this.owners = hs;
        this.speed = speed;
        this.realistic = realistic;
    }

    @Override
    public String toString() {
        return "Lift{" +
                "worldName='" + worldName + '\'' +
                ", y=" + y +
                ", owners=" + owners +
                ", blocks=" + blocks +
                ", floors=" + floors +
                ", signs=" + signs +
                ", inputs=" + inputs +
                ", offlineInputs=" + offlineInputs +
                ", queue=" + queue +
                ", ropes=" + ropes +
                ", toMove=" + toMove +
                ", speed=" + speed +
                ", realistic=" + realistic +
                ", offline=" + offline +
                ", sound=" + sound +
                ", defective=" + defective +
                ", signText='" + signText + '\'' +
                ", counter=" + counter +
                ", doorOpen=" + doorOpen +
                ", doorCloser=" + doorCloser +
                '}';
    }
}
