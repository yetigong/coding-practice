package obstacle;

//  In Roblox, there is a category of game called "Obby", or Obstacle Courses.
//  Users need to walk and jump around a series of platforms to reach the goal.

//  In this question, we would like to know if there is a possible route through
//  an obstacle course.

//  The course is represented by an N x M grid of integers:
//  - 1 represents a platform and
//  - 0 represents a hole

//  The start is always (0,0), and the goal is always (N-1, M-1).

//  A user can perform the following actions:
//  - Walk (left, right, up, down)
//  - Jump (over 1 hole)

//  For example, this course is Valid:
//  [[1, 0, 1],
//   [0, 0, 1],
//   [0, 0, 1]]

//  as the user can follow this path:
//  [[1 -> jump -> 1]
//                     | walk
//   [0       0      1]
//                     | walk
//   [0       0      1]

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

//  Given a N x M grid representing the course, return whether or not the course is valid.
public class ObstaclePath {
    class Point {
        int x;
        int y;
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    /**
     * BFS based approach
     * @param grid
     * @return
     */
    boolean isValid(int[][] grid) {
        // assuming valid grid
        int m = grid.length;
        int n = grid[0].length;
        Point start = new Point(0, 0);
        Point end = new Point(m-1, n-1);
        Set<Point> visited = new HashSet<>();
        Set<Point> curr = new HashSet<>();
        Set<Point> next = new HashSet<>();
        curr.add(start);
        while (!curr.isEmpty()) {
            for (Point point: curr) {
                List<Point> neighbors = getNeighbors(point, grid);
                for (Point neighbor: neighbors) {
                    if (neighbor.equals(end)) return true;
                    if (!visited.contains(neighbor)) {
                        next.add(neighbor);
                    }
                }
                visited.add(point);
            }
            curr = next;
            next = new HashSet<>();
        }
        return false;
    }

    private List<Point> getNeighbors(Point point, int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;
        List<Point> neighbors = new ArrayList<>();

        if (point.x - 1 >= 0 && grid[point.x-1][point.y] == 1) {
            // if left is a valid path
            neighbors.add(new Point(point.x - 1, point.y));
        } else if (point.x - 2 >= 0 && grid[point.x-1][point.y] == 0 && grid[point.x-2][point.y] == 1) {
            // if left is 0 but the grid over it is 1, we can jump over it
            neighbors.add(new Point(point.x - 2, point.y));
        }

        // similarly do this for all 4 directions
        if (point.x + 1 < m && grid[point.x+1][point.y] == 1) {
            // if left is a valid path
            neighbors.add(new Point(point.x + 1, point.y));
        } else if (point.x + 2 < m && grid[point.x+1][point.y] == 0 && grid[point.x+2][point.y] == 1) {
            // if left is 0 but the grid over it is 1, we can jump over it
            neighbors.add(new Point(point.x + 2, point.y));
        }

        if (point.y - 1 >= 0 && grid[point.x][point.y - 1] == 1) {
            // if left is a valid path
            neighbors.add(new Point(point.x, point.y - 1));
        } else if (point.y - 2 >= 0 && grid[point.x][point.y - 1] == 0 && grid[point.x][point.y - 2] == 1) {
            // if left is 0 but the grid over it is 1, we can jump over it
            neighbors.add(new Point(point.x, point.y - 2));
        }

        if (point.y + 1 < n && grid[point.x][point.y + 1] == 1) {
            // if left is a valid path
            neighbors.add(new Point(point.x, point.y + 1));
        } else if (point.y + 2 < n && grid[point.x][point.y + 1] == 0 && grid[point.x][point.y + 2] == 1) {
            // if left is 0 but the grid over it is 1, we can jump over it
            neighbors.add(new Point(point.x, point.y + 2));
        }

        return neighbors;
    }

    /**
     * DFS based approach
     * @param point
     * @return
     */
    boolean isValidDFS(int[][] grid) {
        int m = grid.length;
        int n = grid[0].length;

        Set<Point> visited = new HashSet<>();
        List<Point> path = new ArrayList<>();
        return dfsHelper(grid, new Point(0, 0), visited, path);
    }

    boolean dfsHelper(int[][] grid, Point curr, Set<Point> visited, List<Point> path) {
        int m = grid.length;
        int n = grid[0].length;
        Point dest = new Point(m-1, n-1);
        if (curr.equals(dest)) {
            path.add(dest);
            System.out.println("A path is found! " + path);
            return true;
        }

        if (visited.contains(curr)) return false;

        visited.add(curr);
        path.add(curr);
        List<Point> neighbors = getNeighbors(curr, grid);
        for (Point neighbor: neighbors) {
            if (this.dfsHelper(grid, neighbor, visited, path)) {
                return true;
            }
        }
        path.remove(curr);
        return false;
    }

    public static void main(String[] args) {
        int[][] grid = new int[][] {
                {1, 0, 1},
                {0, 0, 1},
                {0, 0, 1}
        };

        System.out.println("The grid is " + new ObstaclePath().isValidDFS(grid));
    }

    @Test
    public void testCourse() {
        int[][] grid = new int[][] {
                {1, 0, 1},
                {0, 0, 1},
                {1, 0, 1}
        };

        Assert.assertEquals(new ObstaclePath().isValidDFS(grid), true);
    }

    @Test
    public void testCourse1() {
        int[][] grid = new int[][] {
                {1, 0, 1},
                {0, 0, 1},
                {1, 0, 0}
        };

        Assert.assertEquals(new ObstaclePath().isValidDFS(grid), false);
    }

    @Test
    public void testCourse2() {
        int[][] grid = new int[][] {
                {1, 0, 1, 0},
                {0, 0, 1, 1},
                {1, 0, 0, 1}
        };

        Assert.assertEquals(new ObstaclePath().isValidDFS(grid), true);
    }
}
