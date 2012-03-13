package sudokuhelper;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Jaroslaw Pawlak
 */
public class MainFrame extends JFrame {
    private final MouseListenerMethod m = new MouseListenerMethod() {
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
    
    private Sudoku sudoku;
    private boolean possMode = false;
    private newSudokuChoicePanel newSudokuChoices;
    
    private final JSlider autoFillSlider;
    private final JCheckBox autoRemPossC;
    private final JCheckBox possAsNumC;
    private final JLabel possLabel;
    private final JLabel typeLabel;
    private final StopwatchLabel stopwatchLabel;
    
    private int number = 0; //for typing
    
    public MainFrame() {
        super("Sudoku Helper " + Main.VERSION);
        
        JMenuBar menuBar = new JMenuBar();
        
        JMenu sudokuMenu = new JMenu("Sudoku");
        JMenu settingsMenu = new JMenu("Settings");
        JMenu autosolvingMenu = new JMenu("Solver");
        JMenu suggestionsMenu = new JMenu("Suggestions");
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newSudoku();
            }
        });
        
        JMenuItem solveeMenuItem = new JMenuItem("Solve");
        solveeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Do you really want to see the solution?",
                        MainFrame.this.getTitle(),
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    int status = sudoku.solve();
                    if (status == Grid.VALID) {
                        stopwatchLabel.stop();
                        return;
                    }
                    String msg = "<html><font size=5 color=";
                    if (status == Grid.INVALID) {
                        msg += "red>This puzzle does not have a solution";
                    } else if (status == Grid.UNKNOWN) {
                        msg += "orange>This puzzle could not be solved<br>"
                                + "in less than " + (Grid.TEMP_TIME_OUT/1000)
                                + " seconds";
                    }
                    msg += "</font></html>";
                    JOptionPane.showMessageDialog(MainFrame.this, msg,
                            MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
                }
            }
        });
        
        JMenuItem validateMenuItem = new JMenuItem("Validate");
        validateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = "<html><font size=";
                int status = sudoku.isPuzzleValid();
                if (status == Grid.VALID) {
                    msg += "8 color=green>Puzzle is valid";
                } else if (status == Grid.INVALID) {
                    msg += "8 color=red>Puzzle is invalid";
                } else if (status == Grid.UNKNOWN) {
                    msg += "4 color=orange>";
                        msg += "Algorithm was unable to validate this<br>";
                        msg += "puzzle in less than 5 seconds.<br>";
                        msg += "It's highly probable that puzzle is invalid.";
                }
                msg += "</font></html>";
                JOptionPane.showMessageDialog(MainFrame.this, msg,
                        MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        JMenuItem resetMenuItem = new JMenuItem("Reset");
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.reset();
                resetLabels();
                stopwatchLabel.start();
            }
        });
        
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });
        
        autoFillSlider = new JSlider(0, 2, 1);
        autosolvingMenu.add(autoFillSlider);
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
        
        JMenuItem fillPoss = new JMenuItem("Fill all");
        fillPoss.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.fillSuggestions();
                sudoku.refresh();
                resetLabels();
            }
        });
        
        autoRemPossC = new JCheckBox("Auto remove", true);
        autoRemPossC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.setAutoRemovePoss(autoRemPossC.isSelected());
            }
        });
        
        possAsNumC = new JCheckBox("Numpad style", true);
        possAsNumC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.setPossAsNum(possAsNumC.isSelected());
                sudoku.refresh();
            }
        });
        
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            private final String msg = "<html><table>"
                    + "<tr><td>Version:<td></td>" + Main.VERSION + "</td></tr>"
                    + "<tr><td>Release:<td></td>" + Main.DATE + "</td></tr>"
                    + "<tr><td>Author:<td></td>Jaroslaw Pawlak</td></tr>"
                    + "<tr><td>Contact:<td></td>jpawlak7@gmail.com</td></tr>"
                    + "</table></html>";
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, msg,
                        MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        JMenuItem keysMenuItem = new JMenuItem("Keys");
        keysMenuItem.addActionListener(new ActionListener() {
            private final String msg = "<html><table>"
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
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainFrame.this, msg,
                        MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        possLabel = new JLabel("");
        possLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        typeLabel = new JLabel("");
        typeLabel.setHorizontalAlignment(JLabel.CENTER);
        typeLabel.setPreferredSize(new Dimension(20, 0));
        typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        stopwatchLabel = new StopwatchLabel();
        stopwatchLabel.setFont(new Font("Courier New", Font.BOLD, 12));
        
        setJMenuBar(menuBar);
            menuBar.add(sudokuMenu);
                sudokuMenu.add(newMenuItem);
                sudokuMenu.add(solveeMenuItem);
                sudokuMenu.add(validateMenuItem);
                sudokuMenu.add(resetMenuItem);
                sudokuMenu.add(exitMenuItem);
            menuBar.add(settingsMenu);
                settingsMenu.add(autosolvingMenu);
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
                    
        sudoku = new Sudoku(autoRemPossC.isSelected(),
                autoFillSlider.getValue() >= 1,
                autoFillSlider.getValue() >= 2,
                possAsNumC.isSelected(), false, 15, 3, m);
        setContentPane(sudoku);
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
                exit();
            }
        });
        
        pack();
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setVisible(true);
    }
    
    private void exit() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Do you really want to quit?",
                this.getTitle(),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    private void newSudoku() {
        if (newSudokuChoices == null) {
            newSudokuChoices = new newSudokuChoicePanel();
        }
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
            setContentPane(sudoku);
            pack();
            revalidate();
            repaint();
            stopwatchLabel.start();
        }
    }
    
    private void resetLabels() {
        number = 0;
        typeLabel.setText("");
        possLabel.setText(sudoku.getSudokuSize() > 2?
                          sudoku.getSelectedPoss() : "");
    }
    
    
    
    
    
    private class newSudokuChoicePanel extends JPanel {
        private static final int X = 3;
        
        private JLabel sizeLabel = new JLabel("Size");
        private JSlider sizeSlider = new JSlider(2, 4, X);
        private JLabel initialNumbersLabel = new JLabel("Numbers filled");
        private JSlider initialNumbersSlider = new JSlider(0, max(X), max(X)/2);
        private JLabel fillPossLabel = new JLabel("Fill suggestions");
        private JCheckBox fillPossCheckBox = new JCheckBox("", true);
        
        public newSudokuChoicePanel() {
            super(new GridBagLayout());
            
            customizeComponents();
            createLayout();
        }
        
        public int getSudokuSize() {
            return sizeSlider.getValue();
        }
        
        public int getInitialNumbers() {
            return initialNumbersSlider.getValue();
        }
        
        public boolean getFillPoss() {
            return fillPossCheckBox.isSelected();
        }

        private void customizeComponents() {
            sizeSlider.setLabelTable(new Hashtable() {
                {
                    for (int i = sizeSlider.getMinimum();
                            i<= sizeSlider.getMaximum(); i++) {
                        put(i, new JLabel((i*i) + "x" + (i*i)));
                    }
                }
            });
            sizeSlider.setPaintLabels(true);
            sizeSlider.setMajorTickSpacing(1);
            sizeSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int n = sizeSlider.getValue();
                    initialNumbersSlider.setMaximum(max(n));
                    initialNumbersSlider.setLabelTable(null);
                    initialNumbersSlider.createStandardLabels(diff(n));
                    initialNumbersSlider.setMajorTickSpacing(diff(n));
                }
            });
            
            initialNumbersSlider.createStandardLabels(diff(X));
            initialNumbersSlider.setPaintLabels(true);
            initialNumbersSlider.setPaintTicks(true);
            initialNumbersSlider.setMajorTickSpacing(diff(X));
            initialNumbersSlider.setMinorTickSpacing(1);
        }

        private void createLayout() {
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(3, 3, 3, 3);
            
            c.gridx = 0;
            c.gridy = 0;
            add(sizeLabel, c);
            
            c.gridx = 0;
            c.gridx = 1;
            add(sizeSlider, c);
            
            c.gridx = 0;
            c.gridy = 1;
            add(initialNumbersLabel, c);
            
            c.gridx = 1;
            c.gridy = 1;
            add(initialNumbersSlider, c);
            
            c.gridx = 0;
            c.gridy = 2;
            add(fillPossLabel, c);
            
            c.gridx = 1;
            c.gridy = 2;
            c.anchor = GridBagConstraints.WEST;
            add(fillPossCheckBox, c);
        }
        
        private int max(int size) {
            switch (size) {
                case 2: return 3;
                case 3: return 25;
                case 4: return 50;
                default: return -1;
            }
        }
        
        private int diff(int size) {
            switch (size) {
                case 2: return 1;
                case 3: return 5;
                case 4: return 10;
                default: return -1;
            }
        }
    }
}
