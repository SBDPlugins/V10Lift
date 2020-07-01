package nl.SBDeveloper.V10Lift.managers;

import net.milkbowl.vault.permission.Permission;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class VaultManager {
    private static Permission perms = null;

    public static boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = V10LiftPlugin.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @Nonnull
    public static List<String> getGroups() {
        return Arrays.asList(perms.getGroups());
    }

    public static boolean isGroup(String groupName) {
        return Arrays.asList(perms.getGroups()).contains(groupName);
    }

    public static boolean userHasAnyGroup(Player player, @Nonnull HashSet<String> whitelistSet) {
        boolean found = false;
        for (String group : whitelistSet) {
            found = Arrays.asList(perms.getPlayerGroups(player)).contains(group);
        }
        return found;
    }
}
