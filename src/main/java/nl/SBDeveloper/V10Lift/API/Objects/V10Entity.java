package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter
public class V10Entity {
    private final Entity entity;
    private final Location loc;
    private final int y;
    @Setter private short step;

    public V10Entity(Entity entity, Location loc, int y) {
        this.entity = entity;
        this.loc = loc;
        this.y = y;
        this.step = 0;
    }

    public void moveUp() {
        if (entity == null || entity.isDead()) return;
        loc.setY(y + step);
        entity.teleport(loc);
    }

    public void moveDown() {
        if (entity == null || entity.isDead()) return;
        loc.setY(y - step);
        entity.teleport(loc);
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        UUID uuid;
        if (obj instanceof V10Entity) {
            Entity ent = ((V10Entity) obj).getEntity();
            if (ent == null || ent.isDead()) {
                return getEntity() == null || getEntity().isDead();
            }
            uuid = ent.getUniqueId();
        } else if (obj instanceof Entity) {
            Entity ent = (Entity) obj;
            if (ent.isDead()) {
                return getEntity() == null || getEntity().isDead();
            }
            uuid = ent.getUniqueId();
        } else {
            return false;
        }

        if (getEntity() == null || getEntity().isDead()) return false;
        return uuid == getEntity().getUniqueId();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        result.append(this.getClass().getName());
        result.append(" Object {");
        result.append(newLine);

        //determine fields declared in this class only (no fields of superclass)
        Field[] fields = this.getClass().getDeclaredFields();

        //print field names paired with their values
        for (Field field: fields) {
            result.append("  ");
            try {
                result.append(field.getName());
                result.append(": ");
                //requires access to private field:
                result.append(field.get(this));
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            }
            result.append(newLine);
        }
        result.append("}");

        return result.toString();
    }
}
