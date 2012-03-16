package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Debug {
    public static final int METHOD_CALL = 0;
    public static final int GENERATION = 1;

    private static final boolean[] array = new boolean[] {
        false, //METHOD CALL
        false, //GENERATION
        
    };
    
    static void println(Object o, int x) {
        if (array[x]) {
            System.out.println(o);
        }
    }
}
