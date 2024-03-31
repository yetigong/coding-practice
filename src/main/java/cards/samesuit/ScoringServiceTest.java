package cards.samesuit;

import com.google.common.collect.ImmutableList;
import org.testng.Assert;
import org.testng.annotations.Test;
import cards.Deck;
import cards.Player;

import java.util.List;

public class ScoringServiceTest {
    @Test
    public void testHappyPath() {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player cat = new Player("Cat");
        Player dog = new Player("Dog");
        Deck deck = new Deck();

        List<Player> playerList = ImmutableList.of(alice, bob, cat, dog);
        ScoringService scoringService = new ScoringService(playerList);

        scoringService.updatePoints(0, 10);
        scoringService.updatePoints(1, 20);
        scoringService.updatePoints(2, 30);
        scoringService.updatePoints(3, 40);

        Assert.assertEquals(scoringService.getGameWinner(), ImmutableList.of("Dog"));
    }

    @Test
    public void testTies() {
        Player alice = new Player("Alice");
        Player bob = new Player("Bob");
        Player cat = new Player("Cat");
        Player dog = new Player("Dog");
        Deck deck = new Deck();

        List<Player> playerList = ImmutableList.of(alice, bob, cat, dog);
        ScoringService scoringService = new ScoringService(playerList);

        scoringService.updatePoints(0, 10);
        scoringService.updatePoints(1, 20);
        scoringService.updatePoints(2, 30);
        scoringService.updatePoints(3, 30);

        Assert.assertEquals(scoringService.getGameWinner(), ImmutableList.of("Cat", "Dog"));
    }
}
