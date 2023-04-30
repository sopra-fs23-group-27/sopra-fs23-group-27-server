package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;


@Service
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final CountryHandlerService countryHandlerService;
    private final WebSocketService webSocketService;
    private final CountryRepository countryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyRepository lobbyRepository;

    @Autowired
    public GameService(@Qualifier("countryRepository") CountryRepository countryRepository,
                       @Qualifier("lobbyRepository") LobbyRepository lobbyRepository,
                       CountryHandlerService countryHandlerService,
                       SimpMessagingTemplate messagingTemplate,
                       WebSocketService webSocketService) {

        this.countryRepository = countryRepository;
        this.countryHandlerService = countryHandlerService;
        this.webSocketService = webSocketService;
        this.messagingTemplate = messagingTemplate;
        this.lobbyRepository = lobbyRepository;
    }

    public void validateGuess(Integer gameId, GuessDTO guessDTO) {
        Game game = GameRepository.findByLobbyId(gameId.longValue());
        game.validateGuess(guessDTO.getPlayerName(), guessDTO.getGuess());
    }

    public void startGame(Lobby lobby) {
        Long lobbyId = lobby.getLobbyId();

        // Inform all players in the lobby that the game has started
        this.webSocketService.sendToLobby(lobbyId, "/game-start", "{}");

        Game game = new Game(countryHandlerService, webSocketService, countryRepository, messagingTemplate, lobby);
        GameRepository.addGame(lobby.getLobbyId(), game);

        lobby.setCurrentGameId(game.getGameId());
        lobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();
        game.startGame();

    }


}
