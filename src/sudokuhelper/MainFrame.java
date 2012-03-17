package sudokuhelper;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
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
import javax.swing.JSeparator;
import javax.swing.JSlider;

/**
 * @author Jaroslaw Pawlak
 */
public class MainFrame extends JFrame {
    private final NewSudokuChoicePanel newSudokuChoices;
    private final MouseListenerMethod m;
    
    private final JMenuItem easyGameMenuItem;
    private final JMenuItem mediumGameMenuItem;
    private final JMenuItem hardGameMenuItem;
    private final JMenuItem customMenuItem;
    private final JMenuItem solveMenuItem;
    private final JMenuItem validateMenuItem;
    private final JMenuItem hintMenuItem;
    private final JMenuItem resetMenuItem;
    private final JMenuItem exitMenuItem;
    private final JSlider autoFillSlider;
    private final JMenuItem fillPossMenuItem;
    private final JSlider autoPossSlider;
    private final JCheckBox possAsNumStyle;
    private final JMenuItem easyScoresMenuItem;
    private final JMenuItem mediumScoresMenuItem;
    private final JMenuItem hardScoresMenuItem;
    private final JMenuItem extremeScoresMenuItem;
    private final JMenuItem aboutMenuItem;
    private final JMenuItem keysMenuItem;   
    private final JLabel possLabel;
    private int number = 0; //for typing in typeLabel
    private final JLabel typeLabel;
    private final StopwatchLabel stopwatchLabel;
    
    private Sudoku sudoku;
    private boolean possMode = false;
    
    private boolean cheating = true;
    
