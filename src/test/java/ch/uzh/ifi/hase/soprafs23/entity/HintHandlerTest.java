package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.HintDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.time.Duration;
import java.time.Instant;
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

        hintHandler = new HintHandler("CH", 4, 1L, countryRepository, messagingTemplate, webSocketService);
    }

    @Test
    public void testSetHints() {
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
    public void testSendHintViaWebSocket() {
        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendHintViaWebSocket();

        // verify that the WebSocketService.sendToLobby() method was called with the first hint immediately
        verify(webSocketService).sendToLobby(eq(1L), eq("hints-in-round"), any(HintDTO.class));
    }

    @Test
    public void testSendHintViaWebSocketTiming() throws InterruptedException {
        // Call the setHints() method
        hintHandler.setHints();

        // call the sendHintViaWebSocket() method
        hintHandler.sendHintViaWebSocket();

        // wait for 20 seconds to ensure all four hints are sent
        Thread.sleep(20000);

        // verify that sendToLobby was called 3 times with the expected parameters
        verify(webSocketService, times(4)).sendToLobby(eq(1L), eq("hints-in-round"), any(HintDTO.class));
    }
}