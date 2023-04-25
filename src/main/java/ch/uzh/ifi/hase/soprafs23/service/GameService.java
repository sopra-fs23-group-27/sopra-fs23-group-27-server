package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;

@Service
public class GameService {
    private final CountryHandlerService countryHandlerService;
    private final CountryRepository countryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameService(@Qualifier("countryRepository") CountryRepository countryRepository,
                       CountryHandlerService countryHandlerService,
                       SimpMessagingTemplate messagingTemplate) {

        this.countryRepository = countryRepository;
        this.countryHandlerService = countryHandlerService;
        this.messagingTemplate = messagingTemplate;
    }

    public void validateGuess(Integer gameId, GuessDTO guessDTO) {
        Game game = GameRepository.findByLobbyId(gameId.longValue());
        game.validateGuess(guessDTO.getPlayerName(), guessDTO.getGuess());
    }

    public Game createGame(Lobby lobby) {
        Game game = new Game(countryHandlerService, countryRepository, messagingTemplate, lobby);
        GameRepository.addGame(lobby.getLobbyId(), game);
        return game;
    }
}
