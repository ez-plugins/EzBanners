package com.skyblockexp.ezbanners.lifecycle;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.api.EzBannersApi;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.http.ApiClient;
import com.skyblockexp.ezbanners.metrics.ServerDataCollector;
import com.skyblockexp.ezbanners.sync.SyncService;
import org.bukkit.scheduler.BukkitTask;

/**
 * Manages plugin lifecycle: startup and shutdown flows.
 */
public class PluginLifecycle {
    private final EzBannersPlugin plugin;
    private EzBannersConfig config;
    private ServerDataCollector dataCollector;
    private SyncService syncService;
    private EzBannersApi api;
    private BukkitTask usageStatsTask;
    private final long serverStartMillis;

    public PluginLifecycle(EzBannersPlugin plugin) {
        this.plugin = plugin;
        this.serverStartMillis = System.currentTimeMillis();
    }

    public void startup() {
        plugin.getLogger().info("[EzBanners] Starting up...");
        
        // Save default config
        plugin.saveDefaultConfig();
        
        // Ensure server UUID
        ensureServerUuid();
        
        // Initialize configuration
        config = new EzBannersConfig(plugin);
        
        if (!config.isValid()) {
            plugin.getLogger().severe("[EzBanners] Configuration validation failed. Plugin may not work correctly.");
        }
        
        // Initialize components
        dataCollector = new ServerDataCollector(plugin, config, serverStartMillis);
        syncService = new SyncService(plugin, config, dataCollector);
        api = new EzBannersApi(plugin);
        
        // Start usage stats reporting (async)
        startUsageStatsReporting();
        
        // Start sync service
        if (config.isWebsiteSyncEnabled()) {
            plugin.getLogger().info("[EzBanners] Starting sync service");
            syncService.start();
        } else {
            plugin.getLogger().info("[EzBanners] Website sync is disabled");
        }
        
        plugin.getLogger().info("[EzBanners] Startup complete");
    }

    public void shutdown() {
        plugin.getLogger().info("[EzBanners] Shutting down...");
        
        // Stop sync service
        if (syncService != null) {
            syncService.stop();
        }
        
        // Cancel usage stats task
        if (usageStatsTask != null && !usageStatsTask.isCancelled()) {
            usageStatsTask.cancel();
            usageStatsTask = null;
        }
        
        // Stop API auto-stats if running
        if (api != null) {
            api.stopAutoStatsPush();
        }
        
        // Clear references
        syncService = null;
        dataCollector = null;
        config = null;
        api = null;
        
        plugin.getLogger().info("[EzBanners] Shutdown complete");
    }

    public void reload() {
        plugin.getLogger().info("[EzBanners] Reloading configuration...");
        
        plugin.reloadConfig();
        ensureServerUuid();
        
        config = new EzBannersConfig(plugin);
        
        if (!config.isValid()) {
            plugin.getLogger().warning("[EzBanners] Configuration validation failed after reload");
        }
        
        if (dataCollector != null) {
            dataCollector.refreshConfig(config, serverStartMillis);
        }
        
        if (syncService != null) {
            syncService.reload(config, dataCollector);
        }
        
        plugin.getLogger().info("[EzBanners] Reload complete");
    }

    private void ensureServerUuid() {
        String uuid = plugin.getConfig().getString("server.uuid", "");
        if (uuid == null || uuid.trim().isEmpty()) {
            String generated = java.util.UUID.randomUUID().toString();
            plugin.getConfig().set("server.uuid", generated);
            plugin.saveConfig();
            plugin.getLogger().info("[EzBanners] Generated server UUID: " + generated);
        }
    }

    private void startUsageStatsReporting() {
        String ezbannersPluginId = "ca69c7a3-50e0-45fe-b0b6-189b397c86d4";
        ApiClient apiClient = new ApiClient(plugin);
        
        usageStatsTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
            plugin,
            () -> {
                try {
                    String pluginName = plugin.getDescription().getName();
                    String pluginVersion = plugin.getDescription().getVersion();
                    int playerCount = plugin.getServer().getOnlinePlayers().size();
                    
                    apiClient.postPluginUsageStats(ezbannersPluginId, pluginName, pluginVersion, 1, playerCount);
                    plugin.debug("Usage stats sent successfully");
                } catch (Exception ex) {
                    plugin.debug("Usage stats push failed: " + ex.getMessage());
                }
            },
            0L,
            300 * 20L // Every 5 minutes
        );
    }

    public EzBannersConfig getConfig() {
        return config;
    }

    public ServerDataCollector getDataCollector() {
        return dataCollector;
    }

    public SyncService getSyncService() {
        return syncService;
    }

    public EzBannersApi getApi() {
        return api;
    }
}
