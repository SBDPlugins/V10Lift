package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.SBDeveloper.V10Lift.API.Runnables.DoorCloser;

import java.util.*;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lift lift = (Lift) o;
        return y == lift.y &&
                speed == lift.speed &&
                realistic == lift.realistic &&
                offline == lift.offline &&
                sound == lift.sound &&
                defective == lift.defective &&
                counter == lift.counter &&
                Objects.equals(worldName, lift.worldName) &&
                Objects.equals(owners, lift.owners) &&
                Objects.equals(blocks, lift.blocks) &&
                Objects.equals(floors, lift.floors) &&
                Objects.equals(signs, lift.signs) &&
                Objects.equals(inputs, lift.inputs) &&
                Objects.equals(offlineInputs, lift.offlineInputs) &&
                Objects.equals(queue, lift.queue) &&
                Objects.equals(ropes, lift.ropes) &&
                Objects.equals(toMove, lift.toMove) &&
                Objects.equals(signText, lift.signText) &&
                Objects.equals(doorOpen, lift.doorOpen) &&
                Objects.equals(doorCloser, lift.doorCloser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(worldName, y, owners, blocks, floors, signs, inputs, offlineInputs, queue, ropes, toMove, speed, realistic, offline, sound, defective, signText, counter, doorOpen, doorCloser);
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
