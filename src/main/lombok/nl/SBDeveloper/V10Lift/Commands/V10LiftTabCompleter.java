package nl.SBDeveloper.V10Lift.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class V10LiftTabCompleter implements TabCompleter {

    private static final List<String> COMMANDS = Arrays.asList("create", "delete", "rename", "abort", "whois", "edit", "floor", "input", "build", "rope", "door", "speed", "realistic", "repair", "whitelist", "reload", "help", "start", "stop", "offline");

    
    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command cmd, @Nonnull String label, @Nonnull String[] args) {
        if (label.equalsIgnoreCase("v10lift")) {
            return (args.length > 0) ? StringUtil.copyPartialMatches(args[0], COMMANDS, new ArrayList<>()) : null;
        }
        return null;
    }

}
