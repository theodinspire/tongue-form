package com.theodinspire;

import edu.stanford.nlp.ling.HasWord;

/**
 * Created by corms on 4/13/17.
 */
public class SentenceCap implements HasWord {
    private String item;
    
    private SentenceCap(String cap) { item = cap; }
    
    @Override
    public String word() { return item; }
    
    @Override
    public void setWord(String s) { item = s; }
    
    public static SentenceCap beginning() { return new SentenceCap("<s>"); }
    public static SentenceCap ending() { return new SentenceCap("</s>"); }
}
