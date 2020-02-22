package nl.SBDeveloper.V10Lift.Commands;

import nl.SBDeveloper.V10Lift.Managers.DataManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class V10LiftTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (label.equalsIgnoreCase("v10lift")) {
            ArrayList<String> returns = new ArrayList<>();
            if (args.length == 1) {
                String[] strs = { "create", "delete", "abort", "whois", "edit", "floor", "input", "build", "rope", "door", "speed", "realistic", "repair", "whitelist", "reload", "help", "start", "stop", "offline" };
                returns.addAll(Arrays.asList(strs));
            } else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("floor")) {
                    returns.add("add");
                    returns.add("del");
                    returns.add("rename");
                } else if (args[0].equalsIgnoreCase("input") || args[0].equalsIgnoreCase("offline") || args[0].equalsIgnoreCase("whitelist") || args[0].equalsIgnoreCase("rope")) {
                    returns.add("add");
                    returns.add("del");
                } else if (args[0].equalsIgnoreCase("edit") || args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop")) {
                    returns.addAll(DataManager.getLifts().keySet());
                }
            } else if (args.length == 3) {
                if (args[0].equalsIgnoreCase("start")) {
                    returns.addAll(DataManager.getLift(args[1]).getFloors().keySet());
                }
            }

            Collections.sort(returns);
            return returns;
        }
        return null;
    }

}
