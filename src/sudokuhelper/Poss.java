package sudokuhelper;

import java.util.Iterator;

/**
 * @author Jaroslaw Pawlak
 */
public class Poss implements Iterable<Integer> {
    private boolean[] poss;

    public Poss(boolean initValue, int size) {
        poss = new boolean[size*size];
        for (int i = 0; i < poss.length; i++) {
            poss[i] = initValue;
        }
    }
    
    public void add(int n) {
        poss[n-1] = true;
    }
    
    public void remove(int n) {
        poss[n-1] = false;
    }
    
    public int size() {
        int r = 0;
        for (int i = 0; i < poss.length; i++) {
            if (poss[i]) {
                r++;
            }
        }
        return r;
    }
    
    public boolean isEmpty() {
        for (int i = 0; i < poss.length; i++) {
            if (poss[i]) {
                return false;
            }
        }
        return true;
    }
    
    public int get() {
        for (int i = 0; i < poss.length; i++) {
            if (poss[i]) {
                return ++i;
            }
        }
        return -1;
    }
    
    public boolean contains(int n) {
        return poss[n-1];
    }
    
    public void addAll() {
        for (int i = 0; i < poss.length; i++) {
            poss[i] = true;
        }
    }
    
    public void clear() {
        for (int n : this) {
            this.remove(n);
        }
    }

    public Poss copy() {
        Poss r = new Poss(false, (int) Math.round(Math.sqrt(poss.length)));
        for (int n : this) {
            r.poss[n - 1] = true;
        }
        return r;
    }
    
    @Override
    public String toString() {
        String x = "";
        for (int i = 0; i < poss.length; i++) {
            if (poss[i]) {
                x += ", " + (i + 1);
            }
        }
        return "[" + (x.length() > 2? x.substring(2) : "") + "]";
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int last = 0;
            @Override
            public boolean hasNext() {
                for (int i = last; i < poss.length; i++) {
                    if (poss[i]) {
                        return true;
                    }
                }
                return false;
            }
            @Override
            public Integer next() {
                while (!poss[last++]) {}
                return last;
            }
            @Override
            public void remove() {}
        };
    }
}
