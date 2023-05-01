package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.rest.dto.AdvancedLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.BasicLobbyCreateDTO;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.HintDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.FlagDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.util.AbstractMap;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class HintHandlerTest {

    @Mock
    CountryRepository countryRepository;

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @Mock
    WebSocketService webSocketService;

    HintHandler hintHandler;
    Country testCountry;
    private Lobby basicLobby;
    private Lobby advancedLobby;

    @InjectMocks
    CountryHandlerService countryHandlerService;


    @BeforeEach
    public void setUp() {
        testCountry = new Country();
        testCountry.setCountryCode("CH");
        testCountry.setName("Switzerland");
        testCountry.setPopulation(8000000);
        testCountry.setCapital("Bern");
        testCountry.setCurrency("CHF");
        testCountry.setFlag("https://flagcdn.com/h240/ch.png");
        testCountry.setGdp(-9999);
        testCountry.setSurfaceArea(-9999);
        testCountry.setLifeExpectancyMale(-9999);
        testCountry.setLifeExpectancyFemale(-9999);
        testCountry.setUnemploymentRate(-9999);
        testCountry.setImports(-9999);
        testCountry.setExports(-9999);
        testCountry.setHomicideRate(-9999);
        testCountry.setPopulationGrowth(-9999);
        testCountry.setSecondarySchoolEnrollmentFemale(-9999);
        testCountry.setSecondarySchoolEnrollmentMale(-9999);
        testCountry.setCo2Emissions(-9999);
        testCountry.setForestedArea(-9999);
        testCountry.setInfantMortality(-9999);
        testCountry.setPopulationDensity(-9999);
        testCountry.setInternetUsers(-9999);


        // Mock the CountryRepository
        countryRepository = Mockito.mock(CountryRepository.class);
        when(countryRepository.findByCountryCode(Mockito.anyString())).thenReturn(testCountry);

        // Mock the SimpMessagingTemplate
        messagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

        // Mock the WebSocketService
        webSocketService = Mockito.mock(WebSocketService.class);

    }

    @Test
    public void testSetHints_BasicMode() {
        basicLobby = new BasicLobby();
        basicLobby.setLobbyName("testBasicLobby");
        basicLobby.setIsPublic(true);
        basicLobby.setNumSeconds(10);
        ((BasicLobby) basicLobby).setNumOptions(4);

        hintHandler = new HintHandler("CH", basicLobby,  countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(1, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", "https://flagcdn.com/h240/ch.png")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", "Bern")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", "8000000")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", "CHF")));
    }

    @Test
    public void testSetHints_AdvancedMode() {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testBasicLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(20);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(5);
        ((AdvancedLobby) advancedLobby).setHintInterval(5);

        hintHandler = new HintHandler("CH", advancedLobby,  countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(4, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", "https://flagcdn.com/h240/ch.png")));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", "Bern")));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", "8000000")));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", "CHF")));
    }

    @Test
    public void testSendRequiredDetailsViaWebSocket() {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(1L);
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(20);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(5);
        ((AdvancedLobby) advancedLobby).setHintInterval(5);

        hintHandler = new HintHandler("CH", advancedLobby,  countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // verify that the WebSocketService.sendToLobby() method was called immediately amd sent flag
        verify(webSocketService).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
    }

    @Test
    public void testSendRequiredDetailsViaWebSocketTiming_BasicMode() throws InterruptedException {
        basicLobby = new BasicLobby();
        basicLobby.setLobbyId(1L);
        basicLobby.setLobbyName("testBasicLobby");
        basicLobby.setIsPublic(true);
        basicLobby.setNumSeconds(10);
        ((BasicLobby) basicLobby).setNumOptions(4);

        hintHandler = new HintHandler("CH", basicLobby,  countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 20 seconds to ensure all four hints are sent
        Thread.sleep(10000);

        // verify that no hints were sent
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(0)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));

    }

    @Test
    public void testSendRequiredDetailsViaWebSocketTiming_AdvancedMode() throws InterruptedException {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(1L);
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(20);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(5);
        ((AdvancedLobby) advancedLobby).setHintInterval(5);

        hintHandler = new HintHandler("CH", advancedLobby,  countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 20 seconds to ensure all four hints are sent
        Thread.sleep(20000);

        // verify that sendToLobby was called 3 times with the expected parameters
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(3)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
    }
}