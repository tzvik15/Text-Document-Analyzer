package code;

import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

public class Parser {
    double wordCount = 0; // Number of words
    double sentenceCount = 0; // Number of sentences (counts . ! ?)
    double avgWordLength = 0; // Average word length
    double avgWordsPerSentence = 0; // Average words per sentence
    double punctuationCount = 0; // Number of punctuation marks total

    double distinctWordsCount = 0; // Number of distinct words
    double syllableCount = 0; // Number of syllables
    double fleschScore = 0; // Flesch Reading Ease Score
    double avgSyllablesPerWord = 0; // Average syllables per words
    // double avgWordLength = 0; // Average word length
    HashMap<String, Integer> distinctWordsMap;
    HashMap<String, Integer> punctuationCountMap;

    // variable GET methods

    protected String getWordHash() {
        return distinctWordsMap.toString();
    }

    protected String getPunctuationHash() {
        return punctuationCountMap.toString();
    }

    protected double getWordCount() {
        return wordCount;
    }

    protected double getDistinctWordsCount() {
        return distinctWordsCount;
    }

    protected double getPunctuationCount() {
        return punctuationCount;
    }

    protected double getSentenceCount() {
        return sentenceCount;
    }

    protected double getSyllableCount() {
        return syllableCount;
    }

    protected double getFleschScore() {
        return fleschScore;
    }

    protected double getAvgWordsPerSentence() {
        return avgWordsPerSentence;
    }

    protected double getAvgSyllablesPerWord() {
        return avgSyllablesPerWord;
    }

    protected double getAvgWordLength() {
        return avgWordLength;
    }

    // Checks if token is in hashmap
    // Adds token to hashmap OR increments counter if it's already in there
    // Can be used for both distinct words and punctuation hashmap
    public boolean updateHashMap(HashMap<String, Integer> hashmap, String token) {
        // Check if word is already in the hashmap
        if (hashmap.containsKey(token)) {
            // If already contains word, increment counter
            hashmap.put(token, hashmap.get(token) + 1);
            return false; // FALSE if token already exists
        } else {
            // If does not contain word, add word with counter=1
            hashmap.put(token, 1);
            return true; // TRUE if token does not exist
        }
    }

    // Parser prototype functionality
    // Does distinct words, sentence count, etc.
    // Distinct Words Count adapted from code found at
    // https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/FrequencyCounter.java.html
    public boolean parseDoc(BufferedReader f) {
        String currentToken = ""; // Will hold the current word/punctuation being parsed
        SyllableCounter sylCounter = new SyllableCounter(); // Create the syllable counter object

        // Distinct words hashmap AND distinct punctuation hashmap
        distinctWordsMap = new HashMap<String, Integer>();
        punctuationCountMap = new HashMap<String, Integer>();

        try {
            // Tokenizer tokenizer = new Tokenizer(file);
            Tokenizer tokenizer = new Tokenizer(f);

            // Load the first token by calling the Tokenizer's method
            currentToken = tokenizer.getNextToken();

            // While token isn't END_OF_FILE...
            while (currentToken != "END_OF_FILE") {
                // If currentToken is made of letters (so ignores numbers and punctuation)
                if (Character.isLetter(currentToken.charAt(0))) {
                    wordCount++; // Increment total word count
                    syllableCount += sylCounter.countSyllables(currentToken); // Add word syllable count to total
                                                                              // syllable count

                    // Add EACH word length to avgWordLength
                    // This is turned into the average later
                    avgWordLength += currentToken.length();

                    // Check if word is already in the hashmap
                    // Updates hashmap appropriately
                    if (updateHashMap(distinctWordsMap, currentToken)) {
                        distinctWordsCount++; // Increment distinct word count
                    }

                    // If token is not a word or a number parsed here        
                    } else if(!Character.isDigit(currentToken.charAt(0))){
                        punctuationCount++; // Increment total punctuation count

                        // If currentToken is a period, exclamation point, or question mark
                        if (currentToken.charAt(0) == '.' || currentToken.charAt(0) == '!' || currentToken.charAt(0) == '?') {
                            // This counts full sentences, NOT LINE BREAKS
                            // So poems without punctuation may be iffy
                            sentenceCount++;
                        }

                        // Check if punctuation is already in the hashmap
                        // Updates hashmap appropriately
                        updateHashMap(punctuationCountMap, currentToken);
                    }
                    
                    //maybe add in count for numbers in text
                    
                // Get next token before looping again
                currentToken = tokenizer.getNextToken();
            }
            
         // Decimal format for rounding
            DecimalFormat df = new DecimalFormat("0.00");

            // Calculate the averages
            avgWordLength = Double.parseDouble(df.format(avgWordLength / wordCount));
            avgWordsPerSentence = Double.parseDouble(df.format(wordCount / sentenceCount));
            avgSyllablesPerWord = Double.parseDouble(df.format(syllableCount / wordCount));

            // Formula found at
            // https://readabilityformulas.com/flesch-reading-ease-readability-formula.php
            fleschScore = Double.parseDouble(df.format(206.835 - (1.015 * avgWordsPerSentence) - (84.6 * avgSyllablesPerWord)));

            return true;

        } catch (FileNotFoundException ex) {
            return false; // Return false if bad thing happen
        }
    }
}