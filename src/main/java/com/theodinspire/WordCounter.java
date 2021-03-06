package com.theodinspire;

import java.util.HashSet;
import java.util.Set;

/**
 * Eric T Cormack
 *
 * NLP Language Models
 *
 * WordCounter, for counting words and trimming unknowns
 */
public class WordCounter extends Counter<String> {
    public static final String unknownToken = "<UNK>";
    
    public Set<String> trimSmallCounts(int threshold) {
        if (threshold <= 1) return new HashSet<>();
        
        HashSet<String> trimmedWords = new HashSet<>();
        
        int unknownCount = 0;
        
        Set<String> keys = new HashSet<>(map.keySet());
        
        for (String key : keys) {
            int keyCount = map.get(key);
            if (keyCount < threshold) {
                unknownCount += keyCount;
                map.remove(key);
                trimmedWords.add(key);
            }
        }
        
        // Don't put in unknowns if absent
        if (unknownCount > 0) {
            map.put(unknownToken, unknownCount + map.getOrDefault(unknownToken, 0));
        }
        
        return trimmedWords;
    }
    
    public void setUnknowns(Set<String> candidates) {
        int unknownCount = 0;
        
        for (String candidate : candidates) {
            if (map.containsKey(candidate)) {
                unknownCount += map.remove(candidate);
            }
        }
        
        if (unknownCount > 0) {
            map.put(unknownToken, unknownCount + map.getOrDefault(unknownToken, 0));
        }
    }
}
