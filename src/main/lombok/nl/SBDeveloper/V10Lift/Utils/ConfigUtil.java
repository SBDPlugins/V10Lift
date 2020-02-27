package nl.SBDeveloper.V10Lift.Utils;

import nl.SBDeveloper.V10Lift.V10LiftPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.util.HashMap;
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
        String fileMessage = V10LiftPlugin.getMessages().getFile().getString(path);
        if (fileMessage == null) {
            throw new NullPointerException("Message " + path + " not found in messages.yml!");
        }

        String[] messages = fileMessage.split("\n");
        for (String message : messages) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
    }

    /**
     * Get a message from the messages.yml with variables
     *
     * @param p The commandsender to send it to
     * @param path The path to look for
     * @param replacement The replacements -> key: %Name% = value: TheName
     * @return The message with replacements
     */
    public static void sendMessage(CommandSender p, @Nonnull String path, HashMap<String, String> replacement) {
        String fileMessage = V10LiftPlugin.getMessages().getFile().getString(path);
        if (fileMessage == null) {
            throw new NullPointerException("Message " + path + " not found in messages.yml!");
        }

        String[] messages = fileMessage.split("\n");
        for (String message : messages) {
            Pattern pattern = Pattern.compile("%(.*?)%");
            Matcher matcher = pattern.matcher(message);
            StringBuilder builder = new StringBuilder();
            int i = 0;
            while (matcher.find()) {
                String repl = replacement.get(matcher.group(1));
                builder.append(message, i, matcher.start());
                if (repl == null)
                    builder.append(matcher.group(0));
                else
                    builder.append(repl);
                i = matcher.end();
            }
            builder.append(message.substring(i));
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', builder.toString()));
        }
    }
}
