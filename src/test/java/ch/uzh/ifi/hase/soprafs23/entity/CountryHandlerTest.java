package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.entity.Country;
import ch.uzh.ifi.hase.soprafs23.entity.CountryHandler;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CountryHandlerTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryService countryService;

    private ArrayList allCountries;
    private ArrayList allContinents;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        allContinents = new ArrayList<String>(
                Arrays.asList("Africa", "Americas", "Asia", "Europe", "Oceania"));

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

        Country china = new Country();
        china.setCountryCode("CN");

        Country japan = new Country();
        japan.setCountryCode("JP");

        Country india = new Country();
        india.setCountryCode("IN");

        Country unitedStates = new Country();
        unitedStates.setCountryCode("US");

        Country canada = new Country();
        canada.setCountryCode("CA");

        Country mexico = new Country();
        mexico.setCountryCode("MX");

        allCountries = new ArrayList();
        allCountries.add(switzerland);
        allCountries.add(germany);
        allCountries.add(france);
        allCountries.add(italy);
        allCountries.add(spain);
        allCountries.add(austria);
        allCountries.add(netherlands);
        allCountries.add(china);
        allCountries.add(japan);
        allCountries.add(india);
        allCountries.add(unitedStates);
        allCountries.add(canada);
        allCountries.add(mexico);
    }

    @Test
    void testSourceCountryInfo_oneCountryOfWorld() {
        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(allCountries);

        // initiate country handler service
        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(1, allContinents);

        // check if source country is returned correctly
        assertEquals(1, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_fiveCountriesOfWorld() {
        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(allCountries);

        // initiate country handler service
        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(5, allContinents);

        // check if source country is returned correctly
        assertEquals(5, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_sevenCountriesOfWorld() {
        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(allCountries);

        // initiate country handler service
        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(7, allContinents);

        // check if source country is returned correctly
        assertEquals(7, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_tooManyCountries() {
        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(allCountries);

        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        // check if exception is thrown when no countries are found in the database
        assertThrows(IllegalArgumentException.class, () -> countryHandler.sourceCountryInfo(1000, allContinents));
    }

    @Test
    void testSourceCountryInfo_negativeNumberOfCountries() {
        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(allCountries);

        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        // check if exception is thrown when no countries are found in the database
        assertThrows(IllegalArgumentException.class, () -> countryHandler.sourceCountryInfo(-1, allContinents));
    }

    @Test
    void testSourceCountryInfo_noCountriesInDB() {
        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(null);

        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        // check if exception is thrown when no countries are found in the database
        assertThrows(RuntimeException.class, () -> countryHandler.sourceCountryInfo(5, allContinents));
    }

    @Test
    void testSourceCountryInfo_invalidCountryCodeInDB() {
        // given
        Country invalidCountry = new Country();
        invalidCountry.setCountryCode("INVALID");
        allCountries.add(invalidCountry);

        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(allContinents)).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(allContinents)).thenReturn(null);

        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        // check if exception is thrown when no countries are found in the database
        assertThrows(Exception.class, () -> countryHandler.sourceCountryInfo(14, allContinents));
    }

    @Test
    void testSourceCountryInfo_someInvalidContinents_invalidContinentsRemoved() {
        ArrayList<String> someInvalidContinents = new ArrayList<String>(
                Arrays.asList("Antarctica", "Americas", "Asia", "Europe", "Switzerland"));

        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(any())).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(any())).thenReturn(allCountries);

        // initiate country handler service
        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(13, someInvalidContinents);

        // check if source country is returned correctly
        assertEquals(13, sourcedCountries.size());
    }

    @Test
    void testSourceCountryInfo_onlyInvalidContinents_continentsOverwritten() {
        ArrayList<String> onlyInvalidContinents = new ArrayList<String>(
                Arrays.asList("Antarctica", "Switzerland"));

        // mock country repository and country service
        when(countryRepository.findCountryCodesByContinentIn(any())).thenReturn(allCountries);
        when(countryService.getAllCountriesInContinents(any())).thenReturn(allCountries);

        // initiate country handler service
        CountryHandler countryHandler = new CountryHandler(countryRepository, countryService);

        ArrayList<String> sourcedCountries = countryHandler.sourceCountryInfo(13, onlyInvalidContinents);

        // check if source country is returned correctly
        assertEquals(13, sourcedCountries.size());
    }



}