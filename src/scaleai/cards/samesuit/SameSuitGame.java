package scaleai.cards.samesuit;

import com.google.common.collect.ImmutableList;
import scaleai.cards.Card;
import scaleai.cards.CardGame;
import scaleai.cards.Deck;
import scaleai.cards.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 */
public class SameSuitGame implements CardGame {
    /**
     *  the decision to store the mapping at the same class, is because this is something exclusive to this game.
     */
    private List<Player> players;
    private ScoringService scoringService;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;
    private Deck deck;
    private int rounds;
    private Random rand;

    public SameSuitGame(List<Player> players, Deck deck) {
        // make this players list immutable for safety (incase of mutations on it). It should be static once game starts.
        if (players.size() > MAX_PLAYERS || players.size() < MIN_PLAYERS) {
            throw new IllegalArgumentException("Invalid player setup!");
        }
        this.players = ImmutableList.copyOf(players);
        this.scoringService = new ScoringService(this.players);
        this.deck = deck;
        this.deck.shuffle();
        System.out.println(String.format("SameSuitGame initialization completed. The player status is %s", players));

        // assuming we ignore the last round to avoid uneven distribution of cards.
        this.rounds = this.deck.getSIZE() / this.players.size();
        this.rand = new Random();
    }

    public void drawingPhase() {
        for (int i = 0; i < this.rounds; i++) {
            for (Player player : players) {
                player.draw(this.deck);
                System.out.println(String.format("Player %s draws a card, the hand is now %s", player.getName(), player.getHand()));
            }
        }
    }

    @Override
    public void start() {
        // draw cards
        this.drawingPhase();

        int startingPlayer = this.rand.nextInt(this.players.size());
        for (int i = 0; i < this.rounds; i++) {
            startingPlayer = this.playRound(i, startingPlayer, this.players);
        }

        System.out.printf("The game has ended. The status of players are: %s. %n", this.scoringService.summarizeAllPlayerPoints());
        System.out.printf("The winner of the game is %s", this.scoringService.getGameWinner());
    }

    /**
     * TODO: Needs to confirm what if no one can play the original suit
     * For now, assuming it is the starting player wins
     *
     * @return the int representation winner in the list of players
     */
    public int playRound(int roundNumber, int startingPlayer, List<Player> players) {
        List<Card> playedCards = new ArrayList<>();
        Card starter = players.get(startingPlayer).playRandom();
        playedCards.add(starter);
        Card winnerCard = starter;
        Player winnerPlayer = players.get(startingPlayer);
        int winnerIndex = startingPlayer;

        List<Integer> roundSequence = this.generateRoundSeq(startingPlayer);

        for (Integer idx: roundSequence) {
            Card card = players.get(idx).play(starter);
            playedCards.add(card);
            if (card.getSuit().equals(starter.getSuit()) && card.compareTo(winnerCard) > 0) {
                winnerPlayer = players.get(idx);
                winnerCard = card;
                winnerIndex = idx;
            }
        }

        // summarize points of the round
        int roundPoints = 0;
        for (Card card: playedCards) {
            roundPoints += CardPointRule.getInstance().getScore(card);
        }

        // update the players points
        this.scoringService.updatePoints(winnerIndex, roundPoints);

        System.out.printf("The round %s finishes. %s (index %s) is the winner for the round with %s points.%n", roundNumber, winnerPlayer, winnerIndex, roundPoints);
        System.out.printf("Current scoring status is: %s. %n", this.scoringService.summarizeAllPlayerPoints());
        return winnerIndex;
    }

    private List<Integer> generateRoundSeq(int startingPlayer) {
        List<Integer> result = new ArrayList<>();
        for (int i = startingPlayer + 1; i < players.size(); i++) {
            result.add(i);
        }
        for (int i = 0; i < startingPlayer; i++) {
            result.add(i);
        }
        return result;
    }
}
