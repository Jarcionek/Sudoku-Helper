package sudokuhelper;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * @author Jaroslaw Pawlak
 */
public class Grid {
    
    private final Cell[][] grid;
    private final int size;
    private final Stack<Change> history = new Stack<Change>();
    
    boolean autoPossBasic = true;
    boolean autoPossAdvanced = true;
    boolean autoFillBasic = true;
    boolean autoFillAdvanced = true;
    
    private boolean initialPoss;

    private Grid(int size) {
        grid = new Cell[size*size][size*size];
        this.size = size;
    }
    
    private Grid(boolean autoRemovePoss, boolean autoFillBasic,
            boolean autoFillAdvanced, boolean initialPoss, int size) {
        this.autoPossBasic = autoRemovePoss;
        this.autoFillBasic = autoFillBasic;
        this.autoFillAdvanced = autoFillAdvanced;
        this.initialPoss = initialPoss;
        this.size = size;
        
        grid = new Cell[size*size][size*size];
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                grid[i][j] = new Cell(i, j, initialPoss, true, size);
            }
        }
    }
    
    public static Grid generate(boolean autoRemovePoss, boolean autoFillBasic,
            boolean autoFillAdvanced, boolean initialPoss, int initNumbers,
            int size) {
        while (true) {
            Grid g = new Grid(autoRemovePoss, autoFillBasic,
                    autoFillAdvanced, initialPoss, size);

            int i = initNumbers;
            Random rand = new Random();
            int x, y, number;
            while (i > 0) {
                x = rand.nextInt(size*size);
                y = rand.nextInt(size*size);
                number = rand.nextInt(size*size);
                if ((g.grid[x][y].value() == 0) && (null == g.isAllowed(x, y, number))) {
                    g.grid[x][y].setEditable(false);
                    g.grid[x][y].fill(number);
                    g.grid[x][y].poss().clear();
                    i--;
                }
            }

            
            if (g.solvedCopy() == null) {
                Debug.println("generated invalid puzzle", Debug.GENERATION);
                continue;
            }
            
            if (autoRemovePoss) {
                for (Cell cell : g.all()) {
                    for (Cell neighbour : g.neighbours(cell.row, cell.column, false)) {
                        if (neighbour.value() != 0) {
                            cell.poss().remove(neighbour.value());
                        }
                    }
                }
            }

            return g;
        }
    }
    
    /**
     * Return true if stack was not empty and GUI requires repainting.
     */
    public boolean undo() {
        Debug.println("call: undo()", Debug.METHOD_CALL);
        if (history.isEmpty()) {
            return false;
        }
        Change c;
        while (true) {
            c = history.pop();
            switch (c.operation) {
                case Change.NORMAL: grid[c.x][c.y].fill(c.number); break;
                case Change.POSS_ADD: grid[c.x][c.y].poss().add(c.number); break;
                case Change.POSS_REM: grid[c.x][c.y].poss().remove(c.number); break;
            }
            if (c.last) {
                Debug.println("\treturn: " + c, Debug.METHOD_CALL);
                return true;
            }
        }
    }
    
    /**
     * Returns true if something changed or false if unsolvable.
     */
    public boolean solve() {
        Grid solved = this.solvedCopy();
        if (solved == null) {
            return false;
        }
        
        history.add(Change.marker());
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                if (grid[i][j].value() == 0) {
                    grid[i][j].fill(solved.grid[i][j].value());
                    history.add(new Change(i, j, 0, Change.NORMAL, false));
                    if (autoPossBasic) {
                        for (int n : grid[i][j].poss()) {
                            grid[i][j].poss().remove(n);
                            history.add(new Change(i, j, n, Change.POSS_ADD, false));
                        }
                    }
                }
            }
        }
            
        return true;
    }
    
    public void reset() {
        history.clear();
        for (Cell cell : all()) {
            if (cell.isEditable()) {
                cell.clear();
                if (!initialPoss) {
                    cell.poss().clear();
                }
            }
        }
        if (initialPoss) {
            for (Cell cell : all()) {
                cell.poss().addAll();
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
    
    /**
     * Returns a copy of an object.
     */
    public Poss poss(int row, int column) {
        return grid[row][column].poss().copy();
    }
    
    public void possAdd(int row, int column, int number) {
        history.add(new Change(row, column, number, Change.POSS_REM, true));
        grid[row][column].poss().add(number);
    }
    
    public void possRemove(int row, int column, int number) {
        history.add(new Change(row, column, number, Change.POSS_ADD, true));
        grid[row][column].poss().remove(number);
    }
    
    public boolean possContains(int row, int column, int number) {
        return grid[row][column].poss().contains(number);
    }
    
    public boolean isEditable(int row, int column) {
        return grid[row][column].isEditable();
    }
    
    public void clear(int row, int column) {
        clear(row, column, true);
    }
    
    private void clear(int row, int column, boolean user) {
        if (grid[row][column].value() == 0) {
            return;
        }
        int number = grid[row][column].clear();
        history.add(new Change(row, column, number, Change.NORMAL, user));
    }
    
    /**
     * Returns null for true or the coordinates of error cell to highlight.
     */
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
    
    public void fillSuggestions() {
        history.add(Change.marker());
        for (Cell cell : all()) {
            for (int n = 1; n <= size*size; n++) {
                if (!cell.poss().contains(n)) {
                    history.add(new Change(cell.row, cell.column, n,
                            Change.POSS_REM, false));
                    //TODO opt: it may add something that will be removed later
                    cell.poss().add(n);
                }
            }
        }
        for (Cell cell : all()) {
            for (int n : cell.poss()) {
                if (!isAllowed(cell, n) || cell.value() != 0) {
                    history.add(new Change(cell.row, cell.column, n,
                            Change.POSS_ADD, false));
                    cell.poss().remove(n);
                }
            }
        }
    }
    
    public void fill(int row, int column, int number) {
        fill(row, column, number, true);
    }
    
    private void fill(int row, int column, int number, boolean user) {
        Debug.println("call: fill(" + row + ", " + column + ", " + number
                + ", " + user + ")", Debug.METHOD_CALL);
        if (!grid[row][column].isEditable()) {
            return;
        }
        history.add(new Change(row, column, grid[row][column].value(), Change.NORMAL, user));
        
        if (grid[row][column].value() != 0) {
            clear(row, column, user);
        }
        grid[row][column].fill(number);
        
        fillAll();
    }
    
    private void fillAll() {
        Debug.println("call: fillAll()", Debug.METHOD_CALL);
        if (autoPossBasic) {
            for (Cell cell : all()) {
                for (int n : cell.poss()) {
                    if (!isAllowed(cell, n) || cell.value() != 0) {
                        history.add(new Change(cell.row, cell.column, n,
                                Change.POSS_ADD, false));
                        cell.poss().remove(n);
                    }
                }
            }
        }
        
        if (autoPossAdvanced) {
            for (Cell cell : all()) {
                //rows
                int x = 1;
                for (Cell otherCell : row(cell.row, cell.column, false)) {
                    if (cell.poss().equals(otherCell.poss())) {
                        x++;
                    }
                }
                if (cell.poss().size() == x) {
                    for (Cell otherCell : row(cell.row, cell.column, false)) {
                        if (!cell.poss().equals(otherCell.poss())) {
                            for (int n : cell.poss()) {
                                if (otherCell.poss().contains(n)) {
                                    history.add(new Change(otherCell.row, otherCell.column,
                                            n, Change.POSS_ADD, false));
                                    otherCell.poss().remove(n);
                                }
                            }
                        }
                    }
                }
                
                //columns
                x = 1;
                for (Cell otherCell : column(cell.row, cell.column, false)) {
                    if (cell.poss().equals(otherCell.poss())) {
                        x++;
                    }
                }
                if (cell.poss().size() == x) {
                    for (Cell otherCell : column(cell.row, cell.column, false)) {
                        if (!cell.poss().equals(otherCell.poss())) {
                            for (int n : cell.poss()) {
                                if (otherCell.poss().contains(n)) {
                                    history.add(new Change(otherCell.row, otherCell.column,
                                            n, Change.POSS_ADD, false));
                                    otherCell.poss().remove(n);
                                }
                            }
                        }
                    }
                }
                
                //squares
                x = 1;
                for (Cell otherCell : square(cell.row, cell.column, false)) {
                    if (cell.poss().equals(otherCell.poss())) {
                        x++;
                    }
                }
                if (cell.poss().size() == x) {
                    for (Cell otherCell : square(cell.row, cell.column, false)) {
                        if (!cell.poss().equals(otherCell.poss())) {
                            for (int n : cell.poss()) {
                                if (otherCell.poss().contains(n)) {
                                    history.add(new Change(otherCell.row, otherCell.column,
                                            n, Change.POSS_ADD, false));
                                    otherCell.poss().remove(n);
                                }
                            }
                        }
                    }
                }
                
            }
        }
        
        if (autoFillBasic) {
            for (Cell cell : all()) {
                if (cell.value() != 0) {
                    continue;
                }
                if (cell.poss().size() == 1) {
                    if (isAllowed(cell, cell.poss().getFirst())) {
                        fill(cell.row, cell.column, cell.poss().getFirst(), false);
                        return;
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
                    //rows
                    boolean possibleSomewhereElse = false;
                    for (Cell otherCell : row(cell.row, cell.column, false)) {
                        if (otherCell.value() == 0 && otherCell.poss().contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        if (isAllowed(cell, cell.poss().getFirst())) {
                            fill(cell.row, cell.column, n, false);
                            return;
                        }
                    }
                    //columns
                    possibleSomewhereElse = false;
                    for (Cell otherCell : column(cell.row, cell.column, false)) {
                        if (otherCell.value() == 0 && otherCell.poss().contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        if (isAllowed(cell, cell.poss().getFirst())) {
                            fill(cell.row, cell.column, n, false);
                            return;
                        }
                    }
                    //squares
                    possibleSomewhereElse = false;
                    for (Cell otherCell : square(cell.row, cell.column, false)) {
                        if (otherCell.value() == 0 && otherCell.poss().contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        if (isAllowed(cell, cell.poss().getFirst())) {
                            fill(cell.row, cell.column, n, false);
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        String r = "";
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                r += grid[i][j].value() + " ";
            }
            r += "\n";
        }
        return r;
    }
    
    
    /**
     * Makes a copy of a grid - only values and possibilities are copied.
     * History is discarded. Solving options are set to default.
     */
    public Grid copy() {
        Grid r = new Grid(size);
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                r.grid[i][j] = grid[i][j].copy();
            }
        }
        return r;
    }
    
    public boolean isAllFilled() {
        Debug.println("call: isAllFilled()", Debug.METHOD_CALL);
        for (Cell cell : all()) {
            if (cell.value() == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns filled copy of this grid or null if unsolvable
     */
    public Grid solvedCopy() {
        Grid gridCopy = this.copy();
        
        //fill poss
        for (Cell cell : gridCopy.all()) {
            cell.poss().addAll();
            for (Cell neighbour : gridCopy.neighbours(cell.row, cell.column, false)) {
                if (neighbour.value() != 0) {
                    cell.poss().remove(neighbour.value());
                }
            }
        }
        
//        return gridCopy.privateSolveCopying();
        return gridCopy.privateSolveUndoing();
    }
    
    private Grid debuggingSolvedCopy(boolean copying) {
        Grid gridCopy = this.copy();
        
        //fill poss
        for (Cell cell : gridCopy.all()) {
            cell.poss().addAll();
            for (Cell neighbour : gridCopy.neighbours(cell.row, cell.column, false)) {
                if (neighbour.value() != 0) {
                    cell.poss().remove(neighbour.value());
                }
            }
        }
        
        if (copying) {
            return gridCopy.privateSolveCopying();
        } else {
            return gridCopy.privateSolveUndoing();
        }
    }
    
    private Grid privateSolveCopying() {
        Debug.println("call: privateSolveCopying()", Debug.METHOD_CALL);
        this.fillAll();
        if (this.isAllFilled()) {
            return this;
        }
        
        //check if there exists such a zone and such a number that there is no
        //cell in this zone where the number can be inserted
        boolean contains = false;
        for (int n = 1; n <= size*size; n++) {
            for (int i = 0; i < size*size; i++) {
                contains = false;
                for (Cell cell : row(i, -1, true)) {
                    if (cell.poss().contains(n) || cell.value() == n) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    return null;
                }
            }
            for (int i = 0; i < size*size; i++) {
                contains = false;
                for (Cell cell : column(-1, i, true)) {
                    if (cell.poss().contains(n) || cell.value() == n) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    return null;
                }
            }
            for (int i = 0; i < size*size; i += size) {
                for (int j = 0; j < size*size; j += size) {
                    contains = false;
                    for (Cell cell : square(i, j, true)) {
                        if (cell.poss().contains(n) || cell.value() == n) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        return null;
                    }
                }
            }
        }
        
        //find an empty cell
        Cell emptyCell = null;
        for (Cell cell : all()) {
            if (cell.value() == 0) {
                if (cell.poss().isEmpty()) {
                    return null;
                } else if (emptyCell == null || cell.poss().size()
                        < emptyCell.poss().size()) {
                    emptyCell = cell;
                    if (emptyCell.poss().size() == 2) {
                        break;
                    }
                }
            }
        }
        
        for (int n : emptyCell.poss()) {
            Grid gridCopy = this.copy();
            gridCopy.fill(emptyCell.row, emptyCell.column, n, true);
            Grid gridSolved = gridCopy.privateSolveCopying();
            if (gridSolved != null) {
                return gridSolved;
            }
        }
        return null;
    }
    
    private Grid privateSolveUndoing() {
        Debug.println("call: privateSolveUndoing()", Debug.METHOD_CALL);
        
        //check if there exists such a zone and such a number that there is no
        //cell in this zone where the number can be inserted
        boolean contains = false;
        for (int n = 1; n <= size*size; n++) {
            for (int i = 0; i < size*size; i++) {
                contains = false;
                for (Cell cell : row(i, -1, true)) {
                    if (cell.poss().contains(n) || cell.value() == n) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    return null;
                }
            }
            for (int i = 0; i < size*size; i++) {
                contains = false;
                for (Cell cell : column(-1, i, true)) {
                    if (cell.poss().contains(n) || cell.value() == n) {
                        contains = true;
                        break;
                    }
                }
                if (!contains) {
                    return null;
                }
            }
            for (int i = 0; i < size*size; i += size) {
                for (int j = 0; j < size*size; j += size) {
                    contains = false;
                    for (Cell cell : square(i, j, true)) {
                        if (cell.poss().contains(n) || cell.value() == n) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        return null;
                    }
                }
            }
        }
        
        //find an empty cell
        Cell emptyCell = null;
        for (Cell cell : all()) {
            if (cell.value() == 0) {
                if (cell.poss().isEmpty()) {
                    return null;
                } else if (emptyCell == null || cell.poss().size()
                        < emptyCell.poss().size()) {
                    emptyCell = cell;
                    if (emptyCell.poss().size() == 2) {
                        break;
                    }
                }
            }
        }
        
        for (int n : emptyCell.poss()) {
            fill(emptyCell.row, emptyCell.column, n, true);
            if (isAllFilled() || privateSolveUndoing() != null) {
                return this;
            } else if (!history.isEmpty()) {
                undo();
            }
        }
        return null;
    }
    
    
    
    // ITERATOR GETTERS
    
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
    
    // ITERATOR CLASSES
    
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
    
    
    
    
    
    

    
    public static void main(String[] a) {
        final int timeout = 2000;
        
        JFrame frame = new JFrame();
        final JLabel cLabel = new JLabel("a");
        final JLabel uLabel = new JLabel("b");
        final JTextPane cArea = new JTextPane();
        cArea.setContentType("text/html");
        final JTextPane uArea = new JTextPane();
        uArea.setContentType("text/html");
        final JLabel testsLabel = new JLabel("c");
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JPanel(new GridLayout(1, 2)) {
            {
                add(cLabel);
                add(uLabel);
            }
        }, BorderLayout.NORTH);
        frame.getContentPane().add(new JPanel(new GridLayout(1, 2)) {
            {
                add(new JScrollPane(cArea));
                add(new JScrollPane(uArea));
            }
        }, BorderLayout.CENTER);
        frame.getContentPane().add(testsLabel, BorderLayout.SOUTH);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        long cTotal = 0;
        long uTotal = 0;
        int tests = 0;
        int cOver = 0;
        int uOver = 0;
        
        while (true) {
            Grid grid = new Grid(true, true, true, true, 4);

            int i = 40;
            Random rand = new Random();
            int x, y, number;
            while (i > 0) {
                x = rand.nextInt(grid.size*grid.size);
                y = rand.nextInt(grid.size*grid.size);
                number = rand.nextInt(grid.size*grid.size);
                if ((grid.grid[x][y].value() == 0) && (null == grid.isAllowed(x, y, number))) {
                    grid.grid[x][y].setEditable(false);
                    grid.grid[x][y].fill(number);
                    grid.grid[x][y].poss().clear();
                    i--;
                }
            }

            for (Cell c : grid.all()) {
                if (c.value() != 0) {
                    System.out.println("grid.grid[" + c.row + "][" + c.column
                            + "].fill(" + c.value() + ");");
                }
            }

            System.out.println();
            System.out.println(grid);

            long cStart = System.currentTimeMillis();
            long cEnd;
            Grid cSolved = grid.debuggingSolvedCopy(true);
            System.out.println(cSolved);
            System.out.println("COPYING DONE IN: " + ((cEnd = System.currentTimeMillis()) - cStart));

            System.out.println();

            long uStart = System.currentTimeMillis();
            long uEnd;
            Grid uSolved = grid.debuggingSolvedCopy(false);
            System.out.println(uSolved);
            System.out.println("UNDOING DONE IN: " + ((uEnd = System.currentTimeMillis()) - uStart));

            int cTime = (int) (cEnd - cStart);
            int uTime = (int) (uEnd - uStart);
            cTotal += cTime;
            uTotal += uTime;
            tests++;
            String cString = "" + cTime;
            String uString = "" + uTime;
            
            if (cTime > timeout) {
                cString = "<font color=red><b>" + cTime + "</b></font>";
                cOver++;
            }
            if (uTime > timeout) {
                uString = "<font color=red><b>" + uTime + "</b></font>";
                uOver++;
            }
            
            String cSol = cSolved == null? "unsolvable" : "";
            String uSol = uSolved == null? "unsolvable" : "";
            if (!cSol.equals(uSol)) {
                cSol = "<font color=red><b>" + cSol + "</b></font>";
                uSol = "<font color=red><b>" + uSol + "</b></font>";
            }
            
            cArea.setText(cString + " " + cSol + "\n" + cArea.getText());
            uArea.setText(uString + " " + uSol + "\n" + uArea.getText());
            cLabel.setText("COPY: " + (cTotal / tests) + " over: " + cOver);
            uLabel.setText("UNDO: " + (uTotal / tests) + " over: " + uOver);
            String time = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    + ":" + Calendar.getInstance().get(Calendar.MINUTE)
                    + ":" + Calendar.getInstance().get(Calendar.SECOND);
            testsLabel.setText("TESTS: " + tests + "        TIME: " + time);
        }
    }
}
