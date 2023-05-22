package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Country;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CountryRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    @BeforeEach
    public void setup() {
        countryRepository.deleteAll();

        //given
        Country switzerland = new Country();
        switzerland.setCountryCode("CH");
        switzerland.setName("Switzerland");
        switzerland.setFlag("https://flagcdn.com/h240/ch.png");
        switzerland.setContinent("Europe");

        entityManager.persist(switzerland);
        entityManager.flush();

        Country germany = new Country();
        germany.setCountryCode("DE");
        germany.setName("Germany");
        germany.setFlag("https://flagcdn.com/h240/de.png");
        germany.setContinent("Europe");

        entityManager.persist(germany);
        entityManager.flush();

        Country india = new Country();
        india.setCountryCode("IN");
        india.setName("India");
        india.setFlag("https://flagcdn.com/h240/in.png");
        india.setContinent("Asia");

        entityManager.persist(india);
        entityManager.flush();

        Country china = new Country();
        china.setCountryCode("CN");
        china.setName("China");
        china.setFlag("https://flagcdn.com/h240/cn.png");
        china.setContinent("Asia");

        entityManager.persist(china);
        entityManager.flush();

        Country usa = new Country();
        usa.setCountryCode("US");
        usa.setName("United States");
        usa.setFlag("https://flagcdn.com/h240/us.png");
        usa.setContinent("Americas");

        entityManager.persist(usa);
        entityManager.flush();

        Country brazil = new Country();
        brazil.setCountryCode("BR");
        brazil.setName("Brazil");
        brazil.setFlag("https://flagcdn.com/h240/br.png");
        brazil.setContinent("Americas");

        entityManager.persist(brazil);
        entityManager.flush();

        Country australia = new Country();
        australia.setCountryCode("AU");
        australia.setName("Australia");
        australia.setFlag("https://flagcdn.com/h240/au.png");
        australia.setContinent("Oceania");

        entityManager.persist(australia);
        entityManager.flush();

        Country newZealand = new Country();
        newZealand.setCountryCode("NZ");
        newZealand.setName("New Zealand");
        newZealand.setFlag("https://flagcdn.com/h240/nz.png");
        newZealand.setContinent("Oceania");

        entityManager.persist(newZealand);
        entityManager.flush();

        Country southAfrica = new Country();
        southAfrica.setCountryCode("ZA");
        southAfrica.setName("South Africa");
        southAfrica.setFlag("https://flagcdn.com/h240/za.png");
        southAfrica.setContinent("Africa");

        entityManager.persist(southAfrica);
        entityManager.flush();

        Country egypt = new Country();
        egypt.setCountryCode("EG");
        egypt.setName("Egypt");
        egypt.setFlag("https://flagcdn.com/h240/eg.png");
        egypt.setContinent("Africa");

        entityManager.persist(egypt);
        entityManager.flush();
    }

    @AfterEach
    public void tearDown() {
        countryRepository.deleteAll();
    }

    @Test
    void findByCountryCode_success() {
        // when
        Country found = countryRepository.findByCountryCode("CH");

        // then
        assertTrue(found.getName().equals("Switzerland"));
        assertTrue(found.getContinent().equals("Europe"));
    }

    @Test
    void findByName_success() {
        // when
        Country found = countryRepository.findByName("Switzerland");

        // then
        assertTrue(found.getCountryCode().equals("CH"));
        assertTrue(found.getContinent().equals("Europe"));
    }

    @Test
    void findCountryCodesByContinentIn_oneContinent_success() {
        // given
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Europe"));

        // when
        ArrayList<Country> found = countryRepository.findCountryCodesByContinentIn(continents);

        // then
        assertEquals(2, found.size());
        assertEquals("CH", found.get(0).getCountryCode());
        assertEquals("DE", found.get(1).getCountryCode());
    }

    @Test
    void findCountryCodesByContinentIn_multipleContinents_success() {
        // given
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Europe", "Asia", "Americas"));

        // when
        ArrayList<Country> found = countryRepository.findCountryCodesByContinentIn(continents);

        // then
        assertEquals(6, found.size());
        assertEquals("CH", found.get(0).getCountryCode());
        assertEquals("DE", found.get(1).getCountryCode());
        assertEquals("IN", found.get(2).getCountryCode());
        assertEquals("CN", found.get(3).getCountryCode());
        assertEquals("US", found.get(4).getCountryCode());
        assertEquals("BR", found.get(5).getCountryCode());
    }

    @Test
    void findCountryCodesByContinentIn_allContinents_success() {
        // given
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Europe", "Asia", "Americas", "Oceania", "Africa"));

        // when
        ArrayList<Country> found = countryRepository.findCountryCodesByContinentIn(continents);

        // then
        assertEquals(10, found.size());
        assertEquals("CH", found.get(0).getCountryCode());
        assertEquals("DE", found.get(1).getCountryCode());
        assertEquals("IN", found.get(2).getCountryCode());
        assertEquals("CN", found.get(3).getCountryCode());
        assertEquals("US", found.get(4).getCountryCode());
        assertEquals("BR", found.get(5).getCountryCode());
        assertEquals("AU", found.get(6).getCountryCode());
        assertEquals("NZ", found.get(7).getCountryCode());
        assertEquals("ZA", found.get(8).getCountryCode());
        assertEquals("EG", found.get(9).getCountryCode());
    }

    @Test
    void getAllCountryNamesInContinents_oneContinent_success() {
        // given
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Europe"));

        // when
        ArrayList<String> found = countryRepository.getAllCountryNamesInContinents(continents);

        // then
        assertEquals(2, found.size());
        assertEquals("Switzerland", found.get(0));
        assertEquals("Germany", found.get(1));
    }

    @Test
    void getAllCountryNamesInContinents_multipleContinents_success() {
        // given
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Europe", "Asia", "Americas"));

        // when
        ArrayList<String> found = countryRepository.getAllCountryNamesInContinents(continents);

        // then
        assertEquals(6, found.size());
        assertEquals("Switzerland", found.get(0));
        assertEquals("Germany", found.get(1));
        assertEquals("India", found.get(2));
        assertEquals("China", found.get(3));
        assertEquals("United States", found.get(4));
        assertEquals("Brazil", found.get(5));
    }

    @Test
    void getAllCountryNamesInContinents_allContinents_success() {
        // given
        ArrayList<String> continents = new ArrayList<String>(
                Arrays.asList("Europe", "Asia", "Americas", "Oceania", "Africa"));

        // when
        ArrayList<String> found = countryRepository.getAllCountryNamesInContinents(continents);

        // then
        assertEquals(10, found.size());
        assertEquals("Switzerland", found.get(0));
        assertEquals("Germany", found.get(1));
        assertEquals("India", found.get(2));
        assertEquals("China", found.get(3));
        assertEquals("United States", found.get(4));
        assertEquals("Brazil", found.get(5));
        assertEquals("Australia", found.get(6));
        assertEquals("New Zealand", found.get(7));
        assertEquals("South Africa", found.get(8));
        assertEquals("Egypt", found.get(9));
    }

}
