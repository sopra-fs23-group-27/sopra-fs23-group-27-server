package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.CountryContinentMap;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Random;

@Service
public class CountryHandlerService {

    private final CountryRepository countryRepository;

    private final CountryService countryService;

    private ArrayList<String> allCountryCodes = new ArrayList<String>();
    private ArrayList<String> countryCodesByContinent = new ArrayList<String>();
    private ArrayList<String> allContinents = new ArrayList<String>();

    private final Logger log = LoggerFactory.getLogger(CountryHandlerService.class);

    public CountryHandlerService(CountryRepository countryRepository, CountryService countryService) {
        this.countryRepository = countryRepository;
        this.countryService = countryService;
        this.allContinents.add("Asia");
        this.allContinents.add("Europe");
        this.allContinents.add("Africa");
        this.allContinents.add("Oceania");
        this.allContinents.add("Americas");

        sourceAllCountryCodes();

        /*
         * try {
         * // use this function in the Controller;
         * ArrayList<String> sourced = sourceCountryInfo(5);
         * log.info(sourced.toString());
         * } catch (Exception e) {
         * e.printStackTrace();
         * }
         */

    }

    public ArrayList<String> sourceCountryInfo(Integer numCountries, String continent) {

        // get n random countries from the database
        ArrayList<String> randomCountries = getRandomCountryCodes(numCountries);

        // if a continent is specified, create a new list with only the countries from that continent
        if (continent != null) {
            if (allContinents.contains(continent)) {
                randomCountries = getRandomCountryCodesByContinent(numCountries, continent);
            }
        }

        // equivalent to for loop
        // for each country, load data from the API into the database
        randomCountries.forEach(countryCode -> {
            try {
                countryService.sourceAPI(countryCode);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (RuntimeException e) {
                log.info("The country with the code " + countryCode + " is not available.");
                e.printStackTrace();
            }
            catch (Exception e) {
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

        log.info("Random countries (worldwide) are: " + randomCountryCodes.toString());

        return randomCountryCodes;
    }

    private ArrayList<String> getRandomCountryCodesByContinent(int numCountries, String continent) {

        // Init countryContinentMap
        CountryContinentMap map = new CountryContinentMap();

        // get all country codes from the specified continent
        countryCodesByContinent = map.getCountryCodesByContinent(continent);

        if (numCountries > countryCodesByContinent.size()) {
            throw new IllegalArgumentException(
                    "numCountries must be smaller than the number of countries of the continent in the database, i.e. "
                            + countryCodesByContinent.size() + ".");
        }

        if (numCountries <= 0) {
            throw new IllegalArgumentException("numCountries in continent must be larger than 0.");
        }

        // Shuffle the list
        Collections.shuffle(countryCodesByContinent);

        // get first numCountries elements
        ArrayList<String> randomCountryCodesByContinent = new ArrayList<String>(countryCodesByContinent.subList(0, numCountries));

        // log the list and continent
        log.info("Random countries for continent " + continent + " are: " + randomCountryCodesByContinent.toString());

        return randomCountryCodesByContinent;
    }

    // public ArrayList<String> updateCountry(String countryCode){
    // findallcountries / get all isocodes
    // shuffle
    // select n first countryCodes
    // parse API
    // SaveAndFlush
    // }

}
