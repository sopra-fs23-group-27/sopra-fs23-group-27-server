package ch.uzh.ifi.hase.soprafs23.rest.mapper;

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
public class PlayerDTOMapperTest {
    @Test
    public void testCreatePlayer_fromPlayerPostDTO_toPlayer_success() {
        // create PlayerPostDTO
        PlayerPostDTO playerPostDTO = new PlayerPostDTO();
        playerPostDTO.setPassword("password");
        playerPostDTO.setPlayerName("playerName");

        // MAP -> Create player
        Player player = DTOMapper.INSTANCE.convertPlayerPostDTOtoEntity(playerPostDTO);

        // check content
        assertEquals(playerPostDTO.getPassword(), player.getPassword());
        assertEquals(playerPostDTO.getPlayerName(), player.getPlayerName());
    }

    @Test
    public void testGetPlayer_fromPlayer_toPlayerGetDTO_success() {
        // create Player
        Player player = new Player();
        player.setPassword("password");
        player.setPlayerName("firstname@lastname");
        player.setToken("1");

        // MAP -> Create PlayerGetDTO
        PlayerGetDTO playerGetDTO = DTOMapper.INSTANCE.convertEntityToPlayerGetDTO(player);

        // check content
        assertEquals(player.getId(), playerGetDTO.getId());
        assertEquals(player.getPlayerName(), playerGetDTO.getPlayerName());
    }
}
