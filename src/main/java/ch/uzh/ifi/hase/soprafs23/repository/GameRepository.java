package ch.uzh.ifi.hase.soprafs23.repository;

import java.util.HashMap;

import ch.uzh.ifi.hase.soprafs23.entity.Game;

public class GameRepository {
    private static final HashMap<String, Game> allGames = new HashMap<>();

    public static void addGame(String lobbyId, Game game) {
        allGames.put(lobbyId, game);
    }

    public static void removeGame(String lobbyId) {
        if (allGames.containsKey(lobbyId)) {
            allGames.remove(lobbyId);
        }
    }

    public static Game findByLobbyId(String lobbyId) {
        return allGames.get(lobbyId);
    }

}
