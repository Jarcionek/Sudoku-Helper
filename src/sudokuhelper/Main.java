package sudokuhelper;

import java.io.File;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final String NAME = "Sudoku Helper";
    public static final String VERSION = "1.41";
    public static final String DATE = "18/03/2012";
    public static final File DIRECTORY = new File(
                                          System.getProperty("user.dir"), NAME);
    
    public static void main(String[] args) {
        if (!DIRECTORY.exists()) {
            DIRECTORY.mkdirs();
        }
        Scores.load();
        new MainFrame();
    }
}

/** //TODO
 * new: special Sudoku Qx9x9, 1 < Q < 6
 * new: save/load settings ???
 * new: auto last game save/load ???
 */
