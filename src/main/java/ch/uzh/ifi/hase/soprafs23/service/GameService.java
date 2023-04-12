package ch.uzh.ifi.hase.soprafs23.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.uzh.ifi.hase.soprafs23.entity.Game;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;

@Service
public class GameService {

    public GameService(
            CountryHandlerService countryHandlerService,
            CountryRepository countryRepository) {

        // To use methods from the Game repository, use it as follows:
        // GameRepository.addGame("1", new Game(countryHandlerService,
        // countryRepository));
    }
}
