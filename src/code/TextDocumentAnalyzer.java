package code;

import javax.swing.*;

public class TextDocumentAnalyzer {

	// Release.Phase.Build
	public static final String TDA_VERSION = "1.0.0";

	public static void main(String[] args) {
		// create GUI object
		GUI gui = new GUI();
		gui.db.createNewTable();
		JFrame mainWindow = gui.getMainWindow();
		mainWindow.setVisible(true);
	}
}
