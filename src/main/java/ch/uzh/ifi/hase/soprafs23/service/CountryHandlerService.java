package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class CountryHandlerService {

    private final CountryRepository countryRepository;

    private final CountryService countryService;

    private ArrayList<String> allCountryCodes = new ArrayList<String>();

    private final Logger log = LoggerFactory.getLogger(CountryHandlerService.class);

    public CountryHandlerService(CountryRepository countryRepository, CountryService countryService) {
        this.countryRepository = countryRepository;
        this.countryService = countryService;

        sourceAllCountryCodes();

        try {
            // use this function in the Controller
            ArrayList<String> sourced = sourceCountryInfo(5);
            log.info(sourced.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ArrayList<String> sourceCountryInfo(Integer numCountries) {

        // get n random countries from the database
        ArrayList<String> randomCountries = getRandomCountryCodes(numCountries);

        // equivalent to for loop
        // for each country, load data from the API into the database
        randomCountries.forEach(countryCode -> {
            try {
                countryService.sourceAPI(countryCode);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                log.info("The country with the code " + countryCode + " is not available.");
                e.printStackTrace();
            } catch (Exception e) {
                log.info("The country with the code " + countryCode + " could not be sourced due to other reasons.");
                e.printStackTrace();
            }
        });

        // finally, return the list with the countries that are saved into the database.
        return randomCountries;
    }

    private void sourceAllCountryCodes() {
        // loads all Iso2 codes from the database directly into the variable
        // "allCountryCodes"

        countryRepository.findAll().forEach(country -> {
            allCountryCodes.add(country.getCountryCode());
        });

        if (allCountryCodes.size() == 0) {
            throw new RuntimeException("No countries found in the database.");
        }
    }

    private ArrayList<String> getRandomCountryCodes(int numCountries) {

        if (numCountries > allCountryCodes.size()) {
            throw new IllegalArgumentException(
                    "numCountries must be smaller than the number of countries in the database, i.e. "
                            + allCountryCodes.size() + ".");
        }

        if (numCountries <= 0) {
            throw new IllegalArgumentException("numCountries must be larger than 0.");
        }

        // Shuffle the list
        Collections.shuffle(allCountryCodes);

        // get first numCountries elements
        ArrayList<String> randomCountryCodes = new ArrayList<String>(allCountryCodes.subList(0, numCountries));

        log.info(randomCountryCodes.toString());

        return randomCountryCodes;
    }

    // public ArrayList<String> updateCountry(String countryCode){
    // findallcountries / get all isocodes
    // shuffle
    // select n first countryCodes
    // parse API
    // SaveAndFlush
    // }

}
