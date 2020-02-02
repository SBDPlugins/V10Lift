package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

@Getter @Setter
public class Floor {
    private final String world;
    private final int y;
    private final ArrayList<LiftBlock> doorBlocks = new ArrayList<>();
    private final HashSet<UUID> whitelist = new HashSet<>();

    public Floor(int y, String world) {
        this.y = y;
        this.world = world;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Floor other = (Floor) obj;
        if (getWorld() == null) {
            if (other.getWorld() != null) return false;
        } else if (!getWorld().equals(other.getWorld())) {
            return false;
        }
        return getY() == other.getY();
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
