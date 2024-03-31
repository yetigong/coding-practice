package verkd.wordle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Driver {
    public static void main(String[] args) {
        WordleGame game = new WordleGame();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    List<Integer> result = game.guess(line);
                    System.out.printf("The result for your guess %s is %s %n", line, result);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                } catch (IllegalStateException e) {
                    System.err.println(e.getMessage() + " GoodBye!");
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
