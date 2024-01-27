package palindromesubstring;

import utils.ArrayUtils;

public class PalindromeSubstringDP {
    public PalindromeSubstringDP() {

    }
    public String solution(String input) {
        if (input == null || input.length() <= 1) {
            return input;
        }
        int len = input.length();
        int[][] res = new int[len][len];
        for (int j = 0; j < len; j++) {
            for (int i = j; i >= 0; i--) {
                if (input.charAt(i) == input.charAt(j)) {
                    if (i == j || i == j-1) {
                        res[i][j] = 1;
                    } else {
                        if (res[i+1][j-1] == 1) {
                            res[i][j] = 1;
                        } else {
                            res[i][j] = 0;
                        }
                    }
                } else {
                    res[i][j] = 0;
                }
            }
        }

        return this.detectResult(res, input);
    }

    private String detectResult(int[][] res, String input) {
        System.out.println(String.format(ArrayUtils.printArray(res)));
        int start = 0;
        int len = input.length();
        int end = len - 1;
        while (start < len) {
            while (end > start) {
                if (res[start][end] == 0) {
                    end--;
                }
            }
            if (start == end) {
                break;
            } else {
                start = end + 1;
                end = len - 1;
            }
        }
        return input.substring(start, len);
    }
}
