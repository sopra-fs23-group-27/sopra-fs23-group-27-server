package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

    @Autowired
    public PlayerService(@Qualifier("playerRepository") PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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

    public Player createPlayer(Player newPlayer) {

        // create basic authentication token
        String playername = newPlayer.getPlayername();
        String password = newPlayer.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((playername + ":" + password).getBytes());

        newPlayer.setToken("Basic " + encodeBytes);

        newPlayer.setStatus(PlayerStatus.ONLINE);

        // set the current datetime as the creation date
        newPlayer.setCreationDate(LocalDateTime.now());
        checkIfPlayernameExists(newPlayer.getPlayername());
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newPlayer = playerRepository.save(newPlayer);
        playerRepository.flush();

        log.debug("Created Information for Player: {}", newPlayer);
        return newPlayer;
    }

    public Player loginPlayer(Player existingPlayer) {
        Player playerByPlayername = playerRepository.findByPlayername(existingPlayer.getPlayername());

        if (playerByPlayername == null) { //check if a player with the provided playername exists
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The playername provided does not exist. Please register first.");
        }
        else if (!existingPlayer.getPassword().equals(playerByPlayername.getPassword())) { //given a player with the provided playername exists, check if the provided password is correct
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The password provided is not correct. Please try again.");
        }

        // create basic authentication token
        String playername = playerByPlayername.getPlayername();
        String password = playerByPlayername.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((playername + ":" + password).getBytes());
        playerByPlayername.setToken("Basic " + encodeBytes);

        playerByPlayername.setStatus(PlayerStatus.ONLINE);
        playerByPlayername = playerRepository.save(playerByPlayername);
        playerRepository.flush();

        return playerByPlayername;
    }

    public Player logoutPlayer(long playerId, String token) {

        checkIfPlayerIdExists(playerId);
        Player existingPlayer = playerRepository.findById(playerId);

        // check if token is valid
        checkIfPlayerTokenIsValid(token, existingPlayer);

        // check if player is already logged out
        if (existingPlayer.getStatus() == PlayerStatus.OFFLINE) {
            String baseErrorMessage = "The player with id %d is already logged out";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, playerId));
        }

        // logout player
        existingPlayer.setStatus(PlayerStatus.OFFLINE);
        existingPlayer = playerRepository.save(existingPlayer);
        playerRepository.flush();

        return existingPlayer;
    }


    public Player updatePlayer(long playerId, PlayerPutDTO playerUpdateRequest, String token) {
        //checkIfPlayerIdExists(playerId);
        Player playerToBeUpdated = playerRepository.findById(playerId);

        // check if token is valid
        checkIfPlayerTokenIsValid(token, playerToBeUpdated);

        // update password if provided password is not null
        if (playerUpdateRequest.getPassword() != null) {
            playerToBeUpdated.setPassword(playerUpdateRequest.getPassword());
        }

        // update birthday if provided birthday is not null
        if (playerUpdateRequest.getBirthday() != null && !playerUpdateRequest.getBirthday().isBlank()) {
            try {
                LocalDate birthdayParsed = LocalDate.parse(playerUpdateRequest.getBirthday());
                playerToBeUpdated.setBirthday(birthdayParsed);
            }
            catch (DateTimeParseException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Error: The birthday provided is not in the correct format. Please use the format yyyy-mm-dd.");
            }

        }

        // update playername if provided playername is not null
        if (playerUpdateRequest.getPlayername() != null) {
            checkIfPlayernameExists(playerUpdateRequest.getPlayername());
            playerToBeUpdated.setPlayername(playerUpdateRequest.getPlayername());
        }

        String playername = playerToBeUpdated.getPlayername();
        String password = playerToBeUpdated.getPassword();
        String encodeBytes = Base64.getEncoder().encodeToString((playername + ":" + password).getBytes());

        playerToBeUpdated.setToken("Basic " + encodeBytes);

        playerToBeUpdated = playerRepository.save(playerToBeUpdated);
        playerRepository.flush();
        return playerToBeUpdated;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * playername
     * defined in the Player entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param newPlayername
     * @throws org.springframework.web.server.ResponseStatusException
     * @see Player
     */
    private void checkIfPlayernameExists(String newPlayername) {
        Player playerByPlayername = playerRepository.findByPlayername(newPlayername);

        String baseErrorMessage = "Error: The %s provided %s already taken and cannot be used. " +
                "Please select another playername!";
        if (playerByPlayername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format(baseErrorMessage, "playername", "is"));
        }
    }

    public void checkIfPlayerIdExists(long playerId) {
        Player playerById = playerRepository.findById(playerId);
        if (playerById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error: The player with playerId " + playerId + " does not exist.");
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
}
