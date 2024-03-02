package verkd.transpose;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TransposeTest {

    @Test
    public void testTranspose() {
        int[][] matrix = new int[10][10];
        int[][] expected = new int[10][10];
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                expected[j][i] = counter;
                matrix[i][j] = counter;
                counter += 1;
            }
        }
        Transpose transpose = new Transpose();
        int[][] output = transpose.transpose(matrix);
        Assert.assertEquals(output, expected);
    }
    @Test
    public void testTransposeLarge() {
        int[][] matrix = new int[100][200];
        int[][] expected = new int[200][100];
        int counter = 0;
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 200; j++) {
                expected[j][i] = counter;
                matrix[i][j] = counter;
                counter += 1;
            }
        }
        Transpose transpose = new Transpose();
        int[][] output = transpose.transpose(matrix);
        Assert.assertEquals(output, expected);
    }

    @Test
    public void testTransposeInplace() {
        int[][] matrix = new int[100][100];
        int[][] expected = new int[100][100];
        int counter = 0;
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                expected[j][i] = counter;
                matrix[i][j] = counter;
                counter += 1;
            }
        }
        TransposeInplace transpose = new TransposeInplace();
        int[][] output = transpose.transpose(matrix);
        Assert.assertEquals(output, expected);
    }
}
