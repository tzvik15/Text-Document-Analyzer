package code;

import javax.swing.*;

public class TextDocumentAnalyzer {

    // Release.Phase.Build
    public static String tdaVersion = "0.3.3";

    public static void main(String[] args) {
        // create GUI object
        GUI gui = new GUI();
        gui.db.createNewTable();
        JFrame mainWindow = gui.getMainWindow();
        mainWindow.setVisible(true);
    }
}
