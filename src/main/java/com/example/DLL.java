package com.example;

import org.w3c.dom.Node;

public class DLL<K,V> {
    private static class Node<K,V>{
        K  key;
        V value;
        Node<K,V> next;
        Node<K,V> prev;
        Node(K key, V value){
            this.key = key;
            this.value = value;
        }
    }

    private Node<K,V> head;
    private Node<K,V> tail;
    public void append(K key, V Value){
        Node<K,V> node = new Node<>(key, Value);
        if(head == null){
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

}
