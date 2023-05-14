package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.FlagDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.HintDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@WebAppConfiguration
@SpringBootTest
public class CountryServiceIntegrationTest {
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CountryService countryService;
    @Mock
    private WebSocketService webSocketService;

    private HintHandler hintHandler;
    private Lobby advancedLobby;

    @AfterEach
    void tearDown() {
        countryRepository.deleteAll();
    }

    @Test
    @Transactional
    void testSourceAPI_CH() throws IOException {
        countryService.sourceAPI("CH");

        Country foundCountry = countryRepository.findByCountryCode("CH");

        assertNotNull(foundCountry);
        assertEquals("CH", foundCountry.getCountryCode());
        assertEquals("Switzerland", foundCountry.getName());
        assertTrue(foundCountry.getGdp().endsWith("M USD"));
        assertTrue(foundCountry.getSurfaceArea().endsWith(" sq. km"));
        assertTrue(foundCountry.getLifeExpectancyMale().endsWith(" years"));
        assertTrue(foundCountry.getLifeExpectancyFemale().endsWith(" years"));
        assertTrue(foundCountry.getUnemploymentRate().endsWith("%"));
        assertTrue(foundCountry.getImports().endsWith("M USD"));
        assertTrue(foundCountry.getExports().endsWith("M USD"));
        assertTrue(foundCountry.getHomicideRate().endsWith(" per 100'000 people"));
        assertEquals("Swiss Franc", foundCountry.getCurrency());
        assertTrue(foundCountry.getPopulationGrowth().endsWith("%"));
        assertTrue(foundCountry.getSecondarySchoolEnrollmentFemale().endsWith("% of people of secondary school age"));
        assertTrue(foundCountry.getSecondarySchoolEnrollmentMale().endsWith("% of people of secondary school age"));
        assertEquals("Bern", foundCountry.getCapital());
        assertTrue(foundCountry.getCo2Emissions().endsWith(" kilotons"));
        assertTrue(foundCountry.getForestedArea().endsWith("% of surface area"));
        assertTrue(foundCountry.getInfantMortality().endsWith(" per 1000 live births"));
        assertTrue(foundCountry.getPopulation().endsWith("K"));
        assertTrue(foundCountry.getPopulationDensity().endsWith(" people per sq. km of land area"));
        assertTrue(foundCountry.getInternetUsers().endsWith("% of population"));
    }

    @Test
    @Transactional
    void testSourceAPITwice_CH() throws IOException {
        countryService.sourceAPI("CH");
        countryService.sourceAPI("CH");

        Country foundCountry = countryRepository.findByCountryCode("CH");

        assertNotNull(foundCountry);
        assertEquals("CH", foundCountry.getCountryCode());
        assertEquals("Switzerland", foundCountry.getName());
        assertTrue(foundCountry.getGdp().endsWith("M USD"));
        assertTrue(foundCountry.getSurfaceArea().endsWith(" sq. km"));
        assertTrue(foundCountry.getLifeExpectancyMale().endsWith(" years"));
        assertTrue(foundCountry.getLifeExpectancyFemale().endsWith(" years"));
        assertTrue(foundCountry.getUnemploymentRate().endsWith("%"));
        assertTrue(foundCountry.getImports().endsWith("M USD"));
        assertTrue(foundCountry.getExports().endsWith("M USD"));
        assertTrue(foundCountry.getHomicideRate().endsWith(" per 100'000 people"));
        assertEquals("Swiss Franc", foundCountry.getCurrency());
        assertTrue(foundCountry.getPopulationGrowth().endsWith("%"));
        assertTrue(foundCountry.getSecondarySchoolEnrollmentFemale().endsWith("% of people of secondary school age"));
        assertTrue(foundCountry.getSecondarySchoolEnrollmentMale().endsWith("% of people of secondary school age"));
        assertEquals("Bern", foundCountry.getCapital());
        assertTrue(foundCountry.getCo2Emissions().endsWith(" kilotons"));
        assertTrue(foundCountry.getForestedArea().endsWith("% of surface area"));
        assertTrue(foundCountry.getInfantMortality().endsWith(" per 1000 live births"));
        assertTrue(foundCountry.getPopulation().endsWith("K"));
        assertTrue(foundCountry.getPopulationDensity().endsWith(" people per sq. km of land area"));
        assertTrue(foundCountry.getInternetUsers().endsWith("% of population"));
    }

    @Test
    @Transactional
    void testSourceAPI_MA() throws IOException {
        countryService.sourceAPI("MA");

        Country foundCountry = countryRepository.findByCountryCode("MA");

        assertNotNull(foundCountry);
        assertEquals("MA", foundCountry.getCountryCode());
        assertEquals("Morocco", foundCountry.getName());
        assertTrue(foundCountry.getGdp().endsWith("M USD"));
        assertTrue(foundCountry.getSurfaceArea().endsWith(" sq. km"));
        assertTrue(foundCountry.getLifeExpectancyMale().endsWith(" years"));
        assertTrue(foundCountry.getLifeExpectancyFemale().endsWith(" years"));
        assertTrue(foundCountry.getUnemploymentRate().endsWith("%"));
        assertTrue(foundCountry.getImports().endsWith("M USD"));
        assertTrue(foundCountry.getExports().endsWith("M USD"));
        assertTrue(foundCountry.getHomicideRate().endsWith(" per 100'000 people"));
        assertEquals("Moroccan Dirham", foundCountry.getCurrency());
        assertTrue(foundCountry.getPopulationGrowth().endsWith("%"));
        assertTrue(foundCountry.getSecondarySchoolEnrollmentFemale().endsWith("% of people of secondary school age"));
        assertTrue(foundCountry.getSecondarySchoolEnrollmentMale().endsWith("% of people of secondary school age"));
        assertEquals("Rabat", foundCountry.getCapital());
        assertTrue(foundCountry.getCo2Emissions().endsWith(" kilotons"));
        assertTrue(foundCountry.getForestedArea().endsWith("% of surface area"));
        assertTrue(foundCountry.getInfantMortality().endsWith(" per 1000 live births"));
        assertTrue(foundCountry.getPopulation().endsWith("K"));
        assertTrue(foundCountry.getPopulationDensity().endsWith(" people per sq. km of land area"));
        assertTrue(foundCountry.getInternetUsers().endsWith("% of population"));
    }

