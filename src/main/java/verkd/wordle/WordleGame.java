package verkd.wordle;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class  WordleGame {
    static final ImmutableList<String> dict = ImmutableList.<String>builder()
            .add("blind")
            .add("sheet")
            .add("crush")
            .add("relax")
            .add("drain")
            .add("label")
            .add("expel")
            .add("thump")
            .build();
    static final int MAX_GUESS = 5;
    private List<String> guesses;
    private Set<Character> charSet;
    private String answer ;

    private boolean won;

    public WordleGame() {
        this.guesses = new ArrayList<>();
        int wordIndex = new Random().nextInt(dict.size());
        this.answer = dict.get(wordIndex);
        this.charSet = initializeCharSet();
        this.won = false;
    }
    public List<Integer> guess(String word) {
        if (word == null || word.length() != 5) {
            throw new IllegalArgumentException("Invalid input: " + word);
        }
        if (!dict.contains(word)) {
            throw new IllegalArgumentException(String.format("Guessed word %s is not in dictionary!", word));
        }

        if (MAX_GUESS == this.guesses.size()) {
            throw new IllegalStateException("exceeded maximum guesses!");
        }

        if (this.won) {
            throw new IllegalStateException("The game has already won!");
        }

        List<Integer> results = verifyGuess(word);
        this.guesses.add(word);
        if (!this.won) {
            System.out.printf("You have guess %s, and the result is %s! %s out of %s chances left! %n",
                    word, results, this.guesses.size(), MAX_GUESS);
        } else {
            System.out.printf("You have guess %s, and the result is %s! You have won the game!! %n",
                    word, results);
        }

        return results;
    }

    private List<Integer> verifyGuess(String guess) {
        List<Integer> result = new ArrayList<>();
        int total = 0;
        for (int i = 0; i < guess.length(); i++) {
            if (guess.charAt(i) == this.answer.charAt(i)) {
                // exact match at the same char same position
                result.add(2);
                total += 2;
            } else if (this.charSet.contains(guess.charAt(i))) {
                result.add(1);
                total += 1;
            } else {
                result.add(0);
            }
        }
        if (total == 2 * MAX_GUESS) {
            this.won = true;
        }
        return result;
    }

    private Set<Character> initializeCharSet() {
        Set<Character> charSet = new HashSet<>();
        for (int i = 0; i < this.answer.length(); i++) {
            charSet.add(this.answer.charAt(i));
        }
        return charSet;
    }

    public boolean canContinue() {
        return (!won && guesses.size() < MAX_GUESS);
    }
}
