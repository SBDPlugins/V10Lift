package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter @Setter
public class LiftSign {
    private String world;
    private int x;
    private int z;
    private int y;
    private String oldText = null;
    private final byte type;
    private byte state;

    public LiftSign(String world, int x, int y, int z, byte type, byte state) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.state = state;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LiftSign)) {
            if (!(obj instanceof LiftBlock)) return false;
            LiftBlock other = (LiftBlock) obj;
            return getWorld().equals(other.getWorld()) && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
        }
        LiftSign other = (LiftSign) obj;
        return getWorld().equals(other.getWorld()) && getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
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
