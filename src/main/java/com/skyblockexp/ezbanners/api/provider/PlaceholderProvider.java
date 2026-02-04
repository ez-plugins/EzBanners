package com.skyblockexp.ezbanners.api.provider;

import org.bukkit.OfflinePlayer;

import java.util.Map;

/**
 * Public API interface for placeholder providers.
 * Allows external plugins to provide custom placeholders for banner generation.
 */
public interface PlaceholderProvider {
    /**
     * Resolves placeholders for the given context player.
     * @param context The player context for placeholder resolution (may be null)
     * @return Map of placeholder keys to resolved values
     */
    Map<String, String> resolvePlaceholders(OfflinePlayer context);
    
    /**
     * Gets the name of this placeholder provider for logging purposes.
     * @return Provider name
     */
    String getName();
}
