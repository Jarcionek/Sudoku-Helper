package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final String VERSION = "1.3";
    public static final String DATE = "16/03/2012";
    
    public static void main(String[] args) {
        new MainFrame();
    }
}

/** //TODO
 * change: generate sudoku with unique solutions:
 *                  change generator to transformations + removals of numbers
 * new: auto last game save/load
 * new: save/load settings
 * new: best scores
 * new: hint - fill new number
 * new: special Sudoku Qx9x9, 1 < Q < 6
 * 
 * DONE
 * added: advanced automatic suggestions removal
 * added: if solving/validating the puzzle takes more than 2 seconds, the puzzle
 * is considered too complex and the algorithm stops
 * opt: Poss class' performance slightly improved at the little expense of the memory
 * added: advanced algorithm for suggestions removal
 * fixed: solving algorithm improved
 * fixed: sudoku is now in a scroll pane - it is possible to play 16x16 Sudoku
 * in low screen resolution
 * changed: filling a cell clears all suggestions in this cell if auto suggestions
 * remove is on - however it can be undone
 */
