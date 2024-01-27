package utils;

public class ArrayUtils {
    public static String printArray(int[][] array) {
        if (array == null || array.length == 0 || array[0].length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int h = array.length;
        int w = array[0].length;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                sb.append(array[i][j]);
                sb.append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
