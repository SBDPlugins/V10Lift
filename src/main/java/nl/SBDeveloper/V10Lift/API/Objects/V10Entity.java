package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Getter
public class V10Entity {
    private final Entity e;
    private final Location loc;
    private final int y;
    @Setter private short step;

    public V10Entity(Entity e, Location loc, int y) {
        this.e = e;
        this.loc = loc;
        this.y = y;
        this.step = 0;
    }

    public void moveUp() {
        if (e == null || e.isDead()) return;
        loc.setY(y + step);
        e.teleport(loc);
    }

    public void moveDown() {
        if (e == null || e.isDead()) return;
        loc.setY(y - step);
        e.teleport(loc);
    }
}
