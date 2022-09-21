package code;

import javax.swing.*;
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

public class GUI {
    AddEntryGUI addEntryWindow;         // Instantiate objects for each window.
    DeleteEntryGUI deleteEntryWindow;   // This prevents opening multiple windows of the same type.
    SearchGUI searchWindow;             // User can't open two 'Add Entry' windows at once.
    QueryGUI queryWindow;
    AboutGUI aboutWindow;
    UserGuideGUI userGuideWindow;
    BufferedReader file;
    Tokenizer tokenizer;

    //array for input data
    protected String[] inputArr = new String[4];

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
            // array 'options' will need to be populated with database records
            String[] options = {"Test Option 1", "Test Option 2", "Test Option 3"};
            JComboBox<String> recordsDropdown = new JComboBox<>(options);
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
            JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a scroll bar

            // Add components to the panels
            inputPanel.add(recordsDropdownLabel);
            inputPanel.add(recordsDropdown);
            buttonPanel.add(displayButton);
            outputPanel.add(scrollPane); //<--- The display area (within the scroll pane)

            add(inputPanel); // Add panels to the window
            add(buttonPanel);
            add(outputPanel);
            setJMenuBar(topMenuBar); // Add the JMenuBar to the window
        }

        // Display button Action Listener
        private final ActionListener displayClick = event -> { // File > Query Database
            System.out.println("You clicked the main window Display Button!");
        };
    }

    // The top menu bar of the main window
    private class TopMenuBar extends JMenuBar {
        private TopMenuBar() {
            // Create the File, About, and Help menu dropdown options
            JMenu topMenuFile = new JMenu("File");
            topMenuFile.setMnemonic(KeyEvent.VK_A);
            topMenuFile.getAccessibleContext().setAccessibleDescription("The File menu on the top menu of the application.");

            JMenu topMenuAbout = new JMenu("About");
            topMenuFile.setMnemonic(KeyEvent.VK_B);
            topMenuFile.getAccessibleContext().setAccessibleDescription("The About menu on the top menu of the application.");

            JMenu topMenuHelp = new JMenu("Help");
            topMenuFile.setMnemonic(KeyEvent.VK_C);
            topMenuFile.getAccessibleContext().setAccessibleDescription("The Help menu on the top menu of the application.");

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
        //declaring the input textfields to capture input with listeners
        JTextField authorTextField;
        JTextField titleTextField;
        JTextField publishYearTextField;
        JComboBox<String> genreDropdown;

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

            JLabel titleLabel = new JLabel("Title:");
            titleLabel.setPreferredSize(new Dimension(90, 28));
            titleLabel.setVerticalAlignment(SwingConstants.CENTER);
            titleTextField = new JTextField();
            titleTextField.setPreferredSize(new Dimension(275, 28));

            JLabel publishYearLabel = new JLabel("Publish Year:");
            publishYearLabel.setPreferredSize(new Dimension(90, 28));
            publishYearLabel.setVerticalAlignment(SwingConstants.CENTER);
            publishYearTextField = new JTextField();
            publishYearTextField.setPreferredSize(new Dimension(275, 28));

            // Create the Genre dropdown menu and label for the input panel
            JLabel genreLabel = new JLabel("Genre:");
            genreLabel.setPreferredSize(new Dimension(90, 28));
            genreLabel.setVerticalAlignment(SwingConstants.CENTER);
            String[] genres = {"Action", "Adventure", "Comedy", "Mystery", "Fantasy", "Historical", "Horror", "Romance", "Sci-Fi"};
            genreDropdown = new JComboBox<>(genres);
            genreDropdown.setPreferredSize(new Dimension(275, 28));
            genreDropdown.setBackground(Color.WHITE);


            // Create the Browse button, label, and textfield
            JLabel selectDocLabel = new JLabel("Select document to parse:");
            selectDocLabel.setPreferredSize(new Dimension(365, 28));
            
            JTextField browseTextField = new JTextField();
            browseTextField.setPreferredSize(new Dimension(265, 28));
            browseTextField.setEditable(false);
            browseTextField.setBackground(Color.WHITE);
            
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
            System.out.println("You clicked the add entry window Browse Button!");

            try {
                String filePath ="";
    		
            JFrame jf = new JFrame("Dialog");
            jf.setAlwaysOnTop(true);
            
    		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

    		int returnValue = jfc.showOpenDialog(jf);
    		
    		if(returnValue == JFileChooser.APPROVE_OPTION) {
    			 filePath = jfc.getSelectedFile().getAbsolutePath();
    		}
    		
    		jf.dispose();

            file = new BufferedReader(new FileReader(filePath));
            } catch (FileNotFoundException ex) {
                System.out.println("File was not found.");
            }
            

        };

        private final ActionListener parseClick = event -> { // File > Add Entry
            System.out.println("You clicked the add entry window Parse Button!");
            inputArr[0] = authorTextField.getText();
            inputArr[1] = titleTextField.getText();
            inputArr[2] = publishYearTextField.getText();
            inputArr[3] = (String)genreDropdown.getSelectedItem();

            ParserPrototype parser = new ParserPrototype(inputArr[0], inputArr[1], inputArr[2], inputArr[3]);
            parser.parseDoc(file);
        };
    }

    // The 'Delete Entry' window
    private class DeleteEntryGUI extends JFrame {
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
            String[] records = {"Test Option 1", "Test Option 2", "Test Option 3"};
            JComboBox<String> recordsDropdown = new JComboBox<>(records);
            recordsDropdown.setPreferredSize(new Dimension(365, 28));
            recordsDropdown.setBackground(Color.WHITE);

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
            System.out.println("You clicked the delete entry window Delete Button!");
        };
    }

    // The 'Search Distinct Words' window
    private class SearchGUI extends JFrame {
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
            String[] records = {"Test Option 1", "Test Option 2", "Test Option 3"};
            JComboBox<String> recordsDropdown = new JComboBox<>(records);
            recordsDropdown.setPreferredSize(new Dimension(365, 28));
            recordsDropdown.setBackground(Color.WHITE);

            // Create a label for the dropdown menu
            JLabel wordSearchLabel = new JLabel("Enter a word to search for:");

            // Create the dropdown menu
            // array 'options' will need to be populated with database records
            JTextField wordSearchTextField = new JTextField();
            wordSearchTextField.setPreferredSize(new Dimension(365, 28));
            wordSearchTextField.setBackground(Color.WHITE);

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
            JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a scroll bar

            // Add components to the panels
            inputPanel.add(recordsDropdownLabel);
            inputPanel.add(recordsDropdown);
            inputPanel.add(wordSearchLabel);
            inputPanel.add(wordSearchTextField);
            buttonPanel.add(displayButton);
            outputPanel.add(scrollPane); //<--- The display area (within the scroll pane)

            add(inputPanel); // Add panels to the window
            add(buttonPanel);
            add(outputPanel);
        }

        // Display button Action Listener
        private final ActionListener displayClick = event -> { // File > Query Database
            System.out.println("You clicked the search window Display Button!");
        };
    }

    // The 'Query Database' window
    private class QueryGUI extends JFrame {
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
            String[] viewOptions = {"Average", "Total", "Min/Max"};
            JComboBox<String> viewDropdown = new JComboBox<>(viewOptions);
            viewDropdown.setPreferredSize(new Dimension(275, 28));
            viewDropdown.setBackground(Color.WHITE);

            // Create the Genre dropdown menu and label for the input panel
            JLabel ofLabel = new JLabel("OF:");
            ofLabel.setPreferredSize(new Dimension(90, 28));
            ofLabel.setVerticalAlignment(SwingConstants.CENTER);
            String[] ofOptions = {"Word Count", "Distinct Word Count", "Punctuation Count", "Sentence Count", "Syllable Count", "Flesch Reading Ease Score", "Average Words Per Sentence", "Average Syllables Per Word", "Average Word Length"};
            JComboBox<String> ofDropdown = new JComboBox<>(ofOptions);
            ofDropdown.setPreferredSize(new Dimension(275, 28));
            ofDropdown.setBackground(Color.WHITE);

            // Create the Genre dropdown menu and label for the input panel
            JLabel whereLabel = new JLabel("WHERE:");
            whereLabel.setPreferredSize(new Dimension(90, 28));
            whereLabel.setVerticalAlignment(SwingConstants.CENTER);
            String[] whereOptions = {"Author", "Title", "Publish Year", "Genre"};
            JComboBox<String> whereDropdown = new JComboBox<>(whereOptions);
            whereDropdown.setPreferredSize(new Dimension(275, 28));
            whereDropdown.setBackground(Color.WHITE);

            // Create the Genre dropdown menu and label for the input panel
            JLabel equalsLabel = new JLabel("EQUALS:");
            equalsLabel.setPreferredSize(new Dimension(90, 28));
            equalsLabel.setVerticalAlignment(SwingConstants.CENTER);
            JTextField equalsTextfield = new JTextField();
            equalsTextfield.setPreferredSize(new Dimension(275, 28));
            equalsTextfield.setBackground(Color.WHITE);

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
            JScrollPane scrollPane = new JScrollPane(displayArea); // Add text area to scroll pane so it can have a scroll bar

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
            System.out.println("You clicked the query window Display Button!");
        };
    }

    // The 'About Text Document Analyzer' window
    private class AboutGUI extends JFrame {
        private AboutGUI() {
            // Window attributes
            super("About - About Text Document Analyzer");
            setSize(450, 300);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(1, 1));
            setResizable(false);

            String aboutText = "<html><style>html {margin:8px;}</style><h3>Lorem ipsum.</h3>" + 
                "<p>This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space.</p>" +
                "<h3>Lorem ipsum.</h3>" +
                "<p>This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space. " +
                "This is a test. I need to fill up enough space.</p></html>";

            JEditorPane aboutTextArea = new JEditorPane("text/html", aboutText);
            aboutTextArea.setEditable(false);
            aboutTextArea.setCaretPosition(0);
            JScrollPane scrollPane = new JScrollPane(aboutTextArea); // Add text area to scroll pane so it can have a scroll bar

            add(scrollPane); //<--- Scroll pane contains User Guide text area
        }
    }

    // The 'User Guide' window
    private class UserGuideGUI extends JFrame {
        private UserGuideGUI() {
            // Window attributes
            super("Help - User Guide");
            setSize(450, 450);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(1, 1));
            setMinimumSize(new Dimension(450, 450));

            String userGuideText = "<html><style>html {margin:8px;}</style><h3>Lorem ipsum.</h3>" + 
            "<p>This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space.</p>" +
            "<h3>Lorem ipsum.</h3>" +
            "<p>This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space. " +
            "This is a test. I need to fill up enough space.</p></html>";

            JEditorPane userGuideTextArea = new JEditorPane("text/html", userGuideText);
            userGuideTextArea.setEditable(false);
            userGuideTextArea.setCaretPosition(0);
            JScrollPane scrollPane = new JScrollPane(userGuideTextArea); // Add text area to scroll pane so it can have a scroll bar

            add(scrollPane); //<--- Scroll pane contains User Guide text area
        }
    }

    public static void main(String[] args) {
        GUI gui = new GUI();
        JFrame mainWindow = gui.getMainWindow();
        mainWindow.setVisible(true);
    }

}