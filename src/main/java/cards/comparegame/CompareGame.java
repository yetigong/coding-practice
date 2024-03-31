package cards.comparegame;

import cards.CardGame;
import cards.Card;
import cards.Deck;
import cards.Player;

import java.util.ArrayList;
import java.util.List;

public class CompareGame implements CardGame {
    private static final int DRAWING_ROUNDS = 5;
    private static final int MAX_PLAYERS = 10;
    private static final int MIN_PLAYERS = 2;
    private List<Player> players;
    private Deck deck;

    public CompareGame(List<Player> playerList, Deck deck) {
        if (playerList.size() > MAX_PLAYERS || playerList.size() < MIN_PLAYERS) {
            throw new IllegalArgumentException("Invalid player list!");
        }
        this.players = playerList;
        this.deck = deck;

    }
    @Override
    public void start() {
        this.drawCards();
        this.playRound(this.players);
    }

    public void drawCards() {
        for (int i = 0; i < DRAWING_ROUNDS; i++) {
            for (Player player: this.players) {
                player.draw(this.deck);
                System.out.printf("Player %s has drawn a card, the hand is now %s. %n",
                        player.getName(),
                        player.getHand());
            }
        }
    }

    public void playRound(List<Player> players) {
        List<String> winnerNames = new ArrayList<>();
        Card largest = null;
        for (Player player : players) {
            Card curr = player.playLargest();
            if (largest == null || curr.compareTo(largest) > 0) {
                winnerNames = new ArrayList<>();
                winnerNames.add(player.getName());
                largest = curr;
            } else if (curr.compareTo(largest) == 0) {
                winnerNames.add(player.getName());
            }
        }

        System.out.printf("Winners of the game is %s", winnerNames);
    }
}
