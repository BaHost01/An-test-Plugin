package com.cstudioss.cursedcombat.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public final class TextUtil {
    private TextUtil() {
    }

    public static void send(CommandSender sender, String message) {
        if (sender == null || message == null) {
            return;
        }
        String colored = ChatColor.translateAlternateColorCodes('&', message);
        try {
            Class<?> componentClass = Class.forName("net.kyori.adventure.text.Component");
            Method textMethod = componentClass.getMethod("text", String.class);
            Object component = textMethod.invoke(null, ChatColor.stripColor(colored));
            Method sendMethod = sender.getClass().getMethod("sendMessage", componentClass);
            sendMethod.invoke(sender, component);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            sender.sendMessage(colored);
        }
    }
}
