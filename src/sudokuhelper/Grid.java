package sudokuhelper;

import java.awt.Point;
import java.util.Random;
import java.util.Stack;

/**
 * @author Jaroslaw Pawlak
 */
public class Grid {
    public static final int TIMEOUT = 2000;
    public static final int DIFFICULTY_NONE = 0;
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;
    public static final int DIFFICULTY_EXTREME = 4;
    
    private final Cell[][] grid;
    private final int size;
    private final Stack<Change> history = new Stack<Change>();
    
    boolean autoPossBasic = true;
    boolean autoPossAdvanced = true;
    boolean autoFillBasic = true;
    boolean autoFillAdvanced = true;
    
    private boolean initialPoss;
    
    private int difficulty = DIFFICULTY_NONE;

    private Grid(int size) {
        grid = new Cell[size*size][size*size];
        this.size = size;
    }
    
    private Grid(boolean autoPossBasic, boolean autoPossAdvanced,
            boolean autoFillBasic, boolean autoFillAdvanced,
            boolean initialPoss, int size) {
        this.autoPossBasic = autoPossBasic;
        this.autoPossAdvanced = autoPossAdvanced;
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
    
    public static Grid generate(boolean autoPossBasic, boolean autoPossAdvanced,
            boolean autoFillBasic, boolean autoFillAdvanced,
            boolean initialPoss, int initNumbers, int size) {
        while (true) {
            Grid g = new Grid(autoPossBasic, autoPossAdvanced, autoFillBasic,
                    autoFillAdvanced, initialPoss, size);

            int numbers = initNumbers;
            Random rand = new Random();
            int x, y, number;
            while (numbers > 0) {
                x = rand.nextInt(size*size);
                y = rand.nextInt(size*size);
                number = rand.nextInt(size*size);
                if ((g.grid[x][y].value() == 0) && (null == g.isAllowed(x, y, number))) {
                    g.grid[x][y].setEditable(false);
                    g.grid[x][y].fill(number);
                    g.grid[x][y].poss().clear();
                    numbers--;
                }
            }
            
            Object solved = g.solvedCopy();
            if (solved == Status.UNKNOWN) {
                Debug.println("generated unknown puzzle", Debug.GENERATION);
                continue;
            } else if (solved == Status.UNSOLVABLE) {
                Debug.println("generated invalid puzzle", Debug.GENERATION);
                continue;
            }
            
            if (initialPoss) {
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
    
    public static Grid uniqueGen(boolean autoPossBasic, boolean autoPossAdvanced,
            boolean autoFillBasic, boolean autoFillAdvanced,
            boolean initialPoss, int difficulty, int size) {
        
        Grid g;
        Random rand = new Random();
        int x, y, number;
        
        //generate filled Sudoku
        while (true) {
            g = new Grid(true, true, true, true, initialPoss, size);
            
            int i = size * size;
            while (i > 0) {
                x = rand.nextInt(size*size);
                y = rand.nextInt(size*size);
                number = rand.nextInt(size*size);
                if ((g.grid[x][y].value() == 0) && (null == g.isAllowed(x, y, number))) {
                    g.grid[x][y].fill(number);
                    i--;
                }
            }
            
            Object solved = g.solvedCopy();
            if (solved.getClass() == Grid.class) {
                g = (Grid) solved;
                break;
            } else if (solved == Status.UNKNOWN) {
                Debug.println("generated unknown puzzle", Debug.GENERATION);
            } else if (solved == Status.UNSOLVABLE) {
                Debug.println("generated invalid puzzle", Debug.GENERATION);
            }
        }
        
        //set difficulty options
        g.difficulty = difficulty;
        boolean _autoPossBasic = false;
        boolean _autoPossAdvanced = false;
        boolean _autoFillBasic = false;
        boolean _autoFillAdvanced = false;
        
        switch (difficulty) {
            case DIFFICULTY_EXTREME:
            case DIFFICULTY_HARD: _autoPossAdvanced = true;
            case DIFFICULTY_MEDIUM: _autoFillAdvanced = true;
            case DIFFICULTY_EASY: _autoPossBasic = true; _autoFillBasic = true;
        }
        
        g.autoPossBasic = _autoPossBasic;
        g.autoPossAdvanced = _autoPossAdvanced;
        g.autoFillBasic = _autoFillBasic;
        g.autoFillAdvanced = _autoFillAdvanced;
        
        int numbers = 0;
        switch (difficulty) {
            case DIFFICULTY_EXTREME:
            case DIFFICULTY_HARD: numbers = size*size*size*size - 25; break;
            case DIFFICULTY_MEDIUM: numbers = size*size*size*size - 30; break;
            case DIFFICULTY_EASY: numbers = size*size*size*size - 35; break;
        }
        
        //set poss for filler
        for (Cell cell : g.all()) {
            cell.poss().addAll();
        }

        //remove some number and generate unique sudoku
        int i = size*size*size*size;
        while (i > 0) {
            x = rand.nextInt(size*size);
            y = rand.nextInt(size*size);
            number = g.grid[x][y].value();
            if (number != 0) {
                g.clear(x, y, true);
                g.history.push(Change.marker());
                if (g.fillAll().isAllFilled()) {          
                    g.undo();
                    i = size*size*size*size;
                    if (--numbers == 0) {
                        break;
                    }
                } else {
                    g.undo();
                    g.undo();
                }
            }
            i--;
        }
        Debug.println("Not removed: " + numbers, Debug.GENERATION);
        
        //set remaining values uneditable and clear their possibilities
        for (Cell cell : g.all()) {
            if (cell.value() != 0) {
                cell.poss().clear();
                cell.setEditable(false);
            }
        }
        
        //fill possibilities - user setting
        if (initialPoss) {
            for (Cell cell : g.all()) {
                cell.poss().addAll();
                for (Cell neighbour : g.neighbours(cell.row, cell.column, false)) {
                    if (neighbour.value() != 0) {
                        cell.poss().remove(neighbour.value());
                    }
                }
            }
        } else {
            for (Cell cell : g.all()) {
                cell.poss().clear();
            }
        }
        
        //set strength of user's algorithms
        g.autoPossBasic = autoPossBasic;
        g.autoPossAdvanced = autoPossAdvanced;
        g.autoFillBasic = autoFillBasic;
        g.autoFillAdvanced = autoFillAdvanced;
        g.history.clear();
        
        return g;
    }
    
    public int getDifficulty() {
        return difficulty;
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
    public Status solve() {
        Object solved = this.solvedCopy();
        if (solved.getClass() != Grid.class) {
            return (Status) solved;
        }
        
        history.add(Change.marker());
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                if (grid[i][j].value() == 0) {
                    grid[i][j].fill(((Grid) solved).grid[i][j].value());
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
            
        return Status.SOLVED;
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
    
    public void possClear(int row, int column) {
        if (grid[row][column].poss().isEmpty()) {
            return;
        }
        history.add(Change.marker());
        for (int n : grid[row][column].poss()) {
            history.add(new Change(row, column, n, Change.POSS_ADD, false));
            grid[row][column].poss().remove(n);
        }
    }
    
    public boolean isEditable(int row, int column) {
        return grid[row][column].isEditable();
    }
    
    public void clear(int row, int column) {
        clear(row, column, true);
    }
    
    private Grid clear(int row, int column, boolean user) {
        if (grid[row][column].value() != 0) {
            int number = grid[row][column].clear();
            history.add(new Change(row, column, number, Change.NORMAL, user));
        }
        return this;
    }
    
    public Cell hint() {
        if (isAllFilled()) {
            return null;
        }
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(size*size);
            y = rand.nextInt(size*size);
        } while (grid[x][y].value() != 0);
        
        Object solved = solvedCopy();
        if (solved.getClass() != Grid.class) {
            return null;
        } else {
            return ((Grid) solved).grid[x][y].copy();
        }
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
    
    private Grid fillAll() {
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
                        return this;
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
                            return this;
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
                            return this;
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
                            return this;
                        }
                    }
                }
            }
        }
        
        return this;
    }

    @Override
    public String toString() {
        String r = "";
        
//        for (int i = 0; i < size*size; i++) {
//            for (int j = 0; j < size*size; j++) {
//                if (grid[i][j].value() != 0) {
//                    r += "object.grid[" + i + "][" + j + "].fill("
//                            + grid[i][j].value() + ");\n";
//                }
//            }
//        }
        
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
    
    public Status isPuzzleValid() {
        Object solved = solvedCopy();
        if (solved.getClass() == Grid.class) {
            return Status.SOLVED;
        }
        return (Status) solved;
    }
    
    /**
     * Returns filled copy of this grid or Status UNSOLVABLE or UNKNOWN
     */
    private Object solvedCopy() {
        Debug.println("call: solvedCopy()", Debug.METHOD_CALL);
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
        
        return gridCopy.privateSolveUndoing(System.currentTimeMillis());
    }

    
    private Object privateSolveUndoing(long start) {
        Debug.println("call: privateSolveUndoing(" + start + ")", Debug.METHOD_CALL);
        
        if (System.currentTimeMillis() - start > TIMEOUT) {
            return Status.UNKNOWN;
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
                    return Status.UNSOLVABLE;
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
                    return Status.UNSOLVABLE;
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
                        return Status.UNSOLVABLE;
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
        
        if (emptyCell == null) { //if all filled
            return this;
        }
        
        for (int n : emptyCell.poss()) {
            fill(emptyCell.row, emptyCell.column, n, true);
            if (isAllFilled()) {
                return this;
            } else {
                Object solved = privateSolveUndoing(start);
                if (solved.equals(Status.UNKNOWN)) {
                    return Status.UNKNOWN;
                } else if (solved != Status.UNSOLVABLE) {
                    return solved;
                } else if (!history.isEmpty()) {
                    undo();
                }
            }
        }
        return Status.UNSOLVABLE;
    }
    
    private Iterable<Cell> neighbours(int row, int column, boolean inc) {
        return new SudokuIterable<Cell>(grid, size, row, column, inc,
                SudokuIterable.NEIGHBOURS);
    }
    
    private Iterable<Cell> square(int row, int column, boolean inc) {
        return new SudokuIterable<Cell>(grid, size, row, column, inc,
                SudokuIterable.SQUARE);
    }
    
    private Iterable<Cell> column(int row, int column, boolean inc) {
        return new SudokuIterable<Cell>(grid, size, row, column, inc,
                SudokuIterable.COLUMN);
    }
    
    private Iterable<Cell> row(int row, int column, boolean inc) {
        return new SudokuIterable<Cell>(grid, size, row, column, inc,
                SudokuIterable.ROW);
    }
    
    private Iterable<Cell> all() {
        return new SudokuIterable<Cell>(grid, size, -1, -1, true,
                SudokuIterable.ALL);
    }
}
