package code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;
import java.io.*;

public class ParserPrototype {
    String author = "";
    String title = "";
    String yearPublished = "";
    String genre = "";
    double wordCount = 0; // Number of words
    double distinctWordsCount = 0; // Number of distinct words
    double punctuationCount = 0; // Number of punctuation marks total
    double sentenceCount = 0; // Number of sentences (counts . ! ?)
    double syllableCount = 0; // Number of syllables
    double fleschScore = 0; // Flesch Reading Ease Score
    double avgWordsPerSentence = 0; // Average words per sentence
    double avgSyllablesPerWord = 0; // Average syllables per words
    double avgWordLength = 0; // Average word length
    
    public ParserPrototype(String author, String title, String yearPublished, String genre) {
    	this.author = author;
    	this.title = title;
    	this.yearPublished = yearPublished;
    	this.genre = genre;
    	
    }

    // Connect to the database
    private Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:example.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    // Insert a record into the database
    public void insertRecord(
        String table,
        String author,
        String title,
        String year,
        String genre,
        double wordCount,
        double distinctWordCount,
        double punctuationCount,
        double sentenceCount,
        double syllableCount,
        double fleschScore,
        double avgWordsPerSentence,
        double avgSyllablesPerWord,
        double avgWordLength,
        String distinctWords,
        String distinctPunctuation
        ) {  
    	
    	
        try{
            Connection conn = connect(); // Connect to database

            // Create query (as prepared statement)
            String sql = "INSERT INTO " + table + " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Set values for prepared statement
            // Note - parameter index 1 is the primary key ID,
            // so it updates automatically if not set
            pstmt.setString(2, author);
            pstmt.setString(3, title);
            pstmt.setString(4, year);
            pstmt.setString(5, genre);
            pstmt.setInt(6, (int)wordCount);
            pstmt.setInt(7, (int)distinctWordCount);
            pstmt.setInt(8, (int)punctuationCount);
            pstmt.setInt(9, (int)sentenceCount);
            pstmt.setInt(10, (int)syllableCount);
            pstmt.setString(11, Double.toString(fleschScore));
            pstmt.setString(12, Double.toString(avgWordsPerSentence));
            pstmt.setString(13, Double.toString(avgSyllablesPerWord));
            pstmt.setString(14, Double.toString(avgWordLength));
            pstmt.setString(15, distinctWords);
            pstmt.setString(16, distinctPunctuation);

            pstmt.executeUpdate(); // Execute the prepared statement
            conn.close(); // Close the database
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Get the hashmap from the database
    // Converts string data from database back into hashmap
    public HashMap<String, Integer> retrieveHashMapByAuthorTitle(String table, String field, String author, String title) {  

        // Create the hashmap again
        HashMap<String, Integer> myHashMap = new HashMap<String, Integer>();

        try{
            Connection conn = connect(); // Connect to the database

            // Create and execute the SQL query, store the results
            String sql = "SELECT " + field + " FROM " + table + " WHERE AUTHOR=\'" + author + "\' AND TITLE=\'" + title + "\'";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            // Get the results as a string
            String resultStr = result.getString(field);

            // This line removes the curly brackets from the string data { }
            resultStr = resultStr.substring(1, resultStr.length() - 1);

            // Split the data between comma/space ", "
            String[] resultArr = resultStr.split(", ");

            // For each word/value combo, split and add to hashmap
            for (int i = 0; i < resultArr.length; i++) {
                String pair = resultArr[i];
                String[] pairArr = pair.split("=");
                String key = pairArr[0];
                int value = Integer.parseInt(pairArr[1]);
                
                myHashMap.put(key, value);
            }
            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return myHashMap; // Return the hashmap
    }

    // Testing out retrieving a full record from the database
    // Returns the record as a string array
    public String[] retrieveRecordByAuthorTitle(String table, String author, String title) {

        String[] resultStr = new String[16]; // String to hold the returned results

        try{
            Connection conn = connect(); // Connect to the database

            // Create and execute the SQL query, store the results
            String sql = "SELECT * FROM " + table + " WHERE AUTHOR=\'" + author + "\' AND TITLE=\'" + title + "\'";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            // Get the results as a string
            result.next();

            for (int i = 1; i < 17; i++) {
                resultStr[i-1] = result.getString(i);
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultStr; // Return the result string
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
        }
        else {
            // If does not contain word, add word with counter=1
            hashmap.put(token, 1);
            return true; // TRUE if token does not exist
        }
    }

    // Parser prototype functionality
    // Does distinct words, sentence count, etc.
    // Distinct Words Count adapted from code found at https://algs4.cs.princeton.edu/code/edu/princeton/cs/algs4/FrequencyCounter.java.html
    public void parseDoc() {
        String currentToken = ""; // Will hold the current word/punctuation being parsed
        SyllableCounter sylCounter = new SyllableCounter(); // Create the syllable counter object

        // Distinct words hashmap AND distinct punctuation hashmap
        HashMap<String, Integer> distinctWordsMap = new HashMap<String, Integer>();
        HashMap<String, Integer> punctuationCountMap = new HashMap<String, Integer>();

        try {
            // Prompt user to enter in author, title, year, and genre

            // Create a bufferedreader with the file (hardcoded for my file system)
            // Give the bufferedreader to the tokenizer to extract individual words/punctuation

            String filePath ="";
    		
            JFrame jf = new JFrame("Dialog");
            jf.setAlwaysOnTop(true);
            
    		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    		int returnValue = jfc.showOpenDialog(jf);
    		
    		if(returnValue == JFileChooser.APPROVE_OPTION) {
    			 filePath = jfc.getSelectedFile().getAbsolutePath();
    		}
    		
    		jf.dispose();
            
            BufferedReader file = new BufferedReader(new FileReader(filePath));
            Tokenizer tokenizer = new Tokenizer(file);

            // Load the first token by calling the Tokenizer's method
            currentToken = tokenizer.getNextToken();

            // While token isn't END_OF_FILE...
            while (currentToken != "END_OF_FILE") {
                // If currentToken is made of letters (so ignores numbers and punctuation)
                if (Character.isLetter(currentToken.charAt(0))) {
                    wordCount++; // Increment total word count
                    syllableCount += sylCounter.countSyllables(currentToken); // Add word syllable count to total syllable count

                    // Add EACH word length to avgWordLength
                    // This is turned into the average later
                    avgWordLength += currentToken.length();

                    // Check if word is already in the hashmap
                    // Updates hashmap appropriately
                    if (updateHashMap(distinctWordsMap, currentToken)) {
                        distinctWordsCount++; // Increment distinct word count
                    }

                // If token is not a word (so punctuation and numbers parsed here)
                } else {
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
                // Get next token before looping again
                currentToken = tokenizer.getNextToken();
            }

            // Calculate the averages
            avgWordLength = avgWordLength/wordCount;
            avgWordsPerSentence = wordCount/sentenceCount;
            avgSyllablesPerWord = syllableCount/wordCount;

            // Formula found at https://readabilityformulas.com/flesch-reading-ease-readability-formula.php
            fleschScore = 206.835 - (1.015*avgWordsPerSentence) - (84.6*avgSyllablesPerWord);
            
            //decimal fromat for rounding
            DecimalFormat df = new DecimalFormat("0.00");
            
            System.out.println("Author: " + author);
            System.out.println("Title: " + title);
            System.out.println("Year Published: " + yearPublished);
            System.out.println("Genre: " + genre);
            System.out.println("Word Count: " + wordCount);
            System.out.println("Distinct Word Count: " + distinctWordsCount);
            System.out.println("Punctuation Count: " + punctuationCount);
            System.out.println("Sentence Count: " + sentenceCount);
            System.out.println("Syllable Count: " + syllableCount);
            System.out.println("Flesch Reading Ease Score: " + df.format(fleschScore));
            System.out.println("AVG Word Per Sentence: " + df.format(avgWordsPerSentence));
            System.out.println("AVG Syllables Per Word: " + df.format(avgSyllablesPerWord));
            System.out.println("AVG Word Length: " + df.format(avgWordLength) + "\n");
            
            /*
            //Insert record of document into database
            insertRecord(
                "records", // Name of the table
                author,
                title,
                yearPublished,
                genre,
                wordCount,
                distinctWordsCount,
                punctuationCount,
                sentenceCount,
                syllableCount,
                fleschScore,
                avgWordsPerSentence,
                avgSyllablesPerWord,
                avgWordLength,
                distinctWordsMap.toString(),
                punctuationCountMap.toString()
            );
            */

        } catch (FileNotFoundException ex) {
            System.out.println("File was not found.");
        }
    }
    

    
}