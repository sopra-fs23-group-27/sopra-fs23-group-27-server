package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.incoming.RemoveDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.security.SecureRandom;
import java.util.*;

@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;
    private final PlayerService playerService;
    private final GameService gameService;


    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository,
                        @Qualifier("playerRepository") PlayerRepository playerRepository,
                        PlayerService playerService,
                        GameService gameService) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.playerService = playerService;
        this.gameService = gameService;

    }

    public BasicLobby createBasicLobby(Lobby basicLobby, String playerToken, Boolean isPublic) {
        checkIfLobbyIsCreatable(basicLobby);
        checkIfContinentsAreValid(basicLobby);

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
        checkIfContinentsAreValid(advancedLobby);

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
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        String privateLobbyKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
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

        // check if lobby is collecting Play-Agains
        if (lobby.isCollectingPlayAgains()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lobby is still collecting players for a re-match. Once the re-collecting time of 20 seconds" +
                            " is over, the lobby creator can start the game again.");
        }

        lobby.setJoinable(false);
        lobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();

        this.gameService.startGame(lobby);
    }

    public synchronized Lobby leaveLobby(Lobby lobby, String playerToken) {
        Player player = this.playerService.getPlayerByToken(playerToken);

        // check if player is in the lobby
        if (!Objects.equals(lobby.getLobbyId(), player.getLobbyId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Player is not part of this lobby");
        }

        Lobby savedLobby = removePlayerFromLobby(player, lobby);

        return savedLobby;
    }

    public synchronized String kickPlayerFromLobby(Integer lobbyId, RemoveDTO removeDTO, String wsConnectionId) {
        Lobby lobby = getLobbyById(lobbyId);
        Player requester = this.playerService.getPlayerByWsConnectionId(wsConnectionId);
        Player playerToBeKicked = this.playerRepository.findByPlayerName(removeDTO.getPlayerName());
        String playerTokenToBeKicked = playerToBeKicked.getToken();
        String playerWsConnectionIdToBeKicked = playerToBeKicked.getWsConnectionId();

        // check if requester is the lobby creator
        if (!lobby.getLobbyCreatorPlayerToken().equals(requester.getToken())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not the lobby creator. Only the creator can kick players.");
        }

        Lobby savedLobby = leaveLobby(lobby, playerTokenToBeKicked);

        return playerWsConnectionIdToBeKicked;
    }

    private Lobby removePlayerFromLobby(Player player, Lobby lobby) {
        String playerToken = player.getToken();

        // remove player from lobby
        lobby.removePlayerFromLobby(player.getPlayerName());

        // remove player from game
        if (!lobby.isJoinable()) {
            this.gameService.removePlayerFromGame(lobby.getCurrentGameId(), player.getPlayerName());
        }

        // check if player was the lobby creator; if yes, change lobby creator
        if (playerToken.equals(lobby.getLobbyCreatorPlayerToken())) {
            if (lobby.getJoinedPlayerNames().size() > 0) {
                lobby = changeLobbyCreator(lobby);
            }
        }

        lobby = this.lobbyRepository.save(lobby);

        // send updated lobby to all players in lobby
        this.gameService.sendLobbySettings(lobby.getLobbyId().intValue());
        Game game = GameRepository.findByLobbyId(lobby.getLobbyId());
        if (game != null) {
            game.resendStatsToLobby();
        }

        // delete lobby if no players are left in the lobby and the lobby is not waiting for a re-match
        if (lobby.getJoinedPlayerNames().size() == 0 && !lobby.isCollectingPlayAgains()) {
            log.info("Lobby {} has been deleted since last player left the lobby.",
                    lobby.getLobbyId());
            this.lobbyRepository.delete(lobby);
        }

        this.lobbyRepository.flush();

        this.playerService.clearLobbyConfigFromPlayer(playerToken);

        return lobby;
    }

    public synchronized void clearPlayerAfterGameEnd(String playerToken) {
        Player player = this.playerService.getPlayerByToken(playerToken);
        if (player == null) {
            log.info("Player with playerToken {} is not a registered player." +
                    "Returning without action taken.", playerToken);
            return;
        }

        this.playerService.clearLobbyConfigFromPlayer(playerToken);
    }

    public synchronized void disconnectPlayer(String playerToken) {
        Player player = this.playerService.getPlayerByToken(playerToken);
        if (player == null) {
            log.info("Player with playerToken {} is not a registered player." +
                    "Returning without action taken.", playerToken);
            return;
        }


        Lobby lobby = getLobbyById(player.getLobbyId());
        if (lobby != null) {
            log.info("Player {} is removed from lobby {}.", player.getPlayerName(), lobby.getLobbyId());

            Lobby savedLobby = removePlayerFromLobby(player, lobby);
        }

        if (!player.isPermanent()) {
            log.info("Player {} is deleted since he is not a registered player.", player.getPlayerName());

            this.playerService.deletePlayer(player.getId(), player.getToken());
        }

    }

    public void playAgain(Integer lobbyId, String wsConnectionId) {

        Lobby lobby = getLobbyById(lobbyId);
        Player requester = this.playerService.getPlayerByWsConnectionId(wsConnectionId);

        if (requester != null && Objects.equals(lobbyId.longValue(), requester.getLobbyId())) {
            if (lobby.isJoinable()) {

                // first player that clicks "Play Again" becomes the new lobby creator
                if (lobby.getJoinedPlayerNames().size() == 0) {
                    lobby.setLobbyCreatorPlayerToken(requester.getToken());
                }
                lobby.addPlayerToLobby(requester.getPlayerName());
                Lobby savedLobby = this.lobbyRepository.save(lobby);
                this.lobbyRepository.flush();
            }
        }

    }

    public void resendLobbySettings(Integer lobbyId) {
        this.gameService.sendLobbySettings(lobbyId);
    }


    private Lobby changeLobbyCreator(Lobby lobby) {
        String newCreatorUsername = lobby.getJoinedPlayerNames().get(0);

        Player newCreator = this.playerRepository.findByPlayerName(newCreatorUsername);
        lobby.setLobbyCreatorPlayerToken(newCreator.getToken());
        newCreator.setCreator(true);
        this.playerRepository.save(newCreator);
        this.playerRepository.flush();
        log.info("Lobby {}: Player {} has been set as new lobby creator.",
                lobby.getLobbyId(), newCreatorUsername);
        Lobby savedLobby = this.lobbyRepository.save(lobby);
        this.lobbyRepository.flush();
        return savedLobby;
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

    private void checkIfContinentsAreValid(Lobby lobby) {
        ArrayList<String> allContinents = new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania"));
        ArrayList<String> continentsInGame = new ArrayList<String>();
        // check if continents are valid
        for (String continent : lobby.getContinent()) {
            if (allContinents.contains(continent)) {
                continentsInGame.add(continent);
            }
        }
        // if no valid continents are given, return all continents
        if (lobby.getContinent() == null || continentsInGame.size() == 0) {
            lobby.setContinent(allContinents);
        }
        else {
            lobby.setContinent(continentsInGame);
        }
    }
}
