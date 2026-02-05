package com.skyblockexp.ezbanners.domain;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SyncPayloadTest {
    
    @Test
    void testConstructor() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("online_players", 5);
        ServerData serverData = new ServerData(data);
        
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Server-UUID", "test-uuid");
        
        long timestamp = System.currentTimeMillis();
        
        SyncPayload payload = new SyncPayload("test-uuid", timestamp, serverData, headers);
        
        assertEquals("test-uuid", payload.getServerUuid());
        assertEquals(timestamp, payload.getTimestamp());
        assertNotNull(payload.getServerData());
        assertEquals(1, payload.getHeaders().size());
        assertEquals("test-uuid", payload.getHeaders().get("X-Server-UUID"));
    }
    
    @Test
    void testNullHeaders() {
        ServerData serverData = new ServerData(new LinkedHashMap<>());
        SyncPayload payload = new SyncPayload("uuid", 0L, serverData, null);
        
        assertNotNull(payload.getHeaders());
        assertTrue(payload.getHeaders().isEmpty());
    }
    
    @Test
    void testHeadersImmutability() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Test", "value");
        
        ServerData serverData = new ServerData(new LinkedHashMap<>());
        SyncPayload payload = new SyncPayload("uuid", 0L, serverData, headers);
        
        // Modify original headers
        headers.put("X-Test", "modified");
        headers.put("X-New", "new");
        
        // Payload headers should not change
        assertEquals("value", payload.getHeaders().get("X-Test"));
        assertNull(payload.getHeaders().get("X-New"));
        assertEquals(1, payload.getHeaders().size());
    }
    
    @Test
    void testGetHeadersReturnsImmutable() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("X-Test", "value");
        
        ServerData serverData = new ServerData(new LinkedHashMap<>());
        SyncPayload payload = new SyncPayload("uuid", 0L, serverData, headers);
        
        assertThrows(UnsupportedOperationException.class, () -> {
            payload.getHeaders().put("X-New", "value");
        });
    }
}
