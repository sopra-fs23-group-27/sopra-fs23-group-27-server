package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.HintHandler;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class HintHandlerTest {

    @Mock
    CountryRepository countryRepository;

    @Mock
    SimpMessagingTemplate messagingTemplate;

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

        hintHandler = new HintHandler("CH", 4, 1, countryRepository, messagingTemplate);
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
    public void testSendHintViaWebSocket() throws Exception {
        // Call the setHints() method
        hintHandler.setHints();

        // Capture the arguments passed to the convertAndSend method
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> hintCaptor = ArgumentCaptor.forClass(String.class);

        // Call the method
        hintHandler.sendHintViaWebSocket();

        // Verify that the first hint was sent immediately
        verify(messagingTemplate).convertAndSend(urlCaptor.capture(), hintCaptor.capture());
        assertEquals("/topic/games/1/hints-in-round", urlCaptor.getValue());
        assertEquals("URL=https://flagcdn.com/h240/ch.png", hintCaptor.getValue());
    }
}