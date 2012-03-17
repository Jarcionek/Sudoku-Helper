package sudokuhelper;

import java.io.File;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final String NAME = "Sudoku Helper";
    public static final String VERSION = "1.4";
    public static final String DATE = "17/03/2012";
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
 * 
 * DONE:
 * added: mnemonics
 * added: hint that fills random cell
 * added: best scores
 * added: window now is displayed in the centre of the screen
 * added: generating Sudoku with unique solutions, three difficulty levels
 * added: when playing non-custom Sudoku, user is asked for confirmation
 *      whether they really want to cheat (best score will not be saved then)
 * fixed: program does no longer crash when automatically solving
 *      already solved Sudoku
 */
