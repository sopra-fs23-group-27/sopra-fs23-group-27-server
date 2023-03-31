package ch.uzh.ifi.hase.soprafs23.entity;

import javax.persistence.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COUNTRY")
public class Country {

    @Id
    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String capital;

    @Column(nullable = true)
    private String currency;

    @Column(nullable = true)
    private String callingCode;

    @Column(nullable = true)
    private String population;

    @Column(nullable = true)
    private String lifeExpectancy;

    @Column(nullable = true)
    private String surfaceArea;

    @Column(nullable = true)
    private String forestedArea;

    @Column(nullable = true)
    private String gdp;

    @Column(nullable = true)
    private String region;

    @Column(nullable = true)
    private String flag;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getLifeExpectancy() {
        return lifeExpectancy;
    }

    public void setLifeExpectancy(String lifeExpectancy) {
        this.lifeExpectancy = lifeExpectancy;
    }

    public String getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(String surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public String getForestedArea() {
        return forestedArea;
    }

    public void setForestedArea(String forestedArea) {
        this.forestedArea = forestedArea;
    }

    public String getGdp() {
        return gdp;
    }

    public void setGdp(String gdp) {
        this.gdp = gdp;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
