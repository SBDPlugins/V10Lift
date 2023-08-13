package tech.sbdevelopment.v10lift.api.objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tech.sbdevelopment.v10lift.api.runnables.DoorCloser;

import java.util.*;

/**
 * A lift object, to create a lift.
 */
@Getter
@NoArgsConstructor
@ToString
public class Lift {
    @Setter
    private String worldName;
    @Setter
    private int y;
    private HashSet<UUID> owners;
    private final TreeSet<LiftBlock> blocks = new TreeSet<>();
    private final LinkedHashMap<String, Floor> floors = new LinkedHashMap<>();
    private final HashSet<LiftSign> signs = new HashSet<>();
    private final HashSet<LiftBlock> inputs = new HashSet<>();
    private final HashSet<LiftBlock> offlineInputs = new HashSet<>();
    @Setter
    private LinkedHashMap<String, Floor> queue = null;
    private final HashSet<LiftRope> ropes = new HashSet<>();
    @Setter
    private int speed;
    @Setter
    private boolean realistic;
    @Setter
    private boolean offline = false;
    @Setter
    private boolean sound = true;
    @Setter
    private boolean defective = false;
    @Setter
    private String signText = null;
    @Setter
    private int counter = 0;
    @Setter
    private Floor doorOpen = null;
    @Setter
    private DoorCloser doorCloser = null;

    /**
     * Construct a new Lift with multiple owners
     *
     * @param owners    The owners, by uuid
     * @param speed     The speed, 1 is slowest, higher is faster
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
     * @param owner     The owner, by uuid
     * @param speed     The speed, 1 is slowest, higher is faster
     * @param realistic Realistic lift, or not
     */
    public Lift(UUID owner, int speed, boolean realistic) {
        HashSet<UUID> hs = new HashSet<>();
        hs.add(owner);
        this.owners = hs;
        this.speed = speed;
        this.realistic = realistic;
    }
}
