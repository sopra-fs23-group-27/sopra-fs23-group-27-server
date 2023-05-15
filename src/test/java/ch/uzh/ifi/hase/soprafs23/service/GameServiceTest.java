package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.GuessDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.LobbySettingsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private CountryHandlerService countryHandlerService;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private GameService gameService;

    private Lobby basicLobby;
    private Lobby advancedLobby;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        basicLobby = new BasicLobby();
        basicLobby.setLobbyId(1L);
        basicLobby.setLobbyName("testLobby");
        basicLobby.setLobbyCreatorPlayerToken("testToken");

        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(2L);
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setLobbyCreatorPlayerToken("testAdvancedToken");

        gameService = spy(new GameService(
                countryRepository,
                playerRepository,
                lobbyRepository,
                countryHandlerService,
                messagingTemplate,
                webSocketService,
                playerService));
    }

    @Test
    public void testStartGame_basicMode(){
        // given
        Long lobbyId = basicLobby.getLobbyId();

        Game basicGame = mock(Game.class);
        doNothing().when(basicGame).startGame();

        // mock repositories and services
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), anyString());
        doNothing().when(gameService).sendLobbySettings(anyInt());
        when(lobbyRepository.save(any())).thenReturn(basicLobby);

        // call method to be tested
        gameService.startGame(basicLobby);

        // then

        verify(webSocketService, times(1)).sendToLobby(eq(lobbyId), eq("/game-start"), eq("{}"));
        verify(gameService, times(1)).sendLobbySettings(eq(lobbyId.intValue()));
        verify(gameRepository, times(1)).addGame(eq(lobbyId), any(Game.class));
    }

    @Test
    public void testStartGame_advancedMode(){
        // given
        Long lobbyId = advancedLobby.getLobbyId();

        Game basicGame = mock(Game.class);
        doNothing().when(basicGame).startGame();

        // mock repositories and services
        doNothing().when(webSocketService).sendToLobby(anyLong(), anyString(), anyString());
        doNothing().when(gameService).sendLobbySettings(anyInt());
        when(lobbyRepository.save(any())).thenReturn(advancedLobby);

        // call method to be tested
        gameService.startGame(advancedLobby);

        // then

        verify(webSocketService, times(1)).sendToLobby(eq(lobbyId), eq("/game-start"), eq("{}"));
        verify(gameService, times(1)).sendLobbySettings(eq(lobbyId.intValue()));
        verify(gameRepository, times(1)).addGame(eq(lobbyId), any(Game.class));
    }
}