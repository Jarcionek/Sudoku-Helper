package sudokuhelper;

import java.text.DecimalFormat;
import javax.swing.JLabel;

/**
 * @author Jaroslaw Pawlak
 */
public class StopwatchLabel extends JLabel {
    private static final DecimalFormat DF = new DecimalFormat("00");
    
    private final Thread counter;
    
    private long start;
    private long end;
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
                    end = System.currentTimeMillis();
                    setText(convertTime(end - start));
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
    
    public long getTime() {
        return end - start;
    }
    
    public void delete() {
        counter.interrupt();
    }
    
    public static String convertTime(long time) {
        time /= 1000;
        int seconds = (int) (time % 60);
        time /= 60;
        int minutes = (int) (time % 60);
        time /= 60;
        int hours = (int) time;
        return (hours > 0? hours + ":" : "")
                + DF.format(minutes) + ":" + DF.format(seconds);
    }
    
}
