package roblox.obstacle;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

public class Shuffle {
    static class ShuffleMusic {

        /**
         * shuffle is defined as a repeated shuffled collection of chars.
         * For example ABCD CABD BCDA is repeated shuffled of ABCD.
         *
         * @param input
         * @return
         */
        public boolean isShuffle(String input) {
            // step 1. count the unique chars
            int uniqueChars = 0;
            Set<Character> charSet = new HashSet<>();
            for (int i = 0; i < input.length(); i++) {
                if (!charSet.contains(input.charAt(i))) {
                    uniqueChars += 1;
                    charSet.add(input.charAt(i));
                } else {
                    break;
                }
            }
            System.out.printf("Unique chars %s are %s %n",  uniqueChars, charSet);
            int chunks = input.length() % uniqueChars == 0? input.length() / uniqueChars :
                    input.length() / uniqueChars  + 1;

            for (int i = 0; i < chunks; i++) {
                Set<Character> chunkSet = new HashSet<>();
                if (i == chunks - 1) {
                    String chunk = input.substring(i * uniqueChars);
                    for (int j = 0; j < chunk.length(); j++) {
                        chunkSet.add(chunk.charAt(j));
                    }

                    System.out.printf("Processing chunk %s %s and chunkset is %s %n", i, chunk, chunkSet);
                    // chunk should be a subset of unique chars
                    if (!charSet.containsAll(chunkSet) || chunkSet.size() != chunk.length()) {
                        return false; // for any new character like ABCD AE
                    }
                } else {
                    String chunk = input.substring(i * uniqueChars, (i + 1) * uniqueChars);
                    for (int j = 0; j < chunk.length(); j++) {
                        chunkSet.add(chunk.charAt(j));
                    }
                    System.out.printf("Processing chunk %s %s and chunkset is %s %n", i, chunk, chunkSet);
                    if (chunkSet.size() != charSet.size() || !charSet.containsAll(chunkSet)) {
                        return false;
                    }
                }
            }
            return true;
        }


        /**
         * Random is defined as all the inputs have similar frequency. we can perform a chi-square test for it
         *
         * chi = Sum((Oi - Ei)^2 / Ei) < 0.05 which means the p-value is small enough that we can believe our hypothesis is true.
         *
         * @param input
         * @return
         */
        public boolean isRandom(String input) {
            // step 1 compute frequency map
            Map<Character, Integer> counter = new HashMap<>();
            for (int i = 0; i < input.length(); i++) {
                counter.put(input.charAt(i), counter.getOrDefault(input.charAt(i), 0) + 1);
            }
            // step 2, compute the Ei
            double expectation = (1.0 * counter.values().stream().mapToInt(Integer::valueOf).sum()) / counter.values().size();

            // step 3. compute the p-value
            double pvalue = 0;
            for (Integer frequency: counter.values()) {
                pvalue += 1.0 * (frequency - expectation) * (frequency - expectation) / expectation;
            }
            System.out.printf("The pvalue for input %s is %s %n", input, pvalue);
            return pvalue < 0.05;
        }
    }


    @Test
    public void testIsShuffle() {
        ShuffleMusic sol = new ShuffleMusic();
        Assert.assertEquals(sol.isShuffle("ABCDBCDACDAB") , true);
        Assert.assertEquals(sol.isShuffle("ABCDBCDACDA") , true);
        Assert.assertEquals(sol.isShuffle("ABCDBCDAC") , true);
        Assert.assertEquals(sol.isShuffle("ABCDBCDACC") , false);
        Assert.assertEquals(sol.isShuffle("ABCDBCDACE") , false);
        Assert.assertEquals(sol.isShuffle("ABCDA") , true);
        Assert.assertEquals(sol.isShuffle("ABCDAA") , false);
    }

    @Test
    public void testIsRandom() {
        ShuffleMusic sol = new ShuffleMusic();
        Assert.assertEquals(sol.isRandom("AAABC"), false);
        Assert.assertEquals(sol.isRandom("AAAAAAAAAAAAABBBBBBBBBBBBBCCCCCCCCCCCCCC"), true);
        Assert.assertEquals(sol.isRandom("ABCDEABCED"), true);
    }
}
