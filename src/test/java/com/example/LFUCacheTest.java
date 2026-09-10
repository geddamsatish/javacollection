package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LFUCacheTest {

    @Test
    public void testBasicPutAndGet() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
    }

    @Test
    public void testGetNonExistentKey() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("A", 1);
        Assertions.assertNull(cache.get("C"));
    }

    @Test
    public void testEvictionBasedOnFrequency() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);

        // Access A twice to increase its frequency
        cache.get("A");
        cache.get("A");

        // Add C, should evict B (frequency 1) instead of A (frequency 3)
        cache.put("C", 3);
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(3, cache.get("C"));
    }

    @Test
    public void testEvictionLRUWhenFrequencySame() {
        LFUCache<String, Integer> cache = new LFUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // All have frequency 1, add D, should evict A (least recently used)
        cache.put("D", 4);
        Assertions.assertNull(cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
        Assertions.assertEquals(3, cache.get("C"));
        Assertions.assertEquals(4, cache.get("D"));
    }

    @Test
    public void testUpdateExistingKey() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("A", 1);
        cache.put("A", 100);

        Assertions.assertEquals(100, cache.get("A"));
    }

    @Test
    public void testUpdateExistingKeyIncreasesFrequency() {
        LFUCache<String, Integer> cache = new LFUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // Update A, increasing its frequency
        cache.put("A", 10);

        // Add D, should evict B (frequency 1)
        cache.put("D", 4);
        Assertions.assertEquals(10, cache.get("A"));
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(3, cache.get("C"));
        Assertions.assertEquals(4, cache.get("D"));
    }

    @Test
    public void testCapacityOne() {
        LFUCache<String, Integer> cache = new LFUCache<>(1);
        cache.put("A", 1);
        Assertions.assertEquals(1, cache.get("A"));

        cache.put("B", 2);
        Assertions.assertNull(cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
    }

    @Test
    public void testComplexScenario() {
        LFUCache<String, Integer> cache = new LFUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // Access pattern: A(3), B(2), C(1)
        cache.get("A");
        cache.get("A");
        cache.get("B");

        // Add D, evict C (freq 1)
        cache.put("D", 4);
        Assertions.assertNull(cache.get("C"));

        // Access pattern now: A(3), B(2), D(1)
        // Add E, evict D (freq 1)
        cache.put("E", 5);
        Assertions.assertNull(cache.get("D"));

        // Access pattern: A(3), B(2), E(1)
        cache.get("B");
        // Now A(3), B(3), E(1)
        // Add F, evict E (freq 1)
        cache.put("F", 6);
        Assertions.assertNull(cache.get("E"));

        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
        Assertions.assertEquals(6, cache.get("F"));
    }

    @Test
    public void testWithDifferentValueTypes() {
        LFUCache<Integer, String> cache = new LFUCache<>(2);
        cache.put(1, "one");
        cache.put(2, "two");

        Assertions.assertEquals("one", cache.get(1));
        cache.put(3, "three");
        Assertions.assertNull(cache.get(2));
        Assertions.assertEquals("one", cache.get(1));
        Assertions.assertEquals("three", cache.get(3));
    }

    @Test
    public void testNullValues() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("A", null);
        Assertions.assertNull(cache.get("A"));
    }

    @Test
    public void testMultipleAccessesIncreaseFrequency() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);

        // Access A 5 times
        for (int i = 0; i < 5; i++) {
            cache.get("A");
        }

        // B still has frequency 1, A has frequency 6
        cache.put("C", 3);
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(3, cache.get("C"));
    }

    @Test
    public void testEvictionRemovesLRUofMinFreq() {
        LFUCache<String, Integer> cache = new LFUCache<>(4);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        cache.put("D", 4);

        // Access: A(2), B(2), C(1), D(1)
        cache.get("A");
        cache.get("B");

        // Add E, should evict C (freq 1, oldest among freq 1)
        cache.put("E", 5);
        Assertions.assertNull(cache.get("C"));
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
        Assertions.assertEquals(4, cache.get("D"));
        Assertions.assertEquals(5, cache.get("E"));
    }
}