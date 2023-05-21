package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.repository.CountryRepository;
import ch.uzh.ifi.hase.soprafs23.service.WebSocketService;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.ChoicesDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.HintDTO;
import ch.uzh.ifi.hase.soprafs23.websocket.dto.outgoing.FlagDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import java.util.HashMap;
import java.util.List;

public class HintHandler {
    private final Logger log = LoggerFactory.getLogger(CountryHandler.class);

    private String countryCode;

    private Lobby lobby;
    private int numHints;
    private int numChoices;
    private ArrayList<String> continent;

    private final CountryRepository countryRepository;
    private final WebSocketService webSocketService;

    private List<HashMap.Entry<String, String>> hints;
    private Timer timer;

    public HintHandler(String countryCode, Lobby lobby, CountryRepository countryRepository,
                       WebSocketService webSocketService) {
        this.countryCode = countryCode;
        this.lobby = lobby;
        this.numHints = determineNumHints(lobby);
        this.numChoices = determineNumOptions(lobby);
        this.countryRepository = countryRepository;
        this.webSocketService = webSocketService;
        this.continent = lobby.getContinent();
    }

    /**
     * sets a shuffled List<HashMap.Entry<String, String>> of `n` attributes
     * for a given country
     *
     * @return List of country attributes
     */
    public void setHints() {
        List<HashMap.Entry<String, String>> attributeList = getAndShuffleAllAttributes();

        // if there are less attributes than the calculated number of hints, set numHints to the number of attributes
        if (attributeList.size() < numHints) {
            this.numHints = attributeList.size();
        }
        this.hints = attributeList.subList(0, numHints);

    }

    public List<Map.Entry<String, String>> getHints() {
        return hints;
    }

    /**
     * The sendRequiredDetailsViaWebSocket method is a scheduled task that is executed
     * every five seconds. The method first checks if roundStarted is true,
     * and if the hints list is not null and not empty. If all of these
     * conditions are true, it removes the first hint from the list, converts
     * it to a string, and sends it via the WebSocket. This way, a hint will
     * only be sent if the roundStarted flag is set to true, and the hints
     * list is not null and not empty.
     */
    public void sendRequiredDetailsViaWebSocket() {
        // send url of flag immediately
        String url = hints.remove(0).toString();
        FlagDTO flagDTO = new FlagDTO(url);

        // sleep for 1 second to ensure that each player receives flag at the same time
        webSocketService.wait(1000);

        log.info("FLAG-URL: " + url);
        webSocketService.sendToLobby(lobby.getLobbyId(), "/flag-in-round", flagDTO);

        // if game mode is advanced, send remaining hints every hintInterval
        // seconds starting after firstHintAfter seconds
        if (lobby instanceof AdvancedLobby) {
            startSendingHints(((AdvancedLobby) lobby).getNumSecondsUntilHint(),
                    ((AdvancedLobby) lobby).getHintInterval());
        }
        // if game is in basic mode, provide n options immediately
        else {
            sendChoices();
        }
    }

    public void stopSendingHints() {
        this.timer.cancel();
    }

    private int determineNumHints(Lobby lobby) {
        // set variables depending on lobby type
        if (lobby instanceof AdvancedLobby) {
            int numSeconds = ((AdvancedLobby) lobby).getNumSeconds();
            int numSecondsUntilHint = ((AdvancedLobby) lobby).getNumSecondsUntilHint();
            int hintInterval = ((AdvancedLobby) lobby).getHintInterval();

            // ensure that number of hints is at least 1
            if (numSecondsUntilHint >= numSeconds) {
                int nHints = 1;
                return nHints;
            }
            else if (numSecondsUntilHint < numSeconds && hintInterval >= numSeconds) {
                int nHints = 2;
                return nHints;
            }
            else {
                // user integer division to determine number of hints, add 1 to account for flag
                int nHints = ((numSeconds - numSecondsUntilHint) / hintInterval) + 1;
                return nHints;
            }
        }
        else {
            return 1;
        }
    }

    private int determineNumOptions(Lobby lobby) {
        // set variables depending on lobby type
        if (lobby instanceof BasicLobby) {
            int numChoices = ((BasicLobby) lobby).getNumOptions();
            return numChoices;
        }
        else {
            return 1;
        }
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
        // Iterate over the entries and remove those with values containing the substring
        Iterator<Map.Entry<String, String>> iterator = countryAttributes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String value = entry.getValue();
            if (value.contains("not available") || value.contains("-9999") || value.isEmpty()) {
                iterator.remove();
            }
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

    /**
     * Sends hints to client given the country, the number of hints and the hint frequency
     *
     * @return HashMap of URL
     */
    private void startSendingHints(int delay, int period) {
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!hints.isEmpty()) {
                    String nextHint = hints.remove(0).toString();
                    HintDTO hintDTO = new HintDTO(nextHint);
                    log.info("Hint: " + nextHint);
                    webSocketService.sendToLobby(lobby.getLobbyId(), "/hints-in-round", hintDTO);

                }
                else {
                    timer.cancel();
                }
            }
        };
        this.timer.schedule(timerTask, delay * 1000L, period * 1000L);
    }

    /**
     * Sends choices to client given the country, the number of choices
     */
    private void sendChoices() {
        Country countryLookedFor = countryRepository.findByCountryCode(countryCode);

        // get all country names except the one looked for
        List<String> countryNamesList = countryRepository.getAllCountryNamesInContinents(this.continent);
        countryNamesList.remove(countryLookedFor.getName());

        // shuffle list and get first n elements (n = numChoices - 1)
        Collections.shuffle(countryNamesList);
        List<String> choices = countryNamesList.subList(0, numChoices - 1);

        // add country looked for to choices and shuffle again
        choices.add(countryLookedFor.getName());
        Collections.shuffle(choices);

        // send choices via websocket
        ChoicesDTO choicesDTO = new ChoicesDTO(choices);
        webSocketService.sendToLobby(lobby.getLobbyId(), "/choices-in-round", choicesDTO);
    }
}

