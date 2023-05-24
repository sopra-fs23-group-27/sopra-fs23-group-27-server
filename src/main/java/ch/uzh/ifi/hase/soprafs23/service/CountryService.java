package ch.uzh.ifi.hase.soprafs23.service;

import java.io.IOException;

// import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mapstruct.Qualifier;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.uzh.ifi.hase.soprafs23.entity.Country;
import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    private final Logger log = LoggerFactory.getLogger(CountryService.class);

    // make constuctor
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public ArrayList<Country> getAllCountriesInContinents(ArrayList<String> continents) {

        ArrayList<Country> allCountriesInContinents = countryRepository.findCountryCodesByContinentIn(continents);

        return allCountriesInContinents;
    }

    public void sourceAPI(String countryCode) throws IOException {

        InputStream responseStream = null;

        try {

            // write get request to api
            // the request also works with the iso2 code
            URL url = new URL("https://api.api-ninjas.com/v1/country?name=" + countryCode);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("X-Api-Key", "mB7Cx89lu9yKW1n90PfCrcyj9a7Mq6gBlpcABrIG");
            responseStream = connection.getInputStream();

        }
        catch (Exception e) {
            log.info("Exception thrown: ", e);
        }

        // Notice that the responseStream is initiated to null
        // If the responseStream is null, then the API did not return a response
        // If the responseStream is not null, then the API did return a response
        if (responseStream == null) {
            throw new RuntimeException("No response from API");
        }
        else {

            // read response from api
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseStream);

            // insert the returned values into the country object i.e the database
            Country country = countryRepository.findByCountryCode(countryCode);
            country.setGdp(parseString(root.get(0).get("gdp")) + "M USD");
            country.setSurfaceArea(parseString(root.get(0).get("surface_area")) + " sq. km");
            country.setLifeExpectancyMale(parseString(root.get(0).get("life_expectancy_male")) + " years");
            country.setLifeExpectancyFemale(parseString(root.get(0).get("life_expectancy_female")) + " years");
            country.setUnemploymentRate(parseString(root.get(0).get("unemployment")) + "%");
            country.setImports(parseString(root.get(0).get("imports")) + "M USD");
            country.setExports(parseString(root.get(0).get("exports")) + "M USD");
            country.setHomicideRate(parseString(root.get(0).get("homicide_rate")) + " per 100'000 people");
            country.setCurrency(parseString(root.get(0).get("currency").get("name")));
            country.setPopulationGrowth(parseString(root.get(0).get("pop_growth")) + "%");
            country.setSecondarySchoolEnrollmentFemale(parseString(root.get(0).get("secondary_school_enrollment_female")) + "% of people of secondary school age");
            country.setSecondarySchoolEnrollmentMale(parseString(root.get(0).get("secondary_school_enrollment_male")) + "% of people of secondary school age");
            country.setCapital(parseString(root.get(0).get("capital")));
            country.setCo2Emissions(parseString(root.get(0).get("co2_emissions")) + " kilotons");
            country.setForestedArea(parseString(root.get(0).get("forested_area")) + "% of surface area");
            country.setInfantMortality(parseString(root.get(0).get("infant_mortality")) + " per 1000 live births");
            country.setPopulation(parseString(root.get(0).get("population")) + "K");
            country.setPopulationDensity(parseString(root.get(0).get("pop_density")) + " people per sq. km of land area");
            country.setInternetUsers(parseString(root.get(0).get("internet_users")) + "% of population");

            // override country in database
            countryRepository.saveAndFlush(country);
        }

    }

    private String parseString(JsonNode node) {
        // helperfunction solves two purposes:
        // 1. if the value is not available, it returns "not available"
        // 2. if the value is null, because the field is not being sent from the api, it
        // returns "not available"
        try {
            return node.toString().replace("\"", "");
        }
        catch (NumberFormatException e) {
            log.info("At least one of the values is not available");
            return "not available";
        }
        catch (NullPointerException e) {
            log.info("At least one of the values is not available");
            return "not available";
        }
        catch (Exception e) {
            log.info("At least one of the values is not available");
            return "not available";
        }
    }


}