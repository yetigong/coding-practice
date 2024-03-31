package cards.samesuit;

import cards.Card;

import java.util.HashMap;
import java.util.Map;

public class CardPointRule {
    private static Map<String, Integer> rankToPoint;
    static {
        rankToPoint = new HashMap<>();
        rankToPoint.put("5", 5);
        rankToPoint.put("10", 10);
        rankToPoint.put("K", 10);
    }
    private static CardPointRule cardPointRule;
    private CardPointRule () {
    }

    public static CardPointRule getInstance() {
        if (cardPointRule == null) {
            cardPointRule = new CardPointRule();
        }
        return cardPointRule;
    }


    public int getScore(Card card) {
        return rankToPoint.getOrDefault(card.getRank(), 0);
    }
}
