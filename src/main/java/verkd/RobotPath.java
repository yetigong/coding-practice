package verkd;

import org.junit.Test;
import org.testng.Assert;

/***
 * (1) There is a robot at the bottom-left corner, (0, 0) of an m by n grid. Both m and n are positive integers.
 * There also exists a set of cookies. Given that the robot can only move up or right at any point, and
 * that the robot must collect the cookies in a specified order (as given in the input),
 * compute the number of paths possible for the robot to collect the cookies and then reach the top right of the grid.
 * Return -1 if there are no possible paths.
 *
 * For example, given grid size 5x5 and cookies at [(0, 2), (2, 3)], output 9.
 * Given grid size 5x5 and cookies at [(0, 2), (2, 3), (3, 2)], output -1 (since the robot can't go back from (2, 3) to (3, 2)).
 *
 * Follow-up: Rewrite the expression in combinatorics
 */
public class RobotPath {
    public int findPath(int m, int n, int[][] cookies) {
        int[][] grid = new int[m][n];
        if (cookies.length < 1) {
            return helper(0, 0, m, n, grid);
        }
        int[] prev = new int[] {0, 0};
        int result = 1;
        for (int[] cookie: cookies) {
            int subResult = helper(prev[0], prev[1], cookie[0], cookie[1], grid);
            if (subResult == -1) return -1;
            result *= subResult;
            prev = cookie;
        }
        int last = helper(prev[0], prev[1], m-1, n-1, grid);
        if (last == -1) return -1;
        return result * last;
    }

    private int helper(int sourceX, int sourceY, int targetX, int targetY, int[][] grid) {
        if (sourceX > targetX || sourceY > targetY) {
            return -1;
        }

        // initialize grid
        for (int i = 0; i <= (targetX - sourceX); i++) {
            for (int j = 0; j <= (targetY - sourceY); j++) {
                if (i == 0) {
                    grid[sourceX + i][sourceY + j] = 1;
                } else if (j == 0) {
                    grid[sourceX + i][sourceY + j] = 1;
                } else {
                    grid[sourceX + i][sourceY + j] = grid[sourceX + i][sourceY + j - 1] + grid[sourceX + i - 1][sourceY + j];
                }
            }
        }
        return grid[targetX][targetY];
    }

    @Test
    public void testRobot() {
        int[][] cookies = new int[][] {
                {0, 2},
                {2, 3}
        };

        RobotPath solver = new RobotPath();
        Assert.assertEquals(solver.findPath(5, 5, cookies), 9);
    }

    @Test
    public void testRobot1() {
        int[][] cookies = new int[][] {
                {0, 2},
                {2, 3},
                {3, 2},
        };

        RobotPath solver = new RobotPath();
        Assert.assertEquals(solver.findPath(5, 5, cookies), -1);
    }
}
