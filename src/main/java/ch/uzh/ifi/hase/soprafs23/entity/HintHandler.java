package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.CountryHandlerService;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.HintDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.*;

import java.util.HashMap;
import java.util.List;

public class HintHandler {
    private final Logger log = LoggerFactory.getLogger(CountryHandlerService.class);

    private String countryCode;
    private int numHints;
    private Long gameID;
    private final CountryRepository countryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketService webSocketService;

    private List<HashMap.Entry<String, String>> hints;
    private Timer timer;

    public HintHandler(String countryCode, int numHints, Long gameID,
                       CountryRepository countryRepository,
                       SimpMessagingTemplate messagingTemplate, WebSocketService webSocketService) {
        this.countryCode = countryCode;
        this.numHints = numHints;
        this.gameID = gameID;
        this.countryRepository = countryRepository;
        this.messagingTemplate = messagingTemplate;
        this.webSocketService = webSocketService;
    }

    /**
     * sets a shuffled List<HashMap.Entry<String, String>> of `n` attributes
     * for a given country
     *
     * @return List of country attributes
     */
    public void setHints() {
        List<HashMap.Entry<String, String>> attributeList = getAndShuffleAllAttributes();

        this.hints = attributeList.subList(0, numHints);

    }

    public List<Map.Entry<String, String>> getHints() {
        return hints;
    }

    /**
     * The sendHintViaWebSocket method is a scheduled task that is executed
     * every five seconds. This method first checks if roundStarted is true,
     * and if the hints list is not null and not empty. If all of these
     * conditions are true, it removes the first hint from the list, converts
     * it to a string, and sends it via the WebSocket using the
     * convertAndSend method of SimpMessagingTemplate. This way, a hint will
     * only be sent if the roundStarted flag is set to true, and the hints
     * list is not null and not empty.
     */
    public void sendHintViaWebSocket(int firstHintAfter, int hintInterval) {
        // send first hint immediately
        String firstHint = hints.remove(0).toString();
        HintDTO hintDTO = new HintDTO(firstHint);
        log.info("FLAG-URL: " + firstHint);
        webSocketService.sendToLobby(gameID, "/hints-in-round", hintDTO);

        // send remaining hints every hintInterval seconds starting after firstHintAfter seconds
        startTimer(firstHintAfter, hintInterval);

    }

    public void startTimer(int delay, int period) {
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!hints.isEmpty()) {
                    String nextHint = hints.remove(0).toString();
                    HintDTO hintDTO = new HintDTO(nextHint);
                    log.info("Hint: " + nextHint);
                    webSocketService.sendToLobby(gameID, "/hints-in-round", hintDTO);

                }
                else {
                    timer.cancel();
                }
            }
        };
        this.timer.schedule(timerTask, delay * 1000L, period * 1000L);
    }

    public void stopTimer() {
        this.timer.cancel();
    }

    /**
     * returns a shuffled List<HashMap.Entry<String, String>> of all available
     * attributes for a given country
     *
     * @return List of country attributes
     */
    private List<HashMap.Entry<String, String>> getAndShuffleAllAttributes() {
        HashMap<String, String> flagURLMap = getFlagURL();
        HashMap<String, String> countryAttributesMap = getCountryAttributes();

        // remove null values and non-available attributes
        filter(countryAttributesMap);

        // convert hashmap to list
        List<HashMap.Entry<String, String>> flagURLList = new ArrayList<>(flagURLMap.entrySet());
        List<HashMap.Entry<String, String>> countryAttributesList = new ArrayList<>(countryAttributesMap.entrySet());

        // shuffle the list of attributes
        Collections.shuffle(countryAttributesList);

        // merge lists
        List<HashMap.Entry<String, String>> allAttributes = new ArrayList<>();
        allAttributes.addAll(flagURLList);
        allAttributes.addAll(countryAttributesList);

        return allAttributes;
    }

    /**
     * filters the HashMap<String, String> containing all country attributes. Method
     * removes all null values and all columns containing either string "not available"
     * or the string "-9999"
     *
     * @param countryAttributes HashMap containing all country attributes
     */
    private void filter(HashMap<String, String> countryAttributes) {
//        countryAttributes.removeAll(Collections.singleton(null));
        while (countryAttributes.values().remove(null)) ;
        while (countryAttributes.values().remove("not available")) {
        }
        while (countryAttributes.values().remove("-9999")) {
        }

    }

    /**
     * Returns an HashMap<String, String> with the attributes of the given country
     * Note, this list does not contain the country name, the ISO-code nor the url
     * for the flag
     *
     * @return ArrayList of country attributes
     */
    private HashMap<String, String> getCountryAttributes() {
        HashMap<String, String> countryAttributes = new HashMap<>();

        Country country = countryRepository.findByCountryCode(countryCode);

        // add attributes of this country to list
        countryAttributes.put("GDP", country.getGdp().toString());
        countryAttributes.put("Surface Area", country.getSurfaceArea().toString());
        countryAttributes.put("Life Expectancy Male", country.getLifeExpectancyMale().toString());
        countryAttributes.put("Life Expectancy Female", country.getLifeExpectancyFemale().toString());
        countryAttributes.put("Unemployment Rate", country.getUnemploymentRate().toString());
        countryAttributes.put("Imports", country.getImports().toString());
        countryAttributes.put("Exports", country.getExports().toString());
        countryAttributes.put("Homicide Rate", country.getHomicideRate().toString());
        countryAttributes.put("Currency", country.getCurrency());
        countryAttributes.put("Population Growth", country.getPopulationGrowth().toString());
        countryAttributes.put("Secondary School Enrollment Female", country.getSecondarySchoolEnrollmentFemale().toString());
        countryAttributes.put("Secondary School Enrollment Male", country.getSecondarySchoolEnrollmentMale().toString());
        countryAttributes.put("Capital", country.getCapital());
        countryAttributes.put("Co2-Emissions", country.getCo2Emissions().toString());
        countryAttributes.put("Forested Area", country.getForestedArea().toString());
        countryAttributes.put("Infant Mortality", country.getInfantMortality().toString());
        countryAttributes.put("Population", country.getPopulation().toString());
        countryAttributes.put("Population Density", country.getPopulationDensity().toString());
        countryAttributes.put("Internet Users", country.getInternetUsers().toString());

        return countryAttributes;
    }

    /**
     * Returns an HashMap<String, String> with the url for the flag
     *
     * @return HashMap of URL
     */
    private HashMap<String, String> getFlagURL() {
        HashMap<String, String> flagURL = new HashMap<>();

        Country country = countryRepository.findByCountryCode(countryCode);

        // add attributes of this country to list
        flagURL.put("URL", country.getFlag());
        return flagURL;
    }

}

