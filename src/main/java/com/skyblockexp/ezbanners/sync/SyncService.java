package com.skyblockexp.ezbanners.sync;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.domain.SyncPayload;
import com.skyblockexp.ezbanners.http.ApiClient;
import com.skyblockexp.ezbanners.metrics.ServerDataCollector;

/**
 * Coordinator for sync operations. Delegates scheduling to SyncScheduler
 * and execution to SyncExecutor.
 */
public class SyncService {
    private final EzBannersPlugin plugin;
    private final SyncScheduler scheduler;
    private final SyncExecutor executor;
    private EzBannersConfig config;
    private ServerDataCollector dataCollector;

    public SyncService(EzBannersPlugin plugin, EzBannersConfig config, ServerDataCollector dataCollector) {
        this.plugin = plugin;
        this.config = config;
        this.dataCollector = dataCollector;
        this.scheduler = new SyncScheduler(plugin, config);
        this.executor = new SyncExecutor(plugin, config, dataCollector);
    }

    public void start() {
        if (scheduler.isRunning()) {
            plugin.debug("SyncService already running");
            return;
        }
        plugin.debug("Starting SyncService");
        scheduler.start(this::performSync);
    }

    public void stop() {
        plugin.debug("Stopping SyncService");
        scheduler.stop();
    }

    public void reload(EzBannersConfig config, ServerDataCollector dataCollector) {
        plugin.debug("Reloading SyncService");
        this.config = config;
        this.dataCollector = dataCollector;
        scheduler.updateConfig(config);
        executor.updateConfig(config, dataCollector);
        stop();
        start();
    }

    private void performSync() {
        plugin.debug("Performing sync");
        try {
            SyncPayload payload = executor.buildPayload();
            ApiClient.ApiResponse response = executor.executeSync(payload);
            
            if (response.isSuccess()) {
                plugin.debug("Sync successful: " + response.getStatusCode());
                scheduler.onSyncSuccess(this::performSync);
            } else {
                plugin.debug("Sync failed: " + response.getStatusCode() + " - " + response.getMessage());
                scheduler.onSyncFailure(this::performSync);
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("[EzBanners] Sync error: " + ex.getMessage());
            scheduler.onSyncFailure(this::performSync);
        }
    }

    public SyncScheduler getScheduler() {
        return scheduler;
    }

    public SyncExecutor getExecutor() {
        return executor;
    }
}
