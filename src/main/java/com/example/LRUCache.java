package com.example;

import java.util.HashMap;

public class LRUCache<K,V> {
    private static class Node<K,V> {
        K key;
        V value;
        Node<K,V> next;
        Node<K,V> prev;
        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    private final int capacity;
    private final HashMap<K, Node<K, V>> map;
    private Node<K, V> head;
    private Node<K, V> tail;
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    private void moveToHead(Node<K, V> node) {
        //if its same as head nothing to do
        if(node == head){
            return;
        }

        // if a connection exists remove and update prev pointer
        if(node.prev != null){
           node.prev.next = node.next;
        }
        // remove and update the next pointer
        if(node.next != null){
            node.next.prev = node.prev;
        }

        // if node was tail, update the tail pointer to prev
        if (node == tail){
            tail  = node.prev;
        }
        // now insert the node.
        node.next = head;
        node.prev = null;
        // if head exists connect the pointers.
        if(head != null){
            head.prev = node;
        }
        head = node;
        // if tail doesn't exist set it as head
        if(tail == null) {
            tail = head;
        }
    }

    public V get(K key) {
        if(!map.containsKey(key)){
            return null;
        }
        Node<K, V> node = map.get(key);
        moveToHead(node);
        return node.value;
    }
    public void put(K key, V value) {
        // exisiting key
        if(map.containsKey(key)){
            Node<K, V> node = map.get(key);
            node.value = value;
            moveToHead(node);
            return;
        }
        // eviction
        if(map.size() >= capacity){
            map.remove(tail.key);
            if(tail.prev !=null){
               tail.prev.next = null;
               tail = tail.prev;
            } else {
                head = null;
                tail = null;
            }
        }
        // new key
        Node<K, V> node = new Node<>(key, value);
        map.put(key, node);
        if(head == null){
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }
}
