package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.UUID;

@Getter @NoArgsConstructor
public class V10Entity {
    private UUID entityUUID;
    private String world;
    private int locX;
    private int locY;
    private int locZ;
    private int y;
    @Setter private short step;

    public V10Entity(UUID entityUUID, String worldName, int x, int y, int z, int cury) {
        this.entityUUID = entityUUID;
        this.world = worldName;
        this.locX = x;
        this.locY = y;
        this.locZ = z;
        this.y = cury;
        this.step = 0;
    }

    public void moveUp() {
        if (entityUUID == null) return;
        Entity entity = Bukkit.getEntity(entityUUID);
        if (entity == null || entity.isDead()) return;
        locY = y + step;
        entity.teleport(new Location(Bukkit.getWorld(world), locX, locY, locZ));
    }

    public void moveDown() {
        if (entityUUID == null) return;
        Entity entity = Bukkit.getEntity(entityUUID);
        if (entity == null || entity.isDead()) return;
        locY = y - step;
        entity.teleport(new Location(Bukkit.getWorld(world), locX, locY, locZ));
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof V10Entity) {
            return ((V10Entity) obj).getEntityUUID().equals(getEntityUUID());
        } else if (obj instanceof Entity) {
            return ((Entity) obj).getUniqueId().equals(getEntityUUID());
        } else {
            return false;
        }
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
