package com.skyblockexp.ezbanners.sync;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.http.ApiClient;
import com.skyblockexp.ezbanners.metrics.ServerDataCollector;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class SyncService {
    private final EzBannersPlugin plugin;
    private EzBannersConfig config;
    private ServerDataCollector dataCollector;
    private final ApiClient apiClient;
    private BukkitTask scheduledTask;
    private int failureCount;
    private boolean running;

    public SyncService(EzBannersPlugin plugin, EzBannersConfig config, ServerDataCollector dataCollector) {
        this.plugin = plugin;
        this.config = config;
        this.dataCollector = dataCollector;
        this.apiClient = new ApiClient(plugin);
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        scheduleNext(0);
    }

    public void stop() {
        running = false;
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }

    public void reload(EzBannersConfig config, ServerDataCollector dataCollector) {
        this.config = config;
        this.dataCollector = dataCollector;
        stop();
        start();
    }

    private void scheduleNext(long delaySeconds) {
        if (!running) {
            return;
        }
        long ticks = Math.max(1, delaySeconds * 20L);
        scheduledTask = new BukkitRunnable() {
            @Override
            public void run() {
                syncOnce();
            }
        }.runTaskLaterAsynchronously(plugin, ticks);
    }

    private void syncOnce() {
        if (!running) {
            return;
        }
        Map<String, Object> data = dataCollector.collectData();
        ApiClient.ApiResponse response = apiClient.postPayload(config, data);
        if (response.isSuccess()) {
            failureCount = 0;
            plugin.debug("Synced data successfully. Status: " + response.getStatusCode());
            scheduleNext(config.getSyncIntervalSeconds());
        } else {
            failureCount++;
            int backoff = (int) Math.min(config.getMaxBackoffSeconds(),
                config.getSyncIntervalSeconds() * Math.pow(2, failureCount));
            plugin.getLogger().warning("[EzBanners] Sync failed (" + response.getStatusCode() + "): " + response.getMessage());
            plugin.debug("Retrying sync in " + backoff + "s.");
            scheduleNext(backoff);
        }
    }
}
