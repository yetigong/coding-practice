package cards.samesuit;

import com.google.common.collect.ImmutableList;
import cards.Player;

import java.util.List;

public class PlayerService {
    private List<Player> players;

    public PlayerService(List<Player> players) {
        this.players = ImmutableList.copyOf(players);
    }

    /**
     * When this project grows to support multiple number of games concurrently, we need to introduce the concept of game Id
     * . The getPlayers() method should also take in gameId for players within the game.
     *
     * @return
     */
    public List<Player> getPlayers() {
       return this.players;
    }
}
