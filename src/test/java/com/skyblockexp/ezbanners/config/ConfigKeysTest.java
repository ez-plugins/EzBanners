package com.skyblockexp.ezbanners.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigKeysTest {
    
    @Test
    void testConfigKeysAreNotNull() {
        assertNotNull(ConfigKeys.API_ENDPOINT);
        assertNotNull(ConfigKeys.API_TOKEN);
        assertNotNull(ConfigKeys.PLUGIN_ENDPOINT);
        assertNotNull(ConfigKeys.PLUGIN_UUID);
        assertNotNull(ConfigKeys.PLUGIN_TOKEN);
        assertNotNull(ConfigKeys.SERVER_UUID);
        assertNotNull(ConfigKeys.SYNC_INTERVAL);
        assertNotNull(ConfigKeys.SYNC_MAX_BACKOFF);
        assertNotNull(ConfigKeys.FEATURE_METRICS_ENABLED);
        assertNotNull(ConfigKeys.FEATURE_PLACEHOLDERS_ENABLED);
        assertNotNull(ConfigKeys.FEATURE_WEBSITE_SYNC_ENABLED);
        assertNotNull(ConfigKeys.ENABLED_FIELDS);
        assertNotNull(ConfigKeys.PLACEHOLDER_MAPPINGS);
        assertNotNull(ConfigKeys.DEBUG_ENABLED);
    }
    
    @Test
    void testConfigKeysFormat() {
        assertTrue(ConfigKeys.API_ENDPOINT.contains("."));
        assertTrue(ConfigKeys.PLUGIN_ENDPOINT.contains("."));
        assertTrue(ConfigKeys.SERVER_UUID.contains("."));
        assertTrue(ConfigKeys.SYNC_INTERVAL.contains("."));
        assertTrue(ConfigKeys.FEATURE_METRICS_ENABLED.contains("."));
    }
    
    @Test
    void testConfigKeysNotEmpty() {
        assertFalse(ConfigKeys.API_ENDPOINT.isEmpty());
        assertFalse(ConfigKeys.API_TOKEN.isEmpty());
        assertFalse(ConfigKeys.SERVER_UUID.isEmpty());
    }
}
