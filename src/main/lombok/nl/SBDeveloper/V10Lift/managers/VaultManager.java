package nl.SBDeveloper.V10Lift.managers;

import net.milkbowl.vault.permission.Permission;
import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class VaultManager {
    private static Permission perms = null;

    public static boolean setupPermissions() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = V10LiftPlugin.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return true;
    }

    public static List<String> getGroups() {
        return Arrays.asList(perms.getGroups());
    }

    public static boolean isGroup(String groupName) {
        return Arrays.asList(perms.getGroups()).contains(groupName);
    }

    public static boolean userHasAnyGroup(Player player, HashSet<String> whitelistSet) {
        boolean found = false;
        for (String group : whitelistSet) {
            found = Arrays.asList(perms.getPlayerGroups(player)).contains(group);
        }
        return found;
    }
}
