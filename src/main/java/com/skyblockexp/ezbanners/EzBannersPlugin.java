package com.skyblockexp.ezbanners;

import com.skyblockexp.ezbanners.command.LinkCommand;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.api.EzBannersApi;
import com.skyblockexp.ezbanners.metrics.ServerDataCollector;
import com.skyblockexp.ezbanners.sync.SyncService;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.logging.Level;

public class EzBannersPlugin extends JavaPlugin {
    private static EzBannersPlugin instance;
    private EzBannersConfig config;
    private ServerDataCollector dataCollector;
    private SyncService syncService;
    private long serverStartMillis;
    private EzBannersApi api;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.serverStartMillis = System.currentTimeMillis();
        ensureServerUuid();
        this.config = new EzBannersConfig(this);
        this.dataCollector = new ServerDataCollector(this, config, serverStartMillis);
        this.syncService = new SyncService(this, config, dataCollector);
        this.api = new EzBannersApi(this);

        String ezbannersPluginId = "ca69c7a3-50e0-45fe-b0b6-189b397c86d4";
        new org.bukkit.scheduler.BukkitRunnable() {
            @Override
            public void run() {
                try {
                    java.net.URL url = new java.net.URL("https://ezbanners.org/api/plugins/" + ezbannersPluginId + "/data");
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestProperty("User-Agent", "EzBanners/" + getDescription().getVersion());
                    conn.setDoOutput(true);
                    String json = "{\"plugin\":{\"name\":\"EzBanners\",\"version\":\"" + getDescription().getVersion() + "\"},\"stats\":{\"servers\":1,\"players\":" + getServer().getOnlinePlayers().size() + "}}";
                    try (java.io.OutputStream os = conn.getOutputStream()) {
                        os.write(json.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    }
                    int code = conn.getResponseCode();
                    StringBuilder response = new StringBuilder();
                    try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(
                            (code >= 400 ? conn.getErrorStream() : conn.getInputStream()), java.nio.charset.StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line);
                        }
                    }
                } catch (Exception ex) {
                    getLogger().warning("[EzBanners] Usage stats push failed: " + ex.getMessage());
                }
            }
        }.runTaskTimerAsynchronously(this, 0L, 300 * 20L);
        
        PluginCommand command = getCommand("ezbanners");
        if (command != null) {
            command.setExecutor((sender, cmd, label, args) -> {
                if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                    if (!sender.hasPermission("ezbanners.reload")) {
                        sender.sendMessage("§cYou do not have permission to reload EzBanners.");
                        return true;
                    }
                    reloadEzBannersConfig();
                    sender.sendMessage("§aEzBanners configuration reloaded.");
                    return true;
                }
                // Default to link command
                return new LinkCommand(this).onCommand(sender, cmd, label, args);
            });
        } else {
            getLogger().warning("[EzBanners] Command not registered. Check plugin.yml.");
        }

        getLogger().info("[EzBanners] Starting async sync service.");
        syncService.start();
    }

    @Override
    public void onDisable() {
        if (syncService != null) {
            syncService.stop();
        }
        instance = null;
        getLogger().info("[EzBanners] Disabled cleanly.");
    }

    public static EzBannersPlugin getInstance() {
        return instance;
    }

    public EzBannersConfig getEzBannersConfig() {
        return config;
    }

    public EzBannersApi getApi() {
        return api;
    }

    public void reloadEzBannersConfig() {
        reloadConfig();
        ensureServerUuid();
        this.config = new EzBannersConfig(this);
        if (dataCollector != null) {
            dataCollector.refreshConfig(config, serverStartMillis);
        }
        if (syncService != null) {
            syncService.reload(config, dataCollector);
        }
    }

    private void ensureServerUuid() {
        String uuid = getConfig().getString("server.uuid", "");
        if (uuid == null || uuid.trim().isEmpty()) {
            String generated = UUID.randomUUID().toString();
            getConfig().set("server.uuid", generated);
            saveConfig();
            getLogger().info("[EzBanners] Generated server UUID: " + generated);
        }
    }

    public void debug(String message) {
        if (config != null && config.isDebugEnabled()) {
            getLogger().log(Level.INFO, "[EzBanners][Debug] " + message);
        }
    }
}
