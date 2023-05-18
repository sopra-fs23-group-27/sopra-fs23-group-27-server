package ch.uzh.ifi.hase.soprafs23.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CountryContinentMap {
    private HashMap<String, String> countryMap;

    public CountryContinentMap() {
        countryMap = new HashMap<>();
        initializeMap();
    }

    private void initializeMap() {
        countryMap.put("AF", "Asia");
        countryMap.put("AX", "Europe");
        countryMap.put("AL", "Europe");
        countryMap.put("DZ", "Africa");
        countryMap.put("AS", "Oceania");
        countryMap.put("AD", "Europe");
        countryMap.put("AO", "Africa");
        countryMap.put("AI", "Americas");
        countryMap.put("AG", "Americas");
        countryMap.put("AR", "Americas");
        countryMap.put("AM", "Asia");
        countryMap.put("AW", "Americas");
        countryMap.put("AU", "Oceania");
        countryMap.put("AT", "Europe");
        countryMap.put("AZ", "Asia");
        countryMap.put("BS", "Americas");
        countryMap.put("BH", "Asia");
        countryMap.put("BD", "Asia");
        countryMap.put("BB", "Americas");
        countryMap.put("BY", "Europe");
        countryMap.put("BE", "Europe");
        countryMap.put("BZ", "Americas");
        countryMap.put("BJ", "Africa");
        countryMap.put("BM", "Americas");
        countryMap.put("BT", "Asia");
        countryMap.put("BO", "Americas");
        countryMap.put("BQ", "Americas");
        countryMap.put("BA", "Europe");
        countryMap.put("BW", "Africa");
        countryMap.put("BV", "Americas");
        countryMap.put("BR", "Americas");
        countryMap.put("IO", "Africa");
        countryMap.put("BN", "Asia");
        countryMap.put("BG", "Europe");
        countryMap.put("BF", "Africa");
        countryMap.put("BI", "Africa");
        countryMap.put("CV", "Africa");
        countryMap.put("KH", "Asia");
        countryMap.put("CM", "Africa");
        countryMap.put("CA", "Americas");
        countryMap.put("KY", "Americas");
        countryMap.put("CF", "Africa");
        countryMap.put("TD", "Africa");
        countryMap.put("CL", "Americas");
        countryMap.put("CN", "Asia");
        countryMap.put("CX", "Oceania");
        countryMap.put("CC", "Oceania");
        countryMap.put("CO", "Americas");
        countryMap.put("KM", "Africa");
        countryMap.put("CG", "Africa");
        countryMap.put("CD", "Africa");
        countryMap.put("CK", "Oceania");
        countryMap.put("CR", "Americas");
        countryMap.put("CI", "Africa");
        countryMap.put("HR", "Europe");
        countryMap.put("CU", "Americas");
        countryMap.put("CW", "Americas");
        countryMap.put("CY", "Asia");
        countryMap.put("CZ", "Europe");
        countryMap.put("DK", "Europe");
        countryMap.put("DJ", "Africa");
        countryMap.put("DM", "Americas");
        countryMap.put("DO", "Americas");
        countryMap.put("EC", "Americas");
        countryMap.put("EG", "Africa");
        countryMap.put("SV", "Americas");
        countryMap.put("GQ", "Africa");
        countryMap.put("ER", "Africa");
        countryMap.put("EE", "Europe");
        countryMap.put("SZ", "Africa");
        countryMap.put("ET", "Africa");
        countryMap.put("FK", "Americas");
        countryMap.put("FO", "Europe");
        countryMap.put("FJ", "Oceania");
        countryMap.put("FI", "Europe");
        countryMap.put("FR", "Europe");
        countryMap.put("GF", "Americas");
        countryMap.put("PF", "Oceania");
        countryMap.put("TF", "Africa");
        countryMap.put("GA", "Africa");
        countryMap.put("GM", "Africa");
        countryMap.put("GE", "Asia");
        countryMap.put("DE", "Europe");
        countryMap.put("GH", "Africa");
        countryMap.put("GI", "Europe");
        countryMap.put("GR", "Europe");
        countryMap.put("GL", "Americas");
        countryMap.put("GD", "Americas");
        countryMap.put("GP", "Americas");
        countryMap.put("GU", "Oceania");
        countryMap.put("GT", "Americas");
        countryMap.put("GG", "Europe");
        countryMap.put("GN", "Africa");
        countryMap.put("GW", "Africa");
        countryMap.put("GY", "Americas");
        countryMap.put("HT", "Americas");
        countryMap.put("HM", "Oceania");
        countryMap.put("VA", "Europe");
        countryMap.put("HN", "Americas");
        countryMap.put("HK", "Asia");
        countryMap.put("HU", "Europe");
        countryMap.put("IS", "Europe");
        countryMap.put("IN", "Asia");
        countryMap.put("ID", "Asia");
        countryMap.put("IR", "Asia");
        countryMap.put("IQ", "Asia");
        countryMap.put("IE", "Europe");
        countryMap.put("IM", "Europe");
        countryMap.put("IL", "Asia");
        countryMap.put("IT", "Europe");
        countryMap.put("JM", "Americas");
        countryMap.put("JP", "Asia");
        countryMap.put("JE", "Europe");
        countryMap.put("JO", "Asia");
        countryMap.put("KZ", "Asia");
        countryMap.put("KE", "Africa");
        countryMap.put("KI", "Oceania");
        countryMap.put("KP", "Asia");
        countryMap.put("KR", "Asia");
        countryMap.put("KW", "Asia");
        countryMap.put("KG", "Asia");
        countryMap.put("LA", "Asia");
        countryMap.put("LV", "Europe");
        countryMap.put("LB", "Asia");
        countryMap.put("LS", "Africa");
        countryMap.put("LR", "Africa");
        countryMap.put("LY", "Africa");
        countryMap.put("LI", "Europe");
        countryMap.put("LT", "Europe");
        countryMap.put("LU", "Europe");
        countryMap.put("MO", "Asia");
        countryMap.put("MG", "Africa");
        countryMap.put("MW", "Africa");
        countryMap.put("MY", "Asia");
        countryMap.put("MV", "Asia");
        countryMap.put("ML", "Africa");
        countryMap.put("MT", "Europe");
        countryMap.put("MH", "Oceania");
        countryMap.put("MQ", "Americas");
        countryMap.put("MR", "Africa");
        countryMap.put("MU", "Africa");
        countryMap.put("YT", "Africa");
        countryMap.put("MX", "Americas");
        countryMap.put("FM", "Oceania");
        countryMap.put("MD", "Europe");
        countryMap.put("MC", "Europe");
        countryMap.put("MN", "Asia");
        countryMap.put("ME", "Europe");
        countryMap.put("MS", "Americas");
        countryMap.put("MA", "Africa");
        countryMap.put("MZ", "Africa");
        countryMap.put("MM", "Asia");
        countryMap.put("NA", "Africa");
        countryMap.put("NR", "Oceania");
        countryMap.put("NP", "Asia");
        countryMap.put("NL", "Europe");
        countryMap.put("NC", "Oceania");
        countryMap.put("NZ", "Oceania");
        countryMap.put("NI", "Americas");
        countryMap.put("NE", "Africa");
        countryMap.put("NG", "Africa");
        countryMap.put("NU", "Oceania");
        countryMap.put("NF", "Oceania");
        countryMap.put("MK", "Europe");
        countryMap.put("MP", "Oceania");
        countryMap.put("NO", "Europe");
        countryMap.put("OM", "Asia");
        countryMap.put("PK", "Asia");
        countryMap.put("PW", "Oceania");
        countryMap.put("PS", "Asia");
        countryMap.put("PA", "Americas");
        countryMap.put("PG", "Oceania");
        countryMap.put("PY", "Americas");
        countryMap.put("PE", "Americas");
        countryMap.put("PH", "Asia");
        countryMap.put("PN", "Oceania");
        countryMap.put("PL", "Europe");
        countryMap.put("PT", "Europe");
        countryMap.put("PR", "Americas");
        countryMap.put("QA", "Asia");
        countryMap.put("RE", "Africa");
        countryMap.put("RO", "Europe");
        countryMap.put("RU", "Europe");
        countryMap.put("RW", "Africa");
        countryMap.put("BL", "Americas");
        countryMap.put("SH", "Africa");
        countryMap.put("KN", "Americas");
        countryMap.put("LC", "Americas");
        countryMap.put("MF", "Americas");
        countryMap.put("PM", "Americas");
        countryMap.put("VC", "Americas");
        countryMap.put("WS", "Oceania");
        countryMap.put("SM", "Europe");
        countryMap.put("ST", "Africa");
        countryMap.put("SA", "Asia");
        countryMap.put("SN", "Africa");
        countryMap.put("RS", "Europe");
        countryMap.put("SC", "Africa");
        countryMap.put("SL", "Africa");
        countryMap.put("SG", "Asia");
        countryMap.put("SX", "Americas");
        countryMap.put("SK", "Europe");
        countryMap.put("SI", "Europe");
        countryMap.put("SB", "Oceania");
        countryMap.put("SO", "Africa");
        countryMap.put("ZA", "Africa");
        countryMap.put("GS", "Americas");
        countryMap.put("SS", "Africa");
        countryMap.put("ES", "Europe");
        countryMap.put("LK", "Asia");
        countryMap.put("SD", "Africa");
        countryMap.put("SR", "Americas");
        countryMap.put("SJ", "Europe");
        countryMap.put("SE", "Europe");
        countryMap.put("CH", "Europe");
        countryMap.put("SY", "Asia");
        countryMap.put("TW", "Asia");
        countryMap.put("TJ", "Asia");
        countryMap.put("TZ", "Africa");
        countryMap.put("TH", "Asia");
        countryMap.put("TL", "Asia");
        countryMap.put("TG", "Africa");
        countryMap.put("TK", "Oceania");
        countryMap.put("TO", "Oceania");
        countryMap.put("TT", "Americas");
        countryMap.put("TN", "Africa");
        countryMap.put("TR", "Asia");
        countryMap.put("TM", "Asia");
        countryMap.put("TC", "Americas");
        countryMap.put("TV", "Oceania");
        countryMap.put("UG", "Africa");
        countryMap.put("UA", "Europe");
        countryMap.put("AE", "Asia");
        countryMap.put("GB", "Europe");
        countryMap.put("US", "Americas");
        countryMap.put("UM", "Oceania");
        countryMap.put("UY", "Americas");
        countryMap.put("UZ", "Asia");
        countryMap.put("VU", "Oceania");
        countryMap.put("VE", "Americas");
        countryMap.put("VN", "Asia");
        countryMap.put("VG", "Americas");
        countryMap.put("VI", "Americas");
        countryMap.put("WF", "Oceania");
        countryMap.put("EH", "Africa");
        countryMap.put("YE", "Asia");
        countryMap.put("ZM", "Africa");
        countryMap.put("ZW", "Africa");
    }

    public String getContinent(String countryCode) {
        return countryMap.getOrDefault(countryCode, "");
    }

    public ArrayList<String> getCountryCodesByContinent(String continent) {
        ArrayList<String> countryCodes = new ArrayList<>();
        for (Map.Entry<String, String> entry : countryMap.entrySet()) {
            if (entry.getValue().equals(continent)) {
                countryCodes.add(entry.getKey());
            }
        }
        return countryCodes;
    }
}