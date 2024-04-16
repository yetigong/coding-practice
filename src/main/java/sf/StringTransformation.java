package sf;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class StringTransformation {
    public int numberOfWays(String s, String t, long k) {
        List<Integer> baseRotations = rotateString(s, t);
        if (baseRotations.isEmpty()) {
            return -1;
        }

        int n = s.length();
        long maxRotations = (n - 1) * k;
        long count = 0;
        // how many full rotations do we have
        long X = maxRotations / n;
        for (Integer baseRotation: baseRotations) {
            // 0 to X, X is max full rotations we can produce to result in baseRotation
            for (long i = 0; i <= X; i++) {
                long targetRotation =  i * n + baseRotation;
                if (targetRotation <= maxRotations) {
                    count += transformToTarget(s, t, k, targetRotation);
                }
            }
        }

        return (int) (count % (Math.pow(10, 9) + 7));
    }

    public List<Integer> rotateString(String s, String t) {
        StringBuilder sb = new StringBuilder(s);
        int length = s.length();
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i <= length; i++) {
            // shift sb towards right;
            if (sb.toString().equals(t) && i != 0) {
                results.add(i);
            }
            char last = sb.charAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.insert(0, last);
        }
        return results;
    }

    private long transformToTarget(String s, String t, long k, long targetRotation) {
        int n = s.length();
        int target = (int) targetRotation;
        long[] prev = new long[target+1];
        long[] curr = new long[target+1];
        // initialize prev to 1s for the operations that is shiftable;
        for (int i = 0; i <= target; i++) {
            if (i > 0 && i < n) {
                prev[i] = 1;
            }
        }

        // System.out.printf("The output for initial prev is %s %n", Arrays.toString(prev));
         for (int i = 2; i <= k; i++) { // representing 1 - k operations needed
            for (int j = 0; j <= target; j++) {
                // System.out.printf("[i=%s][j=%s] The output for prev is %s %n", i, j, Arrays.toString(prev));
                for (int l = 1; l < n; l++) { // every operation needs to look for [1, n-1] previous operations
                    int prevIndex = j - l;
                    if (prevIndex >= 0 && prevIndex <= target) { // within boundary
                        curr[j] += prev[prevIndex];
                    }
                    // System.out.printf("[i=%s][j=%s][l=%s] The output for prev is %s %n", i, j, l, Arrays.toString(prev));
                }

            }
            // finished processing 0 - target
            prev = curr;
            curr = new long[target+1];
            // System.out.printf("[i=%s] The output for prev is %s %n", i, Arrays.toString(prev));

        }
        return prev[target];
    }

    @Test
    public void testTransformToTarget() {
        String s = "abcd";
        String t = "cdab";
        long k = 2;
        long targetRotation = 6;
        Assert.assertEquals(transformToTarget(s, t, k, targetRotation), 1);
    }

    @Test
    public void testFindRotationPoint() {
        String s = "abcd";
        String t = "cdab";
        long k = 2;
        long targetRotation = 6;
        Assert.assertEquals(rotateString(s, t), List.of(2));
    }

    @Test
    public void testFindRotationPoint1() {
        String s = "ababab";
        String t = "ababab";
        Assert.assertEquals(rotateString(s, t), List.of(2, 4));
    }


    @Test
    public void testOvearll() {
        // Assert.assertEquals(numberOfWays("abcd", "cdab", 2), 2);
        Assert.assertEquals(numberOfWays("ababab", "bababa", 1), 3);
    }

    @Test
    public void testOverall1() {
        // Assert.assertEquals(numberOfWays("abcd", "cdab", 2), 2);
        Assert.assertEquals(numberOfWays("ababab", "bababa", 2), 12);
    }

    @Test
    public void testOverall2() {
        // Assert.assertEquals(numberOfWays("abcd", "cdab", 2), 2);
        Assert.assertEquals(numberOfWays("ababab", "ababab", 1), 2);
    }

    @Test
    public void testOverall3() {
        // Assert.assertEquals(numberOfWays("abcd", "cdab", 2), 2);
        Assert.assertEquals(numberOfWays("ceoceo", "eoceoc", 4), 208);
    }
}
