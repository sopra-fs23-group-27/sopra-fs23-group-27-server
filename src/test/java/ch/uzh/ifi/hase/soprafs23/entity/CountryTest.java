package ch.uzh.ifi.hase.soprafs23.entity;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CountryTest {
    
    @Test
    public void testGettersAndSetters() {
        Country country = new Country();
        
        // Set values
        country.setCountryCode("USA");
        country.setName("United States");
        country.setGdp("21.44 trillion USD");
        country.setSurfaceArea("9,834,000 km²");
        country.setLifeExpectancyMale("76.3 years");
        country.setLifeExpectancyFemale("81.2 years");
        country.setUnemploymentRate("4.2%");
        country.setImports("2.5 trillion USD");
        country.setExports("1.7 trillion USD");
        country.setHomicideRate("5.0 per 100,000");
        country.setCurrency("US Dollar");
        country.setPopulationGrowth("0.71%");
        country.setSecondarySchoolEnrollmentFemale("98%");
        country.setSecondarySchoolEnrollmentMale("98%");
        country.setCapital("Washington D.C.");
        country.setCo2Emissions("5.14 billion metric tons");
        country.setForestedArea("3.08 million km²");
        country.setInfantMortality("5.7 per 1,000 live births");
        country.setPopulation("332,915,073");
        country.setPopulationDensity("35 people per km²");
        country.setInternetUsers("312 million");
        country.setFlag("https://www.example.com/usa-flag.jpg");
        
        // Test values
        assertEquals("USA", country.getCountryCode());
        assertEquals("United States", country.getName());
        assertEquals("21.44 trillion USD", country.getGdp());
        assertEquals("9,834,000 km²", country.getSurfaceArea());
        assertEquals("76.3 years", country.getLifeExpectancyMale());
        assertEquals("81.2 years", country.getLifeExpectancyFemale());
        assertEquals("4.2%", country.getUnemploymentRate());
        assertEquals("2.5 trillion USD", country.getImports());
        assertEquals("1.7 trillion USD", country.getExports());
        assertEquals("5.0 per 100,000", country.getHomicideRate());
        assertEquals("US Dollar", country.getCurrency());
        assertEquals("0.71%", country.getPopulationGrowth());
        assertEquals("98%", country.getSecondarySchoolEnrollmentFemale());
        assertEquals("98%", country.getSecondarySchoolEnrollmentMale());
        assertEquals("Washington D.C.", country.getCapital());
        assertEquals("5.14 billion metric tons", country.getCo2Emissions());
        assertEquals("3.08 million km²", country.getForestedArea());
        assertEquals("5.7 per 1,000 live births", country.getInfantMortality());
        assertEquals("332,915,073", country.getPopulation());
        assertEquals("35 people per km²", country.getPopulationDensity());
        assertEquals("312 million", country.getInternetUsers());
        assertEquals("https://www.example.com/usa-flag.jpg", country.getFlag());
    }
}
