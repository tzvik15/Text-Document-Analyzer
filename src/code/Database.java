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
    protected static void createNewDatabase(String fileName) {

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
    protected void dropTable() {
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
    protected void createNewTable() {
        // SQLite connection string
        String url = "jdbc:sqlite:./ParsedDocumentsData.db";

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS textdata (\n"
                + " id integer PRIMARY KEY,\n"
                + " author text NOT NULL,\n"
                + " title text NOT NULL,\n"
                + " published integer NOT NULL,\n"
                + " genre text NOT NULL,\n"
                + " wordCount real NOT NULL,\n"
                + " sentenceCount real NOT NULL,\n"
                + " avgWordLength real NOT NULL,\n"
                + " avgSentenceLength real NOT NULL,\n"
                + " punctuationCount real NOT NULL,\n"
                + " Flesch real,\n"
                + " syllableCount real,\n"
                + " avgSyllablePerWord real,\n"
                + " distinctWordCount real,\n"
                + " wordsHash text,\n"
                + " punctuationHash text\n"
                + ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Table already exists, or new table created");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // insert new row
    protected void insert(String author, String title, int published, String genre, double wordCount,
            double sentenceCount, double avgWordLength, double avgSentenceLength,
            double punctuationCount, double fleschScore, double syllableCount,
            double avgSyllablePerWord,
            double distinctWordCount,
            String wordHash, String punctuationHash) {
        String sql = "INSERT INTO textdata(author, title, published, genre, wordCount, sentenceCount, avgWordLength, avgSentenceLength, punctuationCount,Flesch, syllableCount, avgSyllablePerWord, distinctWordCount, wordsHash, punctuationHash ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String url = "jdbc:sqlite:./ParsedDocumentsData.db";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, author);
            pstmt.setString(2, title);
            pstmt.setInt(3, published);
            pstmt.setString(4, genre);
            pstmt.setDouble(5, wordCount);
            pstmt.setDouble(6, sentenceCount);
            pstmt.setDouble(7, avgWordLength);
            pstmt.setDouble(8, avgSentenceLength);
            pstmt.setDouble(9, punctuationCount);
            pstmt.setDouble(10, fleschScore);
            pstmt.setDouble(11, syllableCount);
            pstmt.setDouble(12, avgSyllablePerWord);
            pstmt.setDouble(13, distinctWordCount);
            pstmt.setString(14, wordHash);
            pstmt.setString(15, punctuationHash);

            pstmt.executeUpdate();
            System.out.println("inserted row");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // print all table data
    protected void selectAll() {
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
                // testing capturing retrieved data as variable
                String testStr = rs.getString("author");
                System.out.println(testStr.length());

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // fetch all titles from db, store results in array
    protected String[] retrieveTitles() {
        ArrayList<String> titleArr = new ArrayList<>();
        String[] titles;

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create and execute the SQL query, store the results
            String sql = "SELECT TITLE FROM textdata";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                titleArr.add(result.getString("title"));
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        titles = new String[titleArr.size() + 1];
        titles[0] = "";

        for (int i = 0; i < titleArr.size(); i++) {
            titles[i + 1] = titleArr.get(i);
        }

        return titles;
    }

    // fetch all author/title pairs from db, store results in array
    protected String[] retrieveAuthorsAndTitles() {
        ArrayList<String> authorTitleArrList = new ArrayList<>();
        String[] authorTitlesStrArr;

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create and execute the SQL query, store the results
            String sql = "SELECT AUTHOR, TITLE FROM textdata";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            while (result.next()) {
                String author = result.getString("author");
                String title = result.getString("title");
                authorTitleArrList.add(author + " - " + title);
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        authorTitlesStrArr = new String[authorTitleArrList.size() + 1];
        authorTitlesStrArr[0] = "";

        for (int i = 0; i < authorTitleArrList.size(); i++) {
            authorTitlesStrArr[i + 1] = authorTitleArrList.get(i);
        }

        return authorTitlesStrArr;
    }

    // retrieve single record by input title
    protected String[] retrieveRecordByTitle(String title) {

        String[] resultStr = new String[16]; // String to hold the returned results

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create and execute the SQL query, store the results
            String sql = "SELECT * FROM textdata WHERE TITLE=\'" + title + "\'";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            // Get the results as a string
            result.next();

            for (int i = 1; i < 17; i++) {
                resultStr[i - 1] = result.getString(i);
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(resultStr);
        return resultStr; // Return the result string
    }

    protected String[] sqlQuery(String view, String of, String where, String equals) {

        String method = "";
        String convertedOf = "";
        boolean extra = false;
        String resultTitle = "";
        String resultAuthor = "";
        String[] resultArr = new String[3];

        switch (of) {
            case "Word Count":
                convertedOf = "wordCount";
                break;
            case "Distinct Word Count":
                convertedOf = "distinctWordCount";
                break;
            case "Punctuation Count":
                convertedOf = "punctuationCount";
                break;
            case "Sentence Count":
                convertedOf = "sentenceCount";
                break;
            case "Syllable Count":
                convertedOf = "syllableCount";
                break;
            case "Flesch Reading Ease Score":
                convertedOf = "Flesch";
                break;
            case "Average Words Per Sentence":
                convertedOf = "avgSentenceLength";
                break;
            case "Average Syllables Per Word":
                convertedOf = "avgSyllablePerWord";
                break;
            case "Average Word Length":
                convertedOf = "avgWordLength";
                break;

            default:
                break;
        }

        switch (view) {
            case "Min":
                extra = true;
                method = "SELECT MIN(" + convertedOf + "), AUTHOR, TITLE FROM textdata WHERE LOWER(" + where + ") =\'"
                        + equals + "\'";
                break;
            case "Max":
                extra = true;
                method = "SELECT MAX(" + convertedOf + "), AUTHOR, TITLE FROM textdata WHERE LOWER(" + where + ") =\'"
                        + equals + "\'";
                break;
            case "Average":
                method = "SELECT AVG(" + convertedOf + ") FROM textdata WHERE LOWER(" + where + ") =\'" + equals + "\'";
                break;
            case "Total":
                method = "SELECT SUM(" + convertedOf + ") FROM textdata WHERE LOWER(" + where + ") =\'" + equals + "\'";
                break;
            default:
                break;
        }

        double resultDoub = 0.0; // variable to hold the returned results

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create and execute the SQL query, store the results
            String sql = method;
            //System.out.println(sql);
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            // Get the results as a string
            result.next();

            resultDoub = result.getDouble(1);
            resultArr[0] = String.valueOf(resultDoub);
            if (extra) {
                resultAuthor = result.getString(2);
                resultTitle = result.getString(3);
                resultArr[1] = resultAuthor;
                resultArr[2] = resultTitle;
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
       /* for (int i = 0; i < resultArr.length; i++) {
            System.out.println(resultArr[i]);
        }*/
        return resultArr; // Return the result string
    }

    protected void deleteRowById(String id) {

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create SQL query, execute it
            String sql = "DELETE FROM textdata WHERE id=\'" + id + "\'";
            Statement stmt = conn.createStatement();
            stmt.executeQuery(sql);

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Retrieve a full record from the database given an author and title
    // Returns the record as a string array
    protected String[] retrieveRecordByAuthorTitle(String author, String title) {

        String[] resultStr = new String[16]; // String to hold the returned results

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create and execute the SQL query, store the results
            String sql = "SELECT * FROM textdata WHERE AUTHOR=\'" + author + "\' AND TITLE=\'" + title + "\'";
            Statement stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(sql);

            // Get the results as a string
            result.next();

            for (int i = 1; i < 17; i++) {
                resultStr[i - 1] = result.getString(i);
            }

            conn.close(); // Close the database connection
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return resultStr; // Return the result string
    }

    // Get the hashmap from the database
    // Converts string data from database back into hashmap
    protected HashMap<String, Integer> retrieveHashMapByAuthorTitle(String field, String author, String title) {

        // Create the hashmap again
        HashMap<String, Integer> myHashMap = new HashMap<String, Integer>();

        try {
            String url = "jdbc:sqlite:./ParsedDocumentsData.db";
            Connection conn = DriverManager.getConnection(url);

            // Create and execute the SQL query, store the results
            String sql = "SELECT " + field + " FROM textdata WHERE AUTHOR=\'" + author + "\' AND TITLE=\'" + title
                    + "\'";
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

}
