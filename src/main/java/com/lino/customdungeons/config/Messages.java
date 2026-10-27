package com.lino.customdungeons.config;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class Messages {
    private static FileConfiguration config;
    private static String prefix = "&8[&6CustomDungeons&8]&r ";

    public static void init(FileConfiguration messagesConfig) {
        config = messagesConfig;
        if (config != null) {
            prefix = colorize(config.getString("prefix", prefix));
        }
    }

    public static String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String get(String path, Map<String, String> placeholders) {
        if (config == null) return path;
        String msg = config.getString(path, path);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return colorize(msg);
    }

    public static String get(String path) {
        return get(path, null);
    }

    public static void send(CommandSender sender, String path, Map<String, String> placeholders) {
        String msg = get(path, placeholders);
        if (!msg.isEmpty()) {
            sender.sendMessage(prefix + msg);
        }
    }

    public static void send(CommandSender sender, String path) {
        send(sender, path, null);
    }

    public static void sendRaw(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }
}
