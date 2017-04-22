package com.theodinspire;

import edu.stanford.nlp.ling.HasWord;

/**
 * Eric T Cormack
 *
 * NLP Language Models
 *
 * SentenceCap, to implements Stanford's HasWord interface for the ends of sentences
 */

public class SentenceCap implements HasWord {
    private String item;
    
    private SentenceCap(String cap) { item = cap; }
    
    @Override
    public String toString() { return word(); }
    
    @Override
    public String word() { return item; }
    
    @Override
    public void setWord(String s) { item = s; }
    
    public static SentenceCap beginning() { return new SentenceCap("<s>"); }
    public static SentenceCap ending() { return new SentenceCap("</s>"); }
}
