package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Change {
    public static final int POSS_ADD = 1;
    public static final int POSS_REM = 2;
    public static final int NORMAL = 3;
    
    final int x;
    final int y;
    final int number;
    final int operation;
    final boolean last;

    public Change(int x, int y, int number, int operation, boolean last) {
        this.x = x;
        this.y = y;
        this.number = number;
        this.operation = operation;
        this.last = last;
    }
    
    public static Change marker() {
        return new Change(-1, -1, -1, -1, true);
    }

    @Override
    public String toString() {
        return "Change{" + "x=" + x + ", y=" + y + ", number=" + number
                + ", operation=" + operation + ", last=" + last + '}';
    }
    
    
}
