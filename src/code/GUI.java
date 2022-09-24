package code;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Scanner;
import java.io.*;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class GUI {
    AddEntryGUI addEntryWindow; // Instantiate objects for each window.
    DeleteEntryGUI deleteEntryWindow; // This prevents opening multiple windows of the same type.
    SearchGUI searchWindow; // User can't open two 'Add Entry' windows at once.
    QueryGUI queryWindow;
    AboutGUI aboutWindow;
    UserGuideGUI userGuideWindow;
    BufferedReader file;
    Tokenizer tokenizer;

    static Database db = new Database();

    JComboBox<String> recordsDropdown;

    // This function will return the MainGUI, ready to be setVisible()
    // This starts the application
    public MainGUI getMainWindow() {
        return new MainGUI();
    }

    // The main window of the application
    private class MainGUI extends JFrame {
        // Create the panels to hold groups of components
        JPanel inputPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel outputPanel = new JPanel();
        JMenuBar topMenuBar = new TopMenuBar();

        private MainGUI() {
            // Window attributes
            super("Text Document Analyzer");
            setSize(450, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setResizable(false);

            // Panel attributes
            inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
            inputPanel.setPreferredSize(new Dimension(365, 60));
            buttonPanel.setPreferredSize(new Dimension(365, 35));
            outputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));

            // Create a label for the dropdown menu
            JLabel recordsDropdownLabel = new JLabel("Select a record to display metrics:");

            // Create the dropdown menu
            String[] options = db.retrieveTitles();
            recordsDropdown = new JComboBox<>(options);
            recordsDropdown.setPreferredSize(new Dimension(365, 28));
            recordsDropdown.setBackground(Color.WHITE);

            // Create the Display button
            JButton displayButton = new JButton("Display");
            displayButton.setPreferredSize(new Dimension(100, 28));
            displayButton.addActionListener(displayClick);

            // Create the display area for data
            JTextArea displayArea = new JTextArea(16, 40);
            displayArea.setEditable(false);
            displayArea.setLineWrap(true);
            displayArea.setWrapStyleWord(true);
            displayArea.setBorder(BorderFactory.createLoweredBevelBorder());
            JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a
                                                                   // scroll bar

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
            
            String title = (String)recordsDropdown.getSelectedItem();
            
            String[] localResults = db.retrieveRecordByTitle(title);

            for (int i = 0; i<localResults.length; i++) {
                System.out.println(localResults[i] +"\n");
            }
            
            
            System.out.println("You clicked the main window Display Button!");
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
            if (addEntryWindow == null || addEntryWindow.isVisible() == false) {
                addEntryWindow = new AddEntryGUI();
            }
            addEntryWindow.setVisible(true);
        };

        private final ActionListener deleteEntryClick = event -> { // File > Delete Entry
            if (deleteEntryWindow == null || deleteEntryWindow.isVisible() == false) {
                deleteEntryWindow = new DeleteEntryGUI();
            }
            deleteEntryWindow.setVisible(true);
        };

        private final ActionListener searchClick = event -> { // File > Search Distinct Words
            if (searchWindow == null || searchWindow.isVisible() == false) {
                searchWindow = new SearchGUI();
            }
            searchWindow.setVisible(true);
        };

        private final ActionListener queryClick = event -> { // File > Query Database
            if (queryWindow == null || queryWindow.isVisible() == false) {
                queryWindow = new QueryGUI();
            }
            queryWindow.setVisible(true);
        };

        private final ActionListener aboutClick = event -> { // About > About Text Document Analyzer
            if (aboutWindow == null || aboutWindow.isVisible() == false) {
                aboutWindow = new AboutGUI();
            }
            aboutWindow.setVisible(true);
        };

        private final ActionListener userGuideClick = event -> { // Help > User Guide
            if (userGuideWindow == null || userGuideWindow.isVisible() == false) {
                userGuideWindow = new UserGuideGUI();
            }
            userGuideWindow.setVisible(true);
        };
    }

    // The 'Add Entry' window
    private class AddEntryGUI extends JFrame {
        // declaring the input textfields to capture input with listeners
        JTextField authorTextField;
        JTextField titleTextField;
        JTextField publishYearTextField;
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
            setSize(450, 350);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setResizable(false);

            // Panel attributes
            inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
            inputPanel.setPreferredSize(new Dimension(365, 151));
            browsePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 4));
            browsePanel.setPreferredSize(new Dimension(365, 75));
            parsePanel.setPreferredSize(new Dimension(365, 35));

            // Nested panels attributes
            labelsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
            labelsPanel.setPreferredSize(new Dimension(85, 150));
            fieldsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 8));
            fieldsPanel.setPreferredSize(new Dimension(280, 150));

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

            // Create the Genre dropdown menu and label for the input panel
            JLabel genreLabel = new JLabel("Genre:");
            genreLabel.setPreferredSize(new Dimension(90, 28));
            genreLabel.setVerticalAlignment(SwingConstants.CENTER);
            String[] genres = {"", "Action", "Adventure", "Comedy", "Mystery", "Fantasy", "Historical", "Horror", "Romance", "Sci-Fi"};
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
            labelsPanel.add(genreLabel);

            fieldsPanel.add(authorTextField);
            fieldsPanel.add(titleTextField);
            fieldsPanel.add(publishYearTextField);
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

        private final ActionListener browseClick = event -> { // File > Add Entry
            browseTextField.setBorder(UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border"));

            try {
                String filePath = "";

                JFrame jf = new JFrame("Dialog");
                jf.setAlwaysOnTop(true);

                JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                int returnValue = jfc.showOpenDialog(jf);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    filePath = jfc.getSelectedFile().getAbsolutePath();
                    browseTextField.setText(filePath);
                }
                jf.dispose();

                file = new BufferedReader(new FileReader(filePath));

            } catch (FileNotFoundException ex) {
                System.out.println("File was not found.");
            }
        };

        private final ActionListener parseClick = event -> { // File > Add Entry
            Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);

            if (authorTextField.getText().isEmpty()) {
                authorTextField.setBorder(redBorder);
            } else if (titleTextField.getText().isEmpty()) {
                titleTextField.setBorder(redBorder);
            } else if ((String)genreDropdown.getSelectedItem() == "") {
                genreDropdown.setBorder(redBorder);
            } else if (browseTextField.getText().isEmpty()) {
                browseTextField.setBorder(redBorder);
            } else {
                ParserPrototype parser = new ParserPrototype();
                boolean success = parser.parseDoc(file);

                if (success) {
                    // Get all the field values
                    String author = authorTextField.getText();
                    String title = titleTextField.getText();
                    String genre = (String)genreDropdown.getSelectedItem();

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
                    String wordsHash = parser.getWordHash();
                    String punctuationHash = parser.getPunctuationHash();

                    // insert entry in table
                    db.insert(author, title, published, genre, wordCount,
                            sentenceCount, avgWordLength, avgWordsPerSentence,
                            punctuationCount, fleschScore, syllableCount, avgSyllablesPerWord, distinctWordsCount, wordsHash,
                            punctuationHash);

                    authorTextField.setText("");
                    titleTextField.setText("");
                    publishYearTextField.setText("");
                    genreDropdown.setSelectedIndex(0);
                    browseTextField.setText("");
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
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setResizable(false);

            // Panel attributes
            inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
            inputPanel.setPreferredSize(new Dimension(365, 60));
            deletePanel.setPreferredSize(new Dimension(365, 35));

            // Create a label for the dropdown menu
            JLabel recordsDropdownLabel = new JLabel("Select a record to delete:");

            // Create the records dropdown menu
            String[] records = db.retrieveTitles();
            recordsDropdown = new JComboBox<>(records);
            recordsDropdown.setPreferredSize(new Dimension(365, 28));
            recordsDropdown.setBackground(Color.WHITE);
            recordsDropdown.addFocusListener(focus);

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
            if (recordsDropdown.getSelectedItem() == "") {
                Border redBorder = BorderFactory.createLineBorder(Color.RED, 2);
                recordsDropdown.setBorder(redBorder);
            } else {
                int confirmation = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Alert!", JOptionPane.YES_NO_OPTION);
                if (confirmation == 0) {
                    System.out.println("You chose YES!");

                    // Value of the record dropdown as a string
                    String recordStr = (String)recordsDropdown.getSelectedItem();
                    recordsDropdown.setSelectedIndex(0);
                    recordsDropdown.removeItem(recordStr);

                    //CALL DELETE RECORD HERE 
                    String[] localResults = db.retrieveRecordByTitle(recordStr);
                    db.deleteRowById(localResults[0]);
                    System.out.println("record deleted");
                } else {
                    System.out.println("You chose NO!");
                }
            }
        };
    }

    // The 'Search Distinct Words' window
    private class SearchGUI extends JFrame {
        JComboBox<String> recordsDropdown;
        JTextField wordSearchTextField;

        // Create the panels to hold groups of components
        JPanel inputPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JPanel outputPanel = new JPanel();

        public SearchGUI() {
            // Window attributes
            super("Search Distinct Words");
            setSize(450, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setResizable(false);

            // Panel attributes
            inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 8));
            inputPanel.setPreferredSize(new Dimension(365, 120));
            buttonPanel.setPreferredSize(new Dimension(365, 35));
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            outputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));

            // Create a label for the dropdown menu
            JLabel recordsDropdownLabel = new JLabel("Select a record to search:");

            // Create the dropdown menu
            // array 'options' will need to be populated with database records
            String[] records = { "Test Option 1", "Test Option 2", "Test Option 3" };
            recordsDropdown = new JComboBox<>(records);
            recordsDropdown.setPreferredSize(new Dimension(365, 28));
            recordsDropdown.setBackground(Color.WHITE);
            recordsDropdown.addFocusListener(focus);

            // Create a label for the dropdown menu
            JLabel wordSearchLabel = new JLabel("Enter a word to search for:");

            // Create the dropdown menu
            // array 'options' will need to be populated with database records
            wordSearchTextField = new JTextField();
            wordSearchTextField.setPreferredSize(new Dimension(365, 28));
            wordSearchTextField.setBackground(Color.WHITE);
            wordSearchTextField.addFocusListener(focus);

            // Create the Display button
            JButton displayButton = new JButton("Display");
            displayButton.setPreferredSize(new Dimension(100, 28));
            displayButton.addActionListener(displayClick);

            // Create the display area for data
            JTextArea displayArea = new JTextArea(8, 40);
            displayArea.setEditable(false);
            displayArea.setLineWrap(true);
            displayArea.setWrapStyleWord(true);
            displayArea.setBorder(BorderFactory.createLoweredBevelBorder());
            JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a
                                                                   // scroll bar

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

            if (recordsDropdown.getSelectedItem() == "") {
                recordsDropdown.setBorder(redBorder);
            } else if (wordSearchTextField.getText().isEmpty()) {
                wordSearchTextField.setBorder(redBorder);
            } else {
                // Value of the record dropdown as a string
                String recordStr = (String)recordsDropdown.getSelectedItem();
                String word = wordSearchTextField.getText();

                    ////////////////////////////////////////////////////////////////////
                    ///////// CALL SEARCH RECORD HERE //////////////////////////////////
                    ////////////////////////////////////////////////////////////////////
            }
        };
    }

    // The 'Query Database' window
    private class QueryGUI extends JFrame {
        JComboBox<String> viewDropdown;
        JComboBox<String> ofDropdown;
        JComboBox<String> whereDropdown;
        JTextField equalsTextfield;

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
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

            // Create the Genre dropdown menu and label for the input panel
            JLabel viewLabel = new JLabel("VIEW:");
            viewLabel.setPreferredSize(new Dimension(90, 28));
            viewLabel.setVerticalAlignment(SwingConstants.CENTER);
            String[] viewOptions = { "Average", "Total", "Min/Max" };
            viewDropdown = new JComboBox<>(viewOptions);
            viewDropdown.setPreferredSize(new Dimension(275, 28));
            viewDropdown.setBackground(Color.WHITE);
            viewDropdown.addFocusListener(focus);

            // Create the Genre dropdown menu and label for the input panel
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

            // Create the Genre dropdown menu and label for the input panel
            JLabel whereLabel = new JLabel("WHERE:");
            whereLabel.setPreferredSize(new Dimension(90, 28));
            whereLabel.setVerticalAlignment(SwingConstants.CENTER);
            String[] whereOptions = { "Author", "Title", "Publish Year", "Genre" };
            whereDropdown = new JComboBox<>(whereOptions);
            whereDropdown.setPreferredSize(new Dimension(275, 28));
            whereDropdown.setBackground(Color.WHITE);
            whereDropdown.addFocusListener(focus);

            // Create the Genre dropdown menu and label for the input panel
            JLabel equalsLabel = new JLabel("EQUALS:");
            equalsLabel.setPreferredSize(new Dimension(90, 28));
            equalsLabel.setVerticalAlignment(SwingConstants.CENTER);
            equalsTextfield = new JTextField();
            equalsTextfield.setPreferredSize(new Dimension(275, 28));
            equalsTextfield.setBackground(Color.WHITE);
            equalsTextfield.addFocusListener(focus);

            // Create the Parse button
            JButton displayButton = new JButton("Display");
            displayButton.setPreferredSize(new Dimension(100, 28));
            displayButton.addActionListener(displayClick);

            // Create the display area for data
            JTextArea displayArea = new JTextArea(8, 40);
            displayArea.setEditable(false);
            displayArea.setLineWrap(true);
            displayArea.setWrapStyleWord(true);
            displayArea.setBorder(BorderFactory.createLoweredBevelBorder());
            JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a
                                                                   // scroll bar

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

            if (viewDropdown.getSelectedItem() == "") {
                viewDropdown.setBorder(redBorder);
            } else if (ofDropdown.getSelectedItem() == "") {
                ofDropdown.setBorder(redBorder);
            } else if (whereDropdown.getSelectedItem() == "") {
                whereDropdown.setBorder(redBorder);
            } else if (equalsTextfield.getText().isEmpty()) {
                equalsTextfield.setBorder(redBorder);
            } else {
                // Get values of fields as strings
                String view = (String)viewDropdown.getSelectedItem();
                String of = (String)ofDropdown.getSelectedItem();
                String where = (String)whereDropdown.getSelectedItem();
                String equals = equalsTextfield.getText();

                    ////////////////////////////////////////////////////////////////////
                    ///////// CALL QUERY RECORD HERE ///////////////////////////////////
                    ////////////////////////////////////////////////////////////////////
            }
        };
    }

    // The 'About Text Document Analyzer' window
    private class AboutGUI extends JFrame {
        private AboutGUI() {
            // Window attributes
            super("About - About Text Document Analyzer");
            setSize(500, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(1, 1));
            setResizable(false);

            String aboutText = "<html><style>html {margin:8px;}</style><h2>About the Project</h2>" +
                    "<p>The Text Document Analyzer was developed in the Fall semester by Group 4 for their " +
                    "CMSC 495 Capstone Experience course at UMGC. Team members included:</p>" +
                    "<ul>" +
                    "<li>Laura McBride (Project Manager)</li>" +
                    "<li>Jason Howarth (Test Manager)</li>" +
                    "<li>John McGuire (Technical Writer & Requirements Manager)</li>" +
                    "<li>Noah Gray (Software Developer)</li>" +
                    "<li>Daniel Nester (Software Developer)</li>" +
                    "</ul>" +
                    "<p>The software was designed to parse literary documents and collect quantitative metrics " +
                    "that would be useful for literary analysis. Development took place over 8 weeks.</p></html>";

            JEditorPane aboutTextArea = new JEditorPane("text/html", aboutText);
            aboutTextArea.setEditable(false);
            aboutTextArea.setCaretPosition(0);
            JScrollPane scrollPane = new JScrollPane(aboutTextArea); // Add text area to scroll pane so it can have a
                                                                     // scroll bar

            add(scrollPane); // <--- Scroll pane contains User Guide text area
        }
    }

    // The 'User Guide' window
    private class UserGuideGUI extends JFrame {
        private UserGuideGUI() {
            // Window attributes
            super("Help - User Guide");
            setSize(600, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(1, 1));
            setMinimumSize(new Dimension(500, 400));

            String userGuideText = "<html><style>html {margin:24px 48px;}</style>" +
                    "<h2>TEXT DOCUMENT ANALYZER USER GUIDE</h2>" +
                    "<p>The Text Document Analyzer is a stand alone application for parsing literary documents " +
                    "and extracting statistical and mathematical metrics from the written work. Extracted data " +
                    "is stored in an integrated database so that it may be recalled at the user's convenience. " +
                    "Data records can be added to the database, deleted from the database, or displayed using " +
                    "the GUI's menus and controls.</p>" +
                    "<h3>DATABASE RECORD</h3>" +
                    "<p>Records in the database consist of metadata entered by the user and metrics generated when " +
                    "a document is parsed by the application. Each record correlates to a single literary document " +
                    "that has been parsed by the application. Records can be added and deleted by the user through " +
                    "the use of the application's menu system.</p>" +
                    "<p>Metadata fields stored in a record include:</p>" +
                    "<ul>" +
                    "<li>Author</li>" +
                    "<li>Title</li>" +
                    "<li>Year published</li>" +
                    "<li>Genre</li>" +
                    "</ul>" +
                    "<p>Metrics fields stored in a record include:</p>" +
                    "<ul>" +
                    "<li>Word Count</li>" +
                    "<li>Distinct Word Count</li>" +
                    "<li>Punctuation Count</li>" +
                    "<li>Sentence Count</li>" +
                    "<li>Syllable Count</li>" +
                    "<li>Average Word Length</li>" +
                    "<li>Average Syllables Per Word</li>" +
                    "<li>Average Sentence Length</li>" +
                    "<li>Distinct Words List</li>" +
                    "<li>Distinct Punctuation List</li>" +
                    "<li>Flesch Reading Ease Score</li>" +
                    "</ul>" +
                    "<h3>HOW TO: UPLOAD TEXT DOCUMENT FOR ANALYSIS</h3>" +
                    "<p>Uploading a text document for analysis can be done using the applications menu system and " +
                    "input components. Documents uploaded will be parsed by the application for metrics and all  " +
                    "calculated data will be stored in the application database.</p>" +
                    "<ol>" +
                    "<li>Select File > Add Entry from the application's top menu. A new window will appear where you may "
                    +
                    "browse for a file and enter metadata about the document.</li>" +
                    "<li>Enter the Title, Author, Year Published, and Genre.</li>" +
                    "<li>Click the Browse button.</li>" +
                    "<li>Locate and select the document you would like to parse.</li>" +
                    "<li>Click OK.</li>" +
                    "<li>Click the Parse button.</li>" +
                    "<li>Allow the application time to parse the document.</li>" +
                    "</ol>" +
                    "<h3>HOW TO: DELETE AN ENTRY FROM THE DATABASE</h3>" +
                    "<p>Deleting an entry from the database can be done using the application's top menu. Entries must be "
                    +
                    "deleted one at a time. Entries deleted from the database cannot be recovered and must be recreated by "
                    +
                    "reparsing the original text document.</p>" +
                    "<ol>" +
                    "<li>Select File > Delete Entry from the application's menu.</li>" +
                    "<li>Use the dropdown menu to select the document record you would like to delete.</li>" +
                    "<li>Select the Delete button.</li>" +
                    "<li>Allow the application time to delete the database entry.</li>" +
                    "</ol>" +
                    "<h3>HOW TO: VIEW ANALYZED DOCUMENT'S METRICS</h3>" +
                    "<p>After parsing a text document, data can be retrieved from the database and displayed in the application's "
                    +
                    "display panel. All metadata and metrics stored in the document's database record will be displayed.</p>"
                    +
                    "<ol>" +
                    "<li>Use the dropdown menu to select the document you would like to view.</li>" +
                    "<li>Click the Display button.</li>" +
                    "<li>The metrics will appear in the display panel at the bottom of the window.</li>" +
                    "</ol>" +
                    "<h3>HOW TO: QUERY THE DATABASE</h3>" +
                    "<p>The Text Document Analyzer can also be used to perform more advanced queries on the literary data stored "
                    +
                    "in the database. Though this information is not stored in a single database record, the application can aggregate "
                    +
                    "data from multiple records sharing a common field. For example, using the Query Database window a user can ask for "
                    +
                    "the average word count of all records where the author is Edgar Allan Poe.</p>" +
                    "<ol>" +
                    "<li>Click File > Query Database in the application's top menu.</li>" +
                    "<li>Use the dropdown menus and the textbox to construct your query.</li>" +
                    "<li>Click the Display button.</li>" +
                    "<li>The results will appear in the display panel at the bottom of the window.</li>" +
                    "</ol>" +
                    "<p>Statistical and mathematical values such as Average, Total, and Min/Max can be selected using the VIEW "
                    +
                    "dropdown menu. Metrics such as Word Count and Average Word Length can be selected using the OF dropdown. "
                    +
                    "Criteria such as Author or Genre can be selected using the WHERE dropdown. The EQUALS textbox allows the "
                    +
                    "user to enter a custom value corresponding to the WHERE dropdown option, such as the value Edgar Allan Poe "
                    +
                    "for the Author option or Comedy for the Genre option.</p>" +
                    "<h3>HOW TO: SEARCH FOR DISTINCT WORDS</h3>" +
                    "<p>Once a document has been parsed, The Text Document Analyzer stores a list of every distinct word used "
                    +
                    "in the document, as well as how many times each word was used. The Search Distinct Words window can be used "
                    +
                    "to view this information. This functionality can be used to determine how many times the word \"love\" appears "
                    +
                    "in Shakespeare's play \"Much Ado About Nothing\".</p>" +
                    "<ol>" +
                    "<li>Select File > Search Distinct Words from the application's top menu.</li>" +
                    "<li>Use the dropdown menu to select a record to search.</li>" +
                    "<li>Enter a word to search for into the textfield.</li>" +
                    "<li>Click the Display button.</li>" +
                    "<li>The results will appear in the display panel at the bottom of the window.</li>" +
                    "</ol>" +
                    "<h3>HOW TO: LEARN ABOUT THE PROJECT</h3>" +
                    "<p>Information about the project is available if you would like to learn more about the application's development "
                    +
                    "team and the creation of the Text Document Analyzer.</p>" +
                    "<ol>" +
                    "<li>Select About > About Text Document Analyzer from the application's top menu.</li>" +
                    "<li>Read about the project in the new window.</li>" +
                    "</ol>";

            JEditorPane userGuideTextArea = new JEditorPane("text/html", userGuideText);
            userGuideTextArea.setEditable(false);
            userGuideTextArea.setCaretPosition(0);
            JScrollPane scrollPane = new JScrollPane(userGuideTextArea); // Add text area to scroll pane so it can have
                                                                         // a scroll bar

            add(scrollPane); // <--- Scroll pane contains User Guide text area
        }
    }

    // A focus listener to remove the red border from required fields when they gain focus
    private final FocusListener focus = new FocusListener() {
        public void focusGained(FocusEvent e) {
            JComponent field = (JComponent)e.getComponent();
            Border defaultBorder = UIManager.getLookAndFeel().getDefaults().getBorder("TextField.border");
            field.setBorder(defaultBorder);
        }
        public void focusLost(FocusEvent e) { };
    };
}