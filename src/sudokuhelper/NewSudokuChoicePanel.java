package sudokuhelper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Hashtable;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Jaroslaw Pawlak
 */
public class NewSudokuChoicePanel extends JPanel {
    private static final int X = 3;

    private JLabel sizeLabel = new JLabel("Size");
    private JSlider sizeSlider = new JSlider(2, 4, X);
    private JLabel initialNumbersLabel = new JLabel("Numbers filled");
    private JSlider initialNumbersSlider = new JSlider(0, max(X), max(X)/2);
    private JLabel fillPossLabel = new JLabel("Fill suggestions");
    private JCheckBox fillPossCheckBox = new JCheckBox("", true);

    public NewSudokuChoicePanel() {
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
                        i <= sizeSlider.getMaximum(); i++) {
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