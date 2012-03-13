package sudokuhelper;

import java.text.DecimalFormat;
import javax.swing.JLabel;

/**
 * @author Jaroslaw Pawlak
 */
public class StopwatchLabel extends JLabel {
    private static DecimalFormat DF = new DecimalFormat("00");
    
    private final Thread counter;
    
    private long start;
    private boolean stop;
    
    public StopwatchLabel() {
        super("00:00");
        stop = true;
        counter = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (stop) {
                            synchronized (counter) {
                                wait();
                            }
                            setText("00:00");
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    long diff = System.currentTimeMillis() - start;
                    diff /= 1000;
                    int seconds = (int) (diff % 60);
                    diff /= 60;
                    int minutes = (int) (diff % 60);
                    diff /= 60;
                    int hours = (int) diff;
                    StopwatchLabel.this.setText((hours > 0? hours + ":" : "")
                            + DF.format(minutes) + ":" + DF.format(seconds));
                }
            }
        };
        counter.start();
    }
    
    
    public void start() {
        start = System.currentTimeMillis();
        stop = false;
        synchronized (counter) {
            counter.notify();
        }
    }
    
    public void stop() {
        stop = true;
    }
    
    public boolean isStopped() {
        return stop;
    }
    
}
