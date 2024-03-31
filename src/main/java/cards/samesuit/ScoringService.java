package cards.samesuit;

import cards.Player;

import java.util.*;

public class ScoringService {
    private List<Player> players;
    private Map<Integer, Integer> playerIdToPoints;

    public ScoringService(List<Player> players) {
        this.players = players;
        this.playerIdToPoints = new HashMap<>();
        for (int i = 0; i < this.players.size(); i++) {
            this.playerIdToPoints.put(i, 0);
        }
    }

    public void updatePoints(int playerId, int points) {
        this.playerIdToPoints.put(playerId, this.playerIdToPoints.getOrDefault(playerId, 0) + points);
    }

    public int getPlayerPoints(int playerId) {
        return this.playerIdToPoints.getOrDefault(playerId, 0);
    }

    public String summarizeAllPlayerPoints() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.playerIdToPoints.keySet().size(); i++) {
            sb.append(String.format("Player %s has %s points.  ",
                    this.players.get(i).getName(),
                    this.playerIdToPoints.get(i)));
        }
        return sb.toString();
    }

    public List<String> getGameWinner() {
        Set<Integer> winnerIds = new HashSet<>();
        int maxScore = 0;
        for (Map.Entry<Integer, Integer> playerScore: this.playerIdToPoints.entrySet()) {
            if (playerScore.getValue() > maxScore) {
                winnerIds = new HashSet<>();
                winnerIds.add(playerScore.getKey());
                maxScore = playerScore.getValue();
            } else if (playerScore.getValue() == maxScore) {
                winnerIds.add(playerScore.getKey());
            }
        }

        List<String> winnerNames = new ArrayList<>();
        for (int i = 0; i < this.players.size(); i++) {
            if (winnerIds.contains(i)) {
                winnerNames.add(this.players.get(i).getName());
            }
        }
        return winnerNames;
    }
}