    public MainFrame() {
        super(Main.NAME + " " + Main.VERSION);
        
        newSudokuChoices = new NewSudokuChoicePanel();
        m = new MouseListenerMethod() {
            @Override
            public void exec(MouseEvent e) {
                MainFrame.this.requestFocusInWindow();
                resetLabels();
            }
        };

        easyGameMenuItem = new JMenuItem("Easy");
        mediumGameMenuItem = new JMenuItem("Medium");
        hardGameMenuItem = new JMenuItem("Hard");
        customMenuItem = new JMenuItem("Custom");
        solveMenuItem = new JMenuItem("Solve");
        validateMenuItem = new JMenuItem("Validate");
        hintMenuItem = new JMenuItem("Hint");
        resetMenuItem = new JMenuItem("Reset");
        exitMenuItem = new JMenuItem("Exit");
        autoFillSlider = new JSlider(0, 2, 1);
        fillPossMenuItem = new JMenuItem("Fill all");
        autoPossSlider = new JSlider(0, 2, 1);
        possAsNumStyle = new JCheckBox("Numpad style", false);
        easyScoresMenuItem = new JMenuItem("Easy");
        mediumScoresMenuItem = new JMenuItem("Medium");
        hardScoresMenuItem = new JMenuItem("Hard");
        extremeScoresMenuItem = new JMenuItem("Extreme");
        aboutMenuItem = new JMenuItem("About");
        keysMenuItem = new JMenuItem("Keys");        
        possLabel = new JLabel("");        
        typeLabel = new JLabel("");        
        stopwatchLabel = new StopwatchLabel();
        
        createJMenuBar();
                    
        sudoku = new Sudoku(autoPossSlider.getValue() >= 1,
                autoPossSlider.getValue() >= 2,
                autoFillSlider.getValue() >= 1,
                autoFillSlider.getValue() >= 2,
                possAsNumStyle.isSelected(), false, 15, 3, m,
                Grid.DIFFICULTY_NONE);
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
                        || k == KeyEvent.VK_U
                        || k == KeyEvent.VK_Z) {
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
                } else if (sudoku.isAllowed(n)) {
                    sudoku.fillSelected(n);
                    if (sudoku.isAllFilled() && !stopwatchLabel.isStopped()) {
                        stopwatchLabel.stop();
                        boolean askForName = false;
                        String msg = "<html><font size=5>";
                        if (cheating) {
                            if (sudoku.getDifficulty() != Grid.DIFFICULTY_NONE) {
                                msg += "Cheater!<br>";
                            }
                        } else {
                            if (Scores.isBestResult(sudoku.getDifficulty(),
                                    stopwatchLabel.getTime())) {
                                askForName = true;
                                msg += "<font color=green>Best result!</font><br>";
                            } else {
                                msg += "<font color=green>Congratulations! "
                                        + "You solved it!</font><br>";
                            }
                        }
                        msg += "Your time: " + stopwatchLabel.getText();
                        msg += "</font></html>";
                        if (askForName) {
                            String name = (String) JOptionPane.showInputDialog(
                                    MainFrame.this, msg,
                                    MainFrame.this.getTitle(),
                                    JOptionPane.PLAIN_MESSAGE,
                                    null, null, System.getProperty("user.name"));
                            if (name == null || name.equals("")) {
                                name = System.getProperty("user.name");
                            }
                            if (name.length() > 30) {
                                name = name.substring(0, 30);
                            }
                            Scores.addScore(sudoku.getDifficulty(), name,
                                   stopwatchLabel.getTime());
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, msg,
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
        
        //center
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2,
                    (screen.height - getHeight()) / 2);
        
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
        sudokuMenu.setMnemonic(KeyEvent.VK_S);
        JMenu newMenu = new JMenu("New");
        newMenu.setMnemonic(KeyEvent.VK_N);
        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setMnemonic(KeyEvent.VK_T);
        JMenu autoSolvingMenu = new JMenu("Solver");
        JMenu suggestionsMenu = new JMenu("Suggestions");
        suggestionsMenu.setMnemonic(KeyEvent.VK_S);
        JMenu autoPossMenu = new JMenu("Auto remove");
        JMenu scoresMenu = new JMenu("Scores");
        scoresMenu.setMnemonic(KeyEvent.VK_C);
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        easyGameMenuItem.setMnemonic(KeyEvent.VK_E);
        easyGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionNewSudoku(Grid.DIFFICULTY_EASY);
            }
        });
        
        mediumGameMenuItem.setMnemonic(KeyEvent.VK_M);
        mediumGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionNewSudoku(Grid.DIFFICULTY_MEDIUM);
            }
        });
        
        hardGameMenuItem.setMnemonic(KeyEvent.VK_H);
        hardGameMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionNewSudoku(Grid.DIFFICULTY_HARD);
            }
        });
        
        customMenuItem.setMnemonic(KeyEvent.VK_C);
        customMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionNewSudoku(Grid.DIFFICULTY_NONE);
            }
        });

        solveMenuItem.setMnemonic(KeyEvent.VK_S);
        solveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionSolve();
            }
        });
        
        validateMenuItem.setMnemonic(KeyEvent.VK_V);
        validateMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainFrame.this.cheatingConfirmed()) {
                    actionValidate();
                }
            }
        });
        
        hintMenuItem.setMnemonic(KeyEvent.VK_H);
        hintMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainFrame.this.cheatingConfirmed()) {
                    if (!sudoku.hint()) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                                "<html><font size=5 color=red>"
                                + "Hint unavailable"
                                + "</font></html>",
                                MainFrame.this.getTitle(),
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
        });
        
        resetMenuItem.setMnemonic(KeyEvent.VK_R);
        resetMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainFrame.this.cheatingConfirmed()) {
                    sudoku.reset();
                    resetLabels();
                    stopwatchLabel.start();
                }
            }
        });
        
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionExit();
            }
        });
        
        autoFillSlider.setPaintLabels(true);
        autoFillSlider.setLabelTable(new Hashtable() {
            {
                put(0, new JLabel("Off"));
                put(1, new JLabel("Basic"));
                put(2, new JLabel("Advanced"));
            }
        });
        autoFillSlider.setFocusable(false);
        autoFillSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (autoFillSlider.getValue() > 0
                        && MainFrame.this.cheatingConfirmed()) {
                    sudoku.setAutoFillBasic(autoFillSlider.getValue() >= 1);
                    sudoku.setAutoFillAdvanced(autoFillSlider.getValue() >= 2);
                } else {
                    autoFillSlider.setValue(0);
                }
            }
        });
        
        fillPossMenuItem.setMnemonic(KeyEvent.VK_F);
        fillPossMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (MainFrame.this.cheatingConfirmed()) {
                    sudoku.fillSuggestions();
                    sudoku.refresh();
                    resetLabels();
                }
            }
        });
        
        autoPossSlider.setPaintLabels(true);
        autoPossSlider.setLabelTable(new Hashtable() {
            {
                put(0, new JMenu("Off"));
                put(1, new JLabel("Basic"));
                put(2, new JLabel("Advanced"));
            }
        });
        autoPossSlider.setFocusable(false);
        autoPossSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (autoPossSlider.getValue() > 0
                        && MainFrame.this.cheatingConfirmed()) {
                    sudoku.setAutoPossBasic(autoPossSlider.getValue() >= 1);
                    sudoku.setAutoPossAdvanced(autoPossSlider.getValue() >= 2);
                } else {
                    autoPossSlider.setValue(0);
                }
            }
        });
        
        possAsNumStyle.setMnemonic(KeyEvent.VK_N);
        possAsNumStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.setPossAsNum(possAsNumStyle.isSelected());
                sudoku.refresh();
            }
        });
        
        easyScoresMenuItem.setMnemonic(KeyEvent.VK_E);
        easyScoresMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionShowScores(Grid.DIFFICULTY_EASY);
            }
        });
        
        mediumScoresMenuItem.setMnemonic(KeyEvent.VK_M);
        mediumScoresMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionShowScores(Grid.DIFFICULTY_MEDIUM);
            }
        });
        
        hardScoresMenuItem.setMnemonic(KeyEvent.VK_H);
        hardScoresMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionShowScores(Grid.DIFFICULTY_HARD);
            }
        });
        
        extremeScoresMenuItem.setMnemonic(KeyEvent.VK_X);
        extremeScoresMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionShowScores(Grid.DIFFICULTY_EXTREME);
            }
        });
        
        aboutMenuItem.setMnemonic(KeyEvent.VK_A);
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionAbout();
            }
        });
        
        keysMenuItem.setMnemonic(KeyEvent.VK_K);
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
                sudokuMenu.add(newMenu);
                    newMenu.add(easyGameMenuItem);
                    newMenu.add(mediumGameMenuItem);
                    newMenu.add(hardGameMenuItem);
                    newMenu.add(new JSeparator());
                    newMenu.add(customMenuItem);
                sudokuMenu.add(solveMenuItem);
                sudokuMenu.add(validateMenuItem);
                sudokuMenu.add(hintMenuItem);
                sudokuMenu.add(resetMenuItem);
                sudokuMenu.add(exitMenuItem);
            menuBar.add(settingsMenu);
                settingsMenu.add(autoSolvingMenu);
                    autoSolvingMenu.add(autoFillSlider);
                settingsMenu.add(suggestionsMenu);
                    suggestionsMenu.add(autoPossMenu);
                        autoPossMenu.add(autoPossSlider);
                    suggestionsMenu.add(possAsNumStyle);
                    suggestionsMenu.add(fillPossMenuItem);
            menuBar.add(scoresMenu);
                scoresMenu.add(easyScoresMenuItem);
                scoresMenu.add(mediumScoresMenuItem);
                scoresMenu.add(hardScoresMenuItem);
