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

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Notice that the responseStream is initiated to null
        // If the responseStream is null, then the API did not return a response
        // If the responseStream is not null, then the API did return a response
        if (responseStream == null) {
            throw new RuntimeException("No response from API");
        } else {

            // read response from api
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseStream);

            // insert the returned values into the country object i.e the database
            Country country = countryRepository.findByCountryCode(countryCode);
            country.setGdp(parseInt(root.get(0).get("gdp")));
            country.setSurfaceArea(parseInt(root.get(0).get("surface_area")));
            country.setLifeExpectancyMale(parseInt(root.get(0).get("life_expectancy_male")));
            country.setLifeExpectancyFemale(parseInt(root.get(0).get("life_expectancy_female")));
            country.setUnemploymentRate(parseInt(root.get(0).get("unemployment")));
            country.setImports(parseInt(root.get(0).get("imports")));
            country.setExports(parseInt(root.get(0).get("exports")));
            country.setHomicideRate(parseInt(root.get(0).get("homicide_rate")));
            country.setCurrency(parseString(root.get(0).get("currency").get("name")));
            country.setPopulationGrowth(parseInt(root.get(0).get("pop_growth")));
            country.setSecondarySchoolEnrollmentFemale(parseInt(root.get(0).get("secondary_school_enrollment_female")));
            country.setSecondarySchoolEnrollmentMale(parseInt(root.get(0).get("secondary_school_enrollment_male")));
            country.setCapital(parseString(root.get(0).get("capital")));
            country.setCo2Emissions(parseInt(root.get(0).get("co2_emissions")));
            country.setForestedArea(parseInt(root.get(0).get("forested_area")));
            country.setInfantMortality(parseInt(root.get(0).get("infant_mortality")));
            country.setPopulation(parseInt(root.get(0).get("population")));
            country.setPopulationDensity(parseInt(root.get(0).get("pop_density")));
            country.setInternetUsers(parseInt(root.get(0).get("internet_users")));

            // override country in database
            countryRepository.saveAndFlush(country);
        }

    }

    private int parseInt(JsonNode node) {
        // helperfunction solves two purposes:
        // 1. if the value is not available, it returns -9999
        // 2. if the value is null, because the field is not being sent from the api, it
        // returns -9999

        try {
            return node.asInt();
        } catch (NumberFormatException e) {
            log.info("At least one of the values is not available");
            return -9999;
        } catch (NullPointerException e) {
            log.info("At least one of the values is not available");
            return -9999;
        } catch (Exception e) {
            log.info("At least one of the values is not available");
            return -9999;
        }
    }

    private String parseString(JsonNode node) {
        // helperfunction solves two purposes:
        // 1. if the value is not available, it returns "not available"
        // 2. if the value is null, because the field is not being sent from the api, it
        // returns "not available"
        try {
            return node.toString();
        } catch (NumberFormatException e) {
            log.info("At least one of the values is not available");
            return "not available";
        } catch (NullPointerException e) {
            log.info("At least one of the values is not available");
            return "not available";
        } catch (Exception e) {
            log.info("At least one of the values is not available");
            return "not available";
        }
    }

}