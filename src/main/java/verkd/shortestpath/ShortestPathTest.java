package verkd.shortestpath;

import org.testng.Assert;
import org.testng.annotations.Test;

public class ShortestPathTest {
    @Test
    public void testSimpleCase() {
        int[][] matrix = {
                {0, 0, 1},
                {0, 0, 1},
                {0, 1, 1}
        };

        ShortestPath sp = new ShortestPath();
        Assert.assertEquals(sp.dijikstraVersion(matrix, 0, 1), 2);
    }

    @Test
    public void testSimpleCase2() {
        int[][] matrix = {
                {1, 1, 1, 1},
                {0, 0, 0, 1},
                {0, 1, 0, 0},
                {0, 0, 0, 0}
        };

        ShortestPath sp = new ShortestPath();
        Assert.assertEquals(sp.dijikstraVersion(matrix, 0, 3), 1);
        Assert.assertEquals(sp.dijikstraVersion(matrix, 1, 2), -1);
    }

    @Test
    public void testDKSimpleCase() {
        int[][] matrix = {
                {0, 2, 6},
                {0, 0, 3},
                {0, 0, 0}
        };

        ShortestPath sp = new ShortestPath();
        Assert.assertEquals(sp.dijkstra(matrix, 0, 2), 5);
    }

    @Test
    public void testDKCase2() {
        int[][] matrix = {
                {0, 2, 1, 0},
                {0, 0, 0, 2},
                {0, 0, 0, 4},
                {0, 0, 0, 0}
        };

        ShortestPath sp = new ShortestPath();
        Assert.assertEquals(sp.dijkstra(matrix, 0, 3), 4);
    }
}
