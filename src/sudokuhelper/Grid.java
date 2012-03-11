package sudokuhelper;

import java.awt.Point;
import java.util.Iterator;
import java.util.Random;

/**
 * @author Jaroslaw Pawlak
 */
public class Grid {
    private final Cell[][] grid;
    private final int size;
    
    boolean autoAddPoss = true;
    boolean autoRemovePoss = true;
    boolean autoFillBasic = true;
    boolean autoFillAdvanced = true;

    public Grid(boolean autoAddPoss, boolean autoRemovePoss,
            boolean autoFillBasic, boolean autoFillAdvanced,
            boolean initialPoss, int initNumbers, int size) {
        this.autoAddPoss = autoAddPoss;
        this.autoRemovePoss = autoRemovePoss;
        this.autoFillBasic = autoFillBasic;
        this.autoFillAdvanced = autoFillAdvanced;
        this.size = size;
        
        grid = new Cell[size*size][size*size];
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                grid[i][j] = new Cell(i, j, initialPoss, true, size);
            }
        }
        int i = initNumbers;
        Random r = new Random();
        int x, y, number;
        while (i > 0) {
            x = r.nextInt(size*size);
            y = r.nextInt(size*size);
            number = r.nextInt(size*size);
            if ((grid[x][y].value() == 0) && (null == isAllowed(x, y, number))) {
                grid[x][y].setEditable(false);
                grid[x][y].fill(number);
                i--;
            }
        }
        if (autoRemovePoss) {
            for (Cell cell : all()) {
                for (Cell neighbour : neighbours(cell.row, cell.column, false)) {
                    if (neighbour.value() != 0) {
                        cell.poss().remove(neighbour.value());
                    }
                }
            }
        }
    }
    
    public int value(int row, int column) {
        return grid[row][column].value();
    }
    
    public Poss poss(int row, int column) {
        return grid[row][column].poss();
    }
    
    public boolean isEditable(int row, int column) {
        return grid[row][column].isEditable();
    }
    
    public void clear(int row, int column) {
        if (grid[row][column].value() == 0) {
            return;
        }
        int number = grid[row][column].clear();
        
        if (autoAddPoss) { //FIXME
            //adding to poss may break autofill because removed number
            //should not be added to all of the neighbours
            //depending on solving algorithm
            for (Cell cell : neighbours(row, column, true)) {
                if (isAllowed(cell, number)) {
                    cell.poss().add(number);
                }   
            }
//            fillAll(); //this will fix the problem if it will recalculate entire grid
        }
    }
    
    public final Point isAllowed(int row, int column, int number) {
        for (Cell neighbour : neighbours(row, column, false)) {
            if (neighbour.value() == number) {
                return new Point(neighbour.row, neighbour.column);
            }
        }
        return null;
    }
    
    private boolean isAllowed(Cell cell, int number) {
        for (Cell neighbour : neighbours(cell.row, cell.column, false)) {
            if (neighbour.value() == number) {
                return false;
            }
        }
        return true;
    }
    
    public void fill(int row, int column, int number) {
        if (!grid[row][column].isEditable()) {
            return;
        }
        if (grid[row][column].value() != 0) {
            clear(row, column);
        }
        grid[row][column].fill(number);
        
//        if (autoRemovePoss) {
//            for (Cell cell : neighbours(row, column, true)) {
//                cell.poss().remove(number);
//            }
//        }
        
        fillAll();
    }
    
    private void fillAll() {
        if (autoRemovePoss) {
            for (Cell cell : all()) {
                for (int n : cell.poss()) {
                    if (!isAllowed(cell, n)) {
                        cell.poss().remove(n);
                    }
                }
            }
        }
        
        if (autoFillBasic) {
            for (Cell cell : all()) {
                if (cell.value() == 0) {
                    if (cell.poss().size() == 1) {
                        if (isAllowed(cell, cell.poss().get())) {
                            fill(cell.row, cell.column, cell.poss().get());
                            return;
                        }
                    }
                }
            }
        }
        
        if (autoFillAdvanced) {
            for (Cell cell : all()) {
                if (cell.value() != 0) {
                    continue;
                }
                for (int n : cell.poss()) {
                    boolean possibleSomewhereElse = false;
                    for (Cell otherCell : row(cell.row, cell.column, false)) {
                        if (otherCell.value() == 0 && otherCell.poss().contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        if (isAllowed(cell, cell.poss().get())) {
                            fill(cell.row, cell.column, n);
                            return;
                        }
                    }
                    possibleSomewhereElse = false;
                    for (Cell otherCell : column(cell.row, cell.column, false)) {
                        if (otherCell.value() == 0 && otherCell.poss().contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        if (isAllowed(cell, cell.poss().get())) {
                            fill(cell.row, cell.column, n);
                            return;
                        }
                    }
                    possibleSomewhereElse = false;
                    for (Cell otherCell : square(cell.row, cell.column, false)) {
                        if (otherCell.value() == 0 && otherCell.poss().contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        if (isAllowed(cell, cell.poss().get())) {
                            fill(cell.row, cell.column, n);
                            return;
                        }
                    }
                }
            }
        }
    }
    
    
    
    private Iterable<Cell> neighbours(final int row, final int column, final boolean inc) {
        return new Iterable<Cell>() {
            @Override
            public Iterator<Cell> iterator() {
                return new ItrNeigh(row, column, inc);
            }
        };
    }
    
    private Iterable<Cell> square(final int row, final int column, final boolean inc) {
        return new Iterable<Cell>() {
            @Override
            public Iterator<Cell> iterator() {
                return new ItrSqr(row, column, inc);
            }
        };
    }
    
    private Iterable<Cell> column(final int row, final int column, final boolean inc) {
        return new Iterable<Cell>() {
            @Override
            public Iterator<Cell> iterator() {
                return new ItrCol(row, column, inc);
            }
        };
    }
    
    private Iterable<Cell> row(final int row, final int column, final boolean inc) {
        return new Iterable<Cell>() {
            @Override
            public Iterator<Cell> iterator() {
                return new ItrRow(row, column, inc);
            }
        };
    }
    
    private Iterable<Cell> all() {
        return new Iterable<Cell>() {
            @Override
            public Iterator<Cell> iterator() {
                return new ItrAll(-1, -1, true);
            }
        };
    }
    
    private Iterable<Cell> all(final int row, final int column, final boolean inc) {
        return new Iterable<Cell>() {
            @Override
            public Iterator<Cell> iterator() {
                return new ItrAll(row, column, inc);
            }
        };
    }
    
    
    
    private class ItrAll implements Iterator<Cell> {
        protected int i = 0, j = 0;
        protected final int row, column;
        private final boolean inc;

        public ItrAll(int row, int column, boolean inc) {
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
        public final Cell next() {
            Cell x = grid[i][j];
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
    
    
    
    private class ItrNeigh extends ItrAll {
        public ItrNeigh(int row, int column, boolean inc) {
            super(row, column, inc);
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
    
    
    
    private class ItrSqr extends ItrAll {
        public ItrSqr(int row, int column, boolean inc) {
            super(row, column, inc);
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
    
    
    
    private class ItrCol extends ItrAll {
        public ItrCol(int row, int column, boolean inc) {
            super(row, column, inc);
            j = column;
        }
        
        @Override
        protected void increase() {
            i++;
        }
    }
    
    
    
    private class ItrRow extends ItrAll {
        public ItrRow(int row, int column, boolean inc) {
            super(row, column, inc);
            i = row;
        }
        
        @Override
        public boolean hasNext() {
            return super.hasNext() && i == row;
        }
    }
}
