package ch.uzh.ifi.hase.soprafs23.entity;

import java.net.URL;

import javax.persistence.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.repository.query.Param;

@Entity
@Table(name = "COUNTRY")
public class Country {

    @Id
    @Column(nullable = false)
    private String countryCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String gdp;

    @Column(nullable = true)
    private String surfaceArea;

    @Column(nullable = true)
    private String lifeExpectancyMale;

    @Column(nullable = true)
    private String lifeExpectancyFemale;

    @Column(nullable = true)
    private String unemploymentRate;

    @Column(nullable = true)
    private String imports;

    @Column(nullable = true)
    private String exports;

    @Column(nullable = true)
    private String homicideRate;

    @Column(nullable = true)
    private String currency;

    @Column(nullable = true)
    private String populationGrowth;

    @Column(nullable = true)
    private String secondarySchoolEnrollmentFemale;

    @Column(nullable = true)
    private String secondarySchoolEnrollmentMale;

    @Column(nullable = true)
    private String capital;

    @Column(nullable = true)
    private String co2Emissions;

    @Column(nullable = true)
    private String forestedArea;

    @Column(nullable = true)
    private String infantMortality;

    @Column(nullable = true)
    private String population;

    @Column(nullable = true)
    private String populationDensity;

    @Column(nullable = true)
    private String internetUsers;

    @Column(nullable = false)
    private String flag;

    @Column(nullable = false)
    private String continent;

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

    public String getGdp() {
        return gdp;
    }

    public void setGdp(String gdp) {
        this.gdp = gdp;
    }

    public String getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(String surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public String getLifeExpectancyMale() {
        return lifeExpectancyMale;
    }

    public void setLifeExpectancyMale(String lifeExpectancyMale) {
        this.lifeExpectancyMale = lifeExpectancyMale;
    }

    public String getLifeExpectancyFemale() {
        return lifeExpectancyFemale;
    }

    public void setLifeExpectancyFemale(String lifeExpectancyFemale) {
        this.lifeExpectancyFemale = lifeExpectancyFemale;
    }

    public String getUnemploymentRate() {
        return unemploymentRate;
    }

    public void setUnemploymentRate(String unemploymentRate) {
        this.unemploymentRate = unemploymentRate;
    }

    public String getImports() {
        return imports;
    }

    public void setImports(String imports) {
        this.imports = imports;
    }

    public String getExports() {
        return exports;
    }

    public void setExports(String exports) {
        this.exports = exports;
    }

    public String getHomicideRate() {
        return homicideRate;
    }

    public void setHomicideRate(String homicideRate) {
        this.homicideRate = homicideRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPopulationGrowth() {
        return populationGrowth;
    }

    public void setPopulationGrowth(String populationGrowth) {
        this.populationGrowth = populationGrowth;
    }

    public String getSecondarySchoolEnrollmentFemale() {
        return secondarySchoolEnrollmentFemale;
    }

    public void setSecondarySchoolEnrollmentFemale(String secondarySchoolEnrollmentFemale) {
        this.secondarySchoolEnrollmentFemale = secondarySchoolEnrollmentFemale;
    }

    public String getSecondarySchoolEnrollmentMale() {
        return secondarySchoolEnrollmentMale;
    }

    public void setSecondarySchoolEnrollmentMale(String secondarySchoolEnrollmentMale) {
        this.secondarySchoolEnrollmentMale = secondarySchoolEnrollmentMale;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCo2Emissions() {
        return co2Emissions;
    }

    public void setCo2Emissions(String co2Emissions) {
        this.co2Emissions = co2Emissions;
    }

    public String getForestedArea() {
        return forestedArea;
    }

    public void setForestedArea(String forestedArea) {
        this.forestedArea = forestedArea;
    }

    public String getInfantMortality() {
        return infantMortality;
    }

    public void setInfantMortality(String infantMortality) {
        this.infantMortality = infantMortality;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getPopulationDensity() {
        return populationDensity;
    }

    public void setPopulationDensity(String populationDensity) {
        this.populationDensity = populationDensity;
    }

    public String getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(String internetUsers) {
        this.internetUsers = internetUsers;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }
}
