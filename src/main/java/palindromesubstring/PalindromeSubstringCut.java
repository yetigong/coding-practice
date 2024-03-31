package palindromesubstring;

public class PalindromeSubstringCut {
    public PalindromeSubstringCut() {

    }
    public String solution(String input) {
        if (input == null || input.length() <= 1) {
            return input;
        }

        int index = 0;
        for (int i = 2; i <= input.length(); i++) {
            String substring = input.substring(0, i);
            if (isPalindrome(substring)) {
                index = i;
            }
        }

        // if no palindrome found, the directly return the input
        if (index == 0) {
            return input;
        } else {
            return solution(input.substring(index));
        }
    }

    private boolean isPalindrome(String input) {
        if (input == null || input.isEmpty()) {
            return true;
        }
        int start = 0, end = input.length() - 1;
        while (start < end) {
            if (input.charAt(start) == input.charAt(end)) {
                start++;
                end--;
            } else {
                return false;
            }
        }
        return true;
    }
}
