package code;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;


public class Database {

    // initialize db
    public static void createNewDatabase(String fileName) {

        String url = "jdbc:sqlite:./" + fileName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // drop table to start new
    public static void dropTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:./ParsedDocumentsData.db";

        // SQL statement for droping table
        String sql = "DROP TABLE textdata";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("table droped");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // create new table with required schema
    public static void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:./ParsedDocumentsData.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS textdata (\n"
                + " id integer PRIMARY KEY,\n"
                + " author text NOT NULL,\n"
                + " title text NOT NULL,\n"
                + " published integer,\n"
                + " genre text NOT NULL,\n"
                + " wordCount integer NOT NULL,\n"
                + " sentenceCount integer NOT NULL,\n"
                + " avgWordLength real NOT NULL,\n"
                + " avgSentenceLength real NOT NULL,\n"
                + " punctuationCount integer NOT NULL,\n"
                + " Flesch real,\n"
                + " syllableCount integer,\n"
                + " distinctWordCount text,\n"
                + " distinctPunctuationCount text\n"
                + ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // insert new row
    public static void insert(String author, String title, int published, String genre, int wordCount,
            int sentenceCount, double avgWordLength, double avgSentenceLength, int punctuationCount) {
        String sql = "INSERT INTO textdata(author, title, published, genre, wordCount, sentenceCount, avgWordLength, avgSentenceLength, punctuationCount) VALUES(?,?,?,?,?,?,?,?,?)";
        String url = "jdbc:sqlite:./ParsedDocumentsData.db";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, author);
            pstmt.setString(2, title);
            pstmt.setInt(3, published);
            pstmt.setString(4, genre);
            pstmt.setInt(5, wordCount);
            pstmt.setInt(6, sentenceCount);
            pstmt.setDouble(7, avgWordLength);
            pstmt.setDouble(8, avgSentenceLength);
            pstmt.setInt(9, punctuationCount);

            pstmt.executeUpdate();
            System.out.println("inserted row");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // print all table data
    public static void selectAll() {
        String sql = "SELECT * FROM textdata";
        String url = "jdbc:sqlite:./ParsedDocumentsData.db";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" +
                        rs.getString("author") + "\t" +
                        rs.getString("title") + "\t" +
                        rs.getInt("published") + "\t" +
                        rs.getString("genre") + "\t" +
                        rs.getInt("wordCount") + "\t" +
                        rs.getInt("sentenceCount") + "\t" +
                        rs.getDouble("avgWordLength") + "\t" +
                        rs.getDouble("avgSentenceLength") + "\t" +
                        rs.getInt("punctuationCount"));
                //testing capturing retrieved data as variable
                String testStr = rs.getString("author");
                System.out.println(testStr.length());

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Testing out retrieving a full record from the database
    // Returns the record as a string array
    public String[] retrieveRecordByAuthorTitle(String table, String author, String title) {

        String[] resultStr = new String[16]; // String to hold the returned results

        try{
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

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

    // Get the hashmap from the database
    // Converts string data from database back into hashmap
    public HashMap<String, Integer> retrieveHashMapByAuthorTitle(String table, String field, String author, String title) {  

        // Create the hashmap again
        HashMap<String, Integer> myHashMap = new HashMap<String, Integer>();

        try{
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

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

    public static void main(String[] args) throws Exception {

        createNewDatabase("ParsedDocumentsData.db");
        createNewTable();
        // dropTable();
        insert("Bob bobson", "the test that tested me", 2023, "comedy", 3678945, 23599, 4.3, 7.9, 100000);
        selectAll();
    }
}
