package cards;

import java.util.*;

/**
 * Player
 * ● Players have a hand of cards, represented as an ordered collection of cards.
 * ● Players can draw() from a deck. (See definition above).
 */
public class Player {
    private String name;
    private List<Card> hand;
    private Random rand;

    public Player(String name) {
        this.name = name;
        this.hand = new LinkedList<>();
        this.rand = new Random();
    }

    public void draw(Deck deck) {
        if (deck.hasCards()) {
            this.hand.add(deck.draw());
        }
    }

    public String toString() {
        return this.name + " has a hand of: " + this.hand;
    }

    public String getName() {
        return name;
    }

    public List<Card> getHand() {
        return hand;
    }

    public Card playRandom() {
        return this.play(this.rand.nextInt(this.hand.size()));
    }

    /**
     * Since no strategy needs to be considered, we just play the first card if no card found in suit.
     * TODO: optimize for strategies later
     *
     * Ideally these play styles with a particular strategy should be defined in a subclass, either for player styles or
     * for specific games.
     * @param starter
     * @return
     */
    public Card play(Card starter) {
        for (int i = 0; i < this.hand.size(); i++) {
            if (this.hand.get(i).getSuit().equals(starter.getSuit())) {
                return play(i);
            }
        }
        return playRandom();
    }

    /**
     * Ideally these play styles with a particular strategy should be defined in a subclass, either for player styles or
     * for specific games.
     *
     * @return
     */
    public Card playLargest() {
        // convert to card as
        Optional<Card> maxCard = this.hand.stream().max(Card::compareTo);
        Card largest = maxCard.map(card ->this.play(this.hand.indexOf(card))).orElse(null);
        return largest;
    }

    private Card play(int index) {
        Card card = this.hand.remove(index);
        System.out.println(String.format("Player %s played %s", this.name, card));
        return card;

    }
}
