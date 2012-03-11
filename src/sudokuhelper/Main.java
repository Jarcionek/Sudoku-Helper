package sudokuhelper;

import javax.swing.ToolTipManager;

/**
 * @author Jaroslaw Pawlak
 */
public class Main {
    public static final String VERSION = "1.1";
    public static final String DATE = "11/03/2012";
    
    public static void main(String[] args) {
        ToolTipManager.sharedInstance().setInitialDelay(0);
        new MainFrame();
    }
}

/** //TODO
 * history + undo in Grid
 * auto last game save/load
 */