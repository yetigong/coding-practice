package smoothfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmoothFilter {

    public int[][] solution(int[][] input) {
        if (input == null || input.length == 0 || input[0].length == 0) {
            return input;
        }

        // height and width should be at least 1
        int height = input.length;
        int width = input[0].length;
        int[][] result = new int[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                List<Integer> neighbors = getNeighbors(input, i, j);
                if (neighbors.isEmpty()) {
                    result[i][j] = input[i][j];
                } else {
                    result[i][j] = computeAvg(neighbors);
                }

            }
        }
        return result;
    }

    private List<Integer> getNeighbors(int[][] input, int i, int j) {
        int height = input.length;
        int width = input[0].length;
        List<Integer> result = new ArrayList<>();
        if (i - 1 >= 0 && j - 1 >= 0) {
            result.add(input[i-1][j-1]);
        }
        if (i - 1 >= 0) {
            result.add(input[i-1][j]);
        }
        if (i - 1 >= 0 && j + 1 <= width - 1) {
            result.add(input[i-1][j+1]);
        }
        if (j - 1 >= 0) {
            result.add(input[i][j-1]);
        }
        if (j + 1 <= width - 1) {
            result.add(input[i][j+1]);
        }
        if (i+1 <= height - 1 && j - 1 >= 0) {
            result.add(input[i+1][j-1]);
        }
        if (i+1 <= height - 1) {
            result.add(input[i+1][j]);
        }
        if (i+1 <= height - 1 && j + 1 <= width - 1) {
            result.add(input[i+1][j+1]);
        }
        return result;
    }

    private int computeAvg(List<Integer> neighbors) {
        int sum = 0;
        int count = 0;
        for (Integer i: neighbors) {
            sum += i;
            count += 1;
        }

        if (count == 0) {
            return 0;
        } else {
            return sum / count;
        }
    }

    /**
     * the following is provided by GPT
     * @param input
     * @param i
     * @param j
     * @return
     */
    private int computeAvg(int[][] input, int i, int j) {
        int height = input.length;
        int width = input[0].length;
        int sum = 0;
        int count = 0;

        for (int row = i - 1; row <= i + 1; row++) {
            for (int col = j - 1; col <= j + 1; col++) {
                if (row >= 0 && row < height && col >= 0 && col < width) {
                    // Exclude the current point
                    if (row != i || col != j) {
                        sum += input[row][col];
                        count++;
                    }
                }
            }
        }

        return count == 0 ? 0 : sum / count;
    }
}
