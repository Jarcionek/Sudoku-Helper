package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final String VERSION = "1.2+";
    public static final String DATE = "not yet released";
    
    public static void main(String[] args) {
        new MainFrame();
    }
}

/** //TODO IMPORTANT
 * opt: isSolvable - use history instead of copying                             -done?
 * fix: solver in generate method (timeout is for single sudoku only            -done?
 * new: improve auto-fill, add 3rd level - if e.g. there are 2 Poss'es in the
 *              zone with number (a, b), then those numbers can be removed
 *              from all the other Poss'es in this zone
 *              it will be in fact advanced auto poss remover
 * change: change generator to transformations + removals of numbers
 * 
 * //TODO
 * new: auto last game save/load
 * new: save/load settings
 * new: best scores
 * new: hint - fill new number
 * new: special Sudoku Qx9x9, 1 < Q < 6
 * 
 * DONE
 * fix: sudoku is now in a scroll pane - it is possible to play 16x16 Sudoku
 * in low screen resolution
 * change: filling a cell clears all suggestions in this cell if auto suggestions
 * remove is on - however it can be undone
 */
