package com.theodinspire;

import java.util.*;

/**
 * Created by corms on 4/10/17.
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
}
