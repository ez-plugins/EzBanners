package com.skyblockexp.ezbanners.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Immutable domain model representing collected server data.
 */
public class ServerData {
    private final Map<String, Object> data;

    public ServerData(Map<String, Object> data) {
        this.data = data == null ? Collections.emptyMap() : new LinkedHashMap<>(data);
    }

    public Map<String, Object> getData() {
        return Collections.unmodifiableMap(data);
    }

    public Object get(String key) {
        return data.get(key);
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public int size() {
        return data.size();
    }
}
