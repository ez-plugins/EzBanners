package com.skyblockexp.ezbanners.api.provider;

import com.skyblockexp.ezbanners.domain.ServerData;

/**
 * Public API interface for data providers.
 * External plugins can implement this to provide custom data to EzBanners.
 */
public interface DataProvider {
    /**
     * Collects server data for syncing.
     * @return ServerData containing the collected metrics
     */
    ServerData collectData();
    
    /**
     * Gets the priority of this provider. Higher priority providers are called first.
     * @return Priority value (higher = earlier execution)
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * Gets the name of this data provider for logging purposes.
     * @return Provider name
     */
    String getName();
}
