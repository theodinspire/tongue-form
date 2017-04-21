package com.theodinspire;

import edu.stanford.nlp.ling.HasWord;

import java.util.Comparator;
import java.util.List;

/**
 * Created by corms on 4/10/17.
 */
public class UnigramDistribution {
    private WordCounter counter;
    private boolean closed;
    private int N = 0;
    
    public UnigramDistribution() {
        counter = new WordCounter();
        closed = false;
    }
    
    public int getN() {
        return N;
    }
    
    public int getUniqueN() {
        return counter.getKeySet().size();
    }
    
    public boolean add(String word) {
        if (closed) return false;
        
        N += 1;
        return counter.count(word);
    }
    
    public boolean add(HasWord word) {
        return add(word.word());
    }
    
    public void trimUnknowns(int threshold) {
        counter.trimSmallCounts(threshold);
    }
    
    public void close() {
        closed = true;
    }
    
    public double probabilityOf(String key) {
        if (counter.total() == 0) return 0;
        else if (!counter.getKeySet().contains(key))
            return (double) counter.getCount(WordCounter.unknownToken) / (double) counter.total();
        else return (double) counter.getCount(key) / (double) counter.total();
    }
    
    public double probabilityOf(HasWord key) {
        return probabilityOf(key.word());
    }
    
    public String toVerboseString() {
        List<String> keys = counter.getKeysAsList();
        
        keys.sort(Comparator.naturalOrder());
        
        StringBuilder builder = new StringBuilder();
        
        for (String key : keys) {
            double prob = probabilityOf(key);
            builder.append(String.format("%16s : %f\n", key, prob));
        }
        
        return builder.toString();
    }
}
