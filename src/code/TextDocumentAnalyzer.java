package code;

import javax.swing.*;


public class TextDocumentAnalyzer {
    public static void main(String[] args) {
        // create GUI object
        GUI gui = new GUI();
        gui.db.createNewTable();
        JFrame mainWindow = gui.getMainWindow();
        mainWindow.setVisible(true);
    }
}
