package com.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LRUCacheTest {

    @Test
    public void testBasicPutAndGet() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
    }

    @Test
    public void testGetNonExistentKey() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        Assertions.assertNull(cache.get("C"));
    }

    @Test
    public void testEvictionOfLRUItem() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);
        Assertions.assertEquals(1, cache.get("A"));
        cache.put("C", 3);
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(3, cache.get("C"));
    }

    @Test
    public void testEvictionMultipleItems() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // B is least recently used now (A and C accessed)
        cache.get("A");
        cache.get("C");

        // Add D, should evict B
        cache.put("D", 4);
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(3, cache.get("C"));
        Assertions.assertEquals(4, cache.get("D"));
    }

    @Test
    public void testUpdateExistingKey() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        cache.put("A", 100);

        Assertions.assertEquals(100, cache.get("A"));
    }

    @Test
    public void testUpdateExistingKeyMovesToHead() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // Update A, it should move to head (most recently used)
        cache.put("A", 10);

        // Add D, should evict B (least recently used)
        cache.put("D", 4);
        Assertions.assertEquals(10, cache.get("A"));
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(3, cache.get("C"));
        Assertions.assertEquals(4, cache.get("D"));
    }

    @Test
    public void testGetMovesToHead() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // Access A, moving it to head
        cache.get("A");

        // Add D, should evict B (least recently used)
        cache.put("D", 4);
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(3, cache.get("C"));
        Assertions.assertEquals(4, cache.get("D"));
    }

    @Test
    public void testCapacityOne() {
        LRUCache<String, Integer> cache = new LRUCache<>(1);
        cache.put("A", 1);
        Assertions.assertEquals(1, cache.get("A"));

        cache.put("B", 2);
        Assertions.assertNull(cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
    }


    @Test
    public void testComplexEvictionScenario() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);

        // Access sequence to establish usage order
        cache.get("A");  // A is now most recent
        cache.get("B");  // B is now most recent
        // C is least recent

        // Add D, evict C
        cache.put("D", 4);
        Assertions.assertNull(cache.get("C"));

        // Access A
        cache.get("A");  // A is now most recent (B, D, A)
        // B is least recent

        // Add E, evict B
        cache.put("E", 5);
        Assertions.assertNull(cache.get("B"));

        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(4, cache.get("D"));
        Assertions.assertEquals(5, cache.get("E"));
    }

    @Test
    public void testWithDifferentKeyValueTypes() {
        LRUCache<Integer, String> cache = new LRUCache<>(2);
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
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", null);
        Assertions.assertNull(cache.get("A"));
    }

    @Test
    public void testMultipleAccessesAfterEviction() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.get("A");
        cache.get("A");

        cache.put("C", 3);
        Assertions.assertNull(cache.get("B"));
        Assertions.assertEquals(1, cache.get("A"));
        Assertions.assertEquals(3, cache.get("C"));
    }

    @Test
    public void testLRUOrderPreservedAfterMultipleOperations() {
        LRUCache<String, Integer> cache = new LRUCache<>(4);
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        cache.put("D", 4);

        // Access in order: B, C, D (A is least recent)
        cache.get("B");
        cache.get("C");
        cache.get("D");

        // Add E, should evict A
        cache.put("E", 5);
        Assertions.assertNull(cache.get("A"));
        Assertions.assertEquals(2, cache.get("B"));
        Assertions.assertEquals(3, cache.get("C"));
        Assertions.assertEquals(4, cache.get("D"));
        Assertions.assertEquals(5, cache.get("E"));
    }

    @Test
    public void testOriginalTest() {
        LRUCache<String, Integer> cache = new LRUCache<>(2);
        cache.put("A", 1);
        cache.put("B", 2);
        Assertions.assertEquals(1, cache.get("A"));
        cache.put("C", 3);
        Assertions.assertNull(cache.get("B"));
    }
}