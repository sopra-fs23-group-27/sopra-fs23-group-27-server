package ch.uzh.ifi.hase.soprafs23.service;

// import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;

//@Service
public class CountryService {

    // make constuctor
    public CountryService() {

        try {

            URL url = new URL("https://api.api-ninjas.com/v1/country?name=United States");
            System.out.println("URL: " + url.toString());
            // HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // connection.setRequestProperty("accept", "application/json");
            // InputStream responseStream = connection.getInputStream();
            // ObjectMapper mapper = new ObjectMapper();
            // JsonNode root = mapper.readTree(responseStream);
            // System.out.println(root.path("fact").asText());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}