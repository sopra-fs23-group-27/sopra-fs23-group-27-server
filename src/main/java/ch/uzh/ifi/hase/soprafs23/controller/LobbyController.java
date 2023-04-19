package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
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
    public ResponseEntity createBasicLobby(@RequestBody BasicLobbyCreateDTO basicLobbyCreateDTO) {

        BasicLobby basicLobbyInput = DTOMapper.INSTANCE.convertBasicLobbyCreateDTOtoEntity(basicLobbyCreateDTO);
        BasicLobby lobbyCreated = lobbyService.createBasicLobby(basicLobbyInput);
        LobbyGetDTO lobbyGetDTO = DTOMapper.INSTANCE.convertBasicLobbyEntityToLobbyGetDTO(lobbyCreated);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(lobbyGetDTO);
    }

    @PostMapping("/lobbies/advanced")
    public ResponseEntity createAdvancedLobby(@RequestBody AdvancedLobbyCreateDTO advancedLobbyCreateDTO) {

        AdvancedLobby advancedLobbyInput = DTOMapper.INSTANCE.convertAdvancedLobbyCreateDTOtoEntity(advancedLobbyCreateDTO);
        AdvancedLobby lobbyCreated = lobbyService.createAdvancedLobby(advancedLobbyInput);
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
}