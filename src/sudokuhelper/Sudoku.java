package sudokuhelper;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Jaroslaw Pawlak
 */
public class Sudoku extends JFrame implements Constants {
    private Cell[][] cell;
    private int cellSelectedRow = N*N / 2;
    private int cellSelectedColumn = N*N / 2;
    private int cellErrorRow = N*N / 2;
    private int cellErrorColumn = N*N / 2;
    
    public Sudoku(boolean poss) {
        super();
        cell = Cell.generate(poss);
        JPanel contentPane = new JPanel(new GridLayout(N*N, N*N));
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
                final int fi = i;
                final int fj = j;
                cell[i][j].label().addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        select(fi, fj);
                    }
                });
                cell[i][j].label().setPreferredSize(new Dimension(50, 50));
                contentPane.add(cell[i][j].label());
            }
        }
        select(cellSelectedRow, cellSelectedColumn);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int r = Sudoku.this.cellSelectedRow;
                int c = Sudoku.this.cellSelectedColumn;
                int k = e.getKeyCode();
                if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
                    Sudoku.this.select((r-1+N*N) % (N*N), c);
                } else if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
                    Sudoku.this.select((r+1) % (N*N), c);
                } else if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
                    Sudoku.this.select(r, (c-1+N*N) % (N*N));
                } else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
                    Sudoku.this.select(r, (c+1) % (N*N));
                } else if (k == KeyEvent.VK_BACK_SPACE
                        || k == KeyEvent.VK_DELETE
                        || k == KeyEvent.VK_Q) {
                    Sudoku.this.cell[r][c].clear();
                } else {
                    int number;
                    switch (k) {
                        case KeyEvent.VK_NUMPAD1:
                        case KeyEvent.VK_1: number = 1; break;
                        case KeyEvent.VK_NUMPAD2:
                        case KeyEvent.VK_2: number = 2; break;
                        case KeyEvent.VK_NUMPAD3:
                        case KeyEvent.VK_3: number = 3; break;
                        case KeyEvent.VK_NUMPAD4:
                        case KeyEvent.VK_4: number = 4; break;
                        case KeyEvent.VK_NUMPAD5:
                        case KeyEvent.VK_5: number = 5; break;
                        case KeyEvent.VK_NUMPAD6:
                        case KeyEvent.VK_6: number = 6; break;
                        case KeyEvent.VK_NUMPAD7:
                        case KeyEvent.VK_7: number = 7; break;
                        case KeyEvent.VK_NUMPAD8:
                        case KeyEvent.VK_8: number = 8; break;
                        case KeyEvent.VK_NUMPAD9:
                        case KeyEvent.VK_9: number = 9; break;
                        default: return;
                    }
                    if (number <= 0 || number > N*N) {
                        return;
                    }
                    Sudoku.this.removeAutofillColors();
                    if (Sudoku.this.isAllowed(r, c, number)) {
                        Sudoku.this.cell[r][c].fill(number, true);
                    }
                }
            }
        });
        
        setContentPane(contentPane);
        pack();
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private void select(int row, int column) {
        if (cell[cellErrorRow][cellErrorColumn].value() != 0) {
            cell[cellErrorRow][cellErrorColumn].label().setForeground(COLOR_NORMAL);
        }
        cell[cellSelectedRow][cellSelectedColumn].deselect();
        cellSelectedRow = row;
        cellSelectedColumn = column;
        cell[cellSelectedRow][cellSelectedColumn].select();
    }

    private boolean isAllowed(int row, int column, int number) {
        if (cell[cellErrorRow][cellErrorColumn].value() != 0) {
            cell[cellErrorRow][cellErrorColumn]
                    .label().setForeground(COLOR_NORMAL);
        }
        for (int i = row / N * N; i < row / N * N + N; i++) {
            for (int j = column / N * N; j < column / N * N + N; j++) {
                if (i != cellSelectedRow || j != cellSelectedColumn) {
                    if (cell[i][j].value() == number) {
                        cell[i][j].label().setForeground(COLOR_ERROR);
                        cellErrorRow = i;
                        cellErrorColumn = j;
                        Toolkit.getDefaultToolkit().beep();
                        return false;
                    }
                }
            }
        }
        for (int i = 0; i < N*N; i++) {
            if (i != cellSelectedColumn && cell[row][i].value() == number) {
                cell[row][i].label().setForeground(COLOR_ERROR);
                cellErrorRow = row;
                cellErrorColumn = i;
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
            
            if (i != cellSelectedRow && cell[i][column].value() == number) {
                cell[i][column].label().setForeground(COLOR_ERROR);
                cellErrorRow = i;
                cellErrorColumn = column;
                Toolkit.getDefaultToolkit().beep();
                return false;
            }
        }
        return true;
    }

    private void removeAutofillColors() {
        for (int i = 0; i < N*N; i++) {
            for (int j = 0; j < N*N; j++) {
                if (cell[i][j].value() != 0) {
                    cell[i][j].label().setForeground(COLOR_NORMAL);
                }
            }
        }
    }
}
