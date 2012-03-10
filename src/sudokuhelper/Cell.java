package sudokuhelper;

import java.awt.Color;
import java.util.HashSet;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * @author Jaroslaw Pawlak
 */
public class Cell implements Constants {
    
    private Poss poss;
    private int value;
    private boolean editable;
    private JLabel label;
    private HashSet<Cell> neighbours;
    private Cell[] row;
    private Cell[] column;
    private Cell[] square;
    
    public Cell(boolean poss, boolean editable) {
        this.poss = new Poss(poss);
        this.editable = editable;
        this.value = 0;
        this.label = new JLabel(getPossibilities(this.poss));
        this.label.setOpaque(true);
        this.label.setBackground(new Color(255, 255, 191));
        this.label.setHorizontalAlignment(JLabel.CENTER);
        this.label.setFont(editable? FONT_POSS : FONT_UNEDITABLE);
        this.label.setForeground(editable? COLOR_POSS : COLOR_UNEDITABLE);
        this.row = new Cell[N*N-1];
        this.column = new Cell[N*N-1];
        this.square = new Cell[N*N-1];
        neighbours = new HashSet<Cell>(3*N*N - 2*N - 1);
    }
    
    public void possRemove(int n) {
        poss.remove(n);
        if (value == 0) {
            label.setText(getPossibilities(poss));
        }
    }
    
    public void possAdd(int n) {
        poss.add(n);
        if (value == 0) {
            label.setText(getPossibilities(poss));
        }
    }
    
    public boolean possContains(int n) {
        return poss.contains(n);
    }
    
    public int value() {
        return value;
    }
    
    public JLabel label() {
        return label;
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    public void clear() {
        if (value == 0) {
            return;
        }
        int number = value;
        value = 0;
        label.setFont(FONT_POSS);
        label.setForeground(COLOR_POSS);
        
        if (TEMPORARY_AUTOFILL_SUGGESTIONS) {
            possAdd(number);
            for (Cell cell : neighbours) {
                if (cell.isAllowed(number)) {
                    cell.possAdd(number);
                }   
            }
        }
    }
    
    private boolean isAllowed(int number) {
        for (Cell cell : neighbours) {
            if (cell.value == number) {
                return false;
            } 
        }
        return true;
    }
    
    public void fill(int number, boolean user) {
        if (value != 0) {
            clear();
        }
        value = number;
        label.setFont(FONT_NORMAL);
        label.setForeground(user? COLOR_NORMAL : COLOR_AUTOFILL);
        label.setText("" + number);
        
        if (TEMPORARY_AUTOFILL_SUGGESTIONS) {
            poss.remove(number);
            for (Cell cell : neighbours) {
                cell.possRemove(number);
            }
        }
        
        fillAll();
    }
    
    private void fillAll() {
        if (TEMPORARY_AUTOFILL_LEVEL <= 0) {
            return;
        }
        
        if (TEMPORARY_AUTOFILL_LEVEL >= 1) {
            for (Cell cell : neighbours) {
                if (cell.poss.size() == 1 && cell.value == 0) {
                    cell.fill(cell.poss.get(), false);
                }
            }
        }
        
        if (TEMPORARY_AUTOFILL_LEVEL >= 2) {
            for (Cell cell : neighbours) {
                if (cell.value != 0) {
                    continue;
                }
                for (int n : cell.poss) {
                    boolean possibleSomewhereElse = false;
                    for (Cell cell2 : cell.row) {
                        if (cell2.value == 0 && cell2.poss.contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        cell.fill(n, false);
                        break;
                    }
                    possibleSomewhereElse = false;
                    for (Cell cell2 : cell.column) {
                        if (cell2.value == 0 && cell2.poss.contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        cell.fill(n, false);
                        break;
                    }
                    possibleSomewhereElse = false;
                    for (Cell cell2 : cell.square) {
                        if (cell2.value == 0 && cell2.poss.contains(n)) {
                            possibleSomewhereElse = true;
                            break;
                        }
                    }
                    if (!possibleSomewhereElse) {
                        cell.fill(n, false);
                        break;
                    }
                }
            }
        }
    }
    
    public void select() {
        Border outside = ((CompoundBorder) label.getBorder()).getOutsideBorder();
        label.setBorder(new CompoundBorder(outside, new LineBorder(Color.red, 2)));
    }
    
    public void deselect() {
        Border outside = ((CompoundBorder) label.getBorder()).getOutsideBorder();
        label.setBorder(new CompoundBorder(outside, new LineBorder(Color.black, 1)));
    }
    
    
    
    
    
    public static Cell[][] generate(boolean poss) {
        Cell[][] grid = new Cell[N*N][N*N];
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
                grid[i][j] = new Cell(poss, true);
                int top = i > 0 && i % N == 0? 2 : 0;
                int left = j > 0 && j % N == 0? 2 : 0;
                grid[i][j].label.setBorder(new CompoundBorder(
                        new MatteBorder(top, left, 0, 0, Color.black),
                        new LineBorder(Color.black, 1)));
            }
        }
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
                int row = 0;
                int column = 0;
                int square = 0;
                for (int x = 0; x < N*N; x++) {
                    if (x != j) {
                        grid[i][j].row[row++] = grid[i][x];
                        grid[i][j].neighbours.add(grid[i][x]);
                    }
                    if (x != i) {
                        grid[i][j].column[column++] = grid[x][j];
                        grid[i][j].neighbours.add(grid[x][j]);
                    }
                }
                for (int x = i / N * N; x < i / N * N + N; x++) {
                    for (int y = j / N * N; y < j / N * N + N; y++) {
                        if (x != i || y != j) {
                            grid[i][j].square[square++] = grid[x][y];
                            grid[i][j].neighbours.add(grid[x][y]);
                        }
                    }
                }
            }
        }
        return grid;
    }
    
    private static String getPossibilities(Poss poss) {
        String r = "";
        String line = "";
        for (int i = 1; i <= N*N; i++) {
            line += "<td>" + (poss.contains(i)? i : "&nbsp") + "</td>";
            if (i % N == 0) {
                if (TEMPORARY_POSS_AS_NUMERICAL_KEYBOARD) {
                    r = "</tr><tr>" + line + r;
                } else {
                    r += "</tr><tr>" + line;
                }
                line = "";
            }
        }
        r = "<html><table><tr>"
                + r.substring("</tr><tr>".length())
                + "</tr></table></html>";
        return r;
    }
}
