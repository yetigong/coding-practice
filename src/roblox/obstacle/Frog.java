package roblox.obstacle;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Frog {
    static class FrogCroak {
        int max = 0;
        int count = 0;
        static Map<Character, List<Integer>> charMap = Map.of(
                'r', List.of(0),
                'o', List.of(1, 4),
                'b', List.of(2),
                'l', List.of(3),
                'x', List.of(5)
        );

        static Map<Character, Integer> croakMap = Map.of(
                'c', 0,
                'r', 1,
                'o', 2,
                'a', 3,
                'k', 4
        );

        public int getCroak(String input) {
            return croakHelper(input, 0, new ArrayList<>());
        }

        private int croakHelper(String input, int curr, List<List<Character>> sequences) {
            if (curr == input.length()) {
                if (count == 0) {
                    return max;
                } else {
                    return -1;
                }
            }

            char c = input.charAt(curr);
            int index = croakMap.get(c);
            if (index == 0) {
                // special handling for beginning
                count++;
                this.max = Math.max(max, count);
                List<Character> newSeq = new ArrayList<>();
                newSeq.add(c);
                sequences.add(newSeq);
                return croakHelper(input, curr+1, sequences);
            } else {
                for (List<Character> existingSeq: sequences) {
                    if (existingSeq.size() == index) {
                        existingSeq.add(c);
                        if (existingSeq.size() == 5) {
                            count--;
                        }
                        return croakHelper(input, curr+1, sequences);
                    }
                }
                return -1;
            }
        }
        /**
         * This is a special case and follow up on the classic problem
         * https://leetcode.com/problems/minimum-number-of-frogs-croaking/description/
         *
         * the difference is there can be repeated characters in the original string.
         * It brings complexity in terms of there could be multiple ways to determine a character's position
         *
         * think about the case of robrloboxlox and robrloxoblox . In both examples they are valid. But
         * they share the same prefix, robrlo and robrlo while the 2nd o belongs to different sequences.
         *
         * @param input
         * @return
         */
        public int getRoblox(String input) {
            return helper(input, 0, new ArrayList<>());
        }

        private int helper(String input, int curr, List<List<Character>> sequences) {
            System.out.printf("Evaluating index %s curr with sequences %s %n", curr, sequences);
            if (curr == input.length()) {
            // we reached the end of the input. examine all sequences
                if (count == 0) {
                    return max;
                } else {
                    return -1;
                }
            }

            char c = input.charAt(curr);
            List<Integer> possibleIndex = charMap.get(c);
            for (Integer index: possibleIndex) {
                if (index == 0) {
                    // special handling for the first character
                    List<Character> newSequence = new ArrayList<>();
                    newSequence.add(c);
                    sequences.add(newSequence);
                    this.count++;
                    int originalMax = this.max;
                    this.max = Math.max(this.max, this.count);
                    int result = helper(input, curr+1, sequences);
                    if (result == -1) {
                        sequences.remove(newSequence);
                        this.count--;
                        this.max = originalMax;
                    } else {
                        return result;
                    }
                } else  {
                    for (List<Character> existingSequence: sequences) {
                        if (existingSequence.size() == index) {
                            // this current character can be appended to an existing sequence
                            existingSequence.add(c);
                            if (existingSequence.size() == 6) {
                                this.count--;
                            }
                            int result = helper(input, curr+1, sequences);
                            if (result == -1) {
                                existingSequence.remove(existingSequence.size() - 1);
                            } else {
                                return result;
                            }
                        }
                    }
                }
            }
            return -1;
        }
    }


    @Test
    public void testCroak() {
        String input = "croak";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getCroak(input), 1);
    }

    @Test
    public void testCroakBad() {
        String input = "croaka";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getCroak(input), -1);
    }

    @Test
    public void testCroak1() {
        String input = "crcroakoak";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getCroak(input), 2);
    }

    @Test
    public void testCroak2() {
        String input = "croakcrcroakoak";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getCroak(input), 2);
    }

    @Test
    public void testRoblox() {
        String input = "roblox";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getRoblox(input), 1);
    }

    @Test
    public void testRobloxBad() {
        String input = "roblo";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getRoblox(input), -1);
    }

    @Test
    public void testRoblox2() {
        String input = "robrloboxlox";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getRoblox(input), 2);
    }

    @Test
    public void testRoblox1() {
        String input = "robrloboxlox";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getRoblox(input), 2);
    }

    @Test
    public void testRoblox3() {
        String input = "robloxrorobbloloxx";
        FrogCroak sol = new FrogCroak();
        Assert.assertEquals(sol.getRoblox(input), 2);
    }
}
