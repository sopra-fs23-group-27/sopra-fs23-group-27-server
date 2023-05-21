package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.*;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.ChoicesDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.FlagDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.HintDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;

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

    private CountryHandler countryHandler;
    private HintHandler hintHandler;
    private AdvancedLobby advancedLobby;
    private BasicLobby basicLobby;

    @BeforeEach
    void setup(){
        countryHandler = new CountryHandler(countryRepository, countryService);
    }

    @AfterEach
    void tearDown() {
        countryRepository.deleteAll();
    }

    @Test
    @Transactional
    void testGetAllCountryNamesInAllContinents() {
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Oceania", "Africa", "Asia", "Europe", "Americas"));
        ArrayList<Country> foundCountries = countryService.getAllCountriesInContinents(continents);

        // check if the correct number of countries is returned
        assertEquals(212, foundCountries.size());
    }

    @Test
    @Transactional
    void testGetAllCountryNamesInEuropeAndAsia() {
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Asia", "Europe"));
        ArrayList<Country> foundCountries = countryService.getAllCountriesInContinents(continents);

        // check if the correct number of countries is returned
        assertEquals(91, foundCountries.size());
    }

    @Test
    @Transactional
    void testGetAllCountryNamesInContinentOceania_invalidContinentsAreOverwritten() throws IOException {
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Oceania", "Antarctica", "Switzerland"));
        ArrayList<Country> foundCountries = countryService.getAllCountriesInContinents(continents);

        // check if the correct number of countries is returned
        assertEquals(22, foundCountries.size());
        // check if all countries are from the correct continent
        assertEquals("Oceania", foundCountries.get(0).getContinent());
        assertEquals("Oceania", foundCountries.get(1).getContinent());
        assertEquals("Oceania", foundCountries.get(2).getContinent());
        assertEquals("Oceania", foundCountries.get(3).getContinent());
        assertEquals("Oceania", foundCountries.get(4).getContinent());
        assertEquals("Oceania", foundCountries.get(5).getContinent());
        assertEquals("Oceania", foundCountries.get(6).getContinent());
        assertEquals("Oceania", foundCountries.get(7).getContinent());
        assertEquals("Oceania", foundCountries.get(8).getContinent());
        assertEquals("Oceania", foundCountries.get(9).getContinent());
        assertEquals("Oceania", foundCountries.get(10).getContinent());
        assertEquals("Oceania", foundCountries.get(11).getContinent());
        assertEquals("Oceania", foundCountries.get(12).getContinent());
        assertEquals("Oceania", foundCountries.get(13).getContinent());
        assertEquals("Oceania", foundCountries.get(14).getContinent());
        assertEquals("Oceania", foundCountries.get(15).getContinent());
        assertEquals("Oceania", foundCountries.get(16).getContinent());
        assertEquals("Oceania", foundCountries.get(17).getContinent());
        assertEquals("Oceania", foundCountries.get(18).getContinent());
        assertEquals("Oceania", foundCountries.get(19).getContinent());
        assertEquals("Oceania", foundCountries.get(20).getContinent());
        assertEquals("Oceania", foundCountries.get(21).getContinent());
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
    void testSourceAPI_invalidCountry_exceptionThrown() {
        // test if exception is thrown when invalid country code is passed
        assertThrows(Exception.class, () -> {
            countryService.sourceAPI("invalidCountryCode");
        });
    }

    @Test
    @Transactional
    void testSourceAPIAndSetHintsAndSendHints_AdvancedMode_AD() throws IOException, InterruptedException {
        // Mock the WebSocketService
        webSocketService = Mockito.mock(WebSocketService.class);

        // create a advanced lobby
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testAdvancedLobby");
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
    void testSourceAPIAndSetHintsAndSendHints_NotEnoughHints_AdvancedMode_AD() throws IOException, InterruptedException {
        // Mock the WebSocketService
        webSocketService = Mockito.mock(WebSocketService.class);

        // create a advanced lobby
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testAdvancedLobby");
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

    @Test
    @Transactional
    void testSourceAPISetHintsAndSendChoices_basicMode_AD() throws IOException, InterruptedException {
        //given
        ArrayList<String> europe = new ArrayList<String>(
                Arrays.asList("Europe"));

        // Mock the WebSocketService
        webSocketService = Mockito.mock(WebSocketService.class);

        // create a advanced lobby
        basicLobby = new BasicLobby();
        basicLobby.setLobbyName("testBasicLobby");
        basicLobby.setLobbyId(1L);
        basicLobby.setIsPublic(true);
        basicLobby.setNumSeconds(30);
        basicLobby.setContinent(europe);
        ((BasicLobby) basicLobby).setNumOptions(4);

        countryService.sourceAPI("AD");

        Country foundCountry = countryRepository.findByCountryCode("AD");

        hintHandler = new HintHandler(foundCountry.getCountryCode(), basicLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(1, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", foundCountry.getFlag())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("GDP", foundCountry.getGdp())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Surface Area", foundCountry.getSurfaceArea())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Imports", foundCountry.getImports())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Exports", foundCountry.getExports())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Homicide Rate", foundCountry.getHomicideRate())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", foundCountry.getCurrency())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population Growth", foundCountry.getPopulationGrowth())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Forested Area", foundCountry.getForestedArea())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", foundCountry.getCapital())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", foundCountry.getPopulation())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population Density", foundCountry.getPopulationDensity())));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Internet Users", foundCountry.getInternetUsers())));

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 75 seconds to ensure all four hints are sent
        Thread.sleep(3000);

        // verify that sendToLobby was called 3 times with the expected parameters
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/choices-in-round"), any(ChoicesDTO.class));
    }


    @Test
    @Transactional
    void testSourceCountryInfo_fiveCountriesFromTheWorld() {
        ArrayList<String> allContinents = new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania"));

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(5, allContinents);

        assertEquals(5, sourcedCountries.size());
    }

    @Test
    @Transactional
    void testSourceCountryInfo_twoCountriesFromAsia() {
        ArrayList<String> allContinents = new ArrayList<String>(
                Arrays.asList("Asia"));

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(2, allContinents);

        Country firstCountry = countryRepository.findByCountryCode(sourcedCountries.get(0));
        Country secondCountry = countryRepository.findByCountryCode(sourcedCountries.get(1));

        assertEquals(2, sourcedCountries.size());
        assertEquals("Asia", firstCountry.getContinent());
        assertEquals("Asia", secondCountry.getContinent());
    }

    @Test
    @Transactional
    void testSourceCountryInfo_twoCountriesFromAsia_invalidContinents() {
        ArrayList<String> allContinents = new ArrayList<String>(
                Arrays.asList("Asia", "Antarctica", "Switzerland"));

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(2, allContinents);

        Country firstCountry = countryRepository.findByCountryCode(sourcedCountries.get(0));
        Country secondCountry = countryRepository.findByCountryCode(sourcedCountries.get(1));

        assertEquals(2, sourcedCountries.size());
        assertEquals("Asia", firstCountry.getContinent());
        assertEquals("Asia", secondCountry.getContinent());
    }

    @Test
    @Transactional
    void testSourceCountryInfo_twentyFiveCountriesFromOceania_exceptionThrown() {
        ArrayList<String> oceania = new ArrayList<String>(
                Arrays.asList("Oceania"));

        assertThrows(IllegalArgumentException.class, () -> {
            countryHandler.sourceCountryInfo(25, oceania);
        });
    }

    @Test
    @Transactional
    void testSourceCountryInfo_negativeNumberOfCountriesFromOceania_exceptionThrown() {
        ArrayList<String> allContinents = new ArrayList<String>(
                Arrays.asList("Oceania"));

        assertThrows(IllegalArgumentException.class, () -> {
            countryHandler.sourceCountryInfo(-1, allContinents);
        });
    }
}
