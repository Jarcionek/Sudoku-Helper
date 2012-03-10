package sudokuhelper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

/**
 * @author Jaroslaw Pawlak
 */
public class Sudoku extends JPanel implements Constants {
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    
    private final Grid grid;
    private final JLabel[][] label;
    private final boolean[][] containsValue;
    
    private int selectedRow = N*N / 2;
    private int selectedColumn = N*N / 2;
    private int errorRow = N*N / 2;
    private int errorColumn = N*N / 2;
    
    public Sudoku() {
        super(new GridLayout(N*N, N*N));
        grid = new Grid();
        label = new JLabel[N*N][N*N];
        containsValue = new boolean[N*N][N*N];
        
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
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
                
                int top = i > 0 && i % N == 0? 2 : 0;
                int left = j > 0 && j % N == 0? 2 : 0;
                label[i][j].setBorder(new CompoundBorder(
                        new MatteBorder(top, left, 0, 0, Color.black),
                        new LineBorder(Color.black, 1)));
                
                final int fi = i, fj = j;
                label[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        select(fi, fj);
                    }
                });
                add(label[i][j]);
            }
        }
        
        select(selectedRow, selectedColumn);
        refresh();
    }
    
    public void select(int direction) {
        int r = selectedRow;
        int c = selectedColumn;
        
        if (direction == UP) {
            select((r-1+N*N) % (N*N), c);
        } else if (direction == DOWN) {
            select((r+1) % (N*N), c);
        } else if (direction == LEFT) {
            select(r, (c-1+N*N) % (N*N));
        } else if (direction == RIGHT) {
            select(r, (c+1) % (N*N));
        }
    }
    
    private void select(int row, int column) {
        if (grid.value(errorRow, errorColumn) != 0) {
            if (grid.isEditable(errorRow, errorColumn)) {
                label[errorRow][errorColumn].setForeground(COLOR_NORMAL);
            } else {
                label[errorRow][errorColumn].setForeground(COLOR_UNEDITABLE);
            }
        }
        
        //deselect
        Border outside = ((CompoundBorder) label[selectedRow][selectedColumn]
                .getBorder()).getOutsideBorder();
        label[selectedRow][selectedColumn].setBorder(
                   new CompoundBorder(outside, new LineBorder(Color.black, 1)));
        
        selectedRow = row;
        selectedColumn = column;
        
        //select
        outside = ((CompoundBorder) label[selectedRow][selectedColumn]
                .getBorder()).getOutsideBorder();
        label[row][column].setBorder(
                   new CompoundBorder(outside, new LineBorder(Color.red, 2)));
    }
    
    public void clearSelected() {
        if (!grid.isEditable(selectedRow, selectedColumn)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        grid.clear(selectedRow, selectedColumn);
        containsValue[selectedRow][selectedColumn] = false;
        label[selectedRow][selectedColumn].setText(
                getPossibilities(grid.poss(selectedRow, selectedColumn)));
        label[selectedRow][selectedColumn].setForeground(COLOR_POSS);
        label[selectedRow][selectedColumn].setFont(FONT_POSS);
    }
    
    public void fillSelected(int number) {
        containsValue[selectedRow][selectedColumn] = true;
        grid.fill(selectedRow, selectedColumn, number);
        label[selectedRow][selectedColumn].setText(
                "" + grid.value(selectedRow, selectedColumn));
        label[selectedRow][selectedColumn].setForeground(COLOR_NORMAL);
        label[selectedRow][selectedColumn].setFont(FONT_NORMAL);
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
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
    
    public final void refresh() {
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
                if (grid.value(i, j) == 0) {
                    if (!grid.isEditable(i, j)) {
                        System.err.println("uneditable empty cell, i = "
                                + i + ", j = " + j);
                    }
                    label[i][j].setFont(FONT_POSS);
                    label[i][j].setForeground(COLOR_POSS);
                    label[i][j].setText(getPossibilities(grid.poss(i, j)));
                } else {
                    if (grid.isEditable(i, j)) {
                        label[i][j].setFont(FONT_NORMAL);
                        label[i][j].setForeground(COLOR_NORMAL);
                    } else {
                        label[i][j].setFont(FONT_UNEDITABLE);
                        label[i][j].setForeground(COLOR_UNEDITABLE);
                    }
                    label[i][j].setText("" + grid.value(i, j));
                }
            }
        }
    }
    
    public boolean isAllowed(int number) {
        Point p = grid.isAllowed(selectedRow, selectedColumn, number);
        if (p == null) {
            return true;
        } else {
            if (grid.value(errorRow, errorColumn) != 0) {
                label[errorRow][errorColumn].setForeground(COLOR_NORMAL);
            }
            label[errorRow = p.x][errorColumn = p.y].setForeground(COLOR_ERROR);
            Toolkit.getDefaultToolkit().beep();
            return false;
        }
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
