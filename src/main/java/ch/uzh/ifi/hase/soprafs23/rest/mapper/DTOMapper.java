package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.*;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.LobbySettingsDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

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


    BasicLobby convertBasicLobbyCreateDTOtoEntity(BasicLobbyCreateDTO basicLobbyCreateDTO);


    AdvancedLobby convertAdvancedLobbyCreateDTOtoEntity(AdvancedLobbyCreateDTO advancedLobbyCreateDTO);


    LobbyGetDTO convertBasicLobbyEntityToLobbyGetDTO(BasicLobby basicLobby);

    LobbyGetDTO convertAdvancedLobbyEntityToLobbyGetDTO(AdvancedLobby advancedLobby);

    LobbyGetDTO convertLobbyEntityToLobbyGetDTO(Lobby lobby);

    LobbySettingsDTO convertBasicLobbyEntityToLobbySettingsDTO(BasicLobby basicLobby);

    LobbySettingsDTO convertAdvancedLobbyEntityToLobbySettingsDTO(AdvancedLobby advancedLobby);

}
