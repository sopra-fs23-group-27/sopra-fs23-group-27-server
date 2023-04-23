package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class LobbyController {

    private final LobbyService lobbyService;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/lobbies/basic")
    public ResponseEntity createBasicLobby(@RequestBody BasicLobbyCreateDTO basicLobbyCreateDTO,
                                           @RequestHeader("Authorization") String playerToken) {

        Lobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby lobbyCreated = lobbyService.createBasicLobby(basicLobbyInput, playerToken, basicLobbyInput.getIsPublic());
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO(lobbyCreated);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lobbyGetDTO);
    }

    @PostMapping("/lobbies/advanced")
    public ResponseEntity createAdvancedLobby(@RequestBody AdvancedLobbyCreateDTO advancedLobbyCreateDTO,
                                              @RequestHeader("Authorization") String playerToken) {

        Lobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby lobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput, playerToken, advancedLobbyInput.getIsPublic());
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertAdvancedLobbyEntityToLobbyGetDTO(lobbyCreated);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lobbyGetDTO);
    }


    @GetMapping("/lobbies")
    public ResponseEntity getAllPublicLobbies() {

        List<Lobby> lobbies = lobbyService.getAllPublicLobbies();
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
    public ResponseEntity joinLobby(@PathVariable Long lobbyId,
                                    @RequestHeader("Authorization") String playerToken,
                                    @RequestParam(value = "privateLobbyKey", required = false) String privateLobbyKey) {

        Lobby lobby = lobbyService.getLobbyById(lobbyId);

        // if lobby is private, check if privateLobbyKey is correct
        if (!lobby.getIsPublic() && !lobby.getPrivateLobbyKey().equals(privateLobbyKey)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("You are trying to join a private lobby. However, the provided lobby key is incorrect.");
        }

        lobby = lobbyService.joinLobby(lobby, playerToken);
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
}