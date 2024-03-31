package verkd.transpose;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Transpose {
    public int[][] transpose(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int [][] output = new int[n][m];
        int numberOfThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(numberOfThreads);
        System.out.format("creating thread pool with %s number of threads! %n", numberOfThreads);
        for (int i = 0; i < m; i++) {
            final int workerId = i;
            threadPoolExecutor.execute(() -> Transpose.this.transposeWorker(matrix, output, workerId));
        }
        threadPoolExecutor.shutdown();
        try {
            // Wait a certain amount of time for all tasks to finish
            threadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // set the interrupt flag
            System.err.println("Tasks interrupted");
        }
        return output;
    }

    private void transposeWorker(int[][] matrix, int[][] out, int index) {
        if (matrix[index].length != out.length) {
            throw new IllegalArgumentException("The output matrix size is problematic!");
        }
        System.out.format("Thread %s has started to process row %s %n", Thread.currentThread().getName(), index);
        for (int i = 0; i < matrix[index].length; i++) {
            out[i][index] = matrix[index][i];
        }
    }
}
