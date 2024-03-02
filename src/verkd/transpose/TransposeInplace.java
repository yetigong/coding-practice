package verkd.transpose;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransposeInplace {
    public int[][] transpose(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        if (m != n) {
            throw new IllegalArgumentException("invalid input");
        }

        int numberOfThreads = Runtime.getRuntime().availableProcessors();

        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(numberOfThreads);
        System.out.format("creating thread pool with %s number of threads! %n", numberOfThreads);
        for (int i = 0; i < m; i++) {
            final int workerId = i;
            threadPoolExecutor.execute(() -> TransposeInplace.this.transposeWorker(matrix, workerId));
        }
        threadPoolExecutor.shutdown();
        try {
            // Wait a certain amount of time for all tasks to finish
            threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // set the interrupt flag
            System.err.println("Tasks interrupted");
        }
        return matrix;
    }

    private void transposeWorker(int[][] matrix, int index) {
        System.out.format("Thread %s has started to process row %s %n", Thread.currentThread().getName(), index);
        for (int i = index+1; i < matrix[index].length; i++) {
            int temp = matrix[index][i];
             matrix[index][i] = matrix[i][index];
             matrix[i][index] = temp;
        }
    }
}
