package com.skyblockexp.ezbanners.config;

/**
 * Centralized configuration keys for EzBanners.
 */
public final class ConfigKeys {
    // API configuration
    public static final String API_ENDPOINT = "api.endpoint";
    public static final String API_TOKEN = "api.token";
    
    // Plugin configuration
    public static final String PLUGIN_ENDPOINT = "plugin.endpoint";
    public static final String PLUGIN_UUID = "plugin.uuid";
    public static final String PLUGIN_TOKEN = "plugin.token";
    
    // Server configuration
    public static final String SERVER_UUID = "server.uuid";
    
    // Sync configuration
    public static final String SYNC_INTERVAL = "sync.interval";
    public static final String SYNC_MAX_BACKOFF = "sync.max-backoff";
    
    // Feature flags
    public static final String FEATURE_METRICS_ENABLED = "features.metrics.enabled";
    public static final String FEATURE_PLACEHOLDERS_ENABLED = "features.placeholders.enabled";
    public static final String FEATURE_WEBSITE_SYNC_ENABLED = "features.website-sync.enabled";
    
    // Data fields
    public static final String ENABLED_FIELDS = "enabled.data.fields";
    
    // PlaceholderAPI
    public static final String PLACEHOLDER_MAPPINGS = "placeholderapi.mappings";
    
    // Debug
    public static final String DEBUG_ENABLED = "debug.enabled";
    
    private ConfigKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
}
