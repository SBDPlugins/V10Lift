package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class LiftRope {
    private String world;
    private int x;
    private int z;
    private int minY;
    private int maxY;
}
