package sudokuhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * @author Jaroslaw Pawlak
 */
public class Sudoku extends JPanel {
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    
    private static final Font FONT_NORMAL = new Font("Arial", Font.BOLD, 16);
    private static final Font FONT_POSS = new Font("Courier New", Font.BOLD, 9);
    private static final Font FONT_UNEDITABLE = new Font("Arial", Font.BOLD, 20);
    private static final Color COLOR_NORMAL = Color.black;
    private static final Color COLOR_POSS = Color.lightGray;
    private static final Color COLOR_UNEDITABLE = Color.green.darker();
    private static final Color COLOR_ERROR = Color.red;
    private static final Color COLOR_AUTOFILL = Color.blue;
    private static final Color SELECTION_COLOR_NORMAL = Color.red;
    private static final Color SELECTION_COLOR_POSS = Color.blue;
    
    private final Grid grid;
    private final JLabel[][] label;
    private final boolean[][] containsValue;
    private final int size;
    
    private int selectedRow;
    private int selectedColumn;
    private int errorRow;
    private int errorColumn;
    
    private boolean possAsNum;
    private Color selectionColor = SELECTION_COLOR_NORMAL;
    
    public Sudoku(boolean autoPossBasic, boolean autoPossAdvanced,
            boolean autoFillBasic, boolean autoFillAdvanced, boolean possAsNum,
            boolean initialPoss, int initNumbers, int size,
            final MouseListenerMethod m, int difficulty) {
        super(new GridBagLayout());
        this.size = size;
        this.selectedRow = size*size / 2;
        this.selectedColumn = size*size / 2;
        this.errorRow = size*size / 2;
        this.errorColumn = size*size / 2;
        this.possAsNum = possAsNum;
        if (difficulty == Grid.DIFFICULTY_NONE) {
            this.grid = Grid.generate(autoPossBasic, autoPossAdvanced,
                    autoFillBasic, autoFillAdvanced, initialPoss,
                    initNumbers, size);
        } else {
            this.grid = Grid.uniqueGen(autoPossBasic, autoPossAdvanced,
                    autoFillBasic, autoFillAdvanced, initialPoss,
                    difficulty, size);
        }
        this.label = new JLabel[size*size][size*size];
        this.containsValue = new boolean[size*size][size*size];
        
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                label[i][j] = new JLabel();
                label[i][j].setPreferredSize(new Dimension(50, 50));
                label[i][j].setOpaque(true);
                label[i][j].setBackground(new Color(255, 255, 191));
                label[i][j].setHorizontalAlignment(JLabel.CENTER);
                if (grid.isEditable(i, j)) {
                    label[i][j].setFont(FONT_POSS);
                    label[i][j].setForeground(COLOR_POSS);
                } else {
                    label[i][j].setForeground(COLOR_UNEDITABLE);
                    label[i][j].setFont(FONT_UNEDITABLE);
                }
                
                int top = i > 0 && i % size == 0? 2 : 0;
                int left = j > 0 && j % size == 0? 2 : 0;
                label[i][j].setBorder(new CompoundBorder(
                        new MatteBorder(top, left, 0, 0, Color.black),
                                new CompoundBorder(new LineBorder(Color.black, 1),
                                                   new EmptyBorder(1, 1, 1, 1))));
                
                final int fi = i, fj = j;
                label[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        select(fi, fj);
                        m.exec(e);
                    }
                });
                c.gridx = j;
                c.gridy = i;
                add(label[i][j], c);
                containsValue[i][j] = grid.value(i, j) != 0;
            }
        }
        
        select(selectedRow, selectedColumn);
        refresh();
    }
    
    public int getDifficulty() {
        return grid.getDifficulty();
    }
    
    public int getSudokuSize() {
        return size;
    }
    
    public void undo() {
        if (grid.undo()) {
            for (int i = 0; i < size*size; i++) {
                for (int j = 0; j < size*size; j++) {
                    containsValue[i][j] = grid.value(i, j) != 0;
                }
            }
            refresh();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
    
    public Status solve() {
        Status status = grid.solve();
        if (status != Status.SOLVED) {
            return status;
        }

        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                if (!containsValue[i][j]) {
                    containsValue[i][j] = true;
                    label[i][j].setFont(FONT_NORMAL);
                    label[i][j].setForeground(COLOR_AUTOFILL);
                    label[i][j].setText("" + grid.value(i, j));
                }
            }
        }
 
        return status;
    }
    
    public void setPossMode(boolean c) {
        selectionColor = c? SELECTION_COLOR_POSS : SELECTION_COLOR_NORMAL;
        //select
        Border outside = ((CompoundBorder) label[selectedRow][selectedColumn]
                .getBorder()).getOutsideBorder();
        label[selectedRow][selectedColumn].setBorder(
                   new CompoundBorder(outside, new LineBorder(selectionColor, 2)));
    }
    
    public void possChange(int number) {
        if (!grid.isEditable(selectedRow, selectedColumn)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        if (grid.possContains(selectedRow, selectedColumn, number)) {
            grid.possRemove(selectedRow, selectedColumn, number);
        } else {
            grid.possAdd(selectedRow, selectedColumn, number);
        }
        if (grid.value(selectedRow, selectedColumn) == 0) {
            label[selectedRow][selectedColumn].setText(
                    getPossibilities(grid.poss(selectedRow, selectedColumn)));
        }
    }
    
    public void possClear() {
        if (!grid.isEditable(selectedRow, selectedColumn)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
       
        grid.possClear(selectedRow, selectedColumn);
        
        if (grid.value(selectedRow, selectedColumn) == 0) {
            label[selectedRow][selectedColumn].setText(
                    getPossibilities(grid.poss(selectedRow, selectedColumn)));
        }
    }
    
    public void select(int direction) {
        int r = selectedRow;
        int c = selectedColumn;
        
        if (direction == UP) {
            select((r-1+size*size) % (size*size), c);
        } else if (direction == DOWN) {
            select((r+1) % (size*size), c);
        } else if (direction == LEFT) {
            select(r, (c-1+size*size) % (size*size));
        } else if (direction == RIGHT) {
            select(r, (c+1) % (size*size));
        }
    }
    
    private void select(int row, int column) {
        refresh(errorRow, errorColumn);
        
        //deselect
        Border outside = ((CompoundBorder) label[selectedRow][selectedColumn]
                .getBorder()).getOutsideBorder();
        label[selectedRow][selectedColumn].setBorder(
                   new CompoundBorder(outside, 
                           new CompoundBorder(new LineBorder(Color.black, 1), 
                                              new EmptyBorder(1, 1, 1, 1))));
        
        selectedRow = row;
        selectedColumn = column;
        
        //select
        outside = ((CompoundBorder) label[selectedRow][selectedColumn]
                .getBorder()).getOutsideBorder();
        label[row][column].setBorder(
                   new CompoundBorder(outside, new LineBorder(selectionColor, 2)));
    }
    
    public void clearSelected() {
        if (!grid.isEditable(selectedRow, selectedColumn)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        grid.clear(selectedRow, selectedColumn);
        containsValue[selectedRow][selectedColumn] = false;
        refresh();
    }
    
    public void fillSelected(int number) {
        if (grid.value(selectedRow, selectedColumn) == number) {
            return;
        }
        containsValue[selectedRow][selectedColumn] = true;
        grid.fill(selectedRow, selectedColumn, number);
        label[selectedRow][selectedColumn].setText(
                "" + grid.value(selectedRow, selectedColumn));
        label[selectedRow][selectedColumn].setForeground(COLOR_NORMAL);
        label[selectedRow][selectedColumn].setFont(FONT_NORMAL);
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                if (grid.value(i, j) != 0) {
                    if (!containsValue[i][j]) {
                        containsValue[i][j] = true;
                        label[i][j].setText("" + grid.value(i, j));
                        label[i][j].setForeground(COLOR_AUTOFILL);
                        label[i][j].setFont(FONT_NORMAL);
                    } 
                } else {
                    label[i][j].setText(getPossibilities(grid.poss(i, j)));
                }
            }
        }
    }
    
    public boolean isAllFilled() {
        return grid.isAllFilled();
    }
    
    public void fillSuggestions() {
        grid.fillSuggestions();
    }
    
    public void refresh(int row, int column) {
        if (grid.value(row, column) == 0) {
            if (!grid.isEditable(row, column)) {
                System.err.println("uneditable empty cell, i = "
                        + row + ", j = " + column);
            }
            label[row][column].setFont(FONT_POSS);
            label[row][column].setForeground(COLOR_POSS);
            label[row][column].setText(getPossibilities(grid.poss(row, column)));
        } else {
            if (grid.isEditable(row, column)) {
                label[row][column].setFont(FONT_NORMAL);
                label[row][column].setForeground(COLOR_NORMAL);
            } else {
                label[row][column].setFont(FONT_UNEDITABLE);
                label[row][column].setForeground(COLOR_UNEDITABLE);
            }
            label[row][column].setText("" + grid.value(row, column));
        }
    }
    
    public final void refresh() {
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                refresh(i, j);
            }
        }
    }
    
    public boolean isAllowed(int number) {
        if (!grid.isEditable(selectedRow, selectedColumn)) {
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
        Point p = grid.isAllowed(selectedRow, selectedColumn, number);
        if (p == null) {
            return true;
        } else {
            if (grid.value(errorRow, errorColumn) != 0
                    && grid.isEditable(errorRow, errorColumn)) {
                label[errorRow][errorColumn].setForeground(COLOR_NORMAL);
            }
            label[errorRow = p.x][errorColumn = p.y].setForeground(COLOR_ERROR);
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
    }
    
    /**
     * @return false if hint was not available. True otherwise.
     */
    public boolean hint() {
        final Cell c = grid.hint();
        if (c == null) {
            return false;
        }
        select(c.row, c.column);
        fillSelected(c.value());
        return true;
    }
    
    public void setAutoPossBasic(boolean c) {
        grid.autoPossBasic = c;
    }
    
    public void setAutoPossAdvanced(boolean c) {
        grid.autoPossAdvanced = c;
    }
    
    public void setAutoFillBasic(boolean c) {
        grid.autoFillBasic = c;
    }
    
    public void setAutoFillAdvanced(boolean c) {
        grid.autoFillAdvanced = c;
    }
    
    public void setPossAsNum(boolean c) {
        possAsNum = c;
    }
    
    public void reset() {
        grid.reset();
        for (int i = 0; i < size*size; i++) {
            for (int j = 0; j < size*size; j++) {
                containsValue[i][j] = !grid.isEditable(i, j);
            }
        }
        refresh();
    }
    
    public Status isPuzzleValid() {
        return grid.isPuzzleValid();
    }
    
    public String getSelectedPoss() {
        String r = "";
        Poss poss = grid.poss(selectedRow, selectedColumn);
        for (int n : poss) {
            r += " " + n;
        }
        return r.length() > 0? r.substring(1) : r;
    }
    
    private String getPossibilities(Poss poss) {
        String r = "";
        if (size <= 3) {
            String line = "";
            for (int i = 1; i <= size*size; i++) {
                line += "<td>" + (poss.contains(i)? i : "&nbsp") + "</td>";
                if (i % size == 0) {
                    if (possAsNum) {
                        r = "</tr><tr>" + line + r;
                    } else {
                        r += "</tr><tr>" + line;
                    }
                    line = "";
                }
            }
        } else {
            int j = 0;
            for (int n : poss) {
                if (j == 9) {
                    break;
                }
                if (j++ % 3 == 0) {
                    r += "</tr><tr>";
                }
                r += "<td>" + n + "</td>";
            }
            if (r.length() == 0) {
                r = "</tr><tr>";
            }
        }
        r = "<html><table><tr>"
                    + r.substring("</tr><tr>".length())
                    + "</tr></table></html>";
        return r;
    }
}
