package com.skyblockexp.ezbanners.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EzBannersConfig {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final String apiEndpoint;
    private final String apiToken;
    private final String pluginEndpoint;
    private final String pluginUuid;
    private final String pluginToken;
    private final String serverUuid;
    private final int syncIntervalSeconds;
    private final int maxBackoffSeconds;
    private final boolean debugEnabled;
    private final Set<String> enabledFields;
    private final Map<String, String> placeholderMappings;

    public EzBannersConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.apiEndpoint = config.getString("api.endpoint", "");
        this.apiToken = config.getString("api.token", "");
        this.pluginEndpoint = config.getString("plugin.endpoint", "");
        this.pluginUuid = config.getString("plugin.uuid", "");
        this.pluginToken = config.getString("plugin.token", "");
        this.serverUuid = config.getString("server.uuid", "");
        this.syncIntervalSeconds = Math.max(5, config.getInt("sync.interval", 30));
        this.maxBackoffSeconds = Math.max(syncIntervalSeconds, config.getInt("sync.max-backoff", 300));
        this.debugEnabled = config.getBoolean("debug.enabled", false);
        this.enabledFields = readFields();
        this.placeholderMappings = readPlaceholderMappings();
    }

    private Set<String> readFields() {
        List<String> list = config.getStringList("enabled.data.fields");
        if (list == null || list.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> fields = new HashSet<>();
        for (String entry : list) {
            if (entry != null) {
                fields.add(entry.toLowerCase());
            }
        }
        return fields;
    }

    private Map<String, String> readPlaceholderMappings() {
        if (!config.isConfigurationSection("placeholderapi.mappings")) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String key : config.getConfigurationSection("placeholderapi.mappings").getKeys(false)) {
            String value = config.getString("placeholderapi.mappings." + key, "");
            map.put(key, value);
        }
        return map;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getPluginEndpoint() {
        return pluginEndpoint;
    }

    public String getPluginUuid() {
        return pluginUuid;
    }

    public String getPluginToken() {
        return pluginToken;
    }

    public String getServerUuid() {
        return serverUuid;
    }

    public int getSyncIntervalSeconds() {
        return syncIntervalSeconds;
    }

    public int getMaxBackoffSeconds() {
        return maxBackoffSeconds;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public boolean isFieldEnabled(String field) {
        return enabledFields.contains(field.toLowerCase());
    }

    public Map<String, String> getPlaceholderMappings() {
        return Collections.unmodifiableMap(placeholderMappings);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
