package com.acme.salary.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "countries")
public class Country {
    @Id @Column(name = "country_code", length = 2) private String code;
    private String name;
    @Column(name = "currency_code", length = 3) private String currencyCode;
    protected Country() { }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCurrencyCode() { return currencyCode; }
}
