package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Country;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class CountryServiceIntegrationTest {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CountryService countryService;

    @Test
    @Transactional
    void testSourceAPI_CH() throws IOException {
        countryService.sourceAPI("CH");

        Country foundCountry = countryRepository.findByCountryCode("CH");

        assertNotNull(foundCountry);
        assertEquals("CH", foundCountry.getCountryCode());
        assertEquals("Switzerland", foundCountry.getName());
    }

    @Test
    @Transactional
    void testSourceAPI_MA() throws IOException {
        countryService.sourceAPI("MA");

        Country foundCountry = countryRepository.findByCountryCode("MA");

        assertNotNull(foundCountry);
        assertEquals("MA", foundCountry.getCountryCode());
        assertEquals("Morocco", foundCountry.getName());
    }
}
