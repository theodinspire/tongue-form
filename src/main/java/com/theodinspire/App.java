package com.theodinspire;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Eric T Cormack
 *
 * NLP Language Models
 *
 * App, main class for assignment
 */
public class App 
{
    static String trainingText = "HG-train.txt";
    static String testingText = "HG-test.txt";
    static String output = "output-HG.txt";
    
    static UnigramDistribution unigram = new UnigramDistribution();
    static BigramDistribution bigram = new BigramDistribution();
    
    public static void main( String[] args ) {
        // Parse input string
        handleArgs(args);
        
        // Train
        train(trainingText);
        
        // Test and create output
        test(testingText, output);
    }
    
    static void handleArgs(String[] args) {
        List<String> arguments = Arrays.asList(args);
        
        if (arguments.contains("-h")) printHelpAndExit();
        
        int inputIndex = arguments.indexOf("-i");
        int testIndex = arguments.indexOf("-t");
        int outputIndex = arguments.indexOf("-o");
        
        if (inputIndex < 0 || inputIndex >= arguments.size())
            printHelpAndExit();
        else {
            trainingText = arguments.get(inputIndex + 1);
            if (trainingText.matches("-[itoh]")) printHelpAndExit();
        }
        
        if (testIndex < 0 || testIndex >= arguments.size())
            printHelpAndExit();
        else {
            testingText = arguments.get(testIndex + 1);
            if (testingText.matches("-[itoh]")) printHelpAndExit();
        }
        
        if (outputIndex < 0 || outputIndex >= arguments.size())
            output = "output-" + testingText;
        else {
            output = arguments.get(outputIndex + 1);
            if (output.matches("-[itoh]")) printHelpAndExit();
        }
    }
    
    static void train(String filename) {
        DocumentPreprocessor train = new DocumentPreprocessor(filename, DocumentPreprocessor.DocType.Plain, "UTF-8");
    
        for (List<HasWord> sentence : train) {
            HasWord first;
            HasWord second = SentenceCap.beginning();
        
            for (HasWord word : sentence) {
                first = second;
                second = word;
            
                unigram.add(second);
                bigram.add(first, second);
            }
        
            first = second;
            second = SentenceCap.ending();
        
            unigram.add(second);
            bigram.add(first, second);
        }
    
        System.out.println("Token count: " + unigram.getN());
        System.out.println("Vocabulary size: " + unigram.getUniqueN());
        
        unigram.trimUnknowns(10);
        bigram.trimUnknowns(10);
    
        unigram.close();
        bigram.close();
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("wordtypes-" + filename))) {
            writer.write(unigram.toVerboseString());
            writer.flush();
        } catch (Exception e) {
            System.out.println("Failed to write word-types");
            e.printStackTrace();
        }
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bigrams-" + filename))) {
            writer.write(bigram.toVerboseString());
            writer.flush();
        } catch (Exception e) {
            System.out.println("Failed to write bigrams");
            e.printStackTrace();
        }
    }
    
    static void printHelpAndExit() {
        System.out.println("This is the help text");
        
        System.exit(0);
    }
    
    static void test(String testFilename, String outFilename) {
        DocumentPreprocessor test = new DocumentPreprocessor(testFilename);
        int i = 0;
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outFilename))) {
            for (List<HasWord> sentence : test) {
                ++i;
            
                // Print sentence
                writer.write("Sentence " + i + ": ");
                sentence.forEach((a) -> {
                    try { writer.write(a + " "); }
                    catch (Exception e) { System.out.println("Oops, didn't write " + a); } });
                writer.write("\n");
            
                // Initialize probability
                double uniLogProb = 0;
                double biLogProb = 0;
                double laplaceLog = 0;
            
                // Initalize words
                HasWord first;
                HasWord second = SentenceCap.beginning();
            
                for (HasWord word : sentence) {
                    first = second;
                    second = word;
                
                    if (uniLogProb != Double.NEGATIVE_INFINITY)
                        uniLogProb += Math.log(unigram.probabilityOf(second));
                    if (biLogProb != Double.NEGATIVE_INFINITY)
                        biLogProb += Math.log(bigram.probablilityOf(first, second));
                    if (laplaceLog != Double.NEGATIVE_INFINITY)
                        laplaceLog += Math.log(bigram.probabilityLaplace(first, second));
                }
            
                uniLogProb += Math.log(unigram.probabilityOf(SentenceCap.ending()));
                biLogProb += Math.log(bigram.probablilityOf(second, SentenceCap.ending()));
                laplaceLog += Math.log(bigram.probabilityLaplace(second, SentenceCap.ending()));
                
                double uniPerpPower = -1 / ((double) sentence.size() + 1);
                double biPerpPower = uniPerpPower;
            
                double uniProb = Math.exp(uniLogProb);
                double biProb = Math.exp(biLogProb);
                double laplace = Math.exp(laplaceLog);
            
                double uniPerp = Math.pow(uniProb, uniPerpPower);
                double biPerp = Math.pow(biProb, biPerpPower);
                double laPlerp = Math.pow(laplace, biPerpPower);
            
                writer.write(String.format(" - unigram [Prob] %-16G [Perp] %-16G\n", uniProb, uniPerp));
                writer.write(String.format(" - bigram  [Prob] %-16G [Perp] %-16G\n", biProb, biPerp));
                writer.write(String.format(" - laplace [Prob] %-16G [Perp] %-16G\n\n", laplace, laPlerp));
            
                writer.flush();
            }
        } catch (IOException io) {
            System.out.println("Could not write output file");
            io.printStackTrace();
            System.exit(-1);
        }
    }
}
