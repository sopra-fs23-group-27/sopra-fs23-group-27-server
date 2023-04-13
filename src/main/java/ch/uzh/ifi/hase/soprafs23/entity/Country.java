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
    private Integer gdp;

    @Column(nullable = true)
    private Integer surfaceArea;

    @Column(nullable = true)
    private Integer lifeExpectancyMale;

    @Column(nullable = true)
    private Integer lifeExpectancyFemale;

    @Column(nullable = true)
    private Integer unemploymentRate;

    @Column(nullable = true)
    private Integer imports;

    @Column(nullable = true)
    private Integer exports;

    @Column(nullable = true)
    private Integer homicideRate;

    @Column(nullable = true)
    private String currency;

    @Column(nullable = true)
    private Integer populationGrowth;

    @Column(nullable = true)
    private Integer secondarySchoolEnrollmentFemale;

    @Column(nullable = true)
    private Integer secondarySchoolEnrollmentMale;

    @Column(nullable = true)
    private String capital;

    @Column(nullable = true)
    private Integer co2Emissions;

    @Column(nullable = true)
    private Integer forestedArea;

    @Column(nullable = true)
    private Integer infantMortality;

    @Column(nullable = true)
    private Integer population;

    @Column(nullable = true)
    private Integer populationDensity;

    @Column(nullable = true)
    private Integer internetUsers;

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

    public Integer getGdp() {
        return gdp;
    }

    public void setGdp(Integer gdp) {
        this.gdp = gdp;
    }

    public Integer getSurfaceArea() {
        return surfaceArea;
    }

    public void setSurfaceArea(Integer surfaceArea) {
        this.surfaceArea = surfaceArea;
    }

    public Integer getLifeExpectancyMale() {
        return lifeExpectancyMale;
    }

    public void setLifeExpectancyMale(Integer lifeExpectancyMale) {
        this.lifeExpectancyMale = lifeExpectancyMale;
    }

    public Integer getLifeExpectancyFemale() {
        return lifeExpectancyFemale;
    }

    public void setLifeExpectancyFemale(Integer lifeExpectancyFemale) {
        this.lifeExpectancyFemale = lifeExpectancyFemale;
    }

    public Integer getUnemploymentRate() {
        return unemploymentRate;
    }

    public void setUnemploymentRate(Integer unemploymentRate) {
        this.unemploymentRate = unemploymentRate;
    }

    public Integer getImports() {
        return imports;
    }

    public void setImports(Integer imports) {
        this.imports = imports;
    }

    public Integer getExports() {
        return exports;
    }

    public void setExports(Integer exports) {
        this.exports = exports;
    }

    public Integer getHomicideRate() {
        return homicideRate;
    }

    public void setHomicideRate(Integer homicideRate) {
        this.homicideRate = homicideRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getPopulationGrowth() {
        return populationGrowth;
    }

    public void setPopulationGrowth(Integer populationGrowth) {
        this.populationGrowth = populationGrowth;
    }

    public Integer getSecondarySchoolEnrollmentFemale() {
        return secondarySchoolEnrollmentFemale;
    }

    public void setSecondarySchoolEnrollmentFemale(Integer secondarySchoolEnrollmentFemale) {
        this.secondarySchoolEnrollmentFemale = secondarySchoolEnrollmentFemale;
    }

    public Integer getSecondarySchoolEnrollmentMale() {
        return secondarySchoolEnrollmentMale;
    }

    public void setSecondarySchoolEnrollmentMale(Integer secondarySchoolEnrollmentMale) {
        this.secondarySchoolEnrollmentMale = secondarySchoolEnrollmentMale;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public Integer getCo2Emissions() {
        return co2Emissions;
    }

    public void setCo2Emissions(Integer co2Emissions) {
        this.co2Emissions = co2Emissions;
    }

    public Integer getForestedArea() {
        return forestedArea;
    }

    public void setForestedArea(Integer forestedArea) {
        this.forestedArea = forestedArea;
    }

    public Integer getInfantMortality() {
        return infantMortality;
    }

    public void setInfantMortality(Integer infantMortality) {
        this.infantMortality = infantMortality;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getPopulationDensity() {
        return populationDensity;
    }

    public void setPopulationDensity(Integer populationDensity) {
        this.populationDensity = populationDensity;
    }

    public Integer getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(Integer internetUsers) {
        this.internetUsers = internetUsers;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
