package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
public class LiftSign {
    private LiftBlock block;
    private String oldText;

    public LiftSign(Location loc) {
        block = new LiftBlock(loc);
    }
}
