package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private WebSocketService webSocketService;
    private final GameService gameService;


    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository,
                        @Qualifier("playerRepository") PlayerRepository playerRepository,
                        PlayerService playerService,
                        WebSocketService webSocketService,
                        GameService gameService) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.webSocketService = webSocketService;
        this.gameService = gameService;

    }

    public BasicLobby createBasicLobby(Lobby basicLobby, String playerToken, Boolean isPublic) {
        checkIfLobbyIsCreatable(basicLobby);

        Player player = this.playerService.getPlayerByToken(playerToken);

        basicLobby.setLobbyCreatorPlayerToken(playerToken);
        basicLobby.addPlayerToLobby(player.getPlayerName());

        Lobby savedLobby = this.lobbyRepository.save(basicLobby);
        this.lobbyRepository.flush();

        if (!isPublic) {
            savedLobby = setPrivateLobbyKey(savedLobby);
        }

        if (savedLobby.getLobbyName() == null) {
            savedLobby.setLobbyName("Lobby " + savedLobby.getLobbyId().toString());
            this.lobbyRepository.save(savedLobby);
            this.lobbyRepository.flush();
        }

        player.setLobbyId(savedLobby.getLobbyId());
        player.setCreator(true);
        this.playerRepository.save(player);
        this.playerRepository.flush();

        return (BasicLobby) savedLobby;
    }

    public AdvancedLobby createAdvancedLobby(Lobby advancedLobby, String playerToken, Boolean isPublic) {
        checkIfLobbyIsCreatable(advancedLobby);

        Player player = this.playerService.getPlayerByToken(playerToken);

        advancedLobby.setLobbyCreatorPlayerToken(playerToken);
        advancedLobby.addPlayerToLobby(player.getPlayerName());

        Lobby savedLobby = this.lobbyRepository.save(advancedLobby);
        this.lobbyRepository.flush();

        if (!isPublic) {
            savedLobby = setPrivateLobbyKey(savedLobby);
        }

        if (savedLobby.getLobbyName() == null) {
            savedLobby.setLobbyName("Lobby " + savedLobby.getLobbyId().toString());
            this.lobbyRepository.save(savedLobby);
            this.lobbyRepository.flush();
        }

        player.setLobbyId(savedLobby.getLobbyId());
        player.setCreator(true);
        this.playerRepository.save(player);
        this.playerRepository.flush();

        return (AdvancedLobby) savedLobby;
    }

    private Lobby setPrivateLobbyKey(Lobby lobby) {
        String privateLobbyKey = Base64.getEncoder().encodeToString(lobby.getLobbyId().toString().getBytes());
        lobby.setPrivateLobbyKey(privateLobbyKey);
        lobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();
        return lobby;
    }


    public List<Lobby> getAllPublicAndJoinableLobbies() {
        return this.lobbyRepository.findAllByIsPublicAndIsJoinable(true, true);
    }

    public Lobby getLobbyById(long lobbyId) {
        Lobby lobby = this.lobbyRepository.findByLobbyId(lobbyId);
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Lobby with id " + lobbyId + " does not exist");
        }
        return lobby;
    }

    public LobbyGetDTO joinLobby(Lobby lobby, String playerToken, String wsConnectionId) {
        Player player = this.playerService.getPlayerByToken(playerToken);
        if (!player.isCreator()) {
//            // check if player is already in the lobby
//            if (Objects.equals(lobby.getLobbyId(), player.getLobbyId())) {
//                return lobby;
//            }

            // check if player is already in another lobby
            if (player.getLobbyId() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "A player can only be part of one lobby at a time. Please leave the currently joined lobby first.");
            }

            lobby.addPlayerToLobby(player.getPlayerName());
        }
        Lobby savedLobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();

        player.setLobbyId(lobby.getLobbyId());
        player.setWsConnectionId(wsConnectionId);

        this.playerRepository.save(player);
        this.playerRepository.flush();

        LobbyGetDTO lobbyGetDTO = null;
        if (lobby instanceof BasicLobby) {
            lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO((BasicLobby) lobby);
        }
        else if (lobby instanceof AdvancedLobby) {
            lobbyGetDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbyGetDTO((AdvancedLobby) lobby);
        }

        return lobbyGetDTO;
    }

    public Lobby leaveLobby(Lobby lobby, String playerToken) {
        Player player = this.playerService.getPlayerByToken(playerToken);

        // check if player is in the lobby
        if (!Objects.equals(lobby.getLobbyId(), player.getLobbyId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Player is not part of this lobby");
        }

        lobby.removePlayerFromLobby(player.getPlayerName());
        player.setLobbyId(null);

        Lobby savedLobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();

        this.playerRepository.save(player);
        this.playerRepository.flush();

        return savedLobby;
    }

    public void startGame(Long lobbyId, String playerToken) {

        Lobby lobby = getLobbyById(lobbyId);

        // check if player is the lobby creator
        if (!lobby.getLobbyCreatorPlayerToken().equals(playerToken)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the lobby creator. Only the creator can start the game");
        }

        // check if lobby has enough players
        if (lobby.getJoinedPlayerNames().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lobby needs at least 2 players to start the game");
        }

        lobby.setJoinable(false);
        lobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();

        this.gameService.startGame(lobby);
    }

    public void disconnectPlayer(String wsConnectionId) {
        Player player = this.playerService.getPlayerByWsConnectionId(wsConnectionId);
        Lobby lobby = getLobbyById(player.getLobbyId());

        if (lobby != null) {
            lobby.removePlayerFromLobby(player.getPlayerName());
            this.lobbyRepository.save(lobby);
            this.lobbyRepository.flush();

            player.setLobbyId(null);
            this.playerRepository.save(player);
            this.playerRepository.flush();
        }
    }

    public void checkIfLobbyIsJoinable(Long lobbyId, String privateLobbyKey) {
        Lobby lobby = getLobbyById(lobbyId);

        // check if lobby with specific id exists
        if (lobby.getLobbyId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lobby not found.");
        }
        // check if game has already started
        if (!lobby.isJoinable()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are trying to join a lobby that has already started a game.");
        }
        // check if lobby is private and if privateLobbyKey is correct
        if (!lobby.getIsPublic() && !lobby.getPrivateLobbyKey().equals(privateLobbyKey)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are trying to join a private lobby. However, the provided lobby key is incorrect.");
        }
    }

    private void checkIfLobbyIsCreatable(Lobby lobby) {
        if (lobby.getLobbyName() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby's name is missing.");
        }
        if (lobbyRepository.findByLobbyName(lobby.getLobbyName()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lobby's name is already taken.");
        }
    }

}
