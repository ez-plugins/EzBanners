package com.skyblockexp.ezbanners.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable domain model representing a sync payload.
 */
public class SyncPayload {
    private final String serverUuid;
    private final long timestamp;
    private final ServerData serverData;
    private final Map<String, String> headers;

    public SyncPayload(String serverUuid, long timestamp, ServerData serverData, Map<String, String> headers) {
        this.serverUuid = serverUuid;
        this.timestamp = timestamp;
        this.serverData = serverData;
        this.headers = headers == null ? Collections.emptyMap() : new LinkedHashMap<>(headers);
    }

    public String getServerUuid() {
        return serverUuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ServerData getServerData() {
        return serverData;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
