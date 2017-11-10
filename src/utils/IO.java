package utils;

public class IO {
    public static <T> void print(T obj) { System.out.println(obj); }

    public static void print(double[] a) { System.out.println(toString(a)); }

    public static void print(int[] a) { System.out.println(toString(a)); }


    public static <T> void print(T[] array) { System.out.println(toString(array));}


    private static <T> String toString(T[] array) {
        return toString(array, 0, array.length-1);
    }

    private static <T> String toString(T[] array, int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=start; i<end; i++) {
            sb.append(array[i]).append(",\n");
        }
        sb.append(array[end]).append("]");
        return sb.toString();
    }


    public static String toString(double[] array) {
        return toString(array, 0, array.length-1);
    }

    private static String toString(double[] a, int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=start; i<end; i++) {
            sb.append(a[i]).append(", ");
        }
        sb.append(a[end]).append("]");
        return sb.toString();
    }

    private static String toString(int[] array) {
        return toString(array, 0, array.length-1);
    }

    private static String toString(int[] a, int start, int end) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i=start; i<end; i++) {
            sb.append(a[i]).append(", ");
        }
        sb.append(a[end]).append("]");
        return sb.toString();
    }
}
