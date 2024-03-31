package smoothfilter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class SmoothFilterTest {
    @Test
    public void testHappyPath() {
        SmoothFilter filter = new SmoothFilter();
        int [][] input = new int[][] {
                {1, 2, 3},
                {0, 2, 5},
        };

        int [][] output = new int[][] {
                {1, 2, 3},
                {1, 2, 2}
        };
        Assert.assertEquals(filter.solution(input), output);
    }

    @Test
    public void testHappy2by2() {
        SmoothFilter filter = new SmoothFilter();
        int [][] input = new int[][] {
                {1, 3},
                {2, 4},
        };

        int [][] output = new int[][] {
                {3, 2},
                {2, 2}
        };
        Assert.assertEquals(filter.solution(input), output);
    }

    @Test
    public void testHappy1by3() {
        SmoothFilter filter = new SmoothFilter();
        int [][] input = new int[][] {
                {1, 2, 3}
        };

        int [][] output = new int[][] {
                {2, 2, 2}
        };
        Assert.assertEquals(filter.solution(input), output);
    }

    @Test
    public void testHappy3by1() {
        SmoothFilter filter = new SmoothFilter();
        int [][] input = new int[][] {
                {1},
                {2},
                {3}
        };

        int [][] output = new int[][] {
                {2},
                {2},
                {2}
        };
        Assert.assertEquals(filter.solution(input), output);
    }
}
