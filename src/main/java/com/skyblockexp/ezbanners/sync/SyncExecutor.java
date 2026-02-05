package com.skyblockexp.ezbanners.sync;

import com.skyblockexp.ezbanners.EzBannersPlugin;
import com.skyblockexp.ezbanners.config.EzBannersConfig;
import com.skyblockexp.ezbanners.domain.ServerData;
import com.skyblockexp.ezbanners.domain.SyncPayload;
import com.skyblockexp.ezbanners.http.ApiClient;
import com.skyblockexp.ezbanners.metrics.ServerDataCollector;
import com.skyblockexp.ezbanners.util.HmacUtil;
import com.skyblockexp.ezbanners.util.JsonUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles sync execution: payload building and HTTP requests.
 */
public class SyncExecutor {
    private final EzBannersPlugin plugin;
    private final ApiClient apiClient;
    private EzBannersConfig config;
    private ServerDataCollector dataCollector;

    public SyncExecutor(EzBannersPlugin plugin, EzBannersConfig config, ServerDataCollector dataCollector) {
        this.plugin = plugin;
        this.config = config;
        this.dataCollector = dataCollector;
        this.apiClient = new ApiClient(plugin);
    }

    public void updateConfig(EzBannersConfig config, ServerDataCollector dataCollector) {
        this.config = config;
        this.dataCollector = dataCollector;
    }

    /**
     * Builds a sync payload from collected server data.
     */
    public SyncPayload buildPayload() {
        Map<String, Object> data = dataCollector.collectData();
        ServerData serverData = new ServerData(data);
        
        long timestamp = System.currentTimeMillis();
        String serverUuid = config.getServerUuid();
        
        Map<String, String> headers = buildHeaders(serverUuid, timestamp, serverData);
        
        return new SyncPayload(serverUuid, timestamp, serverData, headers);
    }

    /**
     * Executes a sync by sending the payload to the API.
     */
    public ApiClient.ApiResponse executeSync(SyncPayload payload) {
        if (!config.isWebsiteSyncEnabled()) {
            plugin.debug("Website sync is disabled, skipping");
            return new ApiClient.ApiResponse(true, 200, "Sync disabled");
        }

        Map<String, Object> requestPayload = new LinkedHashMap<>();
        requestPayload.put("data", payload.getServerData().getData());

        String json = JsonUtil.toJson(requestPayload);
        String signature = HmacUtil.hmacSha256(config.getApiToken(), json);

        return apiClient.postPayloadWithHeaders(
            config.getApiEndpoint(),
            json,
            signature,
            payload.getHeaders()
        );
    }

    private Map<String, String> buildHeaders(String serverUuid, long timestamp, ServerData serverData) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Server-UUID", serverUuid);
        headers.put("X-Server-Id", serverUuid);
        headers.put("X-Server-Token", config.getApiToken());
        headers.put("X-Timestamp", String.valueOf(timestamp));
        return headers;
    }
}
