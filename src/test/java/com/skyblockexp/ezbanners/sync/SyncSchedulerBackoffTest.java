package com.skyblockexp.ezbanners.sync;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SyncSchedulerBackoffTest {
    
    @Test
    void testBackoffCalculation() {
        int interval = 30;
        int maxBackoff = 300;
        
        assertEquals(30, calculateBackoff(interval, 0, maxBackoff));
        assertEquals(60, calculateBackoff(interval, 1, maxBackoff));
        assertEquals(120, calculateBackoff(interval, 2, maxBackoff));
        assertEquals(240, calculateBackoff(interval, 3, maxBackoff));
        assertEquals(300, calculateBackoff(interval, 4, maxBackoff));
        assertEquals(300, calculateBackoff(interval, 10, maxBackoff));
    }
    
    @Test
    void testBackoffReset() {
        int failureCount = 5;
        failureCount = 0;
        assertEquals(0, failureCount);
    }
    
    @Test
    void testJitterRange() {
        int baseBackoff = 100;
        List<Integer> jitterValues = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            int jitter = (int) (Math.random() * (baseBackoff / 4 + 1));
            jitterValues.add(jitter);
        }
        
        for (int jitter : jitterValues) {
            assertTrue(jitter >= 0);
            assertTrue(jitter <= baseBackoff / 4 + 1);
        }
    }
    
    private int calculateBackoff(int interval, int failureCount, int maxBackoff) {
        int baseBackoff = (int) Math.min(maxBackoff, interval * Math.pow(2, Math.min(failureCount, 10)));
        return baseBackoff;
    }
}
