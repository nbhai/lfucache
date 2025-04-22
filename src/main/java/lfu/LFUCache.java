package lfu;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUCache {
    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> keyNodeMap;
    private final Map<Integer, LinkedHashSet<Node>> freqNodeMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyNodeMap = new HashMap<>();
        this.freqNodeMap = new HashMap<>();
    }

    public int get(int key) {
        if (!keyNodeMap.containsKey(key)) {
            return -1; // Key not found
        }
        Node node = keyNodeMap.get(key);
        updateFrequency(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return; // Capacity is zero, do nothing
        }
        if (keyNodeMap.containsKey(key)) {
            Node node = keyNodeMap.get(key);
            node.value = value;
            updateFrequency(node);
        } else {
            if (keyNodeMap.size() >= capacity) {
                LinkedHashSet<Node> minFreqSet = freqNodeMap.get(minFreq);
                Node evict = minFreqSet.iterator().next(); // LRU from LFU group
                minFreqSet.remove(evict);
                if (minFreqSet.isEmpty()) {
                    freqNodeMap.remove(minFreq);
                }
                keyNodeMap.remove(evict.key);
            }

            Node newNode = new Node(key, value);
            keyNodeMap.put(key, newNode);
            LinkedHashSet<Node> newSet = freqNodeMap.getOrDefault(1, new LinkedHashSet<>());
            newSet.add(newNode);
            freqNodeMap.put(1, newSet);
//            freqNodeMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(newNode);
            minFreq = 1;
        }
    }
    public void updateFrequency(Node node) {
        int oldFreq = node.freq;
        LinkedHashSet<Node> oldSet = freqNodeMap.get(oldFreq);
        oldSet.remove(node);
        if (oldSet.isEmpty()) {
            freqNodeMap.remove(oldFreq);
            if (minFreq == oldFreq) {
                minFreq++;
            }
        }

        node.freq++;
        LinkedHashSet<Node> newSet = freqNodeMap.getOrDefault(node.freq, new LinkedHashSet<>());
        newSet.add(node);
        freqNodeMap.put(node.freq, newSet);
//        freqNodeMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }
}


