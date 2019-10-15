package com.flowable.training.dp.dataobject;

import java.util.Date;

/**
 * DTO representing an Address Data Object
 */
public class Address {

    public static final String DATA_OBJECT_KEY = "DOB-DATA001";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_STREET = "street";
    public static final String FIELD_CITY = "city";
    public static final String FIELD_ZIP_CODE = "zipCode";
    public static final String FIELD_COUNTRY = "country";
    public static final String FIELD_LAST_UPDATED = "lastUpdated";

    private String title;
    private String name;
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private Date lastUpdated;


    public Address() {
        this.title = "";
        this.name = "";
        this.city = "";
        this.zipCode = "";
        this.country = "";
    }

    public Address(String title, String name, String street, String city, String zipCode, String country, Date lastUpdated) {
        this.title = title;
        this.name = name;
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.lastUpdated = lastUpdated;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
