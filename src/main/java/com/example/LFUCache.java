package com.example;

import java.util.HashMap;

/**
 * Least Frequently Used (LFU) Cache implementation.
 *
 * Data Structure:
 * - cache HashMap: Maps key -> Node for O(1) lookup
 * - freqMap HashMap: Maps frequency -> DLL of nodes with that frequency
 * - minfreq: Tracks minimum frequency for eviction
 * - DLL (Doubly Linked List): Maintains LRU order within same frequency
 *   (head = most recent, tail = least recent for same frequency)
 *
 * Eviction Strategy:
 * 1. Remove node with minimum frequency
 * 2. If tie (multiple nodes with minfreq), remove least recently used (tail)
 *
 * Time Complexity: O(1) for get and put operations
 */
public class LFUCache<K,V> {
    /**
     * Node represents a cache entry with frequency and LRU links.
     */
    private static class Node<K,V>{
        K key;
        V value;
        int freq;  // Access frequency
        Node<K,V> prev;  // Link to less recent item (same frequency)
        Node<K,V> next;  // Link to more recent item (same frequency)
        public Node(K key, V value){
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    /**
     * Doubly Linked List maintains nodes of same frequency in LRU order.
     * head = most recently used, tail = least recently used
     */
    private static class DLL<K,V>{
        Node<K,V> head;
        Node<K,V> tail;
        public DLL(){
            this.head = null;
            this.tail = null;
        }
        /** Add node at head (most recently used position) */
        public void addFirst(Node<K,V> node){
            node.next = head;
            node.prev = null;
            if(head!=null){
                head.prev = node;
            }
            head = node;
            if(tail == null){
                tail = head;
            }
        }
        public boolean isEmpty() {
            return head == null;
        }
        /** Remove node from list, updating neighbors */
        public void remove(Node<K,V> node){
            if(node.prev != null){
                node.prev.next = node.next;
            } else {
                head = node.next;
            }
            if(node.next != null){
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }
            node.next = null;
            node.prev = null;
        }
    }
    private final int capacity;
    private int minfreq;  // Minimum frequency for eviction
    private final HashMap<K,Node<K,V>> cache;  // O(1) key lookup
    private final HashMap<Integer, DLL<K,V>> freqMap;  // O(1) frequency lookup

    public LFUCache(int capacity){
        this.capacity = capacity;
        this.minfreq = 0;
        this.cache = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    /**
     * Increment frequency of node and move it to appropriate frequency list.
     * If old frequency list becomes empty and was minfreq, increment minfreq.
     * Time: O(1)
     */
    private void updateFrequency(Node<K,V> node){
        int freq = node.freq;
        DLL<K,V> dll = freqMap.get(freq);
        dll.remove(node);
        // Update minfreq if current frequency bucket is empty
        if(dll.isEmpty() && freq == minfreq){
           minfreq = freq + 1;
        }
        node.freq = freq + 1;
        // Add node to new frequency bucket at head (most recent)
        freqMap.putIfAbsent(node.freq, new DLL<>());
        freqMap.get(node.freq).addFirst(node);
    }

    /**
     * Get value for key. Returns null if not found.
     * Increments access frequency of the key.
     * Time: O(1)
     */
    public V get(K key){
        if(!cache.containsKey(key)){
            return null;
        }
        Node<K,V> node = cache.get(key);
        updateFrequency(node);
        return node.value;
    }

    /**
     * Put key-value pair. Updates if exists, evicts LFU+LRU if capacity exceeded.
     * Eviction: Removes node with minfreq; if tie, removes tail (least recently used).
     * Time: O(1)
     */
    public void put(K key, V value){
        if(capacity <= 0) return;  // Invalid capacity

        // Case 1: Update existing key
        if(cache.containsKey(key)){
            Node<K,V> node = cache.get(key);
            node.value = value;
            updateFrequency(node);
            return;
        }

        // Case 2: Evict LFU+LRU if at capacity
        if(cache.size() >= capacity){
            DLL<K,V> dll = freqMap.get(minfreq);
            Node<K,V> deadNode = dll.tail;  // Least recently used in minfreq list
            dll.remove(deadNode);
            cache.remove(deadNode.key);
        }

        // Case 3: Add new key with frequency 1
        Node<K,V> newNode = new Node<>(key, value);
        cache.put(key, newNode);
        minfreq = 1;
        freqMap.putIfAbsent(1, new DLL<>());
        freqMap.get(1).addFirst(newNode);
    }
}
