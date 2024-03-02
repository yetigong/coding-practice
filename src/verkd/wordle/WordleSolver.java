package verkd.wordle;


import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * There is a key assumption that the solver should get access to the same dictionary, otherwise how it can come up with
 * a word to guess...
 */
public class WordleSolver {
    public String solve(WordleGame game) {
        List<String> dict = WordleGame.dict;
        List<String> candidates = ImmutableList.copyOf(dict);
        // the strategy would be:
        // 1. start with a random guess
        // 2. based on the result, scan through all words in the dic
        //  2.1 filter the ones matching 2s at the exact position
        //  2.2 filter the ones has 1s but exactly not at the position
        //  2.3 filter out the words that has the 0 characters
        // for the shorted list, pick random and start over again
        while (game.canContinue()) {
            String guess = candidates.get(new Random().nextInt(candidates.size()));
            List<Integer> result = game.guess(guess);
            if (guessed(result)) {
                return guess;
            }
            candidates = candidates.stream().filter(candidate -> filterCandidate(candidate, guess, result))
                    .toList();
        }
        return null;
    }

    private boolean filterCandidate(String candidate, String guess, List<Integer> result) {
        for (int i = 0; i < guess.length(); i++) {
            if (result.get(i) == 2) {
                if (candidate.charAt(i) != guess.charAt(i)) {
                    return false;
                }
            } else if (result.get(i) == 1) {
                if (candidate.charAt(i) == guess.charAt(i)) {
                    return false;
                }
                if (!candidate.contains(String.valueOf(guess.charAt(i)))) {
                    return false;
                }
            } else {
                // when result.get(i) == 0
                if (candidate.contains(String.valueOf(guess.charAt(i)))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean guessed(List<Integer> result) {
        int total = 0;
        for (Integer i : result) {
            total += i;
        }
        return total == 2 * WordleGame.MAX_GUESS;
    }
}

