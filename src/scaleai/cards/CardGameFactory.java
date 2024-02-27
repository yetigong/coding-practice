package scaleai.cards;

import scaleai.cards.samesuit.SameSuitGame;

import java.util.List;

public class CardGameFactory {
    public enum GAME {
        SAME_SUIT_GAME,
    }
    public static CardGame getCardGame(GAME game, List<Player> players, Deck deck) {
        switch (game) {
            case SAME_SUIT_GAME:
                return new SameSuitGame(players, deck);
            default:
                throw new IllegalArgumentException("Invalid Game type: " + game);
        }
    }
}
