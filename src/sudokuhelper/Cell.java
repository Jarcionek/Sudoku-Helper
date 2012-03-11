package sudokuhelper;

/**
 * @author Jaroslaw Pawlak
 */
public class Cell {
    private Poss poss;
    private int value;
    private boolean editable;
    
    final int row;
    final int column;
    
    public Cell(int row, int column, boolean poss, boolean editable, int size) {
        this.row = row;
        this.column = column;
        this.poss = new Poss(poss, size);
        this.editable = editable;
        this.value = 0;
    }
    
    public Poss poss() {
        return poss;
    }
    
    public int value() {
        return value;
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public int clear() {
        int number = value;
        value = 0;
        return number;
    }
    
    public void fill(int number) {
        value = number;
    }
    
    public Cell copy() {
        Cell r = new Cell(row, column, false, editable, 0);
        r.poss = this.poss.copy();
        r.value = this.value;
        return r;
    }

    @Override
    public String toString() {
        return this.getClass() + "(editable=" + editable + ",value=" + value
                + ",row=" + row + ",column=" + column + ",poss=" + poss + ")";
    }
        
}
