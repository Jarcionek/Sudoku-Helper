package sudokuhelper;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Jaroslaw Pawlak
 */
public class MainFrame extends JFrame {
    private final NewSudokuChoicePanel newSudokuChoices;
    private final MouseListenerMethod m;
    
    private final JMenuItem newMenuItem;
    private final JMenuItem solveMenuItem;
    private final JMenuItem validateMenuItem;
    private final JMenuItem resetMenuItem;
    private final JMenuItem exitMenuItem;
    private final JSlider autoFillSlider;
    private final JMenuItem fillPoss;
    private final JCheckBox autoRemPossC;
    private final JCheckBox possAsNumC;
    private final JMenuItem aboutMenuItem;
    private final JMenuItem keysMenuItem;   
    private final JLabel possLabel;
    private int number = 0; //for typing in typeLabel
    private final JLabel typeLabel;
    private final StopwatchLabel stopwatchLabel;
    
    private Sudoku sudoku;
    private boolean possMode = false;
    
    public MainFrame() {
        super("Sudoku Helper " + Main.VERSION);
        
        newSudokuChoices = new NewSudokuChoicePanel();
        m = new MouseListenerMethod() {
            @Override
            public void exec(MouseEvent e) {
                MainFrame.this.requestFocusInWindow();
                resetLabels();

                //TODO temporary code below
                if (e.getButton() == MouseEvent.BUTTON3) {
                    sudoku.refresh();
                }
            }
        };
        
        newMenuItem = new JMenuItem("New");
        solveMenuItem = new JMenuItem("Solve");
        validateMenuItem = new JMenuItem("Validate");
        resetMenuItem = new JMenuItem("Reset");
        exitMenuItem = new JMenuItem("Exit");
        autoFillSlider = new JSlider(0, 2, 1);
        fillPoss = new JMenuItem("Fill all");
        autoRemPossC = new JCheckBox("Auto remove", true);
        possAsNumC = new JCheckBox("Numpad style", true);
        aboutMenuItem = new JMenuItem("About");
        keysMenuItem = new JMenuItem("Keys");        
        possLabel = new JLabel("");        
        typeLabel = new JLabel("");        
        stopwatchLabel = new StopwatchLabel();
        
        createJMenuBar();
                    
        sudoku = new Sudoku(autoRemPossC.isSelected(),
                autoFillSlider.getValue() >= 1,
                autoFillSlider.getValue() >= 2,
                possAsNumC.isSelected(), false, 15, 3, m);
        setContentPane(new JScrollPane(sudoku));
        resetLabels();
        stopwatchLabel.start();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int size = sudoku.getSudokuSize();
                int k = e.getKeyCode();
                boolean resetLabel = true;
                if (k == KeyEvent.VK_UP || k == KeyEvent.VK_W) {
                    sudoku.select(Sudoku.UP);
                    resetLabels();
                } else if (k == KeyEvent.VK_DOWN || k == KeyEvent.VK_S) {
                    sudoku.select(Sudoku.DOWN);
                    resetLabels();
                } else if (k == KeyEvent.VK_LEFT || k == KeyEvent.VK_A) {
                    sudoku.select(Sudoku.LEFT);
                    resetLabels();
                } else if (k == KeyEvent.VK_RIGHT || k == KeyEvent.VK_D) {
                    sudoku.select(Sudoku.RIGHT);
                    resetLabels();
                } else if (k == KeyEvent.VK_BACK_SPACE
                        || k == KeyEvent.VK_DELETE
                        || k == KeyEvent.VK_E) {
                    sudoku.clearSelected();
                } else if (k == KeyEvent.VK_SPACE) {
                    sudoku.setPossMode(possMode = !possMode);
                } else if (k == KeyEvent.VK_R
                        || k == KeyEvent.VK_U) {
                    sudoku.undo();
                    resetLabels();
                } else if (k == KeyEvent.VK_ENTER
                        || k == KeyEvent.VK_Q) {
                    if (size > 3 && number > 0 && number <= size*size) {
                        fill(number);
                    }
                } else {
                    int digit;
                    switch (k) {
                        case KeyEvent.VK_NUMPAD1:
                        case KeyEvent.VK_1: digit = 1; break;
                        case KeyEvent.VK_NUMPAD2:
                        case KeyEvent.VK_2: digit = 2; break;
                        case KeyEvent.VK_NUMPAD3:
                        case KeyEvent.VK_3: digit = 3; break;
                        case KeyEvent.VK_NUMPAD4:
                        case KeyEvent.VK_4: digit = 4; break;
                        case KeyEvent.VK_NUMPAD5:
                        case KeyEvent.VK_5: digit = 5; break;
                        case KeyEvent.VK_NUMPAD6:
                        case KeyEvent.VK_6: digit = 6; break;
                        case KeyEvent.VK_NUMPAD7:
                        case KeyEvent.VK_7: digit = 7; break;
                        case KeyEvent.VK_NUMPAD8:
                        case KeyEvent.VK_8: digit = 8; break;
                        case KeyEvent.VK_NUMPAD9:
                        case KeyEvent.VK_9: digit = 9; break;
                        case KeyEvent.VK_NUMPAD0:
                        case KeyEvent.VK_0: digit = 0; break;
                        default: return;
                    }
                    resetLabel = false;
                    if (size <= 3) {
                        if (digit > 0 && digit <= size*size) {
                            fill(digit);
                        }
                    } else {
                        number = number * 10 + digit;
                        if (number > size*size) {
                            number = digit;
                        }
                        if (number != 0) {
                            typeLabel.setText("" + number);
                        }
                    }
                }
                if (resetLabel) {
                    resetLabels();
                }
            }
            private void fill(int n) {
                sudoku.refresh();
                if (possMode) {
                    sudoku.possChange(n);
                } else {
                    if (sudoku.isAllowed(n)) {
                        sudoku.fillSelected(n);
                        if (sudoku.isAllFilled() && !stopwatchLabel.isStopped()) {
                            stopwatchLabel.stop();
                            JOptionPane.showMessageDialog(MainFrame.this,
                                    "<html><font size=5 color=green>"
                                    + "Congratulations! You solved it!<br>"
                                    + "</font><font size=5>"
                                    + "Your time: " + stopwatchLabel.getText()
                                    + "</font></html>",
                                    MainFrame.this.getTitle(),
                                    JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                }
                resetLabels();
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });
        
        pack();
//        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
    
    private void resetLabels() {
        number = 0;
        typeLabel.setText("");
        possLabel.setText(sudoku.getSudokuSize() > 2?
                          sudoku.getSelectedPoss() : "");
    }

    private void createJMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu sudokuMenu = new JMenu("Sudoku");
        JMenu settingsMenu = new JMenu("Settings");
        JMenu autosolvingMenu = new JMenu("Solver");
        JMenu suggestionsMenu = new JMenu("Suggestions");
        JMenu helpMenu = new JMenu("Help");

        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionNewSudoku();
            }
        });

        solveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSolve();
            }
        });
        
        validateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionValidate();
            }
        });
        
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.reset();
                resetLabels();
                stopwatchLabel.start();
            }
        });
        
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        
        autoFillSlider.setPaintLabels(true);
        autoFillSlider.setLabelTable(new Hashtable() {
            {
                put(0, new JLabel("off"));
                put(1, new JLabel("basic"));
                put(2, new JLabel("advanced"));
            }
        });
        autoFillSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                sudoku.setAutoFillBasic(autoFillSlider.getValue() >= 1);
                sudoku.setAutoFillAdvanced(autoFillSlider.getValue() >= 2);
            }
        });
        
        fillPoss.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.fillSuggestions();
                sudoku.refresh();
                resetLabels();
            }
        });
        
        autoRemPossC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.setAutoRemovePoss(autoRemPossC.isSelected());
            }
        });
        
        possAsNumC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.setPossAsNum(possAsNumC.isSelected());
                sudoku.refresh();
            }
        });
        
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionAbout();
            }
        });
        
        keysMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionKeys();
            }
        });

        possLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        typeLabel.setHorizontalAlignment(JLabel.CENTER);
        typeLabel.setPreferredSize(new Dimension(20, 0));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));

        stopwatchLabel.setFont(new Font("Courier New", Font.BOLD, 12));

        setJMenuBar(menuBar);
            menuBar.add(sudokuMenu);
                sudokuMenu.add(newMenuItem);
                sudokuMenu.add(solveMenuItem);
                sudokuMenu.add(validateMenuItem);
                sudokuMenu.add(resetMenuItem);
                sudokuMenu.add(exitMenuItem);
            menuBar.add(settingsMenu);
                settingsMenu.add(autosolvingMenu);
                    autosolvingMenu.add(autoFillSlider);
                settingsMenu.add(suggestionsMenu);
                    suggestionsMenu.add(autoRemPossC);
                    suggestionsMenu.add(possAsNumC);
                    suggestionsMenu.add(fillPoss);
            menuBar.add(helpMenu);
                helpMenu.add(aboutMenuItem);
                helpMenu.add(keysMenuItem);
            menuBar.add(Box.createHorizontalGlue());
            menuBar.add(possLabel);
            menuBar.add(Box.createHorizontalStrut(10));
            menuBar.add(typeLabel);
            menuBar.add(Box.createHorizontalStrut(10));
            menuBar.add(stopwatchLabel);
            menuBar.add(Box.createHorizontalStrut(5));
    }
    
    private void actionNewSudoku() {
        Object[] options = new String[] {"Create", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, newSudokuChoices,
                this.getTitle(), JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == JOptionPane.OK_OPTION) {
            sudoku = new Sudoku(autoRemPossC.isSelected(),
                    autoFillSlider.getValue() >= 1,
                    autoFillSlider.getValue() >= 2,
                    possAsNumC.isSelected(), newSudokuChoices.getFillPoss(),
                    newSudokuChoices.getInitialNumbers(),
                    newSudokuChoices.getSudokuSize(), m);
            resetLabels();
            setContentPane(new JScrollPane(sudoku));
            pack();
            stopwatchLabel.start();
        }
        revalidate();
        repaint();
    }
    
    private void actionSolve() {
        int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                "Do you really want to see the solution?",
                MainFrame.this.getTitle(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        
        if (sudoku.solve()) {
            stopwatchLabel.stop();
        } else {
            String msg = "<html><font size=5 color=red>";
            msg += "This puzzle does not have a solution";
            msg += "</font></html>";
            
            JOptionPane.showMessageDialog(MainFrame.this, msg,
                    MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void actionValidate() {
        String msg = "<html><font size=";
        if (sudoku.isPuzzleValid()) {
            msg += "8 color=green>Puzzle is valid";
        } else {
            msg += "8 color=red>Puzzle is invalid";
        }
        msg += "</font></html>";
        JOptionPane.showMessageDialog(MainFrame.this, msg,
                MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }
    
    private void actionExit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Do you really want to quit?", this.getTitle(),
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void actionAbout() {
        String msg = "<html><table>"
                + "<tr><td>Version:<td></td>" + Main.VERSION + "</td></tr>"
                + "<tr><td>Release:<td></td>" + Main.DATE + "</td></tr>"
                + "<tr><td>Author:<td></td>Jaroslaw Pawlak</td></tr>"
                + "<tr><td>Contact:<td></td>jpawlak7@gmail.com</td></tr>"
                + "</table></html>";
        JOptionPane.showMessageDialog(MainFrame.this, msg,
                MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }
    
    private void actionKeys() {
        String msg = "<html><table>"
                + "<tr><td>Select:<td></td>"
                + "Arrows, W, S, A, D,<br>left mouse button" + "</td></tr>"
                    
                + "<tr><td>Fill:<td></td>"
                + "numbers<br>"
                    
                + "<tr><td>Confirm fill:<td></td>"
                + "ENTER, Q<br>" + "<font size = 2>"
                + "for Sudoku greater than 9x9" + "</font>" + "</td></tr>"
                    
                + "<tr><td>Erase:<td></td>"
                + "BACKSPACE, DELETE, Q" + "</td></tr>"
                    
                + "<tr><td>Change<br>Suggestions:<td></td>"
                + "SPACE" + "</td></tr>"
                    
                + "<tr><td>Undo:<td></td>"
                + "R, U" + "</td></tr>"
                    
                + "</table></html>";
        
        JOptionPane.showMessageDialog(MainFrame.this, msg,
                MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }
}
