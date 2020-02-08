package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        UUID uuid;
        if (o instanceof V10Entity) {
            Entity ent = Bukkit.getEntity(((V10Entity) o).getEntityUUID());
            if (ent == null || ent.isDead()) {
                Entity e = Bukkit.getEntity(entityUUID);
                return e == null || e.isDead();
            }
            uuid = ent.getUniqueId();
        } else if (o instanceof Entity) {
            Entity ent = (Entity) o;
            if (ent.isDead()) {
                Entity e = Bukkit.getEntity(entityUUID);
                return e == null || e.isDead();
            }
            uuid = ((Entity) o).getUniqueId();
        } else
            return false;
        Entity e = Bukkit.getEntity(entityUUID);
        if (e == null || e.isDead())
            return false;
        return uuid == e.getUniqueId();
    }

    @Override
    public int hashCode() {
        return 31 + ((entityUUID == null) ? 0 : entityUUID.hashCode());
    }

    @Override
    public String toString() {
        return "V10Entity{" +
                "entityUUID=" + entityUUID +
                ", world='" + world + '\'' +
                ", locX=" + locX +
                ", locY=" + locY +
                ", locZ=" + locZ +
                ", y=" + y +
                ", step=" + step +
                '}';
    }
}
