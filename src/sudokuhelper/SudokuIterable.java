package sudokuhelper;

import java.util.Iterator;

/**
 * @author Jaroslaw Pawlak
 */
public class SudokuIterable<E> implements Iterable<E> {
    public static final int NEIGHBOURS = 0;
    public static final int ALL = 1;
    public static final int ROW = 2;
    public static final int COLUMN = 3;
    public static final int SQUARE = 4;
    
    private final E[][] grid;
    private final int size;
    private final int row;
    private final int column;
    private final boolean inc;
    private final int type;
    
    /**
     * @param grid a 2D array of size: size^2 x size^2
     */
    public SudokuIterable(E[][] grid, int size, int row, int column,
                          boolean inc, int type) {
        this.grid = grid;
        this.size = size;
        this.row = row;
        this.column = column;
        this.inc = inc;
        this.type = type;
    }

    @Override
    public Iterator<E> iterator() {
        switch (type) {
            case NEIGHBOURS: return new ItrNeigh<E>(grid, size, row, column, inc);
            case ALL: return new ItrAll<E>(grid, size, row, column, inc);
            case ROW: return new ItrRow<E>(grid, size, row, column, inc);
            case COLUMN: return new ItrCol<E>(grid, size, row, column, inc);
            case SQUARE: return new ItrSqr<E>(grid, size, row, column, inc);
            default: return null;
        }
    }
    
    
    
    private class ItrAll<E> implements Iterator<E> {
        protected final E[][] grid;
        protected final int size;
        
        protected int i = 0, j = 0;
        protected final int row, column;
        private final boolean inc;

        public ItrAll(E[][] grid, int size, int row, int column, boolean inc) {
            this.grid = grid;
            this.size = size;
            this.row = row;
            this.column = column;
            this.inc = inc;
        }

        @Override
        public boolean hasNext() {
            if (!inc && i == row && j == column) {
                increase();
            }
            return i < size*size && j < size*size;
        }

        @Override
        public final E next() {
            E x = grid[i][j];
            increase();
            return x;
        }

        @Override
        public final void remove() {}
        
        protected void increase() {
            j++;
            if (j == size*size) {
                j = 0;
                i++;
            }
        }
    }
    
    
    
    private class ItrNeigh<E> extends ItrAll {
        public ItrNeigh(E[][] grid, int size, int row, int column, boolean inc) {
            super(grid, size, row, column, inc);
        }

        @Override
        public boolean hasNext() {
            while (super.hasNext()) {
                if (column / size * size <= j && j < column / size * size + size
                        && row / size * size <= i && i < row / size * size + size) {
                    return true; //square
                }
                if (i == row && (j < column / size * size || column / size * size + size <= j)) {
                    return true; //column not in square
                    
                }
                if (j == column && (i < row / size * size || row / size * size + size <= i)) {
                    return true; //row not in square
                }
                increase();
            }
            return false;
        }

        
    }
    
    
    
    private class ItrSqr<E> extends ItrAll {
        public ItrSqr(E[][] grid, int size, int row, int column, boolean inc) {
            super(grid, size, row, column, inc);
            i = row / size * size;
            j = column / size * size;
        }
        
        @Override
        public boolean hasNext() {
            return super.hasNext() && i < row / size * size + size && j < column / size * size + size;
        }
        
        @Override
        protected void increase() {
            j++;
            if (j == column / size * size + size) {
                j = column / size * size;
                i++;
            }
        }
    }
    
    
    
    private class ItrCol<E> extends ItrAll {
        public ItrCol(E[][] grid, int size, int row, int column, boolean inc) {
            super(grid, size, row, column, inc);
            j = column;
        }
        
        @Override
        protected void increase() {
            i++;
        }
    }
    
    
    
    private class ItrRow<E> extends ItrAll {
        public ItrRow(E[][] grid, int size, int row, int column, boolean inc) {
            super(grid, size, row, column, inc);
            i = row;
        }
        
        @Override
        public boolean hasNext() {
            return super.hasNext() && i == row;
        }
    }
}