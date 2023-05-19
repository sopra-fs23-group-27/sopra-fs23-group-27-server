package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.Country;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CountryHandlerServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryService countryService;

    private ArrayList allCountries;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Country switzerland = new Country();
        switzerland.setCountryCode("CH");

        Country germany = new Country();
        germany.setCountryCode("DE");

        Country france = new Country();
        france.setCountryCode("FR");

        Country italy = new Country();
        italy.setCountryCode("IT");

        Country spain = new Country();
        spain.setCountryCode("ES");

        Country austria = new Country();
        austria.setCountryCode("AT");

        Country netherlands = new Country();
        netherlands.setCountryCode("NL");

        allCountries = new ArrayList();
        allCountries.add(switzerland);
        allCountries.add(germany);
        allCountries.add(france);
        allCountries.add(italy);
        allCountries.add(spain);
        allCountries.add(austria);
        allCountries.add(netherlands);
    }

    @Test
    void testInitiateCountryHandlerServiceClass_noCountriesInDBFound() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(null);

        // check if exception is thrown when no countries are found in the database
        assertThrows(RuntimeException.class, () -> new CountryHandlerService(countryRepository, countryService));
    }

    @Test
    void testInitiateCountryHandlerServiceClass() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(allCountries);

        // check if no exception is thrown when countries are found in the database
        assertDoesNotThrow(() -> new CountryHandlerService(countryRepository, countryService));
    }

    @Test
    void testSourceCountryInfo_1country() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(allCountries);

        // initiate country handler service
        CountryHandlerService countryHandlerService = new CountryHandlerService(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandlerService.sourceCountryInfo(1, "World");

        // check if source country is returned correctly
        assertEquals(1, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_5countries() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(allCountries);

        // initiate country handler service
        CountryHandlerService countryHandlerService = new CountryHandlerService(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandlerService.sourceCountryInfo(5, "World");

        // check if source country is returned correctly
        assertEquals(5, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_7countries() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(allCountries);

        // initiate country handler service
        CountryHandlerService countryHandlerService = new CountryHandlerService(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandlerService.sourceCountryInfo(7, "World");

        // check if source country is returned correctly
        assertEquals(7, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_tooManyCountries() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(allCountries);

        CountryHandlerService countryHandlerService = new CountryHandlerService(countryRepository, countryService);

        // check if exception is thrown when no countries are found in the database
        assertThrows(Exception.class, () -> countryHandlerService.sourceCountryInfo(1000, "World"));
    }

    @Test
    void testSourceCountryInfo_negativeNumberOfCountries() {
        // mock country repository
        when(countryRepository.findAll()).thenReturn(allCountries);

        CountryHandlerService countryHandlerService = new CountryHandlerService(countryRepository, countryService);

        // check if exception is thrown when no countries are found in the database
        assertThrows(IllegalArgumentException.class, () -> countryHandlerService.sourceCountryInfo(-1, "World"));
    }
}