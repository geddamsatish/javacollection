package com.example;

import java.util.HashMap;

public class LFUCache<K,V> {
    private static class Node<K,V>{
        K key;
        V value;
        int freq;
        Node<K,V> prev;
        Node<K,V> next;
        public Node(K key, V value){
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }
    private static class DLL<K,V>{
        Node<K,V> head;
        Node<K,V> tail;
        public DLL(){
            this.head = null;
            this.tail = null;
        }
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
    private int minfreq;
    private final HashMap<K,Node<K,V>> cache;
    private final HashMap<Integer, DLL<K,V>> freqMap;

    public LFUCache(int capacity){
        this.capacity = capacity;
        this.minfreq = 0;
        this.cache = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    private void updateFrequency(Node<K,V> node){
        int freq = node.freq;
        DLL<K,V> dll = freqMap.get(freq);
        dll.remove(node);
        if(dll.isEmpty() && freq == minfreq){
           minfreq = freq + 1;
        }
        node.freq  = freq + 1;
        freqMap.putIfAbsent(node.freq,  new DLL<>());
        freqMap.get(node.freq).addFirst(node);
    }

    public V get(K key){
        if(!cache.containsKey(key)){
            return null;
        }
        Node<K,V> node = cache.get(key);
        updateFrequency(node);
        return node.value;
    }

    public void put(K key, V value){
        if(capacity<=0) return ;
        if(cache.containsKey(key)){
            Node<K,V> node = cache.get(key);
            node.value = value;
            updateFrequency(node);
            return;
        }

        if(cache.size() >= capacity){
            DLL<K,V> dll = freqMap.get(minfreq);
            Node<K,V> deadNode = dll.tail;
            dll.remove(deadNode);
            cache.remove(deadNode.key);
        }

        Node<K,V> newNode = new Node<>(key, value);
        cache.put(key, newNode);
        minfreq = 1;
        freqMap.putIfAbsent(1, new DLL<>());
        freqMap.get(1).addFirst(newNode);
    }
}