    @Test
    @Transactional
    void testSourceAPI_AD() throws IOException {
        // test country with missing values
        countryService.sourceAPI("AD");

        Country foundCountry = countryRepository.findByCountryCode("AD");

        assertNotNull(foundCountry);
        assertEquals("AD", foundCountry.getCountryCode());
        assertEquals("Andorra", foundCountry.getName());
        assertTrue(foundCountry.getGdp().endsWith("M USD"));
        assertTrue(foundCountry.getSurfaceArea().endsWith(" sq. km"));
        assertEquals("not available years", foundCountry.getLifeExpectancyMale());
        assertEquals("not available years", foundCountry.getLifeExpectancyFemale());
        assertEquals("not available%", foundCountry.getUnemploymentRate());
        assertTrue(foundCountry.getImports().endsWith("M USD"));
        assertTrue(foundCountry.getExports().endsWith("M USD"));
        assertTrue(foundCountry.getHomicideRate().endsWith(" per 100'000 people"));
        assertEquals("Euro", foundCountry.getCurrency());
        assertTrue(foundCountry.getPopulationGrowth().endsWith("%"));
        assertEquals("not available% of people of secondary school age", foundCountry.getSecondarySchoolEnrollmentFemale());
        assertEquals("not available% of people of secondary school age", foundCountry.getSecondarySchoolEnrollmentMale());
        assertEquals("Andorra La Vella", foundCountry.getCapital());
        assertEquals("not available kilotons", foundCountry.getCo2Emissions());
        assertTrue(foundCountry.getForestedArea().endsWith("% of surface area"));
        assertEquals("not available per 1000 live births", foundCountry.getInfantMortality());
        assertTrue(foundCountry.getPopulation().endsWith("K"));
        assertTrue(foundCountry.getPopulationDensity().endsWith(" people per sq. km of land area"));
        assertTrue(foundCountry.getInternetUsers().endsWith("% of population"));
    }

    @Test
    @Transactional
    public void testSourceAPIAndSetHintsAndSendHints_AdvancedMode_AD() throws IOException, InterruptedException {
        // Mock the WebSocketService
        webSocketService = Mockito.mock(WebSocketService.class);

        // create a advanced lobby
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testBasicLobby");
        advancedLobby.setLobbyId(1L);
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(15);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(1);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);

        countryService.sourceAPI("AD");

        Country foundCountry = countryRepository.findByCountryCode("AD");

        hintHandler = new HintHandler(foundCountry.getCountryCode(), advancedLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(13, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", foundCountry.getFlag())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("GDP", foundCountry.getGdp())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Surface Area", foundCountry.getSurfaceArea())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Imports", foundCountry.getImports())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Exports", foundCountry.getExports())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Homicide Rate", foundCountry.getHomicideRate())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", foundCountry.getCurrency())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population Growth", foundCountry.getPopulationGrowth())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Forested Area", foundCountry.getForestedArea())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", foundCountry.getCapital())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", foundCountry.getPopulation())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population Density", foundCountry.getPopulationDensity())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Internet Users", foundCountry.getInternetUsers())));

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 65 seconds to ensure all four hints are sent
        Thread.sleep(14000);

        // verify that sendToLobby was called 3 times with the expected parameters
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(12)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
    }

    @Test
    @Transactional
    public void testSourceAPIAndSetHintsAndSendHints_NotEnoughHints_AdvancedMode_AD() throws IOException, InterruptedException {
        // Mock the WebSocketService
        webSocketService = Mockito.mock(WebSocketService.class);

        // create a advanced lobby
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testBasicLobby");
        advancedLobby.setLobbyId(1L);
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(20);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(1);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);

        countryService.sourceAPI("AD");

        Country foundCountry = countryRepository.findByCountryCode("AD");

        hintHandler = new HintHandler(foundCountry.getCountryCode(), advancedLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(13, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", foundCountry.getFlag())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("GDP", foundCountry.getGdp())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Surface Area", foundCountry.getSurfaceArea())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Imports", foundCountry.getImports())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Exports", foundCountry.getExports())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Homicide Rate", foundCountry.getHomicideRate())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", foundCountry.getCurrency())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population Growth", foundCountry.getPopulationGrowth())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Forested Area", foundCountry.getForestedArea())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", foundCountry.getCapital())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", foundCountry.getPopulation())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population Density", foundCountry.getPopulationDensity())));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Internet Users", foundCountry.getInternetUsers())));

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 75 seconds to ensure all four hints are sent
        Thread.sleep(20000);

        // verify that sendToLobby was called 3 times with the expected parameters
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(12)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
    }
}
