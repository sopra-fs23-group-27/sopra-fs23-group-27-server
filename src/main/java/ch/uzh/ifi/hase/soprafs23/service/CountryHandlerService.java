package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CountryHandlerService {
    private final CountryRepository countryRepository;

    public CountryHandlerService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    //public ArrayList<String> updateCountry(String countryCode){
        // TODO:
        // findallcountries / get all isocodes
        // shuffle
        // select n first countryCodes
        // parse API
        // SaveAndFlush
    //}


}
