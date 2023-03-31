package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class CountryHandlerService {

    private final CountryRepository countryRepository;

    private ArrayList<String> allCountryCodes;

    private final Logger log = LoggerFactory.getLogger(CountryHandlerService.class);

    public CountryHandlerService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;

        this.allCountryCodes = new ArrayList<String>();

        sourceAllCountryCodes();

        log.info(getRandomCountryCodes(10).toString());
    }

    /*
     * private void sourceCountryInfo() {
     * getRandomCountryCodes(10).forEach(countryCode -> {
     * countryService.sourceAPI(countryCode);
     * }
     * }
     */
    // Shift + Alt + A

    private void sourceAllCountryCodes() {

        countryRepository.findAll().forEach(country -> {
            allCountryCodes.add(country.getCountryCode());
        });
    }

    private ArrayList<String> getRandomCountryCodes(int numCountries) {

        if (numCountries > allCountryCodes.size()) {
            throw new IllegalArgumentException(
                    "numCountries must be smaller than the number of countries in the database.");
        }

        if (numCountries <= 0) {
            throw new IllegalArgumentException("numCountries must be larger than 0.");
        }

        // Shuffle the list
        Collections.shuffle(allCountryCodes);

        // get first numCountries elements
        ArrayList<String> randomCountryCodes = new ArrayList<String>(allCountryCodes.subList(0, numCountries));

        return randomCountryCodes;
    }

    // public ArrayList<String> updateCountry(String countryCode){
    // TODO:
    // findallcountries / get all isocodes
    // shuffle
    // select n first countryCodes
    // parse API
    // SaveAndFlush
    // }

}
