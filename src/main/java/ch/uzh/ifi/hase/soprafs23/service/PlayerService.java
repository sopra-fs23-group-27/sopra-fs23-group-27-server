package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.AuthenticateDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;

/**
 * Player Service
 * This class is the "worker" and responsible for all functionality related to
 * the player
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;
    private final LobbyRepository lobbyRepository;

    private final PlayerStats PlayerStats;

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository,
                         LobbyRepository lobbyRepository) {
        this.playerRepository = playerRepository;
        this.lobbyRepository = lobbyRepository;
        this.PlayerStats = new PlayerStats(); // Can be changed
    }

    public List<Player> getPlayers(String token) {
        checkIfTokenExists(token);
        return this.playerRepository.findAll();
    }

    public Player getPlayerById(long playerId, String token) {
        checkIfTokenExists(token);
        checkIfPlayerIdExists(playerId);
        return this.playerRepository.findById(playerId);
    }

    public Player getPlayerByToken(String token) {
        return this.playerRepository.findByToken(token);
    }

    public Player getPlayerByWsConnectionId(String wsConnectionId) {
        Player player = this.playerRepository.findByWsConnectionId(wsConnectionId);
        if (player == null) {
            log.debug("Player with wsConnectionId {} does not exist." +
                    "Returning NULL player.", wsConnectionId);
        }
        return player;
    }

    public Player createPlayer(Player newPlayer) {

        // create basic authentication token
        String playerName = newPlayer.getPlayerName();
        String password = newPlayer.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((playerName + ":" + password).getBytes());

        newPlayer.setToken("Basic " + encodeBytes);

        checkIfPlayerNameExists(newPlayer.getPlayerName());
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }

    public Player loginPlayer(Player existingPlayer) {
        Player playerByPlayerName = playerRepository.findByPlayerName(existingPlayer.getPlayerName());

        if (playerByPlayerName == null) { // check if a player with the provided playerName exists
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The playerName provided does not exist. Please register first.");
        }
        else if (!existingPlayer.getPassword().equals(playerByPlayerName.getPassword())) { // given a player with the
            // provided playerName
            // exists, check if the
            // provided password is
            // correct
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "The password provided is not correct. Please try again.");
        }

        // create basic authentication token
        String playerName = playerByPlayerName.getPlayerName();
        String password = playerByPlayerName.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((playerName + ":" + password).getBytes());
        playerByPlayerName.setToken("Basic " + encodeBytes);

        playerByPlayerName = playerRepository.save(playerByPlayerName);
        playerRepository.flush();

        return playerByPlayerName;
    }

    public Player logoutPlayer(long playerId, String token) {

        checkIfPlayerIdExists(playerId);
        Player existingPlayer = playerRepository.findById(playerId);

        // check if token is valid
        checkIfPlayerTokenIsValid(token, existingPlayer);

        existingPlayer = playerRepository.save(existingPlayer);
        playerRepository.flush();

        return existingPlayer;
    }

    public Player updatePlayer(long playerId, PlayerPutDTO playerUpdateRequest, String token) {
        // checkIfPlayerIdExists(playerId);
        Player playerToBeUpdated = playerRepository.findById(playerId);

        // check if token is valid
        checkIfPlayerTokenIsValid(token, playerToBeUpdated);

        // update password if provided password is not null
        if (playerUpdateRequest.getPassword() != null) {
            playerToBeUpdated.setPassword(playerUpdateRequest.getPassword());
        }

        // update playerName if provided playerName is not null
        if (playerUpdateRequest.getPlayerName() != null) {
            checkIfPlayerNameExists(playerUpdateRequest.getPlayerName());
            playerToBeUpdated.setPlayerName(playerUpdateRequest.getPlayerName());
        }

        String playerName = playerToBeUpdated.getPlayerName();
        String password = playerToBeUpdated.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((playerName + ":" + password).getBytes());

        playerToBeUpdated.setToken("Basic " + encodeBytes);

        playerToBeUpdated = playerRepository.save(playerToBeUpdated);
        playerRepository.flush();
        return playerToBeUpdated;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * playerName
     * defined in the Player entity. The method will do nothing if the input is
     * unique
     * and throw an error otherwise.
     *
     * @param newPlayerName
     * @throws org.springframework.web.server.ResponseStatusException
     * @see Player
     */
    private void checkIfPlayerNameExists(String newPlayerName) {
        Player playerByPlayerName = playerRepository.findByPlayerName(newPlayerName);

        String baseErrorMessage = "Error: The %s provided %s already taken and cannot be used. " +
                "Please select another playerName!";
        if (playerByPlayerName != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "playerName", "is"));
        }
    }

    public void checkIfPlayerIdExists(long playerId) {
        Player playerById = playerRepository.findById(playerId);
        if (playerById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Error: The player with playerId " + playerId + " does not exist.");
        }
    }

    private void checkIfPlayerTokenIsValid(String token, Player existingPlayer) {
        if (!token.equals(existingPlayer.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "You are unauthorized to perform this action since your provided token is not valid.");
        }
    }

    private void checkIfTokenExists(String token) {
        boolean tokenExists = playerRepository.existsByToken(token);
        if (!tokenExists) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Error: You are unauthorized to perform this action.");
        }
    }

/*    public void joinLobby(String wsConnectionId, AuthenticateDTO dto) {
        Player player = playerRepository.findByToken(dto.getPlayerToken());
        if (player == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Player with token " + dto.getPlayerToken() + " not found. Please authenticate first.");
        }

        player.setWsConnectionId(wsConnectionId);
        playerRepository.saveAndFlush(player);

        Long lobbyId = player.getLobbyId();
        Lobby lobby = lobbyRepository.findByLobbyId(lobbyId);

        LobbyGetDTO lobbyGetDTO = null;
        if (lobby instanceof BasicLobby) {
            lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO((BasicLobby) lobby);
        }
        else if (lobby instanceof AdvancedLobby) {
            lobbyGetDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbyGetDTO((AdvancedLobby) lobby);
        }

        webSocketService.sendToPlayerInLobby(wsConnectionId, "/register", lobbyId.toString(), lobbyGetDTO);

        webSocketService.wait(500);

        webSocketService.sendToLobby(lobbyId, "/lobby-settings", lobbyGetDTO);
    }*/
}
