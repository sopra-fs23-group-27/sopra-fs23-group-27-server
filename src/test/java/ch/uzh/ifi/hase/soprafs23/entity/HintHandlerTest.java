package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.ChoicesDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.HintDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.FlagDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.util.AbstractMap;
import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class HintHandlerTest {

    @Mock
    CountryRepository countryRepository;

    @Mock
    SimpMessagingTemplate messagingTemplate;

    @Mock
    WebSocketService webSocketService;

    private HintHandler hintHandler;
    private Country testCountry;
    private ArrayList<String> testCountryNamesList;
    private Lobby basicLobby;
    private Lobby advancedLobby;

    @InjectMocks
    CountryHandler countryHandler;


    @BeforeEach
    public void setUp() {
        testCountry = new Country();
        testCountry.setCountryCode("CH");
        testCountry.setName("Switzerland");
        testCountry.setPopulation("8655" + "K");
        testCountry.setCapital("Bern");
        testCountry.setCurrency("Swiss Franc");
        testCountry.setFlag("https://flagcdn.com/h240/ch.png");
        testCountry.setContinent("Europe");
        testCountry.setGdp("-9999");
        testCountry.setSurfaceArea("-9999");
        testCountry.setLifeExpectancyMale("-9999");
        testCountry.setLifeExpectancyFemale("-9999");
        testCountry.setUnemploymentRate("-9999");
        testCountry.setImports("-9999");
        testCountry.setExports("-9999");
        testCountry.setHomicideRate("-9999");
        testCountry.setPopulationGrowth("-9999");
        testCountry.setSecondarySchoolEnrollmentFemale("-9999");
        testCountry.setSecondarySchoolEnrollmentMale("-9999");
        testCountry.setCo2Emissions("-9999");
        testCountry.setForestedArea("-9999");
        testCountry.setInfantMortality("not available");
        testCountry.setPopulationDensity("not available");
        testCountry.setInternetUsers("not available");

        testCountryNamesList = new ArrayList<>();
        testCountryNamesList.add("Switzerland");
        testCountryNamesList.add("Germany");
        testCountryNamesList.add("France");
        testCountryNamesList.add("Italy");
        testCountryNamesList.add("Spain");
        testCountryNamesList.add("Austria");
        testCountryNamesList.add("United Kingdom");
        testCountryNamesList.add("United States");

        // Mock the CountryRepository
        countryRepository = Mockito.mock(CountryRepository.class);
        when(countryRepository.findByCountryCode(Mockito.anyString())).thenReturn(testCountry);
        when(countryRepository.getAllCountryNamesInContinents(any())).thenReturn(testCountryNamesList);

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

        hintHandler = new HintHandler("CH", basicLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(1, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", "https://flagcdn.com/h240/ch.png")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", "Bern")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", "8655K")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", "Swiss Franc")));
    }

    @Test
    public void testSetHints_AdvancedMode() {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testBasicLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(4);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(1);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);

        hintHandler = new HintHandler("CH", advancedLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(4, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", "https://flagcdn.com/h240/ch.png")));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", "Bern")));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", "8655K")));
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", "Swiss Franc")));
    }

    @Test
    public void testSetHints_AdvancedMode_NumSecondsUntilHintLargerThanNumSeconds() {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testBasicLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(1);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(2);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);

        hintHandler = new HintHandler("CH", advancedLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(1, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", "https://flagcdn.com/h240/ch.png")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Capital", "Bern")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Population", "8655K")));
        assertFalse(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("Currency", "Swiss Franc")));
    }

    @Test
    public void testSetHints_AdvancedMode_HintIntervalLargerThanNumSeconds() {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyName("testBasicLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(5);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(2);
        ((AdvancedLobby) advancedLobby).setHintInterval(5);

        hintHandler = new HintHandler("CH", advancedLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // Assert that the hints list is not nulll and has the correct size
        // and contains the correct attributes
        assertNotNull(hintHandler.getHints());
        assertEquals(2, hintHandler.getHints().size());

        // test content of list
        assertTrue(hintHandler.getHints().contains(new AbstractMap.SimpleEntry<>("URL", "https://flagcdn.com/h240/ch.png")));
    }

    @Test
    public void testSendRequiredDetailsViaWebSocket_AdvancedMode() {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(1L);
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(4);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(1);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);

        hintHandler = new HintHandler("CH", advancedLobby, countryRepository, webSocketService);

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
        basicLobby.setNumSeconds(2);
        ((BasicLobby) basicLobby).setNumOptions(4);

        hintHandler = new HintHandler("CH", basicLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 20 seconds to ensure all four hints are sent
        Thread.sleep(2000);

        // verify that the WebSocketService.sendToLobby() method was called immediately and sent flag and n options
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/choices-in-round"), any(ChoicesDTO.class));
        // verify that no hints were sent
        verify(webSocketService, times(0)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
    }

    @Test
    public void testSendRequiredDetailsViaWebSocketTiming_AdvancedMode() throws InterruptedException {
        advancedLobby = new AdvancedLobby();
        advancedLobby.setLobbyId(1L);
        advancedLobby.setLobbyName("testAdvancedLobby");
        advancedLobby.setIsPublic(true);
        advancedLobby.setNumSeconds(4);
        ((AdvancedLobby) advancedLobby).setNumSecondsUntilHint(1);
        ((AdvancedLobby) advancedLobby).setHintInterval(1);

        hintHandler = new HintHandler("CH", advancedLobby, countryRepository, webSocketService);

        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendRequiredDetailsViaWebSocket();

        // wait for 20 seconds to ensure all four hints are sent
        Thread.sleep(4000);

        // verify that sendToLobby was called 3 times with the expected parameters
        verify(webSocketService, times(1)).sendToLobby(eq(1L), eq("/flag-in-round"), any(FlagDTO.class));
        verify(webSocketService, times(3)).sendToLobby(eq(1L), eq("/hints-in-round"), any(HintDTO.class));
        // verify that no option were sent
        verify(webSocketService, times(0)).sendToLobby(eq(1L), eq("/choices-in-round"), any(ChoicesDTO.class));
    }
}