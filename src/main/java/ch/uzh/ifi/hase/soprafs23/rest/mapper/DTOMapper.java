package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import javax.persistence.Basic;

/**
 * DTOMapper
 * This class is responsible for generating classes that will automatically
 * transform/map the internal representation
 * of an entity (e.g., the Player) to the external/API representation (e.g.,
 * PlayerGetDTO for getting, PlayerPostDTO for creating)
 * and vice versa.
 * Additional mappers can be defined for new entities.
 * Always created one mapper for getting information (GET) and one mapper for
 * creating information (POST).
 */
@Mapper
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(source = "password", target = "password")
    @Mapping(source = "playerName", target = "playerName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "token", ignore = true)
    Player convertPlayerPostDTOtoEntity(PlayerPostDTO playerPostDTO);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "playerName", target = "playerName")
    PlayerGetDTO convertEntityToPlayerGetDTO(Player player);


    @Mapping(source = "isPublic", target = "isPublic")
    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "numOptions", target = "numOptions")
    @Mapping(source = "numSeconds", target = "numSeconds")
    BasicLobby convertBasicLobbyCreateDTOtoEntity(BasicLobbyCreateDTO basicLobbyCreateDTO);

    @Mapping(source = "isPublic", target = "isPublic")
    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "numSeconds", target = "numSeconds")
    @Mapping(source = "numSecondsUntilHint", target = "numSecondsUntilHint")
    @Mapping(source = "hintInterval", target = "hintInterval")
    @Mapping(source = "maxNGuesses", target = "maxNGuesses")
    AdvancedLobby convertAdvancedLobbyCreateDTOtoEntity(AdvancedLobbyCreateDTO advancedLobbyCreateDTO);


    @Mapping(source = "isPublic", target = "isPublic")
    @Mapping(source = "lobbyName", target = "lobbyName")
    @Mapping(source = "numOptions", target = "numOptions")
    @Mapping(source = "numSeconds", target = "numSeconds")
    LobbyGetDTO convertBasicLobbyEntityToLobbyGetDTO(BasicLobby basicLobby);
}
