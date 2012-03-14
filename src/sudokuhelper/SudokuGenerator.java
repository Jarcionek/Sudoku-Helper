package sudokuhelper;

import java.util.Random;

/**
 * @author Jaroslaw Pawlak
 */
public class SudokuGenerator {
    private SudokuGenerator() {}
    
    public static int[][] generateSudoku(int size, int numbers) {
        final int S = size * size;
        final int[][] array = new int[S][S];
        
        final Random r = new Random();
//        int x = r.nextInt(S);
        final int x = 0;
        
        for (int i = 0; i < S; i++) {
            for (int j = 0; j < S; j++) {
                array[i][j] = (i*size + i/size + j + x) % (S) + 1;
            }
        }
        printArray(array, size);
        
        int tests = 1000;
        
//        while (tests-- > 0) {
//            System.out.println(tests);
        
        int temp;
        int reallytemp;
        for (int i = 0; i < S*S; i++) {
            switch (r.nextInt(6)) {
                case 0:
                    swapRows(array, size, temp = r.nextInt(S),
                                          r.nextInt(size) + temp/size*size);
//                    System.out.println("swapping " + temp + " with " + reallytemp);
                    break;
                case 1:
                    swapRowsBlock(array, size, r.nextInt(size),
                                               r.nextInt(size));
                    break;
                case 2:
                    swapColumnsBlock(array, size, r.nextInt(size),
                                                  r.nextInt(size));
                    break;
                case 3:
                    swapColumns(array, size, temp = r.nextInt(S),
                                             r.nextInt(size) + temp/size*size);
//                    System.out.println("swapping " + temp + " with " + reallytemp);
                    break;
                case 4:
                    reflectAxis(array, size, true);
                    break;
                case 5:
                    reflectAxis(array, size, false);
                    break;
            }
        }
        
//        if (!isValid(array, size)) {
//            System.out.println("=================================");
//            System.out.println("OPS!");
//            System.out.println("=================================");
//            printArray(array, size);
//            
//        }
//        
//        }
        
//        printArray(array, size);
//        reflectAxis(array, size, false);
        System.out.println("=================================");
        printArray(array, size);
//        reflectAxis(array, size, true);
//        reflectAxis(array, size, false);
//        System.out.println("=================================");
//        printArray(array, size);
        
        
        return null;
    }
    
    /** 0 <= row < size*size */
    private static void swapRows(int[][] array, int size,
                                 int row1, int row2) {
        int[] temp = array[row1];
        array[row1] = array[row2];
        array[row2] = temp;
    }
    
    /** 0 <= block < size */
    private static void swapRowsBlock(int[][] array, int size,
                                      int block1, int block2) {
        for (int i = 0; i < size; i++) {
            swapRows(array, size, block1 * size + i, block2 * size + i);
        }
    }
    
    /** 0 <= column < size*size */
    private static void swapColumns(int[][] array, int size,
                                    int column1, int column2) {
        int temp;
        for (int i = 0; i < size*size; i++) {
            temp = array[i][column1];
            array[i][column1] = array[i][column2];
            array[i][column2] = temp;
        }
    }
    
    /** 0 <= block < size */
    private static void swapColumnsBlock(int[][] array, int size,
                                         int block1, int block2) {
        for (int i = 0; i < size; i++) {
            swapColumns(array, size, block1*size + i, block2*size + i);
        }
    }
    
    private static void reflectAxis(int[][] array, int size, boolean axis) {
        int temp;
        if (axis) {
            for (int i = 0; i < size*size; i++) {
                for (int j = i + 1; j < size*size; j++) {
                    temp = array[i][j];
                    array[i][j] = array[j][i];
                    array[j][i] = temp;
                }
            }
        } else {
            int S = size * size;
            for (int i = 0; i < size*size; i++) {
                for (int j = 0; j < S - 1 - i; j++) {
                    temp = array[i][j];
                    array[i][j] = array[S - 1 - j][S - 1 - i];
                    array[S - 1 - j][S - 1 - i] = temp;
                }
            }
        }
    }
    
    
    
    
    private static void printArray(int[][] array, int size) {
        if (size <= 3) {
            for (int i = 0; i < size*size; i++) {
                for (int j = 0; j < size*size; j++) {
                    System.out.print(array[i][j]);
                    if (j < size*size - 1 && (j + 1) % size == 0) {
                        System.out.print("|");
                    }
                }
                System.out.println();
                if (i < size*size - 1 && (i + 1) % size == 0) {
                    for (int j = 0; j < size*size + size - 1; j++) {
                        System.out.print("-");
                    }
                    System.out.println();
                }
            }
        } else {
            int x;
            for (int i = 0; i < size*size; i++) {
                for (int j = 0; j < size*size; j++) {
                    x = array[i][j];
                    System.out.print(x < 10? " " + x : x);
                    if (j < size*size - 1 && j % size == size - 1) {
                        System.out.print("|");
                    }
                }
                System.out.println();
                if (i > 0 && i % size == size - 1) {
                    for (int j = 0; j < size*size + size - 1; j++) {
    //                    if (j > 0 && j % (2*size) == 2*size - 1) {
    //                        System.out.print("+");
    //                    } else {
                            System.out.print("--");
    //                    }
                    }
                    System.out.println();
                }
            }
        }
        
    }
    
    
    private static boolean isValid(int[][] array, int size) {
        boolean[] found = new boolean[size*size];
        for (int i = 0; i < size*size; i++) {
            
            //rows
            for (int j = 0; j < size*size; j++) {
                found[array[i][j] - 1] = true;
            }
            for (int x = 0; x < found.length; x++) {
                if (!found[x]) {
                    System.out.println("A: i = " + i + ", n = " + (x+1));
                    return false;
                }
                found[x] = false;
            }
            
            for (int j = 0; j < size*size; j++) {
                found[array[j][i] - 1] = true;
            }
            for (int x = 0; x < found.length; x++) {
                if (!found[x]) {
                    System.out.println("B: i = " + i + ", n = " + (x+1));
                    return false;
                }
                found[x] = false;
            }
        }
        return true;
    }
    
    
    
    
    
    public static void main(String[] args) {
        generateSudoku(3, 0);
    }
}
