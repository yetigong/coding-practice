package palindromesubstring;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class PalindromeSubstringMain {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");

        Map<String, String> tests = ImmutableMap.of(
                "abc", "abc",
                "aba", "",
                "aa", "",
                "aaacde", "cde",
                "codesignal", "codesignal"
        );
        PalindromeSubstringDP palindromeSubstringDP = new PalindromeSubstringDP();

        tests.entrySet().stream().forEach(entry -> {
            String result = palindromeSubstringDP.solution(entry.getKey());
            System.out.println("result for input " + entry.getKey() + " is :" + result + " expected is: " + entry.getValue());
        });
    }


}