package sf;

import org.testng.Assert;
import org.testng.annotations.Test;

public class PartialReverse {
    /***
     * 2. Write a function that takes an array, and two numbers (a,n),
     * then reverses everything in the array from the ath element to the nth element.
     *     1. partial_reverse(['a','b','c','d','e','f','g'], 2,5) would print out: a e d c b f g
     *
     * @param input
     * @param start
     * @param end
     * @return
     */
    static String partialReverse(String input, int start, int end) {
        int length = input.length();
        char[] result = new char[length];
        int sum = start + end - 2;
        for (int i = 0; i < length; i++) {
            if (i < (start - 1) || i > (end - 1)) {
                result[i] = input.charAt(i);
            } else {
                result[i] = input.charAt(sum - i);
            }
        }
        return String.valueOf(result);
    }
    @Test
    public void testHappyPath() {
        String input = "abcdefg";
        Assert.assertEquals(partialReverse(input, 2, 5), "aedcbfg");
    }

    @Test
    public void testHappyPath1() {
        String input = "abcdefg";
        Assert.assertEquals(partialReverse(input, 2, 7), "agfedcb");
    }

    @Test
    public void testHappyPath2() {
        String input = "a";
        Assert.assertEquals(partialReverse(input, 1, 1), "a");
    }

    @Test
    public void testHappyPath3() {
        String input = "ab";
        Assert.assertEquals(partialReverse(input, 1, 1), "ab");
    }

    @Test
    public void testHappyPath4() {
        String input = "abc";
        Assert.assertEquals(partialReverse(input, 1, 2), "bac");
    }
}
