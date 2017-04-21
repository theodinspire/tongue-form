package com.theodinspire;

import edu.stanford.nlp.ling.HasWord;

import java.util.*;

/**
 * Created by corms on 4/12/17.
 */
public class BigramDistribution {
    private Map<String, WordCounter> map;
    private WordCounter firstsCounter;
    private boolean closed;
    
    private int N = 0;
    
    public BigramDistribution() {
        map = new HashMap<>();
        firstsCounter = new WordCounter();
        closed = false;
    }
    
    public int getN() {
        return N;
    }
    
    public int getUniqueN() {
        return map.size();
    }
    
    public boolean add(String first, String second) {
        if (closed) return false;
        
        firstsCounter.count(first);
        
        //  else, implied
        N += 1;
        
        if (map.containsKey(first)) {
            return map.get(first).count(second);
        } else {
            WordCounter counter = new WordCounter();
            boolean result = counter.count(second);
            map.put(first, counter);
            return result;
        }
    }
    
    public boolean add(HasWord first, HasWord second) { return add(first.word(), second.word()); }
    public boolean add(String first, HasWord second) { return add(first, second.word()); }
    public boolean add(HasWord first, String second) { return add(first.word(), second); }
    
    public void trimUnknowns(int threshold) {
        Set<String> trimmed = firstsCounter.trimSmallCounts(threshold);
        
        WordCounter unknownFirst = new WordCounter();
        
        Set<String> keys = new HashSet<>(map.keySet());
        
        for (String key : keys) {
            WordCounter keyedCounter = map.get(key);
            keyedCounter.setUnknowns(trimmed);
            
            if (trimmed.contains(key)) {
                unknownFirst.addCounts(keyedCounter);
                map.remove(key);
            }
        }
        
        if (unknownFirst.size > 0) {
            firstsCounter.map.put(WordCounter.unknownToken, unknownFirst.total());
            map.put(WordCounter.unknownToken, unknownFirst);
        }
    }
    
    public void close() { closed = true; }
    
    ///
    public double probablilityOf(String first, String second) {
        String fore = firstsCounter.getKeySet().contains(first) ? first : WordCounter.unknownToken;
        String aft = map.keySet().contains(second) || second == "</s>" ? second : WordCounter.unknownToken;
    
        Counter<String> counter = map.get(fore);
        double num = counter.getCount(aft);
        double den = counter.total();
        return num / den;
    }
    
    public double probablilityOf(HasWord first, HasWord second) {
        return probablilityOf(first.word(), second.word());
    }
    
    public double probabilityLaplace(String first, String second) {
        String fore = firstsCounter.getKeySet().contains(first) ? first : WordCounter.unknownToken;
        String aft = map.keySet().contains(second) ? second : WordCounter.unknownToken;
        
        Counter<String> counter = map.get(fore);
        double num = counter.getCount(aft) + 1;
        double den = counter.total() + map.size();
        return num / den;
    }
    
    public double probabilityLaplace(HasWord first, HasWord second) {
        return probabilityLaplace(first.word(), second.word());
    }
    
    public String toVerboseString() {
        List<String> firsts = firstsCounter.getKeysAsList();
        firsts.sort(Comparator.naturalOrder());
        
        StringBuilder builder = new StringBuilder();
        
        for (String prime : firsts) {
            Counter<String> counter = map.getOrDefault(prime, map.get(WordCounter.unknownToken));
            List<String> seconds = counter.getKeysAsList();
            seconds.sort(Comparator.naturalOrder());
            
            for (String second : seconds) {
                builder.append(
                        String.format("%15s -> %-15s : %2.6f, LaPlace: %2.6f\n",
                                      prime, second, probablilityOf(prime, second), probabilityLaplace(prime, second)));
            }
            
            builder.append("--------------------------------------------------------------------\n");
        }
        
        return builder.toString();
    }
}
