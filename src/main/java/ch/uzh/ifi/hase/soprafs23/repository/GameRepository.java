package ch.uzh.ifi.hase.soprafs23.repository;

import java.util.HashMap;

import ch.uzh.ifi.hase.soprafs23.entity.Game;

public class GameRepository {
    private static final HashMap<Long, Game> allGames = new HashMap<>();

    public static void addGame(Long lobbyId, Game game) {
        allGames.put(lobbyId, game);
    }

    public static void removeGame(Long lobbyId) {
        if (allGames.containsKey(lobbyId)) {
            allGames.get(lobbyId).clearGame();
            allGames.remove(lobbyId);
        }
    }

    public static Game findByLobbyId(Long lobbyId) {
        return allGames.get(lobbyId);
    }

}
