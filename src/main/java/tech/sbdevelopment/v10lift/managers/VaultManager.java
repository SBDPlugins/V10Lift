package tech.sbdevelopment.v10lift.managers;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import tech.sbdevelopment.v10lift.V10LiftPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class VaultManager {
    private static Permission perms = null;

    /**
     * Setup the Vault permission API
     *
     * @return true if success, false if Vault not found
     */
    public static boolean setupPermissions() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = V10LiftPlugin.getInstance().getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return true;
    }

    /**
     * Get all the groups in the server
     *
     * @return A list with all the names of all the groups in the server
     */
    public static List<String> getGroups() {
        return Arrays.asList(perms.getGroups());
    }

    /**
     * Check if a group exists
     *
     * @param groupName The name of the group
     * @return true if exists
     */
    public static boolean isGroup(String groupName) {
        return Arrays.asList(perms.getGroups()).contains(groupName);
    }

    /**
     * Check if a user is in any of the groups provided
     *
     * @param player The player to check for
     * @param groups The groups to check for
     * @return true if in a group
     */
    public static boolean inAnyGroup(Player player, HashSet<String> groups) {
        boolean found = false;
        for (String group : groups) {
            found = Arrays.asList(perms.getPlayerGroups(player)).contains(group);
        }
        return found;
    }
}
