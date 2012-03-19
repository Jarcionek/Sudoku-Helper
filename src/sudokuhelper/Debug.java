package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Debug {
    public static final boolean IS_ON = false;
    
    public static final int METHOD_CALL = 0;
    public static final int GENERATION = 1;

    private static final boolean[] ARRAY = new boolean[] {
        false, //METHOD CALL
        false, //GENERATION
        
    };
    
    static void println(Object o, int x) {
        if (ARRAY[x]) {
            System.out.println(o);
        }
    }
}
