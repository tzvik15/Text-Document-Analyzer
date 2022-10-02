package code;

import javax.swing.*;


public class TextDocumentAnalyzer {
	
	//Release.Phase.Build
	public static String tdaVersion = "0.3.3";
	
    public static void main(String[] args) {
        // create GUI object
        GUI gui = new GUI();
        if (gui.db.dataExists()) {
            ////TO DO/////////////////////////////
            /////CALL PURGE DATA OPTION METHOD////
            //////////////////////////////////////
            System.out.println("exists");
        } else {
            System.out.println("does not exist");
            gui.db.createNewTable();
        }
        JFrame mainWindow = gui.getMainWindow();
        mainWindow.setVisible(true);
    }
}
