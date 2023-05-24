package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;

import ch.uzh.ifi.hase.soprafs23.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class CountryHandler {

    private final CountryRepository countryRepository;

    private final CountryService countryService;

    private ArrayList<String> allCountryCodes = new ArrayList<String>();
    private ArrayList<String> randomCountries = new ArrayList<String>();

    private final Logger log = LoggerFactory.getLogger(CountryHandler.class);

    public CountryHandler(CountryRepository countryRepository, CountryService countryService) {
        this.countryRepository = countryRepository;
        this.countryService = countryService;
    }

    public ArrayList<String> sourceCountryInfo(Integer numCountries, ArrayList<String> continents) {
        // load for all continents the country codes from the database into the variable "allCountryCodes"
        sourceAllCountryCodes(continents);

        // get n random countries from the database
        randomCountries = getRandomCountryCodes(numCountries, continents);

        // equivalent to for loop
        // for each country, load data from the API into the database
        randomCountries.forEach(countryCode -> {
            try {
                countryService.sourceAPI(countryCode);
            }
            catch (IOException e) {
                log.info("The country with the code " + countryCode + " could not be sourced due to an IO error: ", e);
            }
            catch (RuntimeException e) {
                log.info("The country with the code " + countryCode + " is not available. Runtime exception.", e);
            }
            catch (Exception e) {
                log.info("The country with the code " + countryCode + " could not be sourced due to other reasons. Exception: ", e);
            }
        });

        // finally, return the list with the countries that are saved into the database.
        return randomCountries;
    }

    private void sourceAllCountryCodes(ArrayList<String> continents) {
        // loads all Iso2 codes from the database directly into the variable
        // "allCountryCodes"
        countryService.getAllCountriesInContinents(continents).forEach(country -> {
            allCountryCodes.add(country.getCountryCode());
        });

        if (allCountryCodes.size() == 0) {
            throw new RuntimeException("No countries found in the database.");
        }
        log.info("All countries for " + continents.toString() + " are " + allCountryCodes.size());
    }

    private ArrayList<String> getRandomCountryCodes(int numCountries, ArrayList continents) {
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

        // remove potential duplicates without changing the order
        allCountryCodes = new ArrayList<String>(new LinkedHashSet<String>(allCountryCodes));

        // get first numCountries elements
        ArrayList<String> randomCountryCodes = new ArrayList<String>(allCountryCodes.subList(0, numCountries));

        log.info("Random countries for " + continents.toString() + " are " + randomCountryCodes.toString());

        return randomCountryCodes;
    }
}
