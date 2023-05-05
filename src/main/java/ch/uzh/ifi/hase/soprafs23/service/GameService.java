package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.LobbySettingsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;

import java.util.HashMap;


@Service
public class GameService {

    private final Logger log = LoggerFactory.getLogger(GameService.class);
    private final CountryHandlerService countryHandlerService;
    private final WebSocketService webSocketService;
    private final CountryRepository countryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final LobbyRepository lobbyRepository;
    private final LobbyService lobbyService;
    private final PlayerService playerService;

    @Autowired
    public GameService(@Qualifier("countryRepository") CountryRepository countryRepository,
                       @Qualifier("lobbyRepository") LobbyRepository lobbyRepository,
                       CountryHandlerService countryHandlerService,
                       SimpMessagingTemplate messagingTemplate,
                       WebSocketService webSocketService,
                       LobbyService lobbyService,
                       PlayerService playerService) {

        this.countryRepository = countryRepository;
        this.countryHandlerService = countryHandlerService;
        this.webSocketService = webSocketService;
        this.messagingTemplate = messagingTemplate;
        this.lobbyRepository = lobbyRepository;
        this.lobbyService = lobbyService;
        this.playerService = playerService;
    }

    public void validateGuess(Integer gameId, GuessDTO guessDTO, SimpMessageHeaderAccessor smha) {
        Game game = GameRepository.findByLobbyId(gameId.longValue());
        String wsConnectionId = WebSocketService.getIdentity(smha);
        game.validateGuess(guessDTO.getPlayerName(), guessDTO.getGuess(), wsConnectionId);
    }

    public void startGame(Lobby lobby) {
        Long lobbyId = lobby.getLobbyId();

        // Inform all players in the lobby that the game has started
        this.webSocketService.sendToLobby(lobbyId, "/game-start", "{}");

        Game game = new Game(countryHandlerService, webSocketService, countryRepository, lobby);
        GameRepository.addGame(lobby.getLobbyId(), game);

        lobby.setCurrentGameId(game.getGameId());
        lobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();
        game.startGame();

    }

    public void sendLobbySettings(Integer lobbyId, SimpMessageHeaderAccessor smha) {
        Lobby lobby = lobbyService.getLobbyById(lobbyId.longValue());

        LobbySettingsDTO lobbySettingsDTO;
        if (lobby instanceof BasicLobby) {
            lobbySettingsDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbySettingsDTO((BasicLobby) lobby);
        }
        else {
            lobbySettingsDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbySettingsDTO((AdvancedLobby) lobby);
        }

        // Create playerRoleMap: <playerName, isLobbyCreator>
        HashMap<String, Boolean> playerRoleMap = new HashMap<>();
        for (String playername : lobby.getJoinedPlayerNames()) {
            playerRoleMap.put(playername, false);
        }

        // Set the lobbyCreator to true
        String lobbyCreatorToken = lobby.getLobbyCreatorPlayerToken();
        Player player = this.playerService.getPlayerByToken(lobbyCreatorToken);
        playerRoleMap.put(player.getPlayerName(), true);

        lobbySettingsDTO.setPlayerRoleMap(playerRoleMap);

        String wsConnectionId = WebSocketService.getIdentity(smha);
        this.webSocketService.sendToLobby(lobbyId.longValue(), "/lobby-settings", lobbySettingsDTO);

    }


}
