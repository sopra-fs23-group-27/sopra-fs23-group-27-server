package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.AuthenticationService;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;
    private final AuthenticationService authenticationService;
    private final PlayerService playerservice;

    public LobbyController(LobbyService lobbyService, AuthenticationService authenticationService, PlayerService playerservice) {
        this.lobbyService = lobbyService;
        this.authenticationService = authenticationService;
        this.playerservice = playerservice;
    }

    @PostMapping("/lobbies/basic")
    public ResponseEntity createBasicLobby(@RequestBody BasicLobbyCreateDTO basicLobbyCreateDTO,
                                           @RequestHeader("Authorization") String playerToken) {

        playerservice.checkIfPlayerIsAlreadyInLobby(playerToken);

        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby lobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, playerToken, basicLobbyInput.getIsPublic());
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO(lobbyCreated);

        authenticationService.addToAuthenticatedJoins(playerToken, lobbyGetDTO.getLobbyId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lobbyGetDTO);
    }

    @PostMapping("/lobbies/advanced")
    public ResponseEntity createAdvancedLobby(@RequestBody AdvancedLobbyCreateDTO advancedLobbyCreateDTO,
                                              @RequestHeader("Authorization") String playerToken) {

        playerservice.checkIfPlayerIsAlreadyInLobby(playerToken);

        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby lobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, playerToken, advancedLobbyInput.getIsPublic());
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbyGetDTO(lobbyCreated);

        authenticationService.addToAuthenticatedJoins(playerToken, lobbyGetDTO.getLobbyId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lobbyGetDTO);
    }

    @GetMapping("/lobbies")
    public ResponseEntity getAllPublicAndJoinableLobbies() {

        List<Lobby> lobbies = lobbyService.getAllPublicAndJoinableLobbies();
        List<LobbyGetDTO> lobbyGetDTOs = new ArrayList<>();

        for (Lobby lobby : lobbies) {
            LobbyGetDTO lobbyGetDTO = null;
            if (lobby instanceof BasicLobby) {
                lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO((BasicLobby) lobby);
            }
            else if (lobby instanceof AdvancedLobby) {
                lobbyGetDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbyGetDTO((AdvancedLobby) lobby);
            }
            lobbyGetDTOs.add(lobbyGetDTO);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lobbyGetDTOs);
    }

    @GetMapping("/lobbies/{lobbyId}")
    public ResponseEntity getLobbyById(@PathVariable Long lobbyId) {

        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        LobbyGetDTO lobbyGetDTO = null;
        if (lobby instanceof BasicLobby) {
            lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO((BasicLobby) lobby);
        }
        else if (lobby instanceof AdvancedLobby) {
            lobbyGetDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbyGetDTO((AdvancedLobby) lobby);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(lobbyGetDTO);
    }

    @PutMapping("/lobbies/{lobbyId}/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void joinLobby(@PathVariable Long lobbyId,
                          @RequestHeader("Authorization") String playerToken,
                          @RequestParam(value = "privateLobbyKey", required = false) String privateLobbyKey) {

        lobbyService.checkIfLobbyIsJoinable(lobbyId, privateLobbyKey);
        playerservice.checkIfPlayerIsAlreadyInLobby(playerToken);
        authenticationService.addToAuthenticatedJoins(playerToken, lobbyId);
    }

    @PutMapping("/lobbies/{lobbyId}/leave")
    public ResponseEntity leaveLobby(@PathVariable Long lobbyId,
                                     @RequestHeader("Authorization") String playerToken) {

        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        lobby = lobbyService.leaveLobby(lobby, playerToken);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/lobbies/{lobbyId}/start")
    public void startGameInLobby(@PathVariable Long lobbyId,
                                 @RequestHeader("Authorization") String playerToken) {

        this.lobbyService.startGame(lobbyId, playerToken);
    }
}