//                scoresMenu.add(extremeScoresMenuItem); //TODO extreme difficulty
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
    
    private void actionNewSudoku(int difficulty) {
        if (difficulty == Grid.DIFFICULTY_NONE) {
            Object[] options = new String[] {"Create", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this, newSudokuChoices,
                    this.getTitle(), JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice == JOptionPane.OK_OPTION) {
                cheating = true;
                sudoku = new Sudoku(autoPossSlider.getValue() >= 1,
                        autoPossSlider.getValue() >= 2,
                        autoFillSlider.getValue() >= 1,
                        autoFillSlider.getValue() >= 2,
                        possAsNumStyle.isSelected(),
                        newSudokuChoices.getFillPoss(),
                        newSudokuChoices.getInitialNumbers(),
                        newSudokuChoices.getSudokuSize(), m,
                        Grid.DIFFICULTY_NONE);
                resetLabels();
                setContentPane(new JScrollPane(sudoku));
                pack();
                stopwatchLabel.start();
            }
            revalidate();
            repaint();
        } else {
            cheating = false;
            autoPossSlider.setValue(0);
            autoFillSlider.setValue(0);
            sudoku = new Sudoku(false, false, false, false,
                    possAsNumStyle.isSelected(), false, -1, 3, m, difficulty);
            resetLabels();
            setContentPane(new JScrollPane(sudoku));
            pack();
            stopwatchLabel.start();
        }
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
        cheating = true;
        
        Status status = sudoku.solve();
        if (status == Status.SOLVED) {
            stopwatchLabel.stop();
        } else {
            String msg = "<html><font size=5 color=";
            if (status == Status.UNSOLVABLE) {
                msg += "red>This puzzle does not have a solution";
            } else {
                msg += "orange>This puzzle is too complex<br>";
                msg += "to be solved automatically.<br>";
            }
            msg += "</font></html>";
            
            JOptionPane.showMessageDialog(MainFrame.this, msg,
                    MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    private void actionValidate() {
        String msg = "<html><font size=";
        switch (sudoku.isPuzzleValid()) {
            case SOLVED: msg += "5 color=green>Puzzle is valid"; break;
            case UNSOLVABLE: msg += "5 color=red>Puzzle is invalid"; break;
            case UNKNOWN: msg += "5 color=orange>This puzzle is too complex<br>"
                    + "to be validated."; break;
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
    
    private void actionShowScores(int difficulty) {
        String msg = "<html>";
        msg += "<font size=4>";
        msg += "Best results - ";
        switch (difficulty) {
            case Grid.DIFFICULTY_EASY: msg += "easy"; break;
            case Grid.DIFFICULTY_MEDIUM: msg += "medium"; break;
            case Grid.DIFFICULTY_HARD: msg += "hard"; break;
            case Grid.DIFFICULTY_EXTREME: msg += "extreme"; break;
        }
        msg += " difficulty:";
        msg += "</font>";
        msg += "<table>";
        for (int i = 0; i < 10; i++) {
            msg += "<tr><td>";
            msg += Scores.getName(difficulty, i);
            msg += "</td><td>";
            msg += StopwatchLabel.convertTime(Scores.getTime(difficulty, i));
            msg += "</td></tr>";
        }
        msg += "</table></html>";
        JOptionPane.showMessageDialog(MainFrame.this, msg,
                MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
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
                + "BACKSPACE, DELETE, E" + "</td></tr>"
                    
                + "<tr><td>Change<br>Suggestions:<td></td>"
                + "SPACE" + "</td></tr>"
                    
                + "<tr><td>Undo:<td></td>"
                + "R, U, Z" + "</td></tr>"
                    
                + "</table></html>";
        
        JOptionPane.showMessageDialog(MainFrame.this, msg,
                MainFrame.this.getTitle(), JOptionPane.PLAIN_MESSAGE);
    }
    
    /**
     * Returns true if the user is already cheating or is playing custom
     * game. Otherwise ask for cheating confirmation and returns either true
     * or false, depending on user's choice.
     */
    private boolean cheatingConfirmed() {
        if (!cheating && sudoku.getDifficulty() != Grid.DIFFICULTY_NONE) {
            if (JOptionPane.YES_OPTION ==
                    JOptionPane.showConfirmDialog(MainFrame.this,
                    "<html>Do you really want to cheat?<br>"
                    + "Your score will not be saved.</html>",
                    MainFrame.this.getTitle(),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE)) {
                cheating = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
