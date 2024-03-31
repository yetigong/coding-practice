package cards;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/***
 * Card
 * ● A card has two properties: a rank and a suit.
 * ● A card’s rank is an ordered value. There are 13 ranks. From lowest to highest, a
 * rank can be one of the values: { 2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A }
 * ● A card’s suit is an unordered value. A suit can be one of { ♣, ♦, ♥, ♠ }. These
 * are spoken as “clubs”, “diamonds”, “hearts”, and “spades”.
 * ● A card is defined as a rank and a suit. We display them as rank + suit, for
 * example, 2♥, 10♦, Q♦, etc.
 */
public class Card implements Comparable<Card> {
    static Map<String, Integer> MAPPING = new ImmutableMap.Builder<String, Integer>()
            .put("2", 2)
            .put("3", 3)
            .put("4", 4)
            .put("5", 5)
            .put("6", 6)
            .put("7", 7)
            .put("8", 8)
            .put("9", 9)
            .put("10", 10)
            .put("J", 11)
            .put("Q", 12)
            .put("K", 13)
            .put("A", 14).build();
    private int value;
    private String rank;
    private SUIT suit;

    public Card (String rank, SUIT suit) {
        this.rank = rank;
        this.suit = suit;
        this.value = MAPPING.get(rank);
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(this.value, o.value);
    }

    @Override
    public String toString() {
        return this.rank + " " + this.suit;
    }

    public String getRank() {
        return rank;
    }

    public SUIT getSuit() {
        return suit;
    }

    public enum SUIT {
        CLUBS,
        HEARTS,
        SPADES,
        DIAMONDS
    }

}
