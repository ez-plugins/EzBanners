package com.skyblockexp.ezbanners.api;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.http.ApiClient;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.Map;

public class EzBannersApi {
    private final EzBannersPlugin plugin;
    private final ApiClient apiClient;
    private BukkitRunnable statsTask;

    public EzBannersApi(EzBannersPlugin plugin) {
        this.plugin = plugin;
        this.apiClient = new ApiClient(plugin);
    }

    public ApiClient.ApiResponse sendPluginStats(String pluginUuid, JavaPlugin sourcePlugin, int serverCount, int playerCount) {
        return sendPluginStats(pluginUuid, sourcePlugin, serverCount, playerCount, null, null);
    }

    public ApiClient.ApiResponse sendPluginStats(
        String pluginUuid,
        JavaPlugin sourcePlugin,
        int serverCount,
        int playerCount,
        Map<String, Object> extraStats,
        Map<String, Object> metaOverrides
    ) {
        if (sourcePlugin == null) {
            return new ApiClient.ApiResponse(false, 0, "Missing source plugin");
        }
        if (pluginUuid == null || pluginUuid.trim().isEmpty()) {
            return new ApiClient.ApiResponse(false, 0, "Missing plugin UUID");
        }

        EzBannersConfig config = plugin.getEzBannersConfig();
        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> pluginInfo = new LinkedHashMap<>();
        PluginDescriptionFile description = sourcePlugin.getDescription();
        pluginInfo.put("name", description.getName());
        pluginInfo.put("version", description.getVersion());
        payload.put("plugin", pluginInfo);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("servers", Math.max(0, serverCount));
        stats.put("players", Math.max(0, playerCount));
        if (extraStats != null) {
            stats.putAll(extraStats);
        }
        payload.put("stats", stats);

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("server_uuid", config.getServerUuid());
        meta.put("server_name", sourcePlugin.getServer().getName());
        if (metaOverrides != null) {
            meta.putAll(metaOverrides);
        }
        payload.put("meta", meta);

        return apiClient.postPluginPayload(config, pluginUuid, payload);
    }

    /**
     * Starts an async repeating task to send server and player counts to EzBanners automatically.
     * @param pluginUuid The plugin UUID from the EzBanners dashboard.
     * @param sourcePlugin The JavaPlugin instance.
     * @param intervalSeconds How often to send stats (in seconds).
     */
    public void startAutoStatsPush(String pluginUuid, JavaPlugin sourcePlugin, int intervalSeconds) {
        if (statsTask != null) {
            statsTask.cancel();
        }
        statsTask = new BukkitRunnable() {
            @Override
            public void run() {
                int serverCount = 1;
                int playerCount = Bukkit.getOnlinePlayers().size();
                sendPluginStats(pluginUuid, sourcePlugin, serverCount, playerCount);
            }
        };
        statsTask.runTaskTimerAsynchronously(plugin, 0L, intervalSeconds * 20L);
    }

    /**
     * Stops the async stats push task if running.
     */
    public void stopAutoStatsPush() {
        if (statsTask != null) {
            statsTask.cancel();
            statsTask = null;
        }
    }
}
