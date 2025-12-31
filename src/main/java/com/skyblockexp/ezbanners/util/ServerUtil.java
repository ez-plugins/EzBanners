package com.skyblockexp.ezbanners.util;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ServerUtil {
    private ServerUtil() {
    }

    public static String getServerName() {
        try {
            String name = Bukkit.getServer().getName();
            if (name != null && !name.trim().isEmpty()) {
                return name;
            }
        } catch (Throwable ignored) {
            // Ignore and fallback
        }
        return "UnknownServer";
    }

    public static int getOnlinePlayerCount() {
        Object players = getOnlinePlayersRaw();
        if (players instanceof Iterable) {
            int count = 0;
            for (Object ignored : (Iterable<?>) players) {
                count++;
            }
            return count;
        }
        if (players != null && players.getClass().isArray()) {
            return Array.getLength(players);
        }
        return 0;
    }

    public static OfflinePlayer getFirstOnlinePlayer() {
        Object players = getOnlinePlayersRaw();
        if (players instanceof Iterable) {
            for (Object player : (Iterable<?>) players) {
                if (player instanceof Player) {
                    return (Player) player;
                }
            }
        }
        if (players != null && players.getClass().isArray() && Array.getLength(players) > 0) {
            Object player = Array.get(players, 0);
            if (player instanceof Player) {
                return (Player) player;
            }
        }
        return null;
    }

    private static Object getOnlinePlayersRaw() {
        try {
            Method method = Bukkit.class.getMethod("getOnlinePlayers");
            return method.invoke(null);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static double[] getTps() {
        try {
            Method method = Bukkit.getServer().getClass().getMethod("getTPS");
            Object result = method.invoke(Bukkit.getServer());
            if (result instanceof double[]) {
                return (double[]) result;
            }
        } catch (Throwable ignored) {
            // Ignore and fallback
        }
        try {
            Method getServer = Bukkit.getServer().getClass().getMethod("getServer");
            Object mcServer = getServer.invoke(Bukkit.getServer());
            Field recentTps = mcServer.getClass().getField("recentTps");
            Object value = recentTps.get(mcServer);
            if (value instanceof double[]) {
                return (double[]) value;
            }
        } catch (Throwable ignored) {
            // Ignore
        }
        return null;
    }

    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static Map<String, String> resolvePlaceholders(EzBannersPlugin plugin, Map<String, String> mappings, OfflinePlayer context) {
        if (mappings == null || mappings.isEmpty()) {
            return Collections.emptyMap();
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return Collections.emptyMap();
        }
        Map<String, String> resolved = new LinkedHashMap<>();
        try {
            Class<?> placeholderApiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            Method setPlaceholders = placeholderApiClass.getMethod("setPlaceholders", OfflinePlayer.class, String.class);
            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                String placeholder = entry.getValue();
                if (placeholder == null || placeholder.trim().isEmpty()) {
                    continue;
                }
                try {
                    Object value = setPlaceholders.invoke(null, context, placeholder);
                    resolved.put(entry.getKey(), value == null ? "" : value.toString());
                } catch (Exception ex) {
                    plugin.debug("Placeholder resolution failed for " + entry.getKey() + ": " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            plugin.debug("PlaceholderAPI not available: " + ex.getMessage());
        }
        return resolved;
    }
}
