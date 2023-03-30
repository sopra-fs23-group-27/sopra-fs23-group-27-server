package ch.uzh.ifi.hase.soprafs23.repository;


import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
//    Country findByCountryCode(String countryCode);
//    ArrayList<Country> getAllCountryCode(); // please check
}
