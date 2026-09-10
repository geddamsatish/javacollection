package com.example;

import java.util.HashMap;

/**
 * Least Recently Used (LRU) Cache implementation using HashMap and Doubly Linked List.
 *
 * Data Structure:
 * - HashMap: O(1) lookup for cache entries
 * - Doubly Linked List: Maintains LRU order where head = most recent, tail = least recent
 *
 * Operations:
 * - get(K): O(1) - retrieve value and move node to head (mark as recently used)
 * - put(K,V): O(1) - add/update entry; evict tail if capacity exceeded
 */
public class LRUCache<K,V> {
    /**
     * Node represents a cache entry in the doubly linked list.
     * Maintains key-value pair and links to prev/next nodes.
     */
    private static class Node<K,V> {
        K key;
        V value;
        Node<K,V> next;  // Link to more recent item
        Node<K,V> prev;  // Link to less recent item
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private final HashMap<K, Node<K, V>> map;  // O(1) lookup
    private Node<K, V> head;  // Most recently used
    private Node<K, V> tail;  // Least recently used (evict candidate)

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    /**
     * Move node to head (most recently used position).
     * Removes node from current position and inserts at head.
     */
    private void moveToHead(Node<K, V> node) {
        if(node == head) return;  // Already at head, nothing to do

        // Step 1: Remove node from current position
        if(node.prev != null) {
           node.prev.next = node.next;
        }
        if(node.next != null) {
            node.next.prev = node.prev;
        }
        if (node == tail) {
            tail = node.prev;  // Update tail if removing tail
        }

        // Step 2: Insert node at head
        node.next = head;
        node.prev = null;
        if(head != null) {
            head.prev = node;
        }
        head = node;
        if(tail == null) {
            tail = head;  // First element case
        }
    }

    /**
     * Get value for key. Returns null if key doesn't exist.
     * Moves accessed node to head (marks as most recently used).
     * Time: O(1)
     */
    public V get(K key) {
        if(!map.containsKey(key)) {
            return null;
        }
        Node<K, V> node = map.get(key);
        moveToHead(node);  // Mark as recently used
        return node.value;
    }

    /**
     * Put key-value pair. Updates value if key exists, evicts LRU item if capacity exceeded.
     * Time: O(1)
     */
    public void put(K key, V value) {
        // Case 1: Update existing key
        if(map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            moveToHead(node);  // Mark as recently used
            return;
        }

        // Case 2: Evict LRU (tail) if at capacity
        if(map.size() >= capacity) {
            map.remove(tail.key);
            if(tail.prev != null) {
               tail.prev.next = null;
               tail = tail.prev;
            } else {
                head = null;
                tail = null;
            }
        }

        // Case 3: Add new key at head
        Node<K, V> node = new Node<>(key, value);
        map.put(key, node);
        if(head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }
}
