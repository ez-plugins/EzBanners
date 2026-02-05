package com.skyblockexp.ezbanners.command;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.sync.SyncScheduler;
import com.skyblockexp.ezbanners.sync.SyncService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles the /ezbanners status command.
 */
public class StatusCommand implements CommandExecutor {
    private final EzBannersPlugin plugin;

    public StatusCommand(EzBannersPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ezbanners.status")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        EzBannersConfig config = plugin.getEzBannersConfig();
        if (config == null) {
            sender.sendMessage(ChatColor.RED + "EzBanners configuration not loaded");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== EzBanners Status ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Config Valid: " + (config.isValid() ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        sender.sendMessage(ChatColor.YELLOW + "Server UUID: " + ChatColor.WHITE + config.getServerUuid());
        
        // API Configuration
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "API Configuration:");
        sender.sendMessage(ChatColor.YELLOW + "  Endpoint: " + ChatColor.WHITE + config.getApiEndpoint());
        sender.sendMessage(ChatColor.YELLOW + "  Token Configured: " + (isTokenConfigured(config.getApiToken()) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
        
        // Feature Flags
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Features:");
        sender.sendMessage(ChatColor.YELLOW + "  Metrics: " + getStatusColor(config.isMetricsEnabled()) + config.isMetricsEnabled());
        sender.sendMessage(ChatColor.YELLOW + "  Placeholders: " + getStatusColor(config.isPlaceholdersEnabled()) + config.isPlaceholdersEnabled());
        sender.sendMessage(ChatColor.YELLOW + "  Website Sync: " + getStatusColor(config.isWebsiteSyncEnabled()) + config.isWebsiteSyncEnabled());
        sender.sendMessage(ChatColor.YELLOW + "  Debug: " + getStatusColor(config.isDebugEnabled()) + config.isDebugEnabled());
        
        // Sync Status
        SyncService syncService = plugin.getLifecycle().getSyncService();
        if (syncService != null) {
            SyncScheduler scheduler = syncService.getScheduler();
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "Sync Service:");
            sender.sendMessage(ChatColor.YELLOW + "  Running: " + getStatusColor(scheduler.isRunning()) + scheduler.isRunning());
            sender.sendMessage(ChatColor.YELLOW + "  Failure Count: " + ChatColor.WHITE + scheduler.getFailureCount());
            sender.sendMessage(ChatColor.YELLOW + "  Sync Interval: " + ChatColor.WHITE + config.getSyncIntervalSeconds() + "s");
            sender.sendMessage(ChatColor.YELLOW + "  Max Backoff: " + ChatColor.WHITE + config.getMaxBackoffSeconds() + "s");
        }

        return true;
    }

    private boolean isTokenConfigured(String token) {
        return token != null && !token.trim().isEmpty();
    }

    private String getStatusColor(boolean enabled) {
        return enabled ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
    }
}
