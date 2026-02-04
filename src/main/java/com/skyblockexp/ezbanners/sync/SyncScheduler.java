package com.skyblockexp.ezbanners.sync;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

/**
 * Handles sync scheduling with exponential backoff and jitter.
 */
public class SyncScheduler {
    private final EzBannersPlugin plugin;
    private final Random random;
    private EzBannersConfig config;
    private BukkitTask scheduledTask;
    private int failureCount;
    private boolean running;

    public SyncScheduler(EzBannersPlugin plugin, EzBannersConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.random = new Random();
        this.failureCount = 0;
        this.running = false;
    }

    public void start(Runnable syncTask) {
        if (running) {
            plugin.debug("SyncScheduler already running");
            return;
        }
        running = true;
        failureCount = 0;
        scheduleNext(0, syncTask);
    }

    public void stop() {
        running = false;
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
        plugin.debug("SyncScheduler stopped");
    }

    public void onSyncSuccess(Runnable syncTask) {
        failureCount = 0;
        plugin.debug("Sync succeeded, resetting failure count");
        scheduleNext(config.getSyncIntervalSeconds(), syncTask);
    }

    public void onSyncFailure(Runnable syncTask) {
        failureCount++;
        int backoff = calculateBackoff();
        plugin.getLogger().warning("[EzBanners] Sync failed, retrying in " + backoff + "s (failure count: " + failureCount + ")");
        scheduleNext(backoff, syncTask);
    }

    public void updateConfig(EzBannersConfig config) {
        this.config = config;
    }

    private int calculateBackoff() {
        // Exponential backoff: interval * 2^failureCount
        int baseBackoff = (int) Math.min(
            config.getMaxBackoffSeconds(),
            config.getSyncIntervalSeconds() * Math.pow(2, Math.min(failureCount, 10))
        );
        
        // Add jitter: random value between 0% and 25% of the backoff
        int jitter = random.nextInt(baseBackoff / 4 + 1);
        return Math.min(baseBackoff + jitter, config.getMaxBackoffSeconds());
    }

    private void scheduleNext(long delaySeconds, Runnable syncTask) {
        if (!running) {
            return;
        }
        long ticks = Math.max(1, delaySeconds * 20L);
        plugin.debug("Scheduling next sync in " + delaySeconds + "s (" + ticks + " ticks)");
        scheduledTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (running) {
                    syncTask.run();
                }
            }
        }.runTaskLaterAsynchronously(plugin, ticks);
    }

    public boolean isRunning() {
        return running;
    }

    public int getFailureCount() {
        return failureCount;
    }
}
