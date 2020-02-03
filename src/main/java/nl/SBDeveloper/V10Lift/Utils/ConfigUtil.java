package nl.SBDeveloper.V10Lift.Utils;

import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.ChatColor;

import javax.annotation.Nonnull;
import java.util.Objects;

public class ConfigUtil {
    @Nonnull
    public static String getColored(@Nonnull String path) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(V10LiftPlugin.getSConfig().getFile().getString(path), "Message " + path + " not found!"));
    }
}
