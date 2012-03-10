package sudokuhelper;

import java.util.Iterator;

/**
 * @author Jaroslaw Pawlak
 */
public class Poss implements Constants, Iterable<Integer> {
    private boolean[] poss = new boolean[N*N];

    public Poss(boolean initValue) {
        for (int i = 0; i < poss.length; i++) {
            poss[i] = initValue;
        }
    }
    
    public void add(int n) {
        poss[--n] = true;
    }
    
    public void remove(int n) {
        poss[--n] = false;
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
    
    public int get() {
        for (int i = 0; i < poss.length; i++) {
            if (poss[i]) {
                return ++i;
            }
        }
        return -1;
    }
    
    public boolean contains(int n) {
        return poss[--n];
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
