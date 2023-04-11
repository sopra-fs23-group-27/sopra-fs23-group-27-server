package ch.uzh.ifi.hase.soprafs23.entity;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;

import ch.uzh.ifi.hase.soprafs23.entity.Country;

public class Game {

    // This class is the Game object as in the UML diagram
    // This object is not connected to the database
    // here, the game logic is implemented

    // add Logger
    private final Logger log = LoggerFactory.getLogger(CountryHandlerService.class);

    private final CountryHandlerService countryHandlerService;
    private final CountryRepository countryRepository;

    private ArrayList<String> allCountryCodes;
    private Country currentCountry;
    private String correctGuess;
    private Integer round;

    public Game(
            CountryHandlerService countryHandlerService,
            CountryRepository countryRepository) {
        this.countryHandlerService = countryHandlerService;
        this.countryRepository = countryRepository;
        this.allCountryCodes = this.countryHandlerService.sourceCountryInfo(5);

        // set the round to 0, this is to get the first of the sourced countries
        // after each round, this Integer is incremented by 1
        this.round = 0;

        // update the current country with the first country in the randomly loaded
        // countries
        updateCorrectGuess(this.allCountryCodes.get(this.round));
        log.info(allCountryCodes.toString());
        log.info(this.correctGuess);
        log.info(this.round.toString());

    }

    public void updateCorrectGuess(String countryCode) {
        // this function is called after each round
        // it updates the correct guess for the next round
        // INPUT: countryCode (String) e.g. "CH"
        // OUTPUT: void

        // get the country from the database and set it to object variable
        // e.g. transform "CH" to "Switzerland"
        Country country = countryRepository.findByCountryCode(countryCode);
        this.currentCountry = country;

        // get the name corresponding to the country code
        this.correctGuess = country.getName();

        // remove all whitespaces and make it lowercase
        this.correctGuess = this.correctGuess.toLowerCase();
        this.correctGuess = this.correctGuess.replaceAll("\\s+", "");

        // prepare the counter for the next round
        this.round++;
    }

    public Boolean validateGuess(String guess) {
        // prepare the guess
        // remove all whitespaces and make it lowercase
        guess = guess.toLowerCase();
        guess = guess.replaceAll("\\s+", "");

        if (guess.equals(this.correctGuess)) {
            // TODO: Mark the players entry in the scoreboard as correct
            return true;
        } else {
            return false;
        }
    }

}
