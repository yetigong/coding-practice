package scaleai.cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A deck of cards is an ordered collection of cards. It will start with 52 cards, one
 * card for every combination of rank and suit.
 *
 * Players can draw() from a deck. This means that they take the top card from
 * the deck and add it to their hand.
 *
 * You can shuffle() a deck, which randomizes the order of the cards in the
 * deck.
 */
public class Deck {


    private int size = 52;
    private static Random RANDOM = new Random();
    private List<Card> cards;

    public Deck() {
        this.initialize();
    }

    private void initialize() {
        this.cards = new ArrayList<>();
        for (Map.Entry<String, Integer> entry: Card.MAPPING.entrySet()) {
            for (Card.SUIT suite : Card.SUIT.values()) {
                this.cards.add(new Card(entry.getKey(), suite));
            }
        }
    }
    public void shuffle() {
        for (int i = cards.size() - 1; i > 0; i--) {
            int target = RANDOM.nextInt(i + 1);
            Card original = this.cards.get(i);
            Card destination = this.cards.get(target);
            this.cards.set(i, destination);
            this.cards.set(target, original);
        }
    }

    public Card draw() {
        if (this.cards.isEmpty()) {
            return null;
        }
        return this.cards.remove(this.cards.size() - 1);
    }

    public boolean hasCards() {
        return !this.cards.isEmpty();
    }

    public int getSIZE() {
        return size;
    }

}
