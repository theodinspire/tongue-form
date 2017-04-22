package com.theodinspire;

import java.util.*;

/**
 * Eric T Cormack
 *
 * NLP Language Models
 *
 * Counter, an object that counts instances of things
 */
public class Counter<T> {
    protected Map<T, Integer> map;
    protected int size = 0;
    
    public Counter() {
        map = new HashMap<T, Integer>();
    }
    
    public boolean count(T key) {
        ++size;
        if (map.containsKey(key)) {
            map.put(key, map.get(key) + 1);
            return true;
        } else {
            map.put(key, 1);
            return false;
        }
    }
    
    public int getCount(T key) { return map.getOrDefault(key, 0); }
    
    public int total() { return size; }
    
    public Set<T> getKeySet() { return map.keySet(); }
    
    public List<T> getKeysAsList() {
        return new LinkedList<>(getKeySet());
    }
    
    public void addCounts(Counter<T> toAdd) {
        for (T key : toAdd.map.keySet()) {
            int addedCount = toAdd.map.get(key);
            map.put(key, addedCount + map.getOrDefault(key, 0));
            size += addedCount;
        }
    }
}
