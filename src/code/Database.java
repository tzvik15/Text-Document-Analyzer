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

	// SQLite connection string constant
	private static final String SQL_URL = "jdbc:sqlite:./ParsedDocumentsData.db";

	// initialize db
	protected static void createNewDatabase(String fileName) {

		String URL = "jdbc:sqlite:./" + fileName;

		try (Connection conn = DriverManager.getConnection(URL);) {

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
	protected static void dropTable() {

		// SQL statement for droping table
		String sql = "DROP TABLE textdata";

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			stmt.execute(sql);
			System.out.println("table droped");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// create new table with required schema
	protected void createNewTable() {
		// SQL statement for creating a new table
		String sql = "CREATE TABLE IF NOT EXISTS textdata (\n" + " id integer PRIMARY KEY,\n"
				+ " author text NOT NULL,\n" + " title text NOT NULL,\n" + " published integer NOT NULL,\n"
				+ " era text NOT NULL,\n" + " genre text NOT NULL,\n" + " wordCount real NOT NULL,\n"
				+ " sentenceCount real NOT NULL,\n" + " avgWordLength real NOT NULL,\n"
				+ " avgSentenceLength real NOT NULL,\n" + " punctuationCount real NOT NULL,\n" + " Flesch real,\n"
				+ " syllableCount real,\n" + " avgSyllablePerWord real,\n" + " distinctWordCount real,\n"
				+ " wordsHash text,\n" + " punctuationHash text\n" + ");";

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			stmt.execute(sql);
			System.out.println("Table already exists, or new table created");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// insert new row
	protected void insert(String author, String title, int published, String era, String genre, double wordCount,
			double sentenceCount, double avgWordLength, double avgSentenceLength, double punctuationCount,
			double fleschScore, double syllableCount, double avgSyllablePerWord, double distinctWordCount,
			String wordHash, String punctuationHash) {
		String sql = "INSERT INTO textdata(author, title, published, era, genre, wordCount, sentenceCount, avgWordLength, avgSentenceLength, punctuationCount,Flesch, syllableCount, avgSyllablePerWord, distinctWordCount, wordsHash, punctuationHash ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try (Connection conn = DriverManager.getConnection(SQL_URL);
				PreparedStatement pstmt = conn.prepareStatement(sql);) {

			pstmt.setString(1, author);
			pstmt.setString(2, title);
			pstmt.setInt(3, published);
			pstmt.setString(4, era);
			pstmt.setString(5, genre);
			pstmt.setDouble(6, wordCount);
			pstmt.setDouble(7, sentenceCount);
			pstmt.setDouble(8, avgWordLength);
			pstmt.setDouble(9, avgSentenceLength);
			pstmt.setDouble(10, punctuationCount);
			pstmt.setDouble(11, fleschScore);
			pstmt.setDouble(12, syllableCount);
			pstmt.setDouble(13, avgSyllablePerWord);
			pstmt.setDouble(14, distinctWordCount);
			pstmt.setString(15, wordHash);
			pstmt.setString(16, punctuationHash);

			pstmt.executeUpdate();
			System.out.println("inserted row");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	//tests if there is any data already stored in the db, returns boolean
	protected boolean dataExists(){
		String sql = "SELECT * FROM textdata";
		boolean exists = false;
		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			ResultSet rs = stmt.executeQuery(sql);
			exists = rs.next();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return exists;
	}
	
	
	// print all table data
	protected void selectAll() {
		String sql = "SELECT * FROM textdata";

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				System.out.println(rs.getInt("id") + "\t" + rs.getString("author") + "\t" + rs.getString("title") + "\t"
						+ rs.getInt("published") + "\t" + rs.getString("genre") + "\t" + rs.getInt("wordCount") + "\t"
						+ rs.getInt("sentenceCount") + "\t" + rs.getDouble("avgWordLength") + "\t"
						+ rs.getDouble("avgSentenceLength") + "\t" + rs.getInt("punctuationCount"));
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

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create and execute the SQL query, store the results
			String sql = "SELECT TITLE FROM textdata";
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

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create and execute the SQL query, store the results
			String sql = "SELECT AUTHOR, TITLE FROM textdata";

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

		String[] resultStr = new String[17]; // String to hold the returned results

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create and execute the SQL query, store the results
			String sql = "SELECT * FROM textdata WHERE TITLE=\'" + title + "\'";

			ResultSet result = stmt.executeQuery(sql);

			// Get the results as a string
			result.next();

			for (int i = 1; i < 18; i++) {
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
		String[] resultArr = new String[4];

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

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create and execute the SQL query, store the results
			String sql = method;
			// System.out.println(sql);

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
		/*
		 * for (int i = 0; i < resultArr.length; i++) {
		 * System.out.println(resultArr[i]); }
		 */
		return resultArr; // Return the result string
	}

	protected void deleteRowById(String id) {

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create SQL query, execute it
			String sql = "DELETE FROM textdata WHERE id=\'" + id + "\'";

			stmt.executeQuery(sql);

			conn.close(); // Close the database connection
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// Retrieve a full record from the database given an author and title
	// Returns the record as a string array
	protected String[] retrieveRecordByAuthorTitle(String author, String title) {

		String[] resultStr = new String[17]; // String to hold the returned results

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create and execute the SQL query, store the results
			String sql = "SELECT * FROM textdata WHERE AUTHOR=\'" + author + "\' AND TITLE=\'" + title + "\'";

			ResultSet result = stmt.executeQuery(sql);

			// Get the results as a string
			result.next();

			for (int i = 1; i < 18; i++) {
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
		HashMap<String, Integer> myHashMap = new HashMap<>();

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create and execute the SQL query, store the results
			String sql = "SELECT " + field + " FROM textdata WHERE AUTHOR=\'" + author + "\' AND TITLE=\'" + title
					+ "\'";

			ResultSet result = stmt.executeQuery(sql);

			// Get the results as a string
			String resultStr = result.getString(field);

			// This line removes the cURLy brackets from the string data { }
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

//main method for internal use to drop existing table in db for schema update
	public static void main(String[] args) {
		dropTable();
	}

}
