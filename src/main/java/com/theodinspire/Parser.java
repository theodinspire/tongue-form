package com.theodinspire;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by corms on 4/10/17.
 */
public class Parser {
    public static List<String> parseDocToSentences(String filename) {
        String content;
        
        try {
            content = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("Failed to open and read file");
            e.printStackTrace();
            
            content = "";
            System.exit(-1);
        }
        
        BreakIterator iterator = BreakIterator.getSentenceInstance();
        iterator.setText(content);
        
        List<String> sentences = new LinkedList<>();
        
        for (int start = iterator.first(), end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String sentence = content.substring(start, end).trim();
            if (!sentence.equalsIgnoreCase("")) { sentences.add(sentence); }
        }
        
        return sentences;
    }
    
    public static List<String> parseSentenceToWords(String sentence) {
        List<String> words = new LinkedList<>();
        
        BreakIterator iterator = BreakIterator.getWordInstance();
        iterator.setText(sentence);
    
        for (int start = iterator.first(), end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String word = sentence.substring(start, end).trim();
            if (word.compareTo("") != 0) { words.add(word); }
        }
        
        return words;
    }
    
    private Parser() { }
}
