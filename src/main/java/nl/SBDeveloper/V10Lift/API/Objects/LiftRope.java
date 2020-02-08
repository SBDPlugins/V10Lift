package nl.SBDeveloper.V10Lift.API.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import java.lang.reflect.Field;

@Getter @Setter @NoArgsConstructor
public class LiftRope {
    private Material type;
    private BlockFace face;
    private String world;
    private int x;
    private int minY;
    private int maxY;
    private int z;
    private int currently;

    public LiftRope(Material type, BlockFace face, String world, int x, int minY, int maxY, int z) {
        this.type = type;
        this.face = face;
        this.world = world;
        this.x = x;
        this.minY = minY;
        this.maxY = maxY;
        this.z = z;
        this.currently = minY;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof LiftRope)) return false;
        LiftRope other = (LiftRope) obj;
        return getWorld().equals(other.getWorld())
            && getX() == other.getX()
            && getMinY() == other.getMinY()
            && getMaxY() == other.getMaxY()
            && getZ() == other.getZ();
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
