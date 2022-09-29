package code;
import java.io.*;

public class Tokenizer {
    private BufferedReader file;
    private char currentChar;
    private int charCounter = 0;
    private String inputLine = "";
    private String currentLexeme = "";

    // Constructor for the Tokenizer class
    public Tokenizer(BufferedReader file) throws FileNotFoundException {
        // Open the file for reading and extract the first character
        this.file = file;
        currentChar = nextChar();
    }

    // Returns the next character in the current line of the input document
    private char nextChar() {
        // Try to extract the next character
        try {            
            // If the last character was reached...
            if (charCounter == inputLine.length()) {
                inputLine = file.readLine();    // Extract the next line
                charCounter = 0;                // Reset the character count

                if (inputLine != null) {
                    return ' ';
                } else {
                    return 0;
                }
            }

            if (inputLine.length() == 0) {
                inputLine = file.readLine();
            }

            return inputLine.charAt(charCounter++); // Return the enxt character
        } catch (IOException ex) {
            return 0;
        }
    }

    // Returns the next token in the current line of the input document
    public String getNextToken() {
        currentLexeme = "";  // Reset current lexeme

        // Ignore whitespace characters by continuing past them
        while (currentChar != 0 && Character.isWhitespace(currentChar)){
            currentChar = nextChar();
        }
        
        // If no more characters return END_OF_FILE
        if (currentChar == 0) {
            return "END_OF_FILE";
        }

        // Determine if character is a letter, digit, or special character
        if (Character.isLetter(currentChar) || Character.isDigit(currentChar)) {
            // If letter, build lexeme from letter characters
            while (Character.isLetter(currentChar)|| Character.isDigit(currentChar)) {
                currentLexeme += currentChar;
                currentChar = nextChar();
            }
        } else {
            // If special character, store character as current lexeme
            currentLexeme += currentChar;
            currentChar = nextChar();
        }
        return currentLexeme.toLowerCase();
    }

    // Close the input file
    public void close() {
        try {
            file.close();
        } catch (IOException ex) {
            System.out.println("Cannot close file. File not found.");
        }
    }
} //End Tokenizer class
