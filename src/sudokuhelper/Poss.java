package sudokuhelper;

import java.util.Iterator;

/**
 * @author Jaroslaw Pawlak
 */
public class Poss implements Iterable<Integer> {
    private boolean[] poss;

    public Poss(boolean initValue, int size) {
        poss = new boolean[size*size + 1];
        for (int i = 1; i < poss.length; i++) {
            poss[i] = initValue;
        }
    }
    
    public void add(int n) {
        poss[n] = true;
    }
    
    public void remove(int n) {
        poss[n] = false;
    }
    
    public int size() {
        int r = 0;
        for (int i = 1; i < poss.length; i++) {
            if (poss[i]) {
                r++;
            }
        }
        return r;
    }
    
    public boolean isEmpty() {
        for (int i = 1; i < poss.length; i++) {
            if (poss[i]) {
                return false;
            }
        }
        return true;
    }
    
    public int getFirst() {
        for (int i = 1; i < poss.length; i++) {
            if (poss[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean contains(int n) {
        return poss[n];
    }
    
    public void addAll() {
        for (int i = 1; i < poss.length; i++) {
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
            r.poss[n] = true;
        }
        return r;
    }
    
    /**
     * Returns true if and only if "other" contains all the elements of "this"
     */
    public boolean isSubsetOf(Poss that) {
        for (int n : this) {
            if (!that.contains(n)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Poss that) {
        for (int i = 1; i < poss.length; i++) {
            if (this.poss[i] ^ that.poss[i]) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        String x = "";
        for (int i = 1; i < poss.length; i++) {
            if (poss[i]) {
                x += ", " + (i);
            }
        }
        return "[" + (x.length() > 2? x.substring(2) : "") + "]";
    }

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {
            private int last = 1;
            @Override
            public boolean hasNext() {
                while (last < poss.length) {
                    if (poss[last]) {
                        return true;
                    }
                    last++;
                }
                return false;
            }
            @Override
            public Integer next() {
                return last++;
            }
            @Override
            public void remove() {}
        };
    }
}
