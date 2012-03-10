package sudokuhelper;

import java.awt.Color;
import java.awt.Font;

/**
 * @author Jaroslaw Pawlak
 */
public interface Constants {
    public static final int N = 3;
    
    public static final Font FONT_NORMAL = new Font("Arial", Font.BOLD, 16);
    public static final Font FONT_POSS = new Font("Courier New", Font.BOLD, 9);
    public static final Font FONT_UNEDITABLE = new Font("Arial", Font.BOLD, 20);
    public static final Color COLOR_NORMAL = Color.black;
    public static final Color COLOR_POSS = Color.lightGray;
    public static final Color COLOR_UNEDITABLE = Color.black;
    public static final Color COLOR_ERROR = Color.red;
    public static final Color COLOR_AUTOFILL = Color.blue;
    
    public static int TEMPORARY_AUTOFILL_LEVEL = 5;
    public static boolean TEMPORARY_AUTOFILL_SUGGESTIONS = true;
    public static boolean TEMPORARY_POSS_AS_NUMERICAL_KEYBOARD = true;
}
