package code;

import java.util.*;

// Table 'records' has these fields:
    // ID INTEGER PRIMARY KEY AUTOINCREMENT,  <---if value is null, auto increments!
    // AUTHOR TEXT,
    // TITLE TEXT,
    // PUBLISH_YEAR TEXT,
    // GENRE TEXT,
    // WORD_COUNT INT,
    // DISTINCT_WORD_COUNT INT,
    // PUNCTUATION_COUNT INT,
    // SENTENCE_COUNT INT,
    // SYLLABLE_COUNT INT,
    // FLESCH_SCORE TEXT,
    // AVG_WORDS_PER_SENTENCE TEXT,
    // AVG_SYLLABLES_PER_WORD TEXT,
    // AVG_WORD_LENGTH TEXT,
    // DISTINCT_WORDS TEXT,         <---distinct words map as string
    // DISTINCT_PUNCTUATION TEXT    <---distinct punctuation map as string

public class Main {

    public static void main(String[] args) {
    	
    	//prompt user to enter author, title, yearPublished, and Genre
        String author, title, yearPublished, genre;
  
        Scanner scan = new Scanner(System.in);
    	
    	System.out.println("Please enter author: ");
        author = scan.nextLine();
        
        System.out.println("Please enter book title: ");
        title = scan.nextLine();
        
        System.out.println("Please enter year: ");
        yearPublished = scan.nextLine();
        
        System.out.println("Please enter genre: ");
        genre = scan.nextLine();
        
        scan.close();
        
        ParserPrototype parser = new ParserPrototype(author, title, yearPublished, genre); // Create the parser object
        parser.parseDoc(); // Call the method to parse the document

        // After parsing, a record now exists in the database
        // The functions below retrieve data from that record
        
        /*
        // Testing the retrieveRecordByAuthorTitle function
        String[] results = parser.retrieveRecordByAuthorTitle("records", author, title);
        
        System.out.println("ID: " + results[0]);
        System.out.println("Author: " + results[1]);
        System.out.println("Title: " + results[2]);
        System.out.println("Year Published: " + results[3]);
        System.out.println("Genre: " + results[4]);
        System.out.println("Word Count: " + results[5]);
        System.out.println("Distinct Word Count: " + results[6]);
        System.out.println("Punctuation Count: " + results[7]);
        System.out.println("Sentence Count: " + results[8]);
        System.out.println("Syllable Count: " + results[9]);
        System.out.println("Flesch Reading Ease Score: " + results[10]);
        System.out.println("AVG Word Per Sentence: " + results[11]);
        System.out.println("AVG Syllables Per Word: " + results[12]);
        System.out.println("AVG Word Length: " + results[13] + "\n");

        // These results are type String
        System.out.println("Distinct Words: " + results[14] + "\n");
        System.out.println("Distinct Punctuation: " + results[15]);

        // These results are type Hashmap (same results as above, but different data type)
        HashMap<String, Integer> distinctWordHashmap = parser.retrieveHashMapByAuthorTitle("records", "DISTINCT_WORDS", author, title);
        HashMap<String, Integer> distinctPunctuationHashmap = parser.retrieveHashMapByAuthorTitle("records", "DISTINCT_PUNCTUATION", author, title);
        System.out.println(distinctWordHashmap);
        System.out.println(distinctPunctuationHashmap);
        */
    }
}
