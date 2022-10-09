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

	// manual method for db creation
	// type: auxiliary
	protected static void createNewDatabase(String fileName) {

		String url = "jdbc:sqlite:./" + fileName;

		try (Connection conn = DriverManager.getConnection(url);) {

			if (conn != null) {
				DatabaseMetaData meta = conn.getMetaData();
				System.out.println("The driver name is " + meta.getDriverName());
			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	// drop table to clear ALL stored data
	// type:stretch, auxiliary
	protected void dropTable() {

		// SQL statement for droping table
		String sql = "DROP TABLE textdata";

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// create new table with required schema
	// type: core
	protected void createNewTable() {
		// SQL statement for creating a new table
		String sql = """
				CREATE TABLE IF NOT EXISTS textdata (
				id integer PRIMARY KEY,
				author text NOT NULL,
				title text NOT NULL,
				published integer NOT NULL,
				era text NOT NULL,
				genre text NOT NULL,
				wordCount real NOT NULL,
				sentenceCount real NOT NULL,
				avgWordLength real NOT NULL,
				avgSentenceLength real NOT NULL,
				punctuationCount real NOT NULL,
				Flesch real NOT NULL,
				syllableCount real NOT NULL,
				avgSyllablePerWord real NOT NULL,
				distinctWordCount real NOT NULL,
				wordsHash text NOT NULL,
				punctuationHash text NOT NULL
				);
				""";

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// insert new row NOTE: contains code and pseudo-code (with comments) for future
	// application enhancement
	// type: core
	protected void insert(String author, String title, int published, String era, String genre, double wordCount,
			double sentenceCount, double avgWordLength, double avgSentenceLength, double punctuationCount,
			double fleschScore, double syllableCount, double avgSyllablePerWord, double distinctWordCount,
			String wordHash, String punctuationHash) {
		String sql = "INSERT INTO textdata(author, title, published, era, genre, wordCount, sentenceCount, avgWordLength, avgSentenceLength, punctuationCount,Flesch, syllableCount, avgSyllablePerWord, distinctWordCount, wordsHash, punctuationHash ) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		/*
		 * ***POSSIBLE FUTURE ENCHANCEMENT***
		 * //the following commented out code is the skeleton and pseudo-code for a
		 * //future possible enhancment: a checker that identifies duplicate entries in
		 * //the db. While the section is not complete, it has been left in to show
		 * future //plans for this application.
		 * 
		 * //begin segment String testInputStr = title.toLowerCase(); if
		 * (testInputStr.charAt(0) == 't' && testInputStr.charAt(1) == 'h' &&
		 * testInputStr.charAt(2) == 'e') { testInputStr =
		 * testInputStr.replaceFirst("the ", ""); System.out.println(testInputStr); }
		 * 
		 * ArrayList<String> idAuthorTitleArrList = new ArrayList<>(); ArrayList<String>
		 * authorArrList = new ArrayList<>(); ArrayList<String> titleArrList = new
		 * ArrayList<>(); ArrayList<String> idArrList = new ArrayList<>(); String[]
		 * authorStrArr; String[] titleStrArr; String[] idStrArr;
		 * 
		 * try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt =
		 * conn.createStatement();) {
		 * 
		 * // Create and execute the SQL query, store the results String sql2 =
		 * "SELECT ID, AUTHOR, TITLE FROM textdata";
		 * 
		 * ResultSet result = stmt.executeQuery(sql2);
		 * 
		 * while (result.next()) { String retrievedID =
		 * String.valueOf(result.getInt("id")); String retrievedAuthor =
		 * result.getString("author"); String retrievedTitle =
		 * result.getString("title"); if (retrievedTitle.charAt(0) == 't' &&
		 * retrievedTitle.charAt(1) == 'h' && retrievedTitle.charAt(2) == 'e') {
		 * retrievedTitle = retrievedTitle.replaceFirst("the ", ""); }
		 * authorArrList.add(retrievedAuthor); titleArrList.add(retrievedTitle);
		 * idArrList.add(retrievedID); }
		 * 
		 * conn.close(); // Close the database connection } catch (SQLException e) {
		 * System.out.println(e.getMessage()); }
		 * 
		 * authorStrArr = new String[authorArrList.size() + 1]; authorStrArr[0] = "";
		 * titleStrArr = new String[titleArrList.size() + 1]; titleStrArr[0] = "";
		 * idStrArr = new String[idArrList.size() + 1]; idStrArr[0] = "";
		 * 
		 * for (int i = 0; i < authorArrList.size(); i++) { authorStrArr[i + 1] =
		 * authorArrList.get(i); titleStrArr[i + 1] = titleArrList.get(i); idStrArr[i +
		 * 1] = idArrList.get(i); }
		 * 
		 * int choice = 99; int replaceID; for (int i = 0; i < titleStrArr.length; i++)
		 * { if (titleStrArr[i].equals(testInputStr)) {
		 * idAuthorTitleArrList.add(idArrList.get(i)+" - " + authorArrList.get(i) + " -
		 * " + titleArrList.get(i)); } } if (idAuthorTitleArrList.isEmpty()) { //TO DO:
		 * paste ALL insert code here } else {
		 * 
		 * //TO DO: present dropdown of items in idAuthorTitleArrList //ask what they
		 * want to do
		 * 
		 * switch (choice) { case 1: //add new entry
		 * 
		 * TO DO: paste ALL insert code here
		 * 
		 * break; case 2: //replace existing
		 * 
		 * TO DO: 1) collect id to be replaced 2) call deleteRowByID(collected id) 3)
		 * paste ALL insert code
		 * 
		 * break; case 3: //abort transaction
		 * 
		 * TO DO: display 'addition of row cancelled' popup
		 * 
		 * break; default: break; } } // end segment
		 */

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
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// tests if there is any data already stored in the db, returns boolean
	// type: stretch
	protected boolean dataExists() {
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
	// type: auxiliary
	protected void selectAll() {
		String sql = "SELECT * FROM textdata";

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {
			ResultSet rs = stmt.executeQuery(sql);

			// loop through the result set
			while (rs.next()) {
				// do nothing. Insert desired code here
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// fetch all titles from db, store results in array
	// type: auxiliary
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
	// type: core
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
	// type: auxiliary
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

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return resultStr; // Return the result string
	}

	// agrigate method to retrieve data from all rows, using a modular SQL query
	// type: stretch
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

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return resultArr; // Return the result string
	}

	// method to delete a row in the db by the primary key (id)
	// type: core
	protected void deleteRowById(String id) {

		try (Connection conn = DriverManager.getConnection(SQL_URL); Statement stmt = conn.createStatement();) {

			// Create SQL query, execute it
			String sql = "DELETE FROM textdata WHERE id=\'" + id + "\'";

			stmt.executeUpdate(sql);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	// Retrieve a full record from the database given an author and title
	// Returns the record as a string array
	// type: core
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

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return resultStr; // Return the result string
	}

	// Get the hashmap from the database
	// Converts string data from database back into hashmap
	// type: stretch
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

			// This line removes the cURLy brackets from the string data
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

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return myHashMap; // Return the hashmap
	}

}
