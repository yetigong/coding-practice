package verkd.wordle;

public class WordleSolverDriver {
    public static void main(String [] args) {
        WordleGame game = new WordleGame();
        WordleSolver solver = new WordleSolver();
        String finalGuess = solver.solve(game);
        if (finalGuess == null) {
            System.out.printf("Cannot solve the game!");
        } else {
            System.out.printf("Solved the game! The final guess is %s %n", finalGuess);
        }
    }
}
