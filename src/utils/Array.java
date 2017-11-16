package utils;

public class Array {

    public static <T> void swap(T[] a, int i, int j) {
        T tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public static void swap(int[][] a, int i1, int j1, int i2, int j2) {
        int tmp = a[i1][j1];
        a[i1][j1] = a[i2][j2];
        a[i2][j2] = tmp;
    }
}
