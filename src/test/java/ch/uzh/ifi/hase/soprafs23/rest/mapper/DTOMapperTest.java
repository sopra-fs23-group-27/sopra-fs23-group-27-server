package ch.uzh.ifi.hase.soprafs23.rest.mapper;

import ch.uzh.ifi.hase.soprafs23.constant.PlayerStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.PlayerPostDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
public class DTOMapperTest {
    @Test
    public void testCreatePlayer_fromPlayerPostDTO_toPlayer_success() {
        // create PlayerPostDTO
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setPlayername("playername");

        // MAP -> Create player
        Player player = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // check content
        assertEquals(playerPostDTO.getPassword(), player.getPassword());
        assertEquals(playerPostDTO.getPlayername(), player.getPlayername());
    }

    @Test
    public void testGetPlayer_fromPlayer_toPlayerGetDTO_success() {
        // create Player
        Player player = new Player();
        player.setPassword("password");
        player.setPlayername("firstname@lastname");
        player.setStatus(PlayerStatus.OFFLINE);
        player.setToken("1");

        // MAP -> Create PlayerGetDTO
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);

        // check content
        assertEquals(player.getId(), playerGetDTO.getId());
        assertEquals(player.getCreationDate(), playerGetDTO.getCreationDate());
        assertEquals(player.getPlayername(), playerGetDTO.getPlayername());
        assertEquals(player.getStatus(), playerGetDTO.getStatus());
    }
}
