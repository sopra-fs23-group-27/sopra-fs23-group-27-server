package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Player Controller
 * This class is responsible for handling all REST request that are related to
 * the player.
 * The controller will receive the request and delegate the execution to the
 * PlayerService and finally return the result.
 */
@RestController
public class PlayerController {

    private final PlayerService playerService;

    PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/players")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PlayerGetDTO> getAllPlayers(@RequestHeader("Authorization") String token) {
        // fetch all players in the internal representation
        List<Player> players = playerService.getPlayers(token);
        List<PlayerGetDTO> playerGetDTOS = new ArrayList<>();

        // convert each player to the API representation
        for (Player player : players) {
            playerGetDTOS.add(DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player));
        }
        return playerGetDTOS;
    }

    @GetMapping("/players/{playerId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO getPlayer(@PathVariable Long playerId,
                                  @RequestHeader("Authorization") String token) {
        // fetch player in the internal representation
        Player player = playerService.getPlayerById(playerId, token);
        // convert player to the API representation
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);
    }

    @PostMapping("/players")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity createPlayer(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API player to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // create player
        Player createdPlayer = playerService.createPlayer(playerInput);

        // convert internal representation of player back to API
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Authorization", createdPlayer.getToken())
                .body(playerGetDTO);
    }

    @PostMapping("/registration")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity registerPlayer(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API player to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // create player
        Player createdPlayer = playerService.registerPlayer(playerInput);

        // convert internal representation of player back to API
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(createdPlayer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Authorization", createdPlayer.getToken())
                .body(playerGetDTO);
    }


    @PostMapping("/login")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity loginPlayer(@RequestBody PlayerPostDTO playerPostDTO) {
        // convert API player to internal representation
        Player playerInput = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // login player
        Player loggedInPlayer = playerService.loginPlayer(playerInput);


        // convert internal representation of player back to API
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(loggedInPlayer);

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("Authorization", loggedInPlayer.getToken())
                .body(playerGetDTO);
    }

    @PostMapping("/players/{playerId}/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PlayerGetDTO logoutPlayer(@PathVariable Long playerId,
                                     @RequestHeader("Authorization") String token) {

        // logout player
        Player loggedOutPlayer = playerService.logoutPlayer(playerId, token);

        // convert internal representation of player back to API
        return DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(loggedOutPlayer);
    }


    @PutMapping("/players/{playerId}")
    @CrossOrigin(exposedHeaders = "*")
    public ResponseEntity updatePlayer(@PathVariable Long playerId,
                                       @RequestBody PlayerPutDTO playerPutDTO,
                                       @RequestHeader("Authorization") String token) {

        // check if player exists
        playerService.checkIfPlayerIdExists(playerId);

        // update player
        Player updatedPlayer = playerService.updatePlayer(playerId, playerPutDTO, token);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .header("Authorization", updatedPlayer.getToken())
                .build();
    }
}
