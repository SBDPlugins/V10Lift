package tech.sbdevelopment.v10lift.sbutils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LocationSerializer {
    /**
     * Deserialize a serialized location, without {@link Location#getYaw()} and {@link Location#getPitch()}
     *
     * @param string The location string
     *
     * @return The location or null if error
     */
    @Nullable
    public static Location deserialize(@Nonnull String string) {
        String[] split = string.split("_");

        if (split.length < 4) return null;

        //world_x_y_z
        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3])
        );
    }

    /**
     * Deserialize a serialized location, with {@link Location#getYaw()} and {@link Location#getPitch()}
     *
     * @param string The location string
     *
     * @return The location or null if error
     */
    @Nonnull
    public static Location deserializePY(@Nonnull String string) {
        String[] split = string.split("_");

        //world_x_y_z
        return new Location(
                Bukkit.getWorld(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Double.parseDouble(split[3]),
                Float.parseFloat(split[4]),
                Float.parseFloat(split[5])
        );
    }

    /**
     * Serialize a location, without {@link Location#getYaw()} and {@link Location#getPitch()}
     *
     * @param loc The location
     *
     * @return The serialized string
     */
    @Nullable
    public static String serialize(@Nonnull Location loc) {
        if (loc.getWorld() == null) return null;
        return loc.getWorld().getName() + "_" + loc.getX() + "_" + loc.getY() + "_" + loc.getZ();
    }

    /**
     * Serialize a location, with {@link Location#getYaw()} and {@link Location#getPitch()}
     *
     * @param loc The location
     *
     * @return The serialized string
     */
    @Nullable
    public static String serializePY(@Nonnull Location loc) {
        if (loc.getWorld() == null) return null;
        return loc.getWorld().getName() + "_" + loc.getX() + "_" + loc.getY() + "_" + loc.getZ() + "_" + loc.getYaw() + "_" + loc.getPitch();
    }
}