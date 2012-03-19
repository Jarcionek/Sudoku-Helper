package sudokuhelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Jaroslaw Pawlak
 */
public class Scores implements Serializable {
    private static final File FILE = new File(Main.DIRECTORY, "Scores.ser");
    private static final long serialVersionUID = 170320121910L;
    
    private static Scores instance;
    
    private final String[] easyName;
    private final long[] easyTime;
    private final String[] mediumName;
    private final long[] mediumTime;
    private final String[] hardName;
    private final long[] hardTime;
    private final String[] extremeName;
    private final long[] extremeTime;
    
    private Scores() {
        easyName = new String[10];
        easyTime = new long[10];
        mediumName = new String[10];
        mediumTime = new long[10];
        hardName = new String[10];
        hardTime = new long[10];
        extremeName = new String[10];
        extremeTime = new long[10];
        for (int i = 0; i < 10; i++) {
            easyName[i] = "Anonymous";
            mediumName[i] = "Anonymous";
            hardName[i] = "Anonymous";
            extremeName[i] = "Anonymous";
            easyTime[i] = 1000 * 60 * 5 * (i + 1);
            mediumTime[i] = 1000 * 60 * 5 * (i + 2);
            hardTime[i] = 1000 * 60 * 5 * (i + 3);
            extremeTime[i] = 1000 * 60 * 5 * (i + 4);
        }
    }
    
    public static String getName(int difficulty, int i) {
        if (instance == null) {
            return null;
        }
        switch (difficulty) {
            case Grid.DIFFICULTY_EASY: return instance.easyName[i];
            case Grid.DIFFICULTY_MEDIUM: return instance.mediumName[i];
            case Grid.DIFFICULTY_HARD: return instance.hardName[i];
            case Grid.DIFFICULTY_EXTREME: return instance.extremeName[i];
            default: return null;
        }
    }
    
    public static long getTime(int difficulty, int i) {
        if (instance == null) {
            return Long.MIN_VALUE;
        }
        switch (difficulty) {
            case Grid.DIFFICULTY_EASY: return instance.easyTime[i];
            case Grid.DIFFICULTY_MEDIUM: return instance.mediumTime[i];
            case Grid.DIFFICULTY_HARD: return instance.hardTime[i];
            case Grid.DIFFICULTY_EXTREME: return instance.extremeTime[i];
            default: return Long.MIN_VALUE;
        }
    }
    
    public static void addScore(int difficulty, String name, long time) {
        String[] names;
        long[] times;
        switch (difficulty) {
            case Grid.DIFFICULTY_EASY:
                names = instance.easyName;
                times = instance.easyTime;
                break;
            case Grid.DIFFICULTY_MEDIUM:
                names = instance.mediumName;
                times = instance.mediumTime;
                break;
            case Grid.DIFFICULTY_HARD:
                names = instance.hardName;
                times = instance.hardTime;
                break;
            case Grid.DIFFICULTY_EXTREME:
                names = instance.extremeName;
                times = instance.extremeTime;
                break;
            default: return;
        }
        
        if (times[times.length - 1] < time) {
            return;
        }
        
        int place = times.length - 1;
        while (true) {
            if (place < times.length - 1) {
                times[place + 1] = times[place];
                names[place + 1] = names[place];
            }
            if (times[place] < time) {
                place++;
                break;
            }
            place--;
            if (place == -1) {
                place = 0;
                break;
            }
        }
        times[place] = time;
        names[place] = name;
        
        save();
    }
    
    public static boolean isBestResult(int difficulty, long time) {
        long[] times;
        switch (difficulty) {
            case Grid.DIFFICULTY_EASY: times = instance.easyTime; break;
            case Grid.DIFFICULTY_MEDIUM: times = instance.mediumTime; break;
            case Grid.DIFFICULTY_HARD: times = instance.hardTime; break;
            case Grid.DIFFICULTY_EXTREME: times = instance.extremeTime; break;
            default: return false;
        }
        
        for (long result : times) {
            if (time < result) {
                return true;
            }
        }
        return false;
    }
    
    public static void load() {
        if (!FILE.exists()) {
            reset();
            return;
        }
        ObjectInputStream ois = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE);
            ois = new ObjectInputStream(fis);
            instance = (Scores) ois.readObject();
        } catch (Exception ex) {
            System.err.println("Could not load scores: " + ex);
            reset();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ex) {}
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {}
            }
        }
    }
    
    private static void reset() {
        instance = new Scores();
        save();
    }
    
    private static void save() {
        ObjectOutputStream oos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(FILE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(instance);
        } catch (Exception ex) {
            System.err.println("Could not save scores: " + ex);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ex) {}
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {}
            }
        }
    }
}
