package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    public Country findByCountryCode(String countryCode);

    public Country findByName(String name);

    public ArrayList<Country> findCountryCodesByContinentIn(ArrayList<String> continents);

    @Query("SELECT c.name FROM Country c WHERE c.continent IN :continents")
    public ArrayList<String> getAllCountryNamesInContinents(@Param("continents") ArrayList<String> continents);
}
