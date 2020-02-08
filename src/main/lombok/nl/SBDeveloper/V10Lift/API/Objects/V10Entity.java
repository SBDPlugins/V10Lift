package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.reflect.Field;
import java.util.Objects;
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
        if (o == null || getClass() != o.getClass()) return false;
        V10Entity v10Entity = (V10Entity) o;
        return locX == v10Entity.locX &&
                locY == v10Entity.locY &&
                locZ == v10Entity.locZ &&
                y == v10Entity.y &&
                step == v10Entity.step &&
                Objects.equals(entityUUID, v10Entity.entityUUID) &&
                Objects.equals(world, v10Entity.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityUUID, world, locX, locY, locZ, y, step);
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
