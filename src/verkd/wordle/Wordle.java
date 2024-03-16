package verkd.wordle;

import java.util.ArrayList;
import java.util.List;

public class Wordle {
    static class Game {
        List<String> dict;
        final int MAX_GUESSES = 5;
        private String target;
        public Game() {

        }

        public List<Integer> guess(String guess) {
            return new ArrayList<>();
        }
    }
    static class Solver {
        public String solve(Game game) {
            return "";
        }
    }


}
