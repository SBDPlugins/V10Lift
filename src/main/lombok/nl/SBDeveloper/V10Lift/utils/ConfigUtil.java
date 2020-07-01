package nl.SBDeveloper.V10Lift.utils;

import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigUtil {
    /* Helps building the messages */

    /**
     * Get a message from the config.yml
     *
     * @param path The path to look for
     * @return The message
     */
    @Nonnull
    public static String getConfigText(@Nonnull String path) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(V10LiftPlugin.getSConfig().getFile().getString(path), "Message " + path + " not found in config.yml!"));
    }

    /**
     * Send a message from the messages.yml without variables
     *
     * @param p The commandsender to send it to
     * @param path The path to look for
     */
    public static void sendMessage(CommandSender p, @Nonnull String path) {
        if (V10LiftPlugin.getMessages().getFile().get(path) == null) {
            throw new NullPointerException("Message " + path + " not found in messages.yml!");
        }

        if (V10LiftPlugin.getMessages().getFile().isList(path)) {
            //Multi line message
            for (String message : V10LiftPlugin.getMessages().getFile().getStringList(path)) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        } else {
            //Single line message
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(V10LiftPlugin.getMessages().getFile().getString(path))));
        }
    }

    /**
     * Get a message from the messages.yml with variables
     *
     * @param p The commandsender to send it to
     * @param path The path to look for
     * @param replacement The replacements -> key: %Name% = value: TheName
     */
    public static void sendMessage(CommandSender p, @Nonnull String path, Map<String, String> replacement) {
        if (V10LiftPlugin.getMessages().getFile().get(path) == null) {
            throw new NullPointerException("Message " + path + " not found in messages.yml!");
        }

        if (V10LiftPlugin.getMessages().getFile().isList(path)) {
            //Multi line message
            for (String message : V10LiftPlugin.getMessages().getFile().getStringList(path)) {
                p.sendMessage(formatMessage(message, replacement));
            }
        } else {
            //Single line message
            String message = V10LiftPlugin.getMessages().getFile().getString(path);
            p.sendMessage(formatMessage(message, replacement));
        }
    }

    @Nonnull
    private static String formatMessage(String message, @Nonnull Map<String, String> replacement) {
        Map<String, String> fixedMap = new HashMap<>();
        for (Map.Entry<String, String> ent : replacement.entrySet()) {
            fixedMap.put(ent.getKey().replaceAll("%", ""), ent.getValue());
        }

        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(Objects.requireNonNull(message));
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (matcher.find()) {
            String repl = fixedMap.get(matcher.group(1));
            builder.append(message, i, matcher.start());
            if (repl == null)
                builder.append(matcher.group(0));
            else
                builder.append(repl);
            i = matcher.end();
        }
        builder.append(message.substring(i));
        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }
}
