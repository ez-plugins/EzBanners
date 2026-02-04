package com.skyblockexp.ezbanners.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

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
    private final boolean metricsEnabled;
    private final boolean placeholdersEnabled;
    private final boolean websiteSyncEnabled;
    private final Set<String> enabledFields;
    private final Map<String, String> placeholderMappings;
    private boolean valid = true;

    public EzBannersConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.apiEndpoint = config.getString(ConfigKeys.API_ENDPOINT, "");
        this.apiToken = config.getString(ConfigKeys.API_TOKEN, "");
        this.pluginEndpoint = config.getString(ConfigKeys.PLUGIN_ENDPOINT, "");
        this.pluginUuid = config.getString(ConfigKeys.PLUGIN_UUID, "");
        this.pluginToken = config.getString(ConfigKeys.PLUGIN_TOKEN, "");
        this.serverUuid = config.getString(ConfigKeys.SERVER_UUID, "");
        this.syncIntervalSeconds = Math.max(5, config.getInt(ConfigKeys.SYNC_INTERVAL, 30));
        this.maxBackoffSeconds = Math.max(syncIntervalSeconds, config.getInt(ConfigKeys.SYNC_MAX_BACKOFF, 300));
        this.debugEnabled = config.getBoolean(ConfigKeys.DEBUG_ENABLED, false);
        this.metricsEnabled = config.getBoolean(ConfigKeys.FEATURE_METRICS_ENABLED, true);
        this.placeholdersEnabled = config.getBoolean(ConfigKeys.FEATURE_PLACEHOLDERS_ENABLED, true);
        this.websiteSyncEnabled = config.getBoolean(ConfigKeys.FEATURE_WEBSITE_SYNC_ENABLED, true);
        this.enabledFields = readFields();
        this.placeholderMappings = readPlaceholderMappings();
        validate();
    }

    private Set<String> readFields() {
        List<String> list = config.getStringList(ConfigKeys.ENABLED_FIELDS);
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
        String mappingsPath = ConfigKeys.PLACEHOLDER_MAPPINGS;
        if (!config.isConfigurationSection(mappingsPath)) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String key : config.getConfigurationSection(mappingsPath).getKeys(false)) {
            String value = config.getString(mappingsPath + "." + key, "");
            if (value != null && !value.trim().isEmpty()) {
                map.put(key, value);
            } else {
                plugin.getLogger().warning("[EzBanners] Empty placeholder mapping for key: " + key);
            }
        }
        return map;
    }

    private void validate() {
        if (serverUuid == null || serverUuid.trim().isEmpty()) {
            plugin.getLogger().severe("[EzBanners] Missing required config: " + ConfigKeys.SERVER_UUID);
            valid = false;
        }
        if (apiEndpoint == null || apiEndpoint.trim().isEmpty()) {
            plugin.getLogger().warning("[EzBanners] Missing config: " + ConfigKeys.API_ENDPOINT + " - sync will not work until configured");
        }
        if (apiToken == null || apiToken.trim().isEmpty()) {
            plugin.getLogger().warning("[EzBanners] Missing config: " + ConfigKeys.API_TOKEN + " - use /ezbanners link <token> to configure");
        }
        if (syncIntervalSeconds < 5) {
            plugin.getLogger().warning("[EzBanners] Sync interval too low, using minimum of 5 seconds");
        }
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

    public boolean isValid() {
        return valid;
    }

    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    public boolean isPlaceholdersEnabled() {
        return placeholdersEnabled;
    }

    public boolean isWebsiteSyncEnabled() {
        return websiteSyncEnabled;
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
