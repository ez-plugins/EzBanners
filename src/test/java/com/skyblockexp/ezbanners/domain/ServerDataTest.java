package com.skyblockexp.ezbanners.domain;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ServerDataTest {
    
    @Test
    void testConstructorWithNull() {
        ServerData serverData = new ServerData(null);
        assertTrue(serverData.isEmpty());
        assertEquals(0, serverData.size());
    }
    
    @Test
    void testConstructorWithData() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("online_players", 10);
        data.put("server_name", "TestServer");
        
        ServerData serverData = new ServerData(data);
        assertFalse(serverData.isEmpty());
        assertEquals(2, serverData.size());
        assertEquals(10, serverData.get("online_players"));
        assertEquals("TestServer", serverData.get("server_name"));
    }
    
    @Test
    void testImmutability() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("online_players", 10);
        
        ServerData serverData = new ServerData(data);
        
        // Modify original map
        data.put("online_players", 20);
        data.put("new_field", "value");
        
        // ServerData should not change
        assertEquals(10, serverData.get("online_players"));
        assertNull(serverData.get("new_field"));
        assertEquals(1, serverData.size());
    }
    
    @Test
    void testGetDataReturnsImmutable() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("online_players", 10);
        
        ServerData serverData = new ServerData(data);
        Map<String, Object> retrieved = serverData.getData();
        
        assertThrows(UnsupportedOperationException.class, () -> {
            retrieved.put("new_field", "value");
        });
    }
}
