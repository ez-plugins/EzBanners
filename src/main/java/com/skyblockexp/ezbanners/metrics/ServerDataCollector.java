package com.skyblockexp.ezbanners.metrics;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServerDataCollector {
    private EzBannersPlugin plugin;
    private EzBannersConfig config;
    private long serverStartMillis;

    public ServerDataCollector(EzBannersPlugin plugin, EzBannersConfig config, long serverStartMillis) {
        this.plugin = plugin;
        this.config = config;
        this.serverStartMillis = serverStartMillis;
    }

    public void refreshConfig(EzBannersConfig config, long serverStartMillis) {
        this.config = config;
        this.serverStartMillis = serverStartMillis;
    }

    public Map<String, Object> collectData() {
        Map<String, Object> data = new LinkedHashMap<>();
        if (config.isFieldEnabled("server_name")) {
            data.put("server_name", ServerUtil.getServerName());
        }
        if (config.isFieldEnabled("online_players")) {
            data.put("online_players", ServerUtil.getOnlinePlayerCount());
        }
        if (config.isFieldEnabled("max_players")) {
            data.put("max_players", Bukkit.getMaxPlayers());
        }
        if (config.isFieldEnabled("server_version")) {
            data.put("server_version", Bukkit.getVersion());
        }
        if (config.isFieldEnabled("tps_1m") || config.isFieldEnabled("tps_5m") || config.isFieldEnabled("tps_15m")) {
            double[] tps = ServerUtil.getTps();
            if (tps != null) {
                if (config.isFieldEnabled("tps_1m")) {
                    data.put("tps_1m", ServerUtil.round(tps[0]));
                }
                if (config.isFieldEnabled("tps_5m") && tps.length > 1) {
                    data.put("tps_5m", ServerUtil.round(tps[1]));
                }
                if (config.isFieldEnabled("tps_15m") && tps.length > 2) {
                    data.put("tps_15m", ServerUtil.round(tps[2]));
                }
            }
        }
        if (config.isFieldEnabled("uptime")) {
            data.put("uptime", Math.max(0L, System.currentTimeMillis() - serverStartMillis));
        }
        if (config.isFieldEnabled("motd")) {
            data.put("motd", Bukkit.getMotd());
        }
        if (config.isFieldEnabled("whitelist")) {
            data.put("whitelist", Bukkit.hasWhitelist());
        }
        if (config.isFieldEnabled("placeholders")) {
            Map<String, String> placeholders = ServerUtil.resolvePlaceholders(plugin, config.getPlaceholderMappings(), getContextPlayer());
            if (!placeholders.isEmpty()) {
                data.put("placeholders", placeholders);
            }
        }
        return data;
    }

    private OfflinePlayer getContextPlayer() {
        return ServerUtil.getFirstOnlinePlayer();
    }
}
