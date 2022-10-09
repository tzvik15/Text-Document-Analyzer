package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileSystemView;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI {
	// Instantiate objects for each window.
	// This prevents opening multiple windows of the same type.
	// User can't open two 'Add Entry' windows at once.
	MainGUI mainWindow; // Instantiate the main window.
	AddEntryGUI addEntryWindow;
	DeleteEntryGUI deleteEntryWindow;
	SearchGUI searchWindow;
	QueryGUI queryWindow;
	AboutGUI aboutWindow;
	UserGuideGUI userGuideWindow;

	BufferedReader file;
	Tokenizer tokenizer;
	Database db = new Database();

	// This function will return the MainGUI, ready to be setVisible()
	// This starts the application
	public MainGUI getMainWindow() {
		mainWindow = new MainGUI();
		return mainWindow;
	}

	// The main window of the application
	private class MainGUI extends JFrame {
		JComboBox<String> recordsDropdown;

		// Create the panels to hold groups of components
		JPanel inputPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel outputPanel = new JPanel();
		JMenuBar topMenuBar = new TopMenuBar();
		JTextArea displayArea; // Main window data display area

		private MainGUI() {
			// Window attributes
			super("Text Document Analyzer");
			setSize(450, 500);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			setResizable(true);

			// Panel attributes
			inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			inputPanel.setPreferredSize(new Dimension(365, 60));
			buttonPanel.setPreferredSize(new Dimension(365, 35));
			outputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));

			// Create a label for the dropdown menu
			JLabel recordsDropdownLabel = new JLabel("Select a record to display metrics:");

			// Create the dropdown menu
			String[] records = db.retrieveAuthorsAndTitles();
			recordsDropdown = new JComboBox<>(records);
			recordsDropdown.setPreferredSize(new Dimension(365, 28));
			recordsDropdown.setBackground(Color.WHITE);
			recordsDropdown.addFocusListener(focus);

			// Create the Display button
			JButton displayButton = new JButton("Display");
			displayButton.setPreferredSize(new Dimension(100, 28));
			displayButton.addActionListener(displayClick);

			// Create the display area for data
			displayArea = new JTextArea(16, 40);
			displayArea.setEditable(false);
			displayArea.setLineWrap(true);
			displayArea.setWrapStyleWord(true);
			displayArea.setBorder(BorderFactory.createLoweredBevelBorder());
			JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a
																	// scroll bar
			scrollPane.setPreferredSize(new Dimension(365, 275));

			// Add components to the panels
			inputPanel.add(recordsDropdownLabel);
			inputPanel.add(recordsDropdown);
			buttonPanel.add(displayButton);
			outputPanel.add(scrollPane); // <--- The display area (within the scroll pane)

			add(inputPanel); // Add panels to the window
			add(buttonPanel);
			add(outputPanel);
			setJMenuBar(topMenuBar); // Add the JMenuBar to the window
		}

		// Display button Action Listener
		private final ActionListener displayClick = event -> { // File > Query Database
			if (recordsDropdown.getSelectedItem().equals("")) {
				Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
				recordsDropdown.setBorder(redBorder);
			} else {
				// Retrieve user input
				String recordStr = (String) recordsDropdown.getSelectedItem();
				String[] authorAndTitle = recordStr.split(" - ");
				String author = authorAndTitle[0];
				String title = authorAndTitle[1];

				// Retrieve the record from the database
				String[] localResults = db.retrieveRecordByAuthorTitle(author, title);

				// Display the results in the data display area
				displayArea.setText(null);
				displayArea.append("Author: " + localResults[1] + "\n");
				displayArea.append("Title: " + localResults[2] + "\n");
				displayArea.append("Published Year: " + localResults[3] + "\n");
				displayArea.append("Era: " + localResults[4] + "\n");
				displayArea.append("Genre: " + localResults[5] + "\n");
				displayArea.append("Word Count: " + String.format("%,.0f", Double.parseDouble(localResults[6])) + "\n");
				displayArea.append(
						"Sentence Count: " + String.format("%,.0f", Double.parseDouble(localResults[7])) + "\n");
				displayArea.append(
						"Average Word Length: " + String.format("%,.2f", Double.parseDouble(localResults[8])) + "\n");
				displayArea.append("Average Sentence Length: "
						+ String.format("%,.2f", Double.parseDouble(localResults[9])) + "\n");
				displayArea.append(
						"Punctuation Count: " + String.format("%,.0f", Double.parseDouble(localResults[10])) + "\n");
				displayArea.append(
						"Flesch Score Ease: " + String.format("%,.2f", Double.parseDouble(localResults[11])) + "\n");
				displayArea.append(
						"Syllable Count: " + String.format("%,.0f", Double.parseDouble(localResults[12])) + "\n");
				displayArea.append("Average Syllable Per Word: "
						+ String.format("%,.2f", Double.parseDouble(localResults[13])) + "\n");
				displayArea.append(
						"Distinct Word Count: " + String.format("%,.0f", Double.parseDouble(localResults[14])) + "\n");

				displayArea.setFont(new Font("Serif", Font.BOLD, 12));
			}

		};
	}

	// The top menu bar of the main window
	private class TopMenuBar extends JMenuBar {
		private TopMenuBar() {
			// Create the File, About, and Help menu dropdown options
			JMenu topMenuFile = new JMenu("File");
			topMenuFile.setMnemonic(KeyEvent.VK_A);
			topMenuFile.getAccessibleContext()
					.setAccessibleDescription("The File menu on the top menu of the application.");

			JMenu topMenuAbout = new JMenu("About");
			topMenuFile.setMnemonic(KeyEvent.VK_B);
			topMenuFile.getAccessibleContext()
					.setAccessibleDescription("The About menu on the top menu of the application.");

			JMenu topMenuHelp = new JMenu("Help");
			topMenuFile.setMnemonic(KeyEvent.VK_C);
			topMenuFile.getAccessibleContext()
					.setAccessibleDescription("The Help menu on the top menu of the application.");

			// Create and add the menu items to the File menu
			JMenuItem menuItemAdd = new JMenuItem("Add Entry");
			menuItemAdd.addActionListener(addEntryClick);

			JMenuItem menuItemDelete = new JMenuItem("Delete Entry");
			menuItemDelete.addActionListener(deleteEntryClick);

			JMenuItem menuItemSearch = new JMenuItem("Search Distinct Words");
			menuItemSearch.addActionListener(searchClick);

			JMenuItem menuItemQuery = new JMenuItem("Query Database");
			menuItemQuery.addActionListener(queryClick);

			topMenuFile.add(menuItemAdd);
			topMenuFile.addSeparator();
			topMenuFile.add(menuItemDelete);
			topMenuFile.addSeparator();
			topMenuFile.add(menuItemSearch);
			topMenuFile.addSeparator();
			topMenuFile.add(menuItemQuery);

			// Create and add the menu items to the About and Help menus
			JMenuItem menuItemAbout = new JMenuItem("About Text Document Analyzer");
			menuItemAbout.addActionListener(aboutClick);

			JMenuItem menuItemGuide = new JMenuItem("User Guide");
			menuItemGuide.addActionListener(userGuideClick);

			topMenuAbout.add(menuItemAbout);
			topMenuHelp.add(menuItemGuide);

			add(topMenuFile); // Add the File menu to the top bar
			add(topMenuAbout); // Add the About menu to the top bar
			add(topMenuHelp); // Add the Help menu to the top bar
		}

		// Action Listeners for each of the top menu items
		// New window OPENS if: window is null or invisible
		// New window DOES NOT OPEN if: window is not null and already visible
		// Window is set to visible regardless
		private final ActionListener addEntryClick = event -> { // File > Add Entry
			if (addEntryWindow == null || !addEntryWindow.isVisible()) {
				addEntryWindow = new AddEntryGUI();
			}
			addEntryWindow.setVisible(true);
		};

		private final ActionListener deleteEntryClick = event -> { // File > Delete Entry
			if (deleteEntryWindow == null || !deleteEntryWindow.isVisible()) {
				deleteEntryWindow = new DeleteEntryGUI();
			}
			deleteEntryWindow.setVisible(true);
		};

		private final ActionListener searchClick = event -> { // File > Search Distinct Words
			if (searchWindow == null || !searchWindow.isVisible()) {
				searchWindow = new SearchGUI();
			}
			searchWindow.setVisible(true);
		};

		private final ActionListener queryClick = event -> { // File > Query Database
			if (queryWindow == null || !queryWindow.isVisible()) {
				queryWindow = new QueryGUI();
			}
			queryWindow.setVisible(true);
		};

		private final ActionListener aboutClick = event -> { // About > About Text Document Analyzer
			if (aboutWindow == null || !aboutWindow.isVisible()) {
				aboutWindow = new AboutGUI();
			}
			aboutWindow.setVisible(true);
		};

		private final ActionListener userGuideClick = event -> { // Help > User Guide
			if (userGuideWindow == null || !userGuideWindow.isVisible()) {
				userGuideWindow = new UserGuideGUI();
			}
			userGuideWindow.setVisible(true);
		};
	}

	// The 'Add Entry' window
	private class AddEntryGUI extends JFrame {
		// Declaring the input textfields to capture input with listeners
		JTextField authorTextField;
		JTextField titleTextField;
		JTextField publishYearTextField;
		JComboBox<String> eraDropdown;
		JComboBox<String> genreDropdown;
		JTextField browseTextField;

		// Create the panels to hold groups of components
		JPanel inputPanel = new JPanel();
		JPanel browsePanel = new JPanel();
		JPanel parsePanel = new JPanel();

		// Create nested label and fields panels (these go inside the input panel)
		JPanel labelsPanel = new JPanel();
		JPanel fieldsPanel = new JPanel();

		private AddEntryGUI() {
			// Window attributes
			super("Add Entry");
			setSize(450, 380);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			setResizable(false);

			// Panel attributes
			inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			inputPanel.setPreferredSize(new Dimension(365, 190));
			browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 4));
			browsePanel.setPreferredSize(new Dimension(365, 75));
			parsePanel.setPreferredSize(new Dimension(365, 35));

			// Nested panels attributes
			labelsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			labelsPanel.setPreferredSize(new Dimension(85, 180));
			fieldsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 8));
			fieldsPanel.setPreferredSize(new Dimension(280, 180));

			// Create the textfields and labels for the input panel
			JLabel authorLabel = new JLabel("Author:");
			authorLabel.setPreferredSize(new Dimension(90, 28));
			authorLabel.setVerticalAlignment(SwingConstants.CENTER);
			authorTextField = new JTextField();
			authorTextField.setPreferredSize(new Dimension(275, 28));
			authorTextField.addFocusListener(focus);

			JLabel titleLabel = new JLabel("Title:");
			titleLabel.setPreferredSize(new Dimension(90, 28));
			titleLabel.setVerticalAlignment(SwingConstants.CENTER);
			titleTextField = new JTextField();
			titleTextField.setPreferredSize(new Dimension(275, 28));
			titleTextField.addFocusListener(focus);

			JLabel publishYearLabel = new JLabel("Publish Year:");
			publishYearLabel.setPreferredSize(new Dimension(90, 28));
			publishYearLabel.setVerticalAlignment(SwingConstants.CENTER);
			publishYearTextField = new JTextField();
			publishYearTextField.setPreferredSize(new Dimension(275, 28));
			publishYearTextField.addFocusListener(focus);

			// Create the Era dropdown menu and label for the input panel
			JLabel eraLabel = new JLabel("Era:");
			eraLabel.setPreferredSize(new Dimension(90, 28));
			eraLabel.setVerticalAlignment(SwingConstants.CENTER);
			String[] eras = { "", "ACE", "BCE" };
			eraDropdown = new JComboBox<>(eras);
			eraDropdown.setPreferredSize(new Dimension(275, 28));
			eraDropdown.setBackground(Color.WHITE);
			eraDropdown.addFocusListener(focus);

			// Create the Genre dropdown menu and label for the input panel
			JLabel genreLabel = new JLabel("Genre:");
			genreLabel.setPreferredSize(new Dimension(90, 28));
			genreLabel.setVerticalAlignment(SwingConstants.CENTER);
			String[] genres = { "", "Action", "Adventure", "Comedy", "Mystery", "Fantasy", "Historical",
					"Educational", "Reference", "Religious/Spiritual", "Horror", "Romance", "Sci-Fi" };
			genreDropdown = new JComboBox<>(genres);
			genreDropdown.setPreferredSize(new Dimension(275, 28));
			genreDropdown.setBackground(Color.WHITE);
			genreDropdown.addFocusListener(focus);

			// Create the Browse button, label, and textfield
			JLabel selectDocLabel = new JLabel("Select document to parse:");
			selectDocLabel.setPreferredSize(new Dimension(365, 28));

			browseTextField = new JTextField();
			browseTextField.setPreferredSize(new Dimension(265, 28));
			browseTextField.setEditable(false);
			browseTextField.setBackground(Color.WHITE);
			browseTextField.addFocusListener(focus);

			JButton browseButton = new JButton("Browse");
			browseButton.setPreferredSize(new Dimension(100, 28));
			browseButton.addActionListener(browseClick);

			// Create the Parse button
			JButton parseButton = new JButton("Parse");
			parseButton.setPreferredSize(new Dimension(100, 28));
			parseButton.addActionListener(parseClick);

			// Add components to the panels
			labelsPanel.add(authorLabel);
			labelsPanel.add(titleLabel);
			labelsPanel.add(publishYearLabel);
			labelsPanel.add(eraLabel);
			labelsPanel.add(genreLabel);

			fieldsPanel.add(authorTextField);
			fieldsPanel.add(titleTextField);
			fieldsPanel.add(publishYearTextField);
			fieldsPanel.add(eraDropdown);
			fieldsPanel.add(genreDropdown);

			inputPanel.add(labelsPanel);
			inputPanel.add(fieldsPanel);

			browsePanel.add(selectDocLabel);
			browsePanel.add(browseTextField);
			browsePanel.add(browseButton);

			parsePanel.add(parseButton);

			add(inputPanel); // Add panels to the window
			add(browsePanel);
			add(parsePanel);
		}

		private final ActionListener browseClick = event -> {
			browseTextField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

			try {
				String filePath = "";

				JFrame jf = new JFrame("Dialog");
				jf.setAlwaysOnTop(true);

				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				jfc.setAcceptAllFileFilterUsed(false);
				jfc.setFileFilter(new FileNameExtensionFilter("Text file", "txt"));
				int returnValue = jfc.showOpenDialog(jf);

				if (returnValue == JFileChooser.APPROVE_OPTION) {
					filePath = jfc.getSelectedFile().getAbsolutePath();
					browseTextField.setText(filePath);
				}
				jf.dispose();

				Path path = Paths.get(filePath);
				file = Files.newBufferedReader(path);

			} catch (FileNotFoundException ex) {
				JOptionPane.showMessageDialog(null, "The selected file was not found.");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(null, "Could not retrieve selected file.");
			}
		};

		private final ActionListener parseClick = event -> { // File > Add Entry
			Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);

			if (authorTextField.getText().isEmpty()) {
				authorTextField.setBorder(redBorder);
			} else if (titleTextField.getText().isEmpty()) {
				titleTextField.setBorder(redBorder);
			} else if (!publishYearTextField.getText().matches("\\d+")) {
				publishYearTextField.setBorder(redBorder);
			} else if ("".equals((String) eraDropdown.getSelectedItem())) {
				eraDropdown.setBorder(redBorder);
			} else if ("".equals((String) genreDropdown.getSelectedItem())) {
				genreDropdown.setBorder(redBorder);
			} else if (browseTextField.getText().isEmpty()) {
				browseTextField.setBorder(redBorder);
			} else {
				Parser parser = new Parser();
				boolean success = parser.parseDoc(file);

				if (success) {
					// Get all the field values
					String author = authorTextField.getText();
					String title = titleTextField.getText();
					String genre = (String) genreDropdown.getSelectedItem();
					String era = (String) eraDropdown.getSelectedItem();

					int published;

					if (publishYearTextField.getText().isEmpty()) {
						published = 0000;
					} else {
						published = Integer.parseInt(publishYearTextField.getText());
					}

					// Get all the parsed data
					double wordCount = parser.getWordCount(); // Number of words
					double distinctWordsCount = parser.getDistinctWordsCount(); // Number of distinct words
					double punctuationCount = parser.getPunctuationCount(); // Number of punctuation marks total
					double sentenceCount = parser.getSentenceCount(); // Number of sentences (counts . ! ?)
					double syllableCount = parser.getSyllableCount(); // Number of syllables
					double fleschScore = parser.getFleschScore(); // Flesch Reading Ease Score
					double avgWordsPerSentence = parser.getAvgWordsPerSentence(); // Average words per sentence
					double avgSyllablesPerWord = parser.getAvgSyllablesPerWord(); // Average syllables per words
					double avgWordLength = parser.getAvgWordLength(); // Average word length
					String wordsHash = parser.getWordHash(); // HashMap of <Word, Count> pairs
					String punctuationHash = parser.getPunctuationHash(); // HashMap of <Punctuation, Count> pairs

					// insert entry in table
					db.insert(author, title, published, era, genre, wordCount, sentenceCount, avgWordLength,
							avgWordsPerSentence, punctuationCount, fleschScore, syllableCount, avgSyllablesPerWord,
							distinctWordsCount, wordsHash, punctuationHash);

					// Reset the GUI fields
					authorTextField.setText("");
					titleTextField.setText("");
					publishYearTextField.setText("");
					eraDropdown.setSelectedIndex(0);
					genreDropdown.setSelectedIndex(0);
					browseTextField.setText("");
					refreshComponents();
					JOptionPane.showMessageDialog(null, "File was parsed successfully!");
				} else {
					JOptionPane.showMessageDialog(null, "Could not parse file.");
				}
			}
		};
	}

	// The 'Delete Entry' window
	private class DeleteEntryGUI extends JFrame {
		JComboBox<String> recordsDropdown;

		JPanel inputPanel = new JPanel();
		JPanel deletePanel = new JPanel();

		private DeleteEntryGUI() {
			// Window attributes
			super("Delete Entry");
			setSize(450, 175);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			setResizable(false);

			// Panel attributes
			inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			inputPanel.setPreferredSize(new Dimension(365, 60));
			deletePanel.setPreferredSize(new Dimension(365, 35));

			// Create a label for the dropdown menu
			JLabel recordsDropdownLabel = new JLabel("Select a record to delete:");

			// Create the records dropdown menu
			String[] records = db.retrieveAuthorsAndTitles();
			recordsDropdown = new JComboBox<>(records);
			recordsDropdown.setPreferredSize(new Dimension(365, 28));
			recordsDropdown.setBackground(Color.WHITE);
			recordsDropdown.addFocusListener(focus);

			// Add "delete all" option to combo box if data exists
			if (db.dataExists()) {
				recordsDropdown.addItem("DELETE ALL RECORDS");
			}

			// Create the Delete button
			JButton deleteButton = new JButton("Delete");
			deleteButton.setPreferredSize(new Dimension(100, 28));
			deleteButton.addActionListener(deleteClick);

			inputPanel.add(recordsDropdownLabel);
			inputPanel.add(recordsDropdown);
			deletePanel.add(deleteButton);

			add(inputPanel);
			add(deletePanel);
		}

		// Delete button Action Listener
		private final ActionListener deleteClick = event -> { // File > Query Database
			if (recordsDropdown.getSelectedItem().equals("")) {
				Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
				recordsDropdown.setBorder(redBorder);
			} else if (recordsDropdown.getSelectedItem().equals("DELETE ALL RECORDS")) {
				int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to ALL RECORDS?",
						"Alert!", JOptionPane.YES_NO_OPTION);
				if (confirmation == 0) {
					// Drop, then recreate the table
					db.dropTable();
					db.createNewTable();
					refreshComponents();
					JOptionPane.showMessageDialog(null, "All records have been deleted.");
				} else {
					JOptionPane.showMessageDialog(null, "All record deletion CANCLED.");
				}
			} else {
				int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?",
						"Alert!", JOptionPane.YES_NO_OPTION);
				if (confirmation == 0) {
					// Retrieve user input
					String recordStr = (String) recordsDropdown.getSelectedItem();
					String[] authorAndTitle = recordStr.split(" - ");
					String author = authorAndTitle[0];
					String title = authorAndTitle[1];

					// Delete the selected record
					String[] localResults = db.retrieveRecordByAuthorTitle(author, title);
					db.deleteRowById(localResults[0]);

					// Reset the GUI fields
					refreshComponents();
					recordsDropdown.setSelectedIndex(0);

					JOptionPane.showMessageDialog(null, "The record " + author + " - " + title + " has been deleted.");
				} else {
					JOptionPane.showMessageDialog(null, "Record deletion CANCLED.");
				}
			}
		};
	}

	// The 'Search Distinct Words' window
	private class SearchGUI extends JFrame {
		JComboBox<String> recordsDropdown;
		JTextField wordSearchTextField;
		JTextArea displayArea;

		// Create the panels to hold groups of components
		JPanel inputPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel outputPanel = new JPanel();

		public SearchGUI() {
			// Window attributes
			super("Search Distinct Words");
			setSize(450, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			setResizable(false);

			// Panel attributes
			inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			inputPanel.setPreferredSize(new Dimension(365, 120));
			buttonPanel.setPreferredSize(new Dimension(365, 35));
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			outputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));

			// Create the records dropdown menu
			JLabel recordsDropdownLabel = new JLabel("Select a record to search:");
			String[] records = db.retrieveAuthorsAndTitles();
			recordsDropdown = new JComboBox<>(records);
			recordsDropdown.setPreferredSize(new Dimension(365, 28));
			recordsDropdown.setBackground(Color.WHITE);
			recordsDropdown.addFocusListener(focus);

			// Create the word search field
			JLabel wordSearchLabel = new JLabel("Enter a word to search for:");
			wordSearchTextField = new JTextField();
			wordSearchTextField.setPreferredSize(new Dimension(365, 28));
			wordSearchTextField.setBackground(Color.WHITE);
			wordSearchTextField.addFocusListener(focus);

			// Create the Display button
			JButton displayButton = new JButton("Display");
			displayButton.setPreferredSize(new Dimension(100, 28));
			displayButton.addActionListener(displayClick);

			// Create the display area for data
			displayArea = new JTextArea();
			displayArea.setEditable(false);
			displayArea.setLineWrap(true);
			displayArea.setWrapStyleWord(true);
			displayArea.setBorder(BorderFactory.createLoweredBevelBorder());
			JScrollPane scrollPane = new JScrollPane(displayArea);
			scrollPane.setPreferredSize(new Dimension(365, 150));

			// Add components to the panels
			inputPanel.add(recordsDropdownLabel);
			inputPanel.add(recordsDropdown);
			inputPanel.add(wordSearchLabel);
			inputPanel.add(wordSearchTextField);
			buttonPanel.add(displayButton);
			outputPanel.add(scrollPane); // <--- The display area (within the scroll pane)

			add(inputPanel); // Add panels to the window
			add(buttonPanel);
			add(outputPanel);
		}

		// Display button Action Listener
		private final ActionListener displayClick = event -> { // File > Query Database
			Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);

			if (recordsDropdown.getSelectedItem().equals("")) {
				recordsDropdown.setBorder(redBorder);
			} else if (wordSearchTextField.getText().isEmpty()) {
				wordSearchTextField.setBorder(redBorder);
			} else {
				// Retrieve user input
				String recordStr = (String) recordsDropdown.getSelectedItem();
				String[] authorAndTitle = recordStr.split(" - ");
				String author = authorAndTitle[0];
				String title = authorAndTitle[1];

				// Value of the word textfield
				String word = wordSearchTextField.getText();

				// Retrieve the distinct words hashmap from the database
				HashMap<String, Integer> wordsHash = db.retrieveHashMapByAuthorTitle("wordsHash", author, title);
				if (word.equals("*")) {
					// If * entered display all words/counts sorted
					displayArea.setText("");
					TreeMap<String, Integer> sortedMap = new TreeMap<>(wordsHash);
					sortedMap.forEach((key, value) -> {
						displayArea.append(key + ": " + value + "\n");
					});
				} else {
					// Search hashmap for word, display word and frequency
					String msg = "";
					if (wordsHash.containsKey(word.toLowerCase())) {
						msg += "Word: " + word + "\nFrequency: " + wordsHash.get(word.toLowerCase());
					} else {
						msg += "Word: " + word + "\nFrequency: 0";
					}
					displayArea.setText(msg);
					displayArea.setFont(new Font("Serif", Font.BOLD, 12));
				}
			}
		};
	}

	// The 'Query Database' window
	private class QueryGUI extends JFrame {
		JComboBox<String> viewDropdown;
		JComboBox<String> ofDropdown;
		JComboBox<String> whereDropdown;
		JTextField equalsTextfield;
		JTextArea displayAreaQuery;

		// Create the panels to hold groups of components
		JPanel inputPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel outputPanel = new JPanel();

		// Create nested label and fields panels (these go inside the input panel)
		JPanel labelsPanel = new JPanel();
		JPanel fieldsPanel = new JPanel();

		private QueryGUI() {
			// Window attributes
			super("Query Database");
			setSize(450, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			setResizable(false);

			// Panel attributes
			inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			inputPanel.setPreferredSize(new Dimension(365, 151));
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
			buttonPanel.setPreferredSize(new Dimension(365, 35));
			outputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));

			// Nested panels attributes
			labelsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
			labelsPanel.setPreferredSize(new Dimension(85, 150));
			fieldsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 8));
			fieldsPanel.setPreferredSize(new Dimension(280, 150));

			// Create the VIEW dropdown menu and label
			JLabel viewLabel = new JLabel("VIEW:");
			viewLabel.setPreferredSize(new Dimension(90, 28));
			viewLabel.setVerticalAlignment(SwingConstants.CENTER);
			String[] viewOptions = { "Average", "Total", "Min", "Max" };
			viewDropdown = new JComboBox<>(viewOptions);
			viewDropdown.setPreferredSize(new Dimension(275, 28));
			viewDropdown.setBackground(Color.WHITE);
			viewDropdown.addFocusListener(focus);

			// Create the OF dropdown menu and label
			JLabel ofLabel = new JLabel("OF:");
			ofLabel.setPreferredSize(new Dimension(90, 28));
			ofLabel.setVerticalAlignment(SwingConstants.CENTER);
			String[] ofOptions = { "Word Count", "Distinct Word Count", "Punctuation Count", "Sentence Count",
					"Syllable Count", "Flesch Reading Ease Score", "Average Words Per Sentence",
					"Average Syllables Per Word", "Average Word Length" };
			ofDropdown = new JComboBox<>(ofOptions);
			ofDropdown.setPreferredSize(new Dimension(275, 28));
			ofDropdown.setBackground(Color.WHITE);
			ofDropdown.addFocusListener(focus);

			// Create the WHERE dropdown menu and label
			JLabel whereLabel = new JLabel("WHERE:");
			whereLabel.setPreferredSize(new Dimension(90, 28));
			whereLabel.setVerticalAlignment(SwingConstants.CENTER);
			String[] whereOptions = { "Author", "Title", "Publish Year", "Era", "Genre" };
			whereDropdown = new JComboBox<>(whereOptions);
			whereDropdown.setPreferredSize(new Dimension(275, 28));
			whereDropdown.setBackground(Color.WHITE);
			whereDropdown.addFocusListener(focus);

			// Create the EQUALS dropdown menu and label
			JLabel equalsLabel = new JLabel("EQUALS:");
			equalsLabel.setPreferredSize(new Dimension(90, 28));
			equalsLabel.setVerticalAlignment(SwingConstants.CENTER);
			equalsTextfield = new JTextField();
			equalsTextfield.setPreferredSize(new Dimension(275, 28));
			equalsTextfield.setBackground(Color.WHITE);
			equalsTextfield.addFocusListener(focus);

			// Create the Display button
			JButton displayButton = new JButton("Display");
			displayButton.setPreferredSize(new Dimension(100, 28));
			displayButton.addActionListener(displayClick);

			// Create the display area for data
			displayAreaQuery = new JTextArea(8, 40);
			displayAreaQuery.setEditable(false);
			displayAreaQuery.setLineWrap(true);
			displayAreaQuery.setWrapStyleWord(true);
			displayAreaQuery.setBorder(BorderFactory.createLoweredBevelBorder());
			JScrollPane scrollPane = new JScrollPane(displayAreaQuery);
			scrollPane.setPreferredSize(new Dimension(375, 120));

			// Add components to the panels
			labelsPanel.add(viewLabel);
			labelsPanel.add(ofLabel);
			labelsPanel.add(whereLabel);
			labelsPanel.add(equalsLabel);

			fieldsPanel.add(viewDropdown);
			fieldsPanel.add(ofDropdown);
			fieldsPanel.add(whereDropdown);
			fieldsPanel.add(equalsTextfield);

			inputPanel.add(labelsPanel);
			inputPanel.add(fieldsPanel);

			buttonPanel.add(displayButton);

			outputPanel.add(scrollPane);

			add(inputPanel); // Add panels to the window
			add(buttonPanel);
			add(outputPanel);
		}

		private final ActionListener displayClick = event -> { // File > Add Entry
			Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);

			if (viewDropdown.getSelectedItem().equals("")) {
				viewDropdown.setBorder(redBorder);
			} else if (ofDropdown.getSelectedItem().equals("")) {
				ofDropdown.setBorder(redBorder);
			} else if (whereDropdown.getSelectedItem().equals("")) {
				whereDropdown.setBorder(redBorder);
			} else if (equalsTextfield.getText().isEmpty()) {
				equalsTextfield.setBorder(redBorder);
			} else {
				// Get values of fields as strings
				String view = (String) viewDropdown.getSelectedItem();
				String of = (String) ofDropdown.getSelectedItem();
				String where = (String) whereDropdown.getSelectedItem();
				String equals = equalsTextfield.getText().toLowerCase();

				// Query the database and display results
				String[] localResults = db.sqlQuery(view, of, where, equals);
				displayAreaQuery.setText(null);
				displayAreaQuery
						.append(view + ": " + String.format("%,.2f", Double.parseDouble(localResults[0])) + "\n");
				if (localResults[1] != null) {
					displayAreaQuery.append("Author: " + localResults[1] + "\n");
				}
				if (localResults[2] != null) {
					displayAreaQuery.append("Title: " + localResults[2] + "\n");
				}
				displayAreaQuery.setFont(new Font("Serif", Font.BOLD, 12));
			}
		};
	}

	// The 'About Text Document Analyzer' window
	private class AboutGUI extends JFrame {
		private AboutGUI() {
			// Window attributes
			super("About Text Document Analyzer v" + TextDocumentAnalyzer.TDA_VERSION);
			setSize(600, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setLayout(new GridLayout(1, 1));
			setResizable(false);

			JEditorPane aboutTextArea = new JEditorPane();
			aboutTextArea.setContentType("text/html");
			aboutTextArea.setEditable(false);
			aboutTextArea.setCaretPosition(0);
			JScrollPane scrollPane = new JScrollPane(aboutTextArea);

			// Retrieve AboutHTML.html and display in About window
			try {
				URL aboutUrl = this.getClass().getResource("AboutHTML.html");
				JEditorPane abouthtml = new JEditorPane();
				abouthtml.setPage(aboutUrl);
				abouthtml.setEditable(false);
				scrollPane.getViewport().add(abouthtml);
			} catch (IOException ex) {
				aboutTextArea.setText("Could not load About page.");
			}

			add(scrollPane); // <--- Scroll pane contains About text area
		}
	}

	// The 'User Guide' window
	private class UserGuideGUI extends JFrame {
		private UserGuideGUI() {
			// Window attributes
			super("Text Document Analyzer v" + TextDocumentAnalyzer.TDA_VERSION + " User Guide");
			setSize(600, 500);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setLayout(new GridLayout(1, 1));
			setMinimumSize(new Dimension(500, 400));

			JEditorPane userGuideTextArea = new JEditorPane();
			userGuideTextArea.setContentType("text/html");
			userGuideTextArea.setEditable(false);
			JScrollPane helpScrollPane = new JScrollPane(userGuideTextArea);

			// Retrieve UserGuideHTML.html and display in UserGuide window
			try {
				URL helpUrl = this.getClass().getResource("UserGuideHTML.html");
				JEditorPane helphtml = new JEditorPane();
				helphtml.setPage(helpUrl);
				helphtml.setEditable(false);
				helpScrollPane.getViewport().add(helphtml);
			} catch (IOException ex) {
				userGuideTextArea.setText("Could not load User Guide page.");
			}

			add(helpScrollPane); // <--- Scroll pane contains User Guide text area
		}
	}

	// Focus listener to remove the red border from required fields when they gain
	// focus
	private final FocusListener focus = new FocusListener() {
		public void focusGained(FocusEvent e) {
			JComponent field = (JComponent) e.getComponent();
			Border defaultBorder = UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border");
			field.setBorder(defaultBorder);
		}

		public void focusLost(FocusEvent e) {// Empty method needed for FocusListener
		};
	};

	private void refreshComponents() {
		String[] records = db.retrieveAuthorsAndTitles();
		mainWindow.recordsDropdown.setModel(new DefaultComboBoxModel<>(records));
		mainWindow.displayArea.setText("");

		if (deleteEntryWindow != null && deleteEntryWindow.isVisible()) {
			deleteEntryWindow.recordsDropdown.setModel(new DefaultComboBoxModel<>(records));
			if (db.dataExists()) {
				deleteEntryWindow.recordsDropdown.addItem("DELETE ALL RECORDS");
			}
		}

		if (searchWindow != null && searchWindow.isVisible()) {
			searchWindow.recordsDropdown.setModel(new DefaultComboBoxModel<>(records));
			searchWindow.wordSearchTextField.setText("");
			searchWindow.displayArea.setText("");
		}
	}
}