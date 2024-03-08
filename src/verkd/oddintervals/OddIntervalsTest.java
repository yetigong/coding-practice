package verkd.oddintervals;

import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class OddIntervalsTest {
    @Test
    public void testOddIntervalsCase1() {
        List<int[]> input = ImmutableList.of(
                new int[] {1, 7},
                new int[] {3, 5},
                new int[] {4, 9}
        );

        List<int[]> expected = ImmutableList.of(
                new int[] {1, 3},
                new int[] {4, 5},
                new int[] {7, 9}
        );

        OddIntervals solver = new OddIntervals();
        List<int[]> out = solver.getOddIntervals(input);

        assertListArrayEquals(out, expected);
    }

    public static void assertListArrayEquals(List<int[]> expected, List<int[]> actual) {
        Assert.assertEquals(expected.size(), actual.size()); // Check size first
        for (int i = 0; i < expected.size(); i++) {
            Assert.assertEquals(expected.get(i), actual.get(i)); // Check array contents
        }
    }

}
