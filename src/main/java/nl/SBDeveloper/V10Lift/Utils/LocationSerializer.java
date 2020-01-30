package nl.SBDeveloper.V10Lift.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LocationSerializer {

    //Hieronder de methodes zonder yaw & pitch!
    @Nonnull
    public static Location deserialize(@Nonnull String string) {
        String[] split = string.split("_");

        //world_x_y_z
        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );
    }

    @Nullable
    public static String serialize(@Nonnull Location loc) {
        if (loc.getWorld() == null) return null;
        return loc.getWorld().getName() + "_" + loc.getX() + "_" + loc.getY() + "_" + loc.getZ();
    }

}