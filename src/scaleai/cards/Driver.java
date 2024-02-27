package scaleai.cards;

import com.google.common.collect.ImmutableList;

/**
 * TODO:
 * 1. code reviews and watch others deck design
 * 2. factory pattern for card game generation
 */
public class Driver {
    public static void main(String [] args) {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player cat = new Player("Cat");
        Player dog = new Player("Dog");
        Deck deck = new Deck();
        CardGame game = CardGameFactory.getCardGame(CardGameFactory.GAME.SAME_SUIT_GAME,
                ImmutableList.of(alice, bob, cat, dog),
                deck);
        game.start();
    }
}
