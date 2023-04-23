package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
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


    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository,
                        @Qualifier("playerRepository") PlayerRepository playerRepository,
                        PlayerService playerService) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
    }

    public BasicLobby createBasicLobby(Lobby basicLobby, String playerToken, Boolean isPublic) {
        Player player = this.playerService.getPlayerByToken(playerToken);

        basicLobby.setLobbyCreatorPlayerToken(playerToken);
        basicLobby.addPlayerToLobby(player.getPlayerName());

        Lobby savedLobby = this.lobbyRepository.save(basicLobby);
        this.lobbyRepository.flush();

        if (!isPublic) {
            savedLobby = setPrivateLobbyKey(savedLobby);
        }

        player.setLobbyId(savedLobby.getLobbyId());
        player.setCreator(true);
        this.playerRepository.save(player);
        this.playerRepository.flush();

        return (BasicLobby) savedLobby;
    }

    public AdvancedLobby createAdvancedLobby(Lobby advancedLobby, String playerToken, Boolean isPublic) {
        Player player = this.playerService.getPlayerByToken(playerToken);

        advancedLobby.setLobbyCreatorPlayerToken(playerToken);
        advancedLobby.addPlayerToLobby(player.getPlayerName());

        Lobby savedLobby = this.lobbyRepository.save(advancedLobby);
        this.lobbyRepository.flush();

        if (!isPublic) {
            savedLobby = setPrivateLobbyKey(savedLobby);
        }

        player.setLobbyId(savedLobby.getLobbyId());
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


    public List<Lobby> getAllPublicLobbies() {
        return this.lobbyRepository.findAllByIsPublic(true);
    }

    public Lobby getLobbyById(long lobbyId) {
        Lobby lobby = this.lobbyRepository.findByLobbyId(lobbyId);
        if (lobby == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Lobby with id " + lobbyId + " does not exist");
        }
        return lobby;
    }

    public Lobby joinLobby(Lobby lobby, String playerToken) {
        Player player = this.playerService.getPlayerByToken(playerToken);

        // check if player is already in the lobby
        if (Objects.equals(lobby.getLobbyId(), player.getLobbyId())) {
            return lobby;
        }

        // check if player is already in another lobby
        if (player.getLobbyId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A player can only be part of one lobby at a time. Please leave the currently joined lobby first.");
        }

        lobby.addPlayerToLobby(player.getPlayerName());
        player.setLobbyId(lobby.getLobbyId());

        Lobby savedLobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();

        this.playerRepository.save(player);
        this.playerRepository.flush();

        return savedLobby;
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
}
