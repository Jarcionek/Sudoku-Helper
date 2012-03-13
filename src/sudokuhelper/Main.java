package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final String VERSION = "1.2";
    public static final String DATE = "12/03/2012";
    
    public static void main(String[] args) {
        
        new MainFrame();
    }
}

/** //TODO
 * opt: isSolvable - use history instead of copying
 * new: auto last game save/load
 * new: save/load settings
 * new: best scores
 * new: hint - fill new number
 * fix: solver in generate method (timeout is for single sudoku only
 */